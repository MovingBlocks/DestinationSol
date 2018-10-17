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
package org.destinationsol.game.planet;

import com.badlogic.gdx.math.Vector2;
import org.destinationsol.Const;
import org.destinationsol.common.Bound;
import org.destinationsol.common.SolMath;
import org.destinationsol.common.SolRandom;
import org.destinationsol.game.HardnessCalc;
import org.destinationsol.game.SolGame;

import java.util.ArrayList;
import java.util.List;

public class Planet {
    private final SolSystem system;
    private final Vector2 position;
    private final float distance;
    private final float rotationSpeedInSystem;
    private final float rotationSpeed;
    private final float groundHeight;
    private final PlanetConfig config;
    private final String name;
    private final float gravitationConstant;
    private final List<Vector2> landingPlaces;
    private final float groundDamagePerSecond;
    private final float atmosphereDamagePerSecond;
    private boolean areObjectsCreated;

    private float angleInSystem;
    private float angle;
    private float minGroundHeight;
    private Vector2 velocity;

    public Planet(SolSystem sys, float angleToSys, float dist, float angle, float toSysRotationSpeed, float rotationSpeed,
                  float groundHeight, boolean objsCreated, PlanetConfig config, String name) {
        system = sys;
        angleInSystem = angleToSys;
        distance = dist;
        this.angle = angle;
        rotationSpeedInSystem = toSysRotationSpeed;
        this.rotationSpeed = rotationSpeed;
        this.groundHeight = groundHeight;
        this.config = config;
        this.name = name;
        minGroundHeight = this.groundHeight;
        areObjectsCreated = objsCreated;
        position = new Vector2();
        velocity = new Vector2();
        float grav = SolRandom.randomFloat(config.minGrav, config.maxGrav);
        gravitationConstant = grav * this.groundHeight * this.groundHeight;
        groundDamagePerSecond = HardnessCalc.getGroundDps(config, grav);
        atmosphereDamagePerSecond = HardnessCalc.getAtmDps(config);
        landingPlaces = new ArrayList<>();
        setSecondaryParams();
    }

    public void update(SolGame game, float timeStep) {
        angleInSystem += rotationSpeedInSystem * timeStep;
        angle += rotationSpeed * timeStep;

        setSecondaryParams();
        Vector2 camPos = game.getCam().getPosition();
        if (!areObjectsCreated && camPos.dst(position) < getGroundHeight() + Const.MAX_SKY_HEIGHT_FROM_GROUND) {
            minGroundHeight = new PlanetObjectsBuilder().createPlanetObjs(game, this);
            fillLangingPlaces(game);
            areObjectsCreated = true;
        }
    }

    private void setSecondaryParams() {
        SolMath.fromAl(position, angleInSystem, distance);
        position.add(system.getPosition());
        float speed = SolMath.angleToArc(rotationSpeedInSystem, distance);
        float velocityAngle = angleInSystem + 90;
        SolMath.fromAl(velocity, velocityAngle, speed);
    }

    private void fillLangingPlaces(SolGame game) {
        for (int i = 0; i < 10; i++) {
            Vector2 landingPlace = game.getPlanetManager().findFlatPlace(game, this, null, 0);
            landingPlaces.add(landingPlace);
        }
    }

    public float getAngle() {
        return angle;
    }

    public Vector2 getPosition() {
        return position;
    }

    public float getFullHeight() {
        return groundHeight + Const.ATM_HEIGHT;
    }

    public float getGroundHeight() {
        return groundHeight;
    }

    public SolSystem getSystem() {
        return system;
    }

    @Bound
    public Vector2 getAdjustedEffectVelocity(Vector2 position, Vector2 velocity) {
        Vector2 r = SolMath.getVec(velocity);
        if (config.skyConfig == null) {
            return r;
        }
        Vector2 up = SolMath.distVec(this.position, position);
        float dst = up.len();
        if (dst == 0 || getFullHeight() < dst) {
            SolMath.free(up);
            return r;
        }
        float smokeConst = 1.2f * gravitationConstant;
        if (dst < groundHeight) {
            up.scl(smokeConst / dst / groundHeight / groundHeight);
            r.set(up);
            SolMath.free(up);
            return r;
        }
        float speedPercentage = (dst - groundHeight) / Const.ATM_HEIGHT;
        r.scl(speedPercentage);
        up.scl(smokeConst / dst / dst / dst);
        r.add(up);
        SolMath.free(up);
        return r;
    }

    public float getGravitationConstant() {
        return gravitationConstant;
    }

    public float getDistance() {
        return distance;
    }

    public float getAngleInSystem() {
        return angleInSystem;
    }

    public float getRotationSpeed() {
        return rotationSpeed;
    }

    public boolean areObjectsCreated() {
        return areObjectsCreated;
    }

    public List<Vector2> getLandingPlaces() {
        return landingPlaces;
    }

    public float getMinGroundHeight() {
        return minGroundHeight;
    }

    public boolean isNearGround(Vector2 position) {
        return this.position.dst(position) - groundHeight < .25f * Const.ATM_HEIGHT;
    }

    public PlanetConfig getConfig() {
        return config;
    }

    public float getRotationSpeedInSystem() {
        return rotationSpeedInSystem;
    }

    public String getName() {
        return name;
    }

    public void calculateVelocityAtPosition(Vector2 velocity, Vector2 position) {
        Vector2 toPos = SolMath.distVec(this.position, position);
        float fromPlanetAngle = SolMath.angle(toPos);
        float hSpeed = SolMath.angleToArc(rotationSpeed, toPos.len());
        SolMath.free(toPos);
        SolMath.fromAl(velocity, fromPlanetAngle + 90, hSpeed);
        velocity.add(this.velocity);
    }

    public float getAtmosphereDamagePerSecond() {
        return atmosphereDamagePerSecond;
    }

    public float getGroundDamagePerSecond() {
        return groundDamagePerSecond;
    }
}
