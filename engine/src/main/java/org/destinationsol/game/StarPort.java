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
package org.destinationsol.game;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import org.destinationsol.Const;
import org.destinationsol.assets.Assets;
import org.destinationsol.common.Bound;
import org.destinationsol.common.SolColor;
import org.destinationsol.common.SolMath;
import org.destinationsol.common.SolRandom;
import org.destinationsol.game.drawables.Drawable;
import org.destinationsol.game.drawables.DrawableLevel;
import org.destinationsol.game.drawables.RectSprite;
import org.destinationsol.game.particle.DSParticleEmitter;
import org.destinationsol.game.particle.EffectConfig;
import org.destinationsol.game.particle.LightSource;
import org.destinationsol.game.planet.Planet;
import org.destinationsol.game.ship.FarShip;
import org.destinationsol.game.ship.ForceBeacon;
import org.destinationsol.game.ship.SolShip;

import java.util.ArrayList;
import java.util.List;

public class StarPort implements SolObject {
    public static final int SIZE = 8;

    private static final float DIST_FROM_PLANET = Const.PLANET_GAP * .5f;
    private static final float FARE = 10f;
    private final Body body;
    private final ArrayList<LightSource> lightSources;
    private final Vector2 position;
    private final Planet fromPlanet;
    private final Planet toPlanet;
    private final ArrayList<Drawable> drawables;
    private final boolean isSecondary;
    private float angle;

    StarPort(Planet from, Planet to, Body body, ArrayList<Drawable> drawables, boolean secondary, ArrayList<LightSource> lights) {
        this.fromPlanet = from;
        this.toPlanet = to;
        this.drawables = drawables;
        this.body = body;
        lightSources = lights;
        position = new Vector2();
        setParamsFromBody();
        isSecondary = secondary;
    }

    private static void blip(SolGame game, SolShip ship) {
        TextureAtlas.AtlasRegion texture = Assets.getAtlasRegion("engine:teleportBlip");
        float blipSize = ship.getHull().config.getApproxRadius() * 10;
        game.getPartMan().blip(game, ship.getPosition(), SolRandom.randomFloat(180), blipSize, 1, Vector2.Zero, texture);
    }

    @Bound
    public static Vector2 getDesiredPosition(Planet from, Planet to, boolean precise) {
        Vector2 fromPosition = from.getPosition();
        float angle = SolMath.angle(fromPosition, to.getPosition());
        Vector2 position = SolMath.getVec();
        SolMath.fromAl(position, angle, from.getFullHeight() + DIST_FROM_PLANET);
        position.add(fromPosition);
        return position;
    }

    private static Vector2 adjustDesiredPos(SolGame game, StarPort port, Vector2 desired) {
        Vector2 newPosition = desired;
        List<SolObject> objects = game.getObjectManager().getObjects();
        for (SolObject object : objects) {
            if (object instanceof StarPort && object != port) {
                StarPort starPort = (StarPort) object;
                // Check if the positions overlap
                Vector2 fromPosition = starPort.getPosition();
                Vector2 distanceVector = SolMath.distVec(fromPosition, desired);
                float distance = SolMath.hypotenuse(distanceVector.x, distanceVector.y);
                if (distance <= (float) StarPort.SIZE) {
                    distanceVector.scl((StarPort.SIZE + .5f) / distance);
                    newPosition = fromPosition.cpy().add(distanceVector);
                }
                SolMath.free(distanceVector);
            }
        }
        return newPosition;
    }

    @Override
    public void update(SolGame game) {
        setParamsFromBody();

        float fps = 1 / game.getTimeStep();

        Vector2 velocity = getDesiredPosition(fromPlanet, toPlanet, true);
        // Adjust position so that StarPorts are not overlapping
        velocity = adjustDesiredPos(game, this, velocity);
        velocity.sub(position).scl(fps / 4);
        body.setLinearVelocity(velocity);
        SolMath.free(velocity);
        float desiredAngle = SolMath.angle(fromPlanet.getPosition(), toPlanet.getPosition());
        body.setAngularVelocity((desiredAngle - angle) * MathUtils.degRad * fps / 4);

        SolShip ship = ForceBeacon.pullShips(game, this, position, null, null, .4f * SIZE);
        if (ship != null && ship.getMoney() >= FARE && ship.getPosition().dst(position) < .05f * SIZE) {
            ship.setMoney(ship.getMoney() - FARE);
            Transcendent transcendent = new Transcendent(ship, fromPlanet, toPlanet, game);
            if (transcendent.getShip().getPilot().isPlayer()) {
                game.saveShip();
                game.getHero().setTranscendent(transcendent);
            }
            ObjectManager objectManager = game.getObjectManager();
            objectManager.addObjDelayed(transcendent);
            blip(game, ship);
            game.getSoundManager().play(game, game.getSpecialSounds().transcendentCreated, null, transcendent);
            objectManager.removeObjDelayed(ship);
        }
        for (LightSource light : lightSources) {
            light.update(true, angle, game);
        }

    }

    public boolean isSecondary() {
        return isSecondary;
    }

    @Override
    public boolean shouldBeRemoved(SolGame game) {
        return false;
    }

    @Override
    public void onRemove(SolGame game) {
        body.getWorld().destroyBody(body);

    }

    @Override
    public void receiveDmg(float dmg, SolGame game, Vector2 position, DmgType dmgType) {
        game.getSpecialSounds().playHit(game, this, position, dmgType);
    }

    @Override
    public boolean receivesGravity() {
        return false;
    }

    @Override
    public void receiveForce(Vector2 force, SolGame game, boolean acc) {

    }

    @Override
    public Vector2 getPosition() {
        return position;
    }

    @Override
    public FarObject toFarObject() {
        return new FarStarPort(fromPlanet, toPlanet, position, isSecondary);
    }

    @Override
    public List<Drawable> getDrawables() {
        return drawables;
    }

    @Override
    public float getAngle() {
        return angle;
    }

    @Override
    public Vector2 getVelocity() {
        return null;
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
        return true;
    }

    @Override
    public boolean hasBody() {
        return true;
    }

    private void setParamsFromBody() {
        position.set(body.getPosition());
        angle = body.getAngle() * MathUtils.radDeg;
    }

    public Planet getFromPlanet() {
        return fromPlanet;
    }

    public Planet getToPlanet() {
        return toPlanet;
    }

    public static class Builder {
        public static final float FLOW_DIST = .26f * SIZE;
        private final CollisionMeshLoader myLoader;

        Builder() {
            myLoader = new CollisionMeshLoader("engine:miscCollisionMeshes");
        }

        public StarPort build(SolGame game, Planet from, Planet to, boolean secondary) {
            float angle = SolMath.angle(from.getPosition(), to.getPosition());
            Vector2 position = getDesiredPosition(from, to, false);
            // Adjust position so that StarPorts are not overlapping
            position = adjustDesiredPos(game, null, position);
            ArrayList<Drawable> drawables = new ArrayList<>();
            Body body = myLoader.getBodyAndSprite(game, Assets.getAtlasRegion("engine:starPort"), SIZE,
                    BodyDef.BodyType.KinematicBody, new Vector2(position), angle, drawables, 10f, DrawableLevel.BIG_BODIES);
            SolMath.free(position);
            ArrayList<LightSource> lights = new ArrayList<>();
            addFlow(game, position, drawables, 0, lights);
            addFlow(game, position, drawables, 90, lights);
            addFlow(game, position, drawables, -90, lights);
            addFlow(game, position, drawables, 180, lights);
            DSParticleEmitter force = game.getSpecialEffects().buildForceBeacon(FLOW_DIST * 1.5f, game, new Vector2(), position, Vector2.Zero);
            force.setWorking(true);
            drawables.addAll(force.getDrawables());
            StarPort sp = new StarPort(from, to, body, drawables, secondary, lights);
            body.setUserData(sp);
            return sp;
        }

        private void addFlow(SolGame game, Vector2 position, ArrayList<Drawable> drawables, float angle, ArrayList<LightSource> lights) {
            EffectConfig flow = game.getSpecialEffects().starPortFlow;
            Vector2 relPos = new Vector2();
            SolMath.fromAl(relPos, angle, -FLOW_DIST);
            DSParticleEmitter f1 = new DSParticleEmitter(flow, FLOW_DIST, DrawableLevel.PART_BG_0, relPos, false, game, position, Vector2.Zero, angle);
            f1.setWorking(true);
            drawables.addAll(f1.getDrawables());
            LightSource light = new LightSource(.6f, true, 1, relPos, flow.tint);
            light.collectDrawables(drawables);
            lights.add(light);
        }
    }

    public static class FarStarPort implements FarObject {
        private final Planet fromPlanet;
        private final Planet toPlanet;
        private final Vector2 position;
        private final boolean isSecondary;
        private float angle;

        FarStarPort(Planet from, Planet to, Vector2 position, boolean secondary) {
            fromPlanet = from;
            toPlanet = to;
            this.position = new Vector2(position);
            isSecondary = secondary;
        }

        @Override
        public boolean shouldBeRemoved(SolGame game) {
            return false;
        }

        @Override
        public SolObject toObject(SolGame game) {
            return game.getStarPortBuilder().build(game, fromPlanet, toPlanet, isSecondary);
        }

        @Override
        public void update(SolGame game) {

            Vector2 desiredPosition = getDesiredPosition(fromPlanet, toPlanet, false);
            position.set(desiredPosition);
            SolMath.free(desiredPosition);
            angle = SolMath.angle(fromPlanet.getPosition(), toPlanet.getPosition());
        }

        @Override
        public float getRadius() {
            return SIZE / 2;
        }

        @Override
        public Vector2 getPosition() {
            return position;
        }

        @Override
        public String toDebugString() {
            return null;
        }

        @Override
        public boolean hasBody() {
            return true;
        }

        public Planet getFrom() {
            return fromPlanet;
        }

        public Planet getTo() {
            return toPlanet;
        }

        public float getAngle() {
            return angle;
        }

        public boolean isSecondary() {
            return isSecondary;
        }
    }

    /**
     * The state ship is in when travelling through the StarPort
     */
    public static class Transcendent implements SolObject {
        private static final float TRAN_SZ = 1f;
        private final Planet fromPlanet;
        private final Planet toPlanet;
        private final Vector2 position;
        private final Vector2 destinationPosition;
        private final ArrayList<Drawable> drawables;
        private final FarShip ship;
        private final Vector2 velocity;
        private final LightSource lightSource;
        private final DSParticleEmitter effect;
        private float angle;

        public Transcendent(SolShip ship, Planet from, Planet to, SolGame game) {
            this.ship = ship.toFarObject();
            fromPlanet = from;
            toPlanet = to;
            position = new Vector2(ship.getPosition());
            velocity = new Vector2();
            destinationPosition = new Vector2();

            RectSprite s = new RectSprite(Assets.getAtlasRegion("engine:transcendent"), TRAN_SZ, .3f,
                    0, new Vector2(), DrawableLevel.PROJECTILES, 0, 0, SolColor.WHITE, false);

            drawables = new ArrayList<>();
            drawables.add(s);
            EffectConfig eff = game.getSpecialEffects().transcendentWork;
            effect = new DSParticleEmitter(eff, TRAN_SZ, DrawableLevel.PART_BG_0, new Vector2(), true, game, position, Vector2.Zero, 0);
            effect.setWorking(true);
            drawables.addAll(effect.getDrawables());
            lightSource = new LightSource(.6f * TRAN_SZ, true, .5f, new Vector2(), eff.tint);
            lightSource.collectDrawables(drawables);
            setDependentParams();
        }

        public FarShip getShip() {
            return ship;
        }

        @Override
        public void update(SolGame game) {
            setDependentParams();

            float timeStep = game.getTimeStep();
            Vector2 moveDiff = SolMath.getVec(velocity);
            moveDiff.scl(timeStep);
            position.add(moveDiff);
            SolMath.free(moveDiff);

            if (position.dst(destinationPosition) < .5f) {
                ObjectManager objectManager = game.getObjectManager();
                objectManager.removeObjDelayed(this);
                ship.setPos(position);
                ship.setVelocity(new Vector2());
                SolShip ship = this.ship.toObject(game);
                if (ship.getPilot().isPlayer()) {
                    game.getHero().setSolShip(ship, game);
                    game.saveShip();
                }
                objectManager.addObjDelayed(ship);
                blip(game, ship);
                game.getSoundManager().play(game, game.getSpecialSounds().transcendentFinished, null, this);
                game.getObjectManager().resetDelays(); // because of the hacked speed
            } else {
                game.getSoundManager().play(game, game.getSpecialSounds().transcendentMove, null, this);
                lightSource.update(true, angle, game);
            }
        }

        private void setDependentParams() {
            Vector2 toPosition = toPlanet.getPosition();
            float nodeAngle = SolMath.angle(toPosition, fromPlanet.getPosition());
            SolMath.fromAl(destinationPosition, nodeAngle, toPlanet.getFullHeight() + DIST_FROM_PLANET + SIZE / 2);
            destinationPosition.add(toPosition);
            angle = SolMath.angle(position, destinationPosition);
            SolMath.fromAl(velocity, angle, Const.MAX_MOVE_SPD * 2); //hack again : (
        }

        @Override
        public boolean shouldBeRemoved(SolGame game) {
            return false;
        }

        @Override
        public void onRemove(SolGame game) {
            game.getPartMan().finish(game, effect, position);
        }

        @Override
        public void receiveDmg(float dmg, SolGame game, Vector2 position, DmgType dmgType) {
            game.getSpecialSounds().playHit(game, this, position, dmgType);
        }

        @Override
        public boolean receivesGravity() {
            return false;
        }

        @Override
        public void receiveForce(Vector2 force, SolGame game, boolean acc) {
        }

        @Override
        public Vector2 getPosition() {
            return position;
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
            return angle;
        }

        @Override
        public Vector2 getVelocity() {
            return velocity;
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
            return false;
        }
    }
}
