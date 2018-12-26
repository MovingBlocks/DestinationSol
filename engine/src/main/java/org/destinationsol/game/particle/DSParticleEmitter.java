/*
 * Copyright 2018 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.destinationsol.game.particle;

import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.utils.Array;
import org.json.JSONObject;
import com.google.common.base.Preconditions;
import org.destinationsol.assets.audio.OggSoundSet;
import org.destinationsol.common.NotNull;
import org.destinationsol.common.SolMath;
import org.destinationsol.common.SolRandom;
import org.destinationsol.game.GameColors;
import org.destinationsol.game.GameDrawer;
import org.destinationsol.game.SolGame;
import org.destinationsol.game.SolObject;
import org.destinationsol.game.drawables.Drawable;
import org.destinationsol.game.drawables.DrawableLevel;
import org.destinationsol.game.planet.Planet;
import org.destinationsol.game.ship.SolShip;

import java.util.ArrayList;
import java.util.List;

public class DSParticleEmitter {

    private static final float JUMP_SPEED_THRESHOLD = 0.9f;
    private static final float JUMP_SIZE_THRESHOLD = 0.7f;
    private static final float MAX_BOUNDINGBOX_RECALC_AWAIT = .5f;
    private static final float MAX_TIME_BETWEEN_POSITION_CHANGE = .25f;

    private Vector2 position;
    private String trigger;
    private float angleOffset;
    private boolean hasLight;
    private EffectConfig config;
    private List<String> workSounds;
    private OggSoundSet workSoundSet;

    private List<Drawable> drawables;
    private ParticleEmitter particleEmitter;
    private DrawableLevel drawableLevel;
    private Vector2 relativePosition;
    private Vector2 originalRelativePosition;
    private float relativeAngle;
    private float areaSize;
    private float timeSinceLastPositionChange;
    private float boundingBoxRecalcAwait;
    private ParticleEmitter.ScaledNumericValue originalVelocityAngle;
    private ParticleEmitter.ScaledNumericValue originalRotation;
    private boolean inheritsVelocity;
    private boolean working;
    private boolean floatedUp;
    private BoundingBox boundingBox;
    private LightSource light;
    private SolGame game;

    public DSParticleEmitter(@NotNull Vector2 position, @NotNull String trigger, float angleOffset, boolean hasLight, JSONObject effectConfigNode, List<String> sounds) {
        Preconditions.checkNotNull(position, "position cannot be null");
        this.position = new Vector2(position);
        this.trigger = Preconditions.checkNotNull(trigger, "trigger cannot be null");
        this.angleOffset = angleOffset;
        this.hasLight = hasLight;
        this.config = EffectConfig.load(effectConfigNode, new EffectTypes(), new GameColors());
        this.workSounds = sounds;

        workSoundSet = null;
        drawables = null;
        particleEmitter = null;
        drawableLevel = null;
        relativePosition = null;
        originalRelativePosition = null;
        relativeAngle = 0f;
        game = null;
    }

    public DSParticleEmitter(SolGame game, DSParticleEmitter particleEmitter, SolShip ship) {
        this.angleOffset = particleEmitter.getAngleOffset();
        this.hasLight = particleEmitter.getHasLight();
        this.trigger = particleEmitter.getTrigger();
        this.position = particleEmitter.getPosition();
        this.config = particleEmitter.getEffectConfig();
        if (!particleEmitter.getWorkSounds().isEmpty()) {
            this.workSoundSet = new OggSoundSet(game.getSoundManager(), particleEmitter.getWorkSounds());
        } else {
            this.workSoundSet = null;
        }
        Vector2 shipPos = ship.getPosition();
        Vector2 shipVelocity = ship.getVelocity();

        initialiseEmitter(config, -1, DrawableLevel.PART_BG_0, position, true, game, shipPos, shipVelocity, angleOffset, hasLight);
    }

    public DSParticleEmitter(EffectConfig config, float size, DrawableLevel drawableLevel, Vector2 relativePosition,
                             boolean inheritsVelocity, SolGame game, Vector2 basePosition, Vector2 baseVelocity,
                             float relativeAngle) {
        initialiseEmitter(config, size, drawableLevel, relativePosition, inheritsVelocity, game, basePosition, baseVelocity,
                relativeAngle, false);
    }

    private void initialiseEmitter(EffectConfig config, float size, DrawableLevel drawableLevel, Vector2 relativePosition, boolean inheritsVelocity,
                                   SolGame game, Vector2 basePosition, Vector2 baseVelocity, float relativeAngle, boolean hasLight) {

        drawables = new ArrayList<>();
        ParticleEmitterDrawable drawable = new ParticleEmitterDrawable();
        drawables.add(drawable);

        this.config = config;
        this.particleEmitter = config.emitter.newEmitter();
        this.drawableLevel = drawableLevel;
        this.relativePosition = new Vector2(relativePosition);
        this.originalRelativePosition = new Vector2(this.relativePosition);
        this.position = new Vector2();
        this.relativeAngle = relativeAngle;
        this.game = game;

        light = new LightSource(config.size * 2.5f, true, 0.7f, relativePosition, config.tint);
        if (hasLight) {
            light.collectDrawables(drawables);
        }

        if (size <= 0) {
            size = config.size;
        }

        if (particleEmitter.getSpawnShape().getShape() != ParticleEmitter.SpawnShape.point) { // has area
            multiplyValue(particleEmitter.getEmission(), size * size);
            multiplyValue(particleEmitter.getSpawnWidth(), size);
            multiplyValue(particleEmitter.getSpawnHeight(), size);
            areaSize = 0;
        } else if (JUMP_SPEED_THRESHOLD < particleEmitter.getVelocity().getHighMax()) { // moves fast
            multiplyValue(particleEmitter.getEmission(), size * size);
            ParticleEmitter.ScaledNumericValue velocity = particleEmitter.getVelocity();
            velocity.setHigh(velocity.getHighMin() * size, velocity.getHighMax() * size);
            areaSize = 0;
        } else if (JUMP_SIZE_THRESHOLD < particleEmitter.getXScale().getHighMax()) { // large scale
            ParticleEmitter.ScaledNumericValue scale = particleEmitter.getXScale();
            scale.setHigh(scale.getHighMin() * size, scale.getHighMax() * size);
            areaSize = 0;
        } else {
            areaSize = size;
        }

        particleEmitter.setSprites(new Array<Sprite>(new Sprite[] { new Sprite(config.tex)}));
        float[] tint = particleEmitter.getTint().getColors();
        tint[0] = config.tint.r;
        tint[1] = config.tint.g;
        tint[2] = config.tint.b;

        originalVelocityAngle = new ParticleEmitter.ScaledNumericValue();
        originalRotation = new ParticleEmitter.ScaledNumericValue();
        transferAngle(particleEmitter.getAngle(), originalVelocityAngle, 0f);
        transferAngle(particleEmitter.getRotation(), originalRotation, 0f);

        this.inheritsVelocity = inheritsVelocity;
        updateVelocity(game, baseVelocity, basePosition);

        if (config.emitter.continuous) {
            // making it continuous after setting initial velocity
            particleEmitter.setContinuous(true);
            // this is needed because making effect continuous starts it
            particleEmitter.allowCompletion();
            // ... and still initial velocity is not applied : (
        } else {
            particleEmitter.start();
        }
        boundingBox = particleEmitter.getBoundingBox();
    }

    private void multiplyValue(ParticleEmitter.ScaledNumericValue value, float multiplier) {
        value.setHigh(value.getHighMin() * multiplier, value.getHighMax() * multiplier);
        value.setLow(value.getLowMin() * multiplier, value.getLowMax() * multiplier);
    }

    private static void transferAngle(ParticleEmitter.ScaledNumericValue from, ParticleEmitter.ScaledNumericValue to, float diff) {
        if (!to.isRelative()) {
            to.setHigh(from.getHighMin() + diff, from.getHighMax() + diff);
        }
        to.setLow(from.getLowMin() + diff, from.getLowMax() + diff);
    }

    private void updateVelocity(SolGame game, Vector2 baseVelocity, Vector2 basePosition) {
        if ((isContinuous() && !isWorking()) || floatedUp) {
            return;
        } else {
            floatedUp = true;
        }
        if (!inheritsVelocity) {
            baseVelocity = Vector2.Zero;
        }
        if (!config.floatsUp) {
            setVelocity(baseVelocity);
            return;
        }
        Planet nearestPlanet = game.getPlanetManager().getNearestPlanet();
        Vector2 velocity = nearestPlanet.getAdjustedEffectVelocity(basePosition, baseVelocity);
        setVelocity(velocity);
        SolMath.free(velocity);
    }

    public void onRemove(SolGame game, Vector2 basePos) {
        PartMan partMan = game.getPartMan();
        partMan.finish(game, this, basePos);
    }

    public boolean isComplete() {
        return particleEmitter.isComplete();
    }

    public boolean isContinuous() {
        return config.emitter.continuous;
    }

    public boolean isWorking() {
        return working;
    }

    public void setWorking(boolean working) {
        setWorking(working, null);
    }

    public void setWorking(boolean working, SolShip ship) {
        if (working && workSoundSet != null) {
            game.getSoundManager().play(game, workSoundSet, position, ship);
        }
        light.update(working, relativeAngle, game);

        if (!isContinuous()) {
            throw new AssertionError("only continuous emitters can start working");
        }
        if (this.working == working) {
            return;
        }

        this.working = working;
        if (working) {
            particleEmitter.start();
        } else {
            particleEmitter.allowCompletion();
        }
    }

    private void setVelocity(Vector2 velocity) {
        ParticleEmitter.ScaledNumericValue wind = particleEmitter.getWind();
        wind.setActive(true);
        wind.setHigh(velocity.x);
        wind.setLow(velocity.x);

        ParticleEmitter.ScaledNumericValue gravity = particleEmitter.getGravity();
        gravity.setActive(true);
        gravity.setHigh(velocity.y);
        gravity.setLow(velocity.y);
    }

    /**
     * Returns the position, relative to the ship hull origin that owns the slot.
     *
     * @return The position, relative to the ship hull origin that owns the slot.
     */
    public Vector2 getPosition() {
        return position;
    }

    /**
     * Returns the trigger type set on the Particle Emitter
     *
     * @return The trigger type set on the Particle Emitter
     */
    public String getTrigger() {
        return trigger;
    }

    /**
     * Returns the angle offset set on the Particle Emitter
     *
     * @return The angle offset set on the Particle Emitter
     */
    public float getAngleOffset() {
        return angleOffset;
    }

    /**
     * Returns boolean describing whether Particle Emitter has a light
     *
     * @return boolean describing whether Particle Emitter has a light
     */
    public boolean getHasLight() {
        return hasLight;
    }

    /**
     * Returns the effect config of the Particle Emitter
     *
     * @return EffectConfig of the Particle Emitter
     */
    public EffectConfig getEffectConfig() {
        return config;
    }

    /**
     * Returns the sound list of the Particle Emitter
     *
     * @return List<String> work sounds of the Particle Emitter
     */
    public List<String> getWorkSounds() {
        return workSounds;
    }

    /**
     * Returns the list of ParticleEmitterDrawables
     *
     * @return The list of ParticleEmitterDrawables
     */
    public List<Drawable> getDrawables() {
        return drawables;
    }

    public class ParticleEmitterDrawable implements Drawable {

        public void update(SolGame game, SolObject object) {

            maybeSwitchRelativePosition(game);
            Vector2 basePos = object.getPosition();
            float baseAngle = object.getAngle();
            SolMath.toWorld(position, relativePosition, baseAngle, basePos);
            float timeStep = game.getTimeStep();

            // fix speed bug
            position.x -= particleEmitter.getWind().getLowMin() * timeStep;
            position.y -= particleEmitter.getGravity().getLowMin() * timeStep;

            particleEmitter.setPosition(position.x, position.y);
            transferAngle(originalVelocityAngle, particleEmitter.getAngle(), baseAngle + relativeAngle);
            transferAngle(originalRotation, particleEmitter.getRotation(), baseAngle + relativeAngle);

            updateVelocity(game, object.getVelocity(), object.getPosition());
            particleEmitter.update(timeStep);

            if (boundingBoxRecalcAwait > 0) {
                boundingBoxRecalcAwait -= game.getTimeStep();
            } else {
                boundingBoxRecalcAwait = MAX_BOUNDINGBOX_RECALC_AWAIT;
                particleEmitter.getBoundingBox();
            }
        }

        private void maybeSwitchRelativePosition(SolGame game) {
            if (areaSize == 0) {
                return;
            }
            float timeStep = game.getTimeStep();
            timeSinceLastPositionChange += timeStep;
            if (!working || timeSinceLastPositionChange < MAX_TIME_BETWEEN_POSITION_CHANGE) {
                return;
            }
            timeSinceLastPositionChange = 0;
            SolMath.fromAl(relativePosition, SolRandom.randomFloat(180), SolRandom.randomFloat(0, areaSize));
            relativePosition.add(originalRelativePosition);
        }

        @Override
        public void prepare(SolObject object) {
        }

        @Override
        public Vector2 getPosition() {
            return position;
        }

        @Override
        public Vector2 getRelativePosition() {
            return relativePosition;
        }

        @Override
        public float getRadius() {
            Vector3 center = new Vector3();
            center = boundingBox.getCenter(center);
            float toCenter = position.dst(center.x, center.y);
            float radius = boundingBox.getDimensions(center).len() / 2;
            return radius > 0 ? toCenter + radius : 0;
        }

        @Override
        public void draw(GameDrawer drawer, SolGame game) {
            drawer.draw(particleEmitter, config.tex, config.emitter.additive);
        }

        @Override
        public boolean isEnabled() {
            return true;
        }

        @Override
        public boolean okToRemove() {
            return isComplete();
        }

        @Override
        public DrawableLevel getLevel() {
            return drawableLevel;
        }

        @Override
        public TextureAtlas.AtlasRegion getTexture() {
            return config.tex;
        }
    }
}
