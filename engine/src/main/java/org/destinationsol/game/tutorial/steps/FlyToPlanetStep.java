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
import org.destinationsol.game.Hero;
import org.destinationsol.game.planet.Planet;

import javax.inject.Inject;

public class FlyToPlanetStep extends FlyToWaypointStep {
    protected Planet planet;

    @Inject
    protected FlyToPlanetStep() {
        throw new RuntimeException("Attempted to instantiate TutorialStep via DI. This is not supported.");
    }

    public FlyToPlanetStep(Planet planet, String message) {
        super(planet != null ? planet.getPosition() : Vector2.Zero, message);
        this.planet = planet;
    }

    @Override
    public void start() {
        waypointPosition = planet.getPosition();
        super.start();
    }

    @Override
    public boolean checkComplete(float timeStep) {
        Hero hero = game.getHero();
        if (planet.isNearGround(hero.getPosition())) {
            hero.removeWaypoint(waypoint);
            return true;
        }
        return super.checkComplete(timeStep);
    }
}
