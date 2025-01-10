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
package org.destinationsol.game.projectile;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import org.destinationsol.common.SolColor;
import org.destinationsol.common.SolMath;
import org.destinationsol.common.SolRandom;
import org.destinationsol.game.DmgType;
import org.destinationsol.game.FactionManager;
import org.destinationsol.game.FarObject;
import org.destinationsol.game.GameDrawer;
import org.destinationsol.game.SolGame;
import org.destinationsol.game.SolObject;
import org.destinationsol.game.SolObjectEntityWrapper;
import org.destinationsol.game.drawables.Drawable;
import org.destinationsol.game.drawables.DrawableLevel;
import org.destinationsol.game.drawables.SpriteManager;
import org.destinationsol.game.faction.DefaultReputationEvent;
import org.destinationsol.game.faction.Faction;
import org.destinationsol.game.item.Shield;
import org.destinationsol.game.particle.DSParticleEmitter;
import org.destinationsol.game.particle.EffectConfig;
import org.destinationsol.game.particle.LightSource;
import org.destinationsol.game.ship.SolShip;
import org.destinationsol.health.events.DamageEvent;
import org.destinationsol.location.components.Position;
import org.terasology.gestalt.entitysystem.entity.EntityIterator;
import org.terasology.gestalt.entitysystem.entity.EntityRef;

import java.util.ArrayList;
import java.util.List;

public class Projectile implements SolObject {

    private static final float MIN_ANGLE_TO_GUIDE = 2f;
    private final ArrayList<Drawable> drawables;
    private final ProjectileBody body;
    private final Faction faction;
    private final DSParticleEmitter bodyEffect;
    private final DSParticleEmitter trailEffect;
    private final LightSource lightSource;
    private final ProjectileConfig config;

    private boolean shouldBeRemoved;
    private SolObject obstacle;
    private SolShip ship;
    private boolean wasDamageDealt;

    public Projectile(SolGame game, float angle, Vector2 muzzlePos, Vector2 gunVelocity, Faction faction,
                      ProjectileConfig config, boolean varySpeed, SolShip ship) {
        drawables = new ArrayList<>();
        this.config = config;

        this.ship = ship;

        Drawable drawable;
        if (config.stretch) {
            drawable = new ProjectileDrawable(this, config.sprite.frames, config.texSz);
        } else {
            drawable = SpriteManager.createSprite(config.sprite.displayName, config.texSz, config.origin.x, config.origin.y, new Vector2(), DrawableLevel.PROJECTILES, 0, 0, SolColor.WHITE, false);
        }
        drawables.add(drawable);
        float speed = config.speed;
        if (varySpeed) {
            speed *= SolRandom.randomFloat(.9f, 1.1f);
        }
        if (config.physSize > 0) {
            body = new BallProjectileBody(game, muzzlePos, angle, this, gunVelocity, speed, config);
        } else {
            body = new PointProjectileBody(angle, muzzlePos, gunVelocity, speed, this, game, config.acc);
        }
        this.faction = faction;
        bodyEffect = buildEffect(game, config.bodyEffect, DrawableLevel.PART_BG_0, null, true);
        trailEffect = buildEffect(game, config.trailEffect, DrawableLevel.PART_BG_0, null, false);
        if (config.lightSz > 0) {
            Color col = SolColor.WHITE;
            if (bodyEffect != null) {
                col = config.bodyEffect.tint;
            }
            lightSource = new LightSource(config.lightSz, true, 1f, new Vector2(), col);
            lightSource.collectDrawables(drawables);
        } else {
            lightSource = null;
        }
    }

    private DSParticleEmitter buildEffect(SolGame game, EffectConfig ec, DrawableLevel drawableLevel, Vector2 position, boolean inheritsVelocity) {
        if (ec == null) {
            return null;
        }
        DSParticleEmitter res = new DSParticleEmitter(ec, -1, drawableLevel, new Vector2(), inheritsVelocity, game, position, body.getVelocity(), 0);
        if (res.isContinuous()) {
            res.setWorking(true);
            drawables.addAll(res.getDrawables());
        } else {
            game.getPartMan().finish(game, res, position);
        }
        return res;
    }

    @Override
    public void update(SolGame game) {
        body.update(game);

        if (obstacle != null) {

            //This handles collision of a Projectile with an entity.
            if (obstacle instanceof SolObjectEntityWrapper) {
                EntityRef entity = ((SolObjectEntityWrapper) obstacle).getEntity();

                if (!wasDamageDealt) {
                    if (config.aoeRadius >= 0) { //This checks if the projectile does Area-Of-Effect damage. If it does not, the value is usually -1
                        EntityIterator iterator = game.getEntitySystemManager().getEntityManager().iterate(new Position());
                        while (iterator.next()) {
                            Vector2 entityPosition = iterator.getEntity().getComponent(Position.class).get().position;
                            if (getPosition().dst2(entityPosition) <= config.aoeRadius) {
                                game.getEntitySystemManager().sendEvent(new DamageEvent(config.dmg), entity);
                            }
                        }

                    } else {
                        game.getEntitySystemManager().sendEvent(new DamageEvent(config.dmg), entity);
                    }
                }

                if (config.density > 0) {
                    obstacle = null;
                    wasDamageDealt = true;
                } else {
                    collided(game);

                    //TODO Once SolShip is an entity, this should be refactored to work with it
//                    if (config.emTime > 0 && obstacle instanceof SolShip) {
//                        ((SolShip) obstacle).disableControls(config.emTime, game);
//                    }

                    return;
                }

            } else {
                //TODO this else block is legacy code for handling contact between a SolObject and an entity. Once every
                // SolObject has been converted to an entity, this can be removed.
                if (!wasDamageDealt) {
                    if (config.aoeRadius >= 0) { //If AoE is enabled for this Projectile, damage all within the radius.
                        game.getObjectManager().doToAllCloserThan(config.aoeRadius, this, (SolObject obj) ->
                                obj.receiveDmg(config.dmg, game, body.getPosition(), config.dmgType)
                        );
                    } else {
                        obstacle.receiveDmg(config.dmg, game, body.getPosition(), config.dmgType);
                    }
                }
                if (config.density > 0) {
                    obstacle = null;
                    wasDamageDealt = true;
                } else {
                    collided(game);
                    if (config.emTime > 0 && obstacle instanceof SolShip) {
                        ((SolShip) obstacle).disableControls(config.emTime, game);
                    }
                    return;
                }
            }
        }
        if (lightSource != null) {
            lightSource.update(true, body.getAngle(), game);
        }
        maybeGuide(game);
        game.getSoundManager().play(game, config.workSound, null, this);
    }

    private void maybeGuide(SolGame game) {
        if (config.guideRotationSpeed == 0) {
            return;
        }
        float ts = game.getTimeStep();
        SolShip ne = game.getFactionMan().getNearestEnemy(game, this);
        if (ne == null) {
            return;
        }
        float desiredAngle = body.getDesiredAngle(ne);
        float angle = getAngle();
        float diffAngle = SolMath.norm(desiredAngle - angle);
        if (SolMath.abs(diffAngle) < MIN_ANGLE_TO_GUIDE) {
            return;
        }
        float rot = ts * config.guideRotationSpeed;
        float min = -rot;
        diffAngle = MathUtils.clamp(diffAngle, min, rot);
        body.changeAngle(diffAngle);
    }

    private void collided(SolGame game) {
        shouldBeRemoved = true;
        Vector2 position = body.getPosition();
        buildEffect(game, config.collisionEffect, DrawableLevel.PART_FG_1, position, false);
        buildEffect(game, config.collisionEffectBackground, DrawableLevel.PART_FG_0, position, false);
        if (config.collisionEffectBackground != null) {
            game.getPartMan().blinks(position, game, config.collisionEffectBackground.size);
        }
        if (ship.getPilot().isPlayer() && obstacle instanceof SolShip) {
            game.getFactionMan().reportEvent(ship.getFaction(), ((SolShip) obstacle).getFaction(), DefaultReputationEvent.DAMAGED_SHIP);
        }

        game.getSoundManager().play(game, config.collisionSound, null, this);
    }

    @Override
    public boolean shouldBeRemoved(SolGame game) {
        return shouldBeRemoved;
    }

    @Override
    public void onRemove(SolGame game) {
        Vector2 position = body.getPosition();
        if (bodyEffect != null) {
            game.getPartMan().finish(game, bodyEffect, position);
        }
        if (trailEffect != null) {
            game.getPartMan().finish(game, trailEffect, position);
        }
        body.onRemove(game);
    }

    @Override
    public void receiveDmg(float dmg, SolGame game, Vector2 position, DmgType dmgType) {
        if (config.density > 0) {
            return;
        }
        collided(game);
    }

    @Override
    public boolean receivesGravity() {
        return !config.massless;
    }

    @Override
    public void receiveForce(Vector2 force, SolGame game, boolean acc) {
        body.receiveForce(force, game, acc);
    }

    @Override
    public Vector2 getPosition() {
        return body.getPosition();
    }

    @Override
    public FarObject toFarObject() {
        return null;
    }

    @Override
    public List<Drawable> getDrawables() {
        return drawables;
    }

    @Override
    public float getAngle() {
        return body.getAngle();
    }

    @Override
    public Vector2 getVelocity() {
        return body.getVelocity();
    }

    @Override
    public void handleContact(SolObject other, float absImpulse,
                              SolGame game, Vector2 collPos) {
    }

    @Override
    public String toDebugString() {
        return null;
    }

    @Override
    public Boolean isMetal() {
        return null;
    }

    @Override
    public boolean hasBody() {
        return true;
    }

    public Faction getFaction() {
        return faction;
    }

    public boolean shouldCollide(SolObject object, Fixture fixture, FactionManager factionManager) {
        if (object instanceof SolShip) {
            SolShip ship = (SolShip) object;
            if (this.ship == ship) {
                return false;
            }
            if (ship.getHull().getShieldFixture() == fixture) {
                if (config.density > 0) {
                    return false;
                }
                Shield shield = ship.getShield();
                return shield != null && shield.canAbsorb(config.dmgType);
            }
            return true;
        } else if (object instanceof Projectile) {
            // Projectiles from the same sources are allowed to overlap
            Projectile projectile = (Projectile) object;
            return projectile.ship != this.ship;
        }

        return true;
    }

    public void setObstacle(SolObject object, SolGame game) {
        if (!shouldCollide(object, null, game.getFactionMan())) {
            return; // happens for some reason when projectile is just created
        }
        obstacle = object;
    }

    public boolean isMassless() {
        return config.massless;
    }

    public ProjectileConfig getConfig() {
        return config;
    }

    private static class ProjectileDrawable implements Drawable {
        private final Projectile projectile;
        private final Animation<TextureAtlas.AtlasRegion> animation;
        private final float width;
        private TextureAtlas.AtlasRegion texture;
        private float animationTime;

        ProjectileDrawable(Projectile projectile, Animation<TextureAtlas.AtlasRegion> animation, float width) {
            this.projectile = projectile;
            this.animation = animation;
            texture = animation.getKeyFrame(animationTime, true);
            this.width = width;
        }

        @Override
        public TextureAtlas.AtlasRegion getTexture() {
            return texture;
        }

        @Override
        public DrawableLevel getLevel() {
            return DrawableLevel.PROJECTILES;
        }

        @Override
        public void update(SolGame game, SolObject o) {
            animationTime += game.getTimeStep();
            texture = animation.getKeyFrame(animationTime, true);
        }

        @Override
        public void prepare(SolObject o) {
        }

        @Override
        public Vector2 getPosition() {
            return projectile.getPosition();
        }

        @Override
        public Vector2 getRelativePosition() {
            return Vector2.Zero;
        }

        @Override
        public float getRadius() {
            return projectile.config.texSz / 2;
        }

        @Override
        public void draw(GameDrawer drawer, SolGame game) {
            float h = width;
            float minH = game.getCam().getRealLineWidth() * 3;
            if (h < minH) {
                h = minH;
            }
            Vector2 position = projectile.getPosition();
            float w = projectile.getVelocity().len() * game.getTimeStep();
            if (w < 4 * h) {
                w = 4 * h;
            }
            drawer.draw(texture, w, h, w, h / 2, position.x, position.y, SolMath.angle(projectile.getVelocity()), SolColor.LG);
        }

        @Override
        public boolean isEnabled() {
            return true;
        }

        @Override
        public boolean okToRemove() {
            return false;
        }

    }

}
