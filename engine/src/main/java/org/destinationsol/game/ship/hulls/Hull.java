/*
 * Copyright 2018 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.destinationsol.game.ship.hulls;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import org.destinationsol.common.SolMath;
import org.destinationsol.game.Faction;
import org.destinationsol.game.SolCam;
import org.destinationsol.game.SolGame;
import org.destinationsol.game.drawables.Drawable;
import org.destinationsol.game.gun.GunMount;
import org.destinationsol.game.input.Pilot;
import org.destinationsol.game.item.Engine;
import org.destinationsol.game.item.Gun;
import org.destinationsol.game.item.ItemContainer;
import org.destinationsol.game.particle.LightSrc;
import org.destinationsol.game.particle.DSParticleEmitter;
import org.destinationsol.game.planet.PlanetBind;
import org.destinationsol.game.ship.Door;
import org.destinationsol.game.ship.ForceBeacon;
import org.destinationsol.game.ship.ShipEngine;
import org.destinationsol.game.ship.SolShip;

import java.util.ArrayList;
import java.util.List;

public class Hull {

    public final HullConfig config;
    private final Body myBody;
    private final GunMount myGunMount1;
    private final GunMount myGunMount2;
    private final Fixture myBase;
    private final List<LightSrc> myLightSrcs;
    private final Vector2 myPos;
    private final Vector2 mySpd;
    private final ArrayList<ForceBeacon> myBeacons;
    private final PlanetBind myPlanetBind;
    private final float myMass;
    private final ArrayList<Door> myDoors;
    private final Fixture myShieldFixture;
    public float life;
    private float myAngle;
    private float myRotSpd;
    private ShipEngine engine;
    private List<DSParticleEmitter> particleEmitters;

    public Hull(SolGame game, HullConfig hullConfig, Body body, GunMount gunMount1, GunMount gunMount2, Fixture base,
                List<LightSrc> lightSrcs, float life, ArrayList<ForceBeacon> forceBeacons,
                ArrayList<Door> doors, Fixture shieldFixture) {
        config = hullConfig;
        myBody = body;
        myGunMount1 = gunMount1;
        myGunMount2 = gunMount2;
        myBase = base;
        myLightSrcs = lightSrcs;
        this.life = life;
        myDoors = doors;
        myShieldFixture = shieldFixture;
        myPos = new Vector2();
        mySpd = new Vector2();
        myBeacons = forceBeacons;

        myMass = myBody.getMass();
        setParamsFromBody();

        myPlanetBind = config.getType() == HullConfig.Type.STATION ? PlanetBind.tryBind(game, myPos, myAngle) : null;

        particleEmitters = new ArrayList<>();
    }

    public Body getBody() {
        return myBody;
    }

    public Fixture getBase() {
        return myBase;
    }

    public GunMount getGunMount(boolean second) {
        return second ? myGunMount2 : myGunMount1;
    }

    public Gun getGun(boolean second) {
        GunMount m = getGunMount(second);
        if (m == null) {
            return null;
        }
        return m.getGun();
    }

    public void update(SolGame game, ItemContainer container, Pilot provider, SolShip ship, SolShip nearestEnemy) {
        setParamsFromBody();
        boolean controlsEnabled = ship.isControlsEnabled() && !SolCam.DIRECT_CAM_CONTROL;

        if (engine != null) {
            engine.update(myAngle, game, provider, myBody, mySpd, ship, controlsEnabled, myMass, this);
        }

        Faction faction = ship.getPilot().getFaction();
        myGunMount1.update(container, game, myAngle, ship, controlsEnabled && provider.isShoot(), nearestEnemy, faction);
        if (myGunMount2 != null) {
            myGunMount2.update(container, game, myAngle, ship, controlsEnabled && provider.isShoot2(), nearestEnemy, faction);
        }

        for (LightSrc src : myLightSrcs) {
            src.update(true, myAngle, game);
        }

        for (ForceBeacon b : myBeacons) {
            b.update(game, myPos, myAngle, ship);
        }

        for (Door door : myDoors) {
            door.update(game, ship);
        }

        if (myPlanetBind != null) {
            Vector2 spd = SolMath.getVec();
            myPlanetBind.setDiff(spd, myPos, true);
            float fps = 1 / game.getTimeStep();
            spd.scl(fps);
            myBody.setLinearVelocity(spd);
            SolMath.free(spd);
            float angleDiff = myPlanetBind.getDesiredAngle() - myAngle;
            myBody.setAngularVelocity(angleDiff * SolMath.degRad * fps);
        }

        game.getPartMan().updateAllHullEmittersOfType(this, "none", true);
    }

    private void setParamsFromBody() {
        myPos.set(myBody.getPosition());
        myAngle = myBody.getAngle() * SolMath.radDeg;
        myRotSpd = myBody.getAngularVelocity() * SolMath.radDeg;
        mySpd.set(myBody.getLinearVelocity());
    }

    public void onRemove(SolGame game) {
        for (Door door : myDoors) {
            door.onRemove(game);
        }
        myBody.getWorld().destroyBody(myBody);
        particleEmitters.forEach(pe -> pe.onRemove(game, myPos));

    }

    public void setEngine(Engine engine) {
        this.engine = new ShipEngine(engine);
    }

    public void setParticleEmitters(SolGame game, SolShip ship) {
        List<Drawable> drawables = ship.getDrawables();
        // Remove the old particle emitters and their associated drawables
        if (!particleEmitters.isEmpty()) {
            List<Drawable> particleEmitterDrawables = new ArrayList<>();
            particleEmitters.forEach(pe -> particleEmitterDrawables.addAll(pe.getDrawables()));
            drawables.removeAll(particleEmitterDrawables);
            game.getDrawableManager().removeAll(particleEmitterDrawables);
            particleEmitters.clear();
        }
        // Add the new particle emitters and their associated drawables
        config.getParticleEmitters().forEach(pes -> particleEmitters.add(new DSParticleEmitter(game, pes, ship)));
        List<Drawable> particleEmitterDrawables = new ArrayList<>();
        particleEmitters.forEach(pe -> particleEmitterDrawables.addAll(pe.getDrawables()));
        drawables.addAll(particleEmitterDrawables);
        game.getDrawableManager().addAll(particleEmitterDrawables);
    }

    public float getAngle() {
        return myAngle;
    }

    public Vector2 getPos() {
        return myPos;
    }

    public Vector2 getSpd() {
        return mySpd;
    }

    public Engine getEngine() {
        return engine == null ? null : engine.getItem();
    }

    public float getRotSpd() {
        return myRotSpd;
    }

    public ArrayList<Door> getDoors() {
        return myDoors;
    }

    public Fixture getShieldFixture() {
        return myShieldFixture;
    }

    public float getMass() {
        return myMass;
    }

    public HullConfig getHullConfig() {
        return config;
    }

    public List<DSParticleEmitter> getParticleEmitters() {
        return particleEmitters;
    }
}
