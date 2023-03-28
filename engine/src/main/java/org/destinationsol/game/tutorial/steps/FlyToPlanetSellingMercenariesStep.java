/*
 * Copyright 2023 The Terasology Foundation
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

package org.destinationsol.game.tutorial.steps;

import com.badlogic.gdx.math.Vector2;
import org.destinationsol.game.SolObject;
import org.destinationsol.game.planet.Planet;
import org.destinationsol.game.ship.FarShip;
import org.destinationsol.game.ship.SolShip;
import org.destinationsol.game.ship.hulls.HullConfig;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

/**
 * A tutorial step that completes when the player reaches a planet-based store selling mercenaries.
 * It guides the player towards the planet first with a waypoint, then towards the shop itself.
 */
public class FlyToPlanetSellingMercenariesStep extends FlyToPlanetStep {
    private final String onPlanetMessage;

    @Inject
    protected FlyToPlanetSellingMercenariesStep() {
        throw new RuntimeException("Attempted to instantiate TutorialStep via DI. This is not supported.");
    }

    public FlyToPlanetSellingMercenariesStep(String message, String onPlanetMessage) {
        super(null, message);
        this.onPlanetMessage = onPlanetMessage;
    }

    @Override
    public void start() {
        List<Planet> planetsWithMercenaries = new ArrayList<>();

        Vector2 heroPosition = game.getHero().getPosition();
        for (Planet planet : game.getPlanetManager().getNearestSystem(heroPosition).getPlanets()) {
            if (planet.getConfig().easyOnly && planet.getConfig().tradeConfig.mercs.groupCount() > 0) {
                planetsWithMercenaries.add(planet);
            }
        }

        if (planetsWithMercenaries.size() == 0) {
            setTutorialText("ERROR: Failed to find suitable planet.");
            return;
        }

        Planet closestPlanet = planetsWithMercenaries.get(0);
        float closestDistance = Float.MAX_VALUE;

        for (Planet planet : planetsWithMercenaries) {
            float distance = planet.getPosition().dst(heroPosition);
            if (distance < closestDistance) {
                closestPlanet = planet;
                closestDistance = distance;
            }
        }

        planet = closestPlanet;
        super.start();
    }

    private void setPlanetStationWaypoint() {
        for (FarShip farShip : game.getObjectManager().getFarShips()) {
            if (farShip.getHullConfig().getType() == HullConfig.Type.STATION &&
                    farShip.getTradeContainer() != null &&
                    farShip.getTradeContainer().getMercs().groupCount() > 0 &&
                    planet.isNearGround(farShip.getPosition())) {
                waypointPosition = farShip.getPosition();
                waypoint.position.set(waypointPosition);
                if (!game.getHero().getWaypoints().contains(waypoint)) {
                    game.getHero().addWaypoint(waypoint);
                    game.getObjectManager().addObjNow(game, waypoint);
                }
                return;
            }
        }
        for (SolObject solObject : game.getObjectManager().getObjects()) {
            if (solObject instanceof SolShip &&
                    ((SolShip) solObject).getHull().getHullConfig().getType() == HullConfig.Type.STATION &&
                    ((SolShip) solObject).getTradeContainer() != null &&
                    ((SolShip) solObject).getTradeContainer().getMercs().groupCount() > 0 &&
                    !planet.isNearGround(solObject.getPosition())) {
                waypointPosition = solObject.getPosition();
                waypoint.position.set(waypointPosition);
                if (!game.getHero().getWaypoints().contains(waypoint)) {
                    game.getHero().addWaypoint(waypoint);
                    game.getObjectManager().addObjNow(game, waypoint);
                }
                return;
            }
        }
    }

    @Override
    public boolean checkComplete(float timeStep) {
        boolean nearPlanet = super.checkComplete(timeStep);
        if (nearPlanet) {
            setTutorialText(onPlanetMessage);
            if (planet.areObjectsCreated()) {
                setPlanetStationWaypoint();
                if (game.getMainGameScreen().getTalkButton().isEnabled()) {
                    game.getHero().getWaypoints().remove(waypoint);
                    game.getObjectManager().removeObjDelayed(waypoint);
                    return true;
                }
            }
        }
        return false;
    }
}
