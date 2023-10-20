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

import javax.inject.Inject;

/**
 * A tutorial step that completes when the player ship reaches a nearby spawned waypoint.
 */
public class FlyToHeroFirstWaypointStep extends FlyToWaypointStep {
    private final String missingWaypointMessage;

    @Inject
    protected FlyToHeroFirstWaypointStep() {
        throw new RuntimeException("Attempted to instantiate TutorialStep via DI. This is not supported.");
    }

    public FlyToHeroFirstWaypointStep(String message, String missingWaypointMessage) {
        super(Vector2.Zero, message);
        this.missingWaypointMessage = missingWaypointMessage;
    }

    @Override
    public void start() {
        Hero hero = game.getHero();
        if (hero.getWaypoints().isEmpty()) {
            setTutorialText(missingWaypointMessage);
        } else {
            waypoint = game.getHero().getWaypoints().get(0);
            setTutorialText(message);
        }
    }

    @Override
    public boolean checkComplete(float timeStep) {
        Hero hero = game.getHero();
        if (hero.getWaypoints().isEmpty()) {
            setTutorialText(missingWaypointMessage);
            return false;
        } else {
            setTutorialText(message);
        }

        if (!hero.getWaypoints().contains(waypoint) && hero.getWaypoints().size() > 0) {
            // Change the target waypoint just in-case the player removes it.
            waypoint = hero.getWaypoints().get(0);
        }
        return super.checkComplete(timeStep);
    }
}
