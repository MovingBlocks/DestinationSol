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
import org.destinationsol.game.StarPort;
import org.destinationsol.game.planet.Planet;

import javax.inject.Inject;

/**
 * A tutorial step that completes when the player reaches the nearest star port (star lane) to their location at
 * the start of the step.
 */
public class FlyToNearestStarPortStep extends FlyToWaypointStep {
    private Planet fromPlanet;
    private Planet toPlanet;

    @Inject
    protected FlyToNearestStarPortStep() {
        throw new RuntimeException("Attempted to instantiate TutorialStep via DI. This is not supported.");
    }

    public FlyToNearestStarPortStep(String message) {
        super(Vector2.Zero, message);
    }

    private Vector2 findNearestStarPort() {
        Vector2 heroPosition = game.getHero().getPosition();
        float nearestStarPortDistance = Float.MAX_VALUE;
        Vector2 nearestStarPortPosition = null;

        for (SolObject solObject : game.getObjectManager().getObjects()) {
            float distance = solObject.getPosition().dst(heroPosition);
            if (solObject instanceof StarPort && distance < nearestStarPortDistance) {
                StarPort starPort = (StarPort) solObject;
                fromPlanet = starPort.getFromPlanet();
                toPlanet = starPort.getToPlanet();
                nearestStarPortPosition = solObject.getPosition();
                nearestStarPortDistance = distance;
            }
        }
        for (StarPort.FarStarPort farStarPort : game.getObjectManager().getFarPorts()) {
            float distance = farStarPort.getPosition().dst(heroPosition);
            if (distance < nearestStarPortDistance) {
                fromPlanet = farStarPort.getFrom();
                toPlanet = farStarPort.getTo();
                nearestStarPortPosition = farStarPort.getPosition();
                nearestStarPortDistance = distance;
            }
        }

        return nearestStarPortPosition;
    }

    @Override
    public void start() {
        Vector2 nearestStarPortPosition = findNearestStarPort();
        if (nearestStarPortPosition == null) {
            throw new RuntimeException("Unable to find nearby star port!");
        }
        waypointPosition = nearestStarPortPosition;
        super.start();
    }

    @Override
    public boolean checkComplete(float timeStep) {
        for (SolObject solObject : game.getObjectManager().getObjects()) {
            if (solObject instanceof StarPort && ((StarPort)solObject).getFromPlanet() == fromPlanet &&
                    ((StarPort) solObject).getToPlanet() == toPlanet) {
                if (game.getHero().getPosition().dst(solObject.getPosition()) < game.getObjectManager().getRadius(solObject)) {
                    game.getHero().removeWaypoint(waypoint);
                    game.getObjectManager().removeObjDelayed(waypoint);
                    return true;
                } else {
                    waypoint.position.set(solObject.getPosition());
                }
                break;
            }
        }
        return super.checkComplete(timeStep);
    }
}
