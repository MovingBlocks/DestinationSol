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

package org.destinationsol.game.ship.hulls;

import com.badlogic.gdx.math.MathUtils;
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
import org.destinationsol.game.particle.DSParticleEmitter;
import org.destinationsol.game.particle.LightSource;
import org.destinationsol.game.planet.PlanetBind;
import org.destinationsol.game.ship.Door;
import org.destinationsol.game.ship.ForceBeacon;
import org.destinationsol.game.ship.ShipEngine;
import org.destinationsol.game.ship.SolShip;

import java.util.ArrayList;
import java.util.List;

public class Hull {

    public final HullConfig config;
    private final Body body;
    private final GunMount gunMount1;
    private final GunMount gunMount2;
    private final Fixture base;
    private final List<LightSource> lightSources;
    private final Vector2 position;
    private final Vector2 velocity;
    private final ArrayList<ForceBeacon> beacons;
    private final PlanetBind planetBind;
    private final float mass;
    private final ArrayList<Door> doors;
    private final Fixture shieldFixture;
    public float life;
    private float angle;
    private float rotationSpeed;
    private ShipEngine engine;
    private List<DSParticleEmitter> particleEmitters;

    public Hull(SolGame game, HullConfig hullConfig, Body body, GunMount gunMount1, GunMount gunMount2, Fixture base,
                List<LightSource> lightSources, float life, ArrayList<ForceBeacon> forceBeacons,
                ArrayList<Door> doors, Fixture shieldFixture) {
        config = hullConfig;
        this.body = body;
        this.gunMount1 = gunMount1;
        this.gunMount2 = gunMount2;
        this.base = base;
        this.lightSources = lightSources;
        this.life = life;
        this.doors = doors;
        this.shieldFixture = shieldFixture;
        position = new Vector2();
        velocity = new Vector2();
        beacons = forceBeacons;

        mass = this.body.getMass();
        setParamsFromBody();

        planetBind = config.getType() == HullConfig.Type.STATION ? PlanetBind.tryBind(game, position, angle) : null;

        particleEmitters = new ArrayList<>();
    }

    public Body getBody() {
        return body;
    }

    public Fixture getBase() {
        return base;
    }

    public GunMount getGunMount(boolean second) {
        return second ? gunMount2 : gunMount1;
    }

    public Gun getGun(boolean second) {
        GunMount mount = getGunMount(second);
        if (mount == null) {
            return null;
        }
        return mount.getGun();
    }

    public void update(SolGame game, ItemContainer container, Pilot provider, SolShip ship, SolShip nearestEnemy) {
        setParamsFromBody();
        boolean controlsEnabled = ship.isControlsEnabled() && !SolCam.DIRECT_CAM_CONTROL;

        if (engine != null) {
            engine.update(angle, game, provider, body, velocity, controlsEnabled, mass, ship);
        }

        Faction faction = ship.getPilot().getFaction();
        gunMount1.update(container, game, angle, ship, controlsEnabled && provider.isShoot(), nearestEnemy, faction);
        if (gunMount2 != null) {
            gunMount2.update(container, game, angle, ship, controlsEnabled && provider.isShoot2(), nearestEnemy, faction);
        }

        for (LightSource src : lightSources) {
            src.update(true, angle, game);
        }

        for (ForceBeacon b : beacons) {
            b.update(game, position, angle, ship);
        }

        for (Door door : doors) {
            door.update(game, ship);
        }

        if (planetBind != null) {
            Vector2 velocity = SolMath.getVec();
            planetBind.setDiff(velocity, position, true);
            float fps = 1 / game.getTimeStep();
            velocity.scl(fps);
            body.setLinearVelocity(velocity);
            SolMath.free(velocity);
            float angleDiff = planetBind.getDesiredAngle() - angle;
            body.setAngularVelocity(angleDiff * MathUtils.degRad * fps);
        }

        game.getPartMan().updateAllHullEmittersOfType(ship, "none", true);
    }

    private void setParamsFromBody() {
        position.set(body.getPosition());
        angle = body.getAngle() * MathUtils.radDeg;
        rotationSpeed = body.getAngularVelocity() * MathUtils.radDeg;
        velocity.set(body.getLinearVelocity());
    }

    public void onRemove(SolGame game) {
        for (Door door : doors) {
            door.onRemove(game);
        }
        body.getWorld().destroyBody(body);
        particleEmitters.forEach(pe -> pe.onRemove(game, position));

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
        return angle;
    }

    public Vector2 getPosition() {
        return position;
    }

    public Vector2 getVelocity() {
        return velocity;
    }

    public Engine getEngine() {
        return engine == null ? null : engine.getItem();
    }

    public float getRotationSpeed() {
        return rotationSpeed;
    }

    public ArrayList<Door> getDoors() {
        return doors;
    }

    public Fixture getShieldFixture() {
        return shieldFixture;
    }

    public float getMass() {
        return mass;
    }

    public HullConfig getHullConfig() {
        return config;
    }

    public List<DSParticleEmitter> getParticleEmitters() {
        return particleEmitters;
    }
}
