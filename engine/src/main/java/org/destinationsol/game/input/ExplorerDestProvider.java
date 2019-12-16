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

package org.destinationsol.game.input;

import com.badlogic.gdx.math.Vector2;
import org.destinationsol.Const;
import org.destinationsol.common.SolMath;
import org.destinationsol.common.SolRandom;
import org.destinationsol.game.SolGame;
import org.destinationsol.game.planet.Planet;
import org.destinationsol.game.planet.SolSystem;
import org.destinationsol.game.ship.SolShip;
import org.destinationsol.game.ship.hulls.HullConfig;

import java.util.ArrayList;
import java.util.List;

/**
 * Flies from planet to planet, stays on the planet ground or in atmosphere for some time, then flies to the next planet
 */
public class ExplorerDestProvider implements MoveDestProvider {
    public static final int MAX_AWAIT_ON_PLANET = 30;
    public static final int LAST_PLANETS_TO_AVOID = 2;
    private final Vector2 destination;
    private final boolean isAggressive;
    private final float desiredSpeed;
    private final SolSystem system;
    private Vector2 relativeDestination;
    private Planet planet;
    private float awaitOnPlanet;
    private boolean landInDestination;
    private Vector2 destinationVelocity;

    public ExplorerDestProvider(Vector2 position, boolean aggressive, HullConfig config, SolSystem system) {
        this.system = system;
        destination = new Vector2();
        float minDistance = Float.MAX_VALUE;
        ArrayList<Planet> planets = this.system.getPlanets();
        for (int i = 0, sz = allowedSize(); i < sz; i++) {
            Planet planet = planets.get(i);
            float distance = planet.getPosition().dst(position);
            if (distance < minDistance) {
                minDistance = distance;
                this.planet = planet;
            }
        }
        calculateRelativeDestination(config);
        awaitOnPlanet = MAX_AWAIT_ON_PLANET;
        isAggressive = aggressive;
        desiredSpeed = config.getType() == HullConfig.Type.BIG ? Const.BIG_AI_SPD : Const.DEFAULT_AI_SPD;
        destinationVelocity = new Vector2();
    }

    private int allowedSize() {
        int size = system.getPlanets().size();
        if (!system.getConfig().hard) {
            size -= LAST_PLANETS_TO_AVOID;
        }
        return size;
    }

    private void calculateRelativeDestination(HullConfig hullConfig) {
        List<Vector2> landingPlaces = planet.getLandingPlaces();
        if (landingPlaces.size() > 0) {
            relativeDestination = new Vector2(SolRandom.randomElement(landingPlaces));
            float distance = relativeDestination.len();
            float aboveGround = hullConfig.getType() == HullConfig.Type.BIG ? Const.ATM_HEIGHT * .75f : .75f * hullConfig.getSize();
            relativeDestination.scl((distance + aboveGround) / distance);
            landInDestination = true;
        } else {
            relativeDestination = new Vector2();
            SolMath.fromAl(relativeDestination, SolRandom.randomFloat(180), planet.getGroundHeight() + .3f * Const.ATM_HEIGHT);
            landInDestination = false;
        }
    }

    @Override
    public Vector2 getDestination() {
        return destination;
    }

    @Override
    public boolean shouldStopNearDestination() {
        return true;
    }

    @Override
    public void update(SolGame game, Vector2 shipPos, float maxIdleDist, HullConfig hullConfig, SolShip nearestEnemy) {
        if (destination.dst(shipPos) < maxIdleDist) {
            if (awaitOnPlanet > 0) {
                awaitOnPlanet -= game.getTimeStep();
            } else {
                ArrayList<Planet> planets = system.getPlanets();
                int planetIndex = SolRandom.randomInt(allowedSize());
                planet = planets.get(planetIndex);
                calculateRelativeDestination(hullConfig);
                awaitOnPlanet = MAX_AWAIT_ON_PLANET;
            }
        }

        if (!landInDestination && !planet.getLandingPlaces().isEmpty()) {
            calculateRelativeDestination(hullConfig);
        }

        SolMath.toWorld(destination, relativeDestination, planet.getAngle(), planet.getPosition());
        planet.calculateVelocityAtPosition(destinationVelocity, destination);
    }

    @Override
    public Boolean shouldManeuver(boolean canShoot, SolShip nearestEnemy, boolean nearGround) {
        if (isAggressive && canShoot) {
            return true;
        }
        return null;
    }

    @Override
    public Vector2 getDestinationVelocity() {
        return destinationVelocity;
    }

    @Override
    public boolean shouldAvoidBigObjects() {
        return true;
    }

    @Override
    public float getDesiredSpeed() {
        return desiredSpeed;
    }
}
