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

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import org.destinationsol.assets.Assets;
import org.destinationsol.game.Hero;
import org.destinationsol.game.SolGame;
import org.destinationsol.game.tutorial.TutorialStep;
import org.destinationsol.ui.Waypoint;
import org.destinationsol.ui.nui.screens.TutorialScreen;

public class FlyToWaypointStep extends TutorialStep {
    private static final float MIN_WAYPOINT_DISTANCE = 0.2f;
    protected final TutorialScreen tutorialScreen;
    protected final SolGame game;
    protected final String message;
    protected Vector2 waypointPosition;
    protected Waypoint waypoint;

    public FlyToWaypointStep(TutorialScreen tutorialScreen, SolGame game, Vector2 waypointPosition, String message) {
        this.tutorialScreen = tutorialScreen;
        this.game = game;
        this.waypointPosition = waypointPosition;
        this.message = message;
    }

    @Override
    public void start() {
        waypoint = new Waypoint(waypointPosition, Color.WHITE, Assets.getAtlasRegion("engine:mapObjects/waypoint"));

        Hero hero = game.getHero();
        hero.addWaypoint(waypoint);
        game.getObjectManager().addObjDelayed(waypoint);
        tutorialScreen.setTutorialText(message);
    }

    @Override
    public boolean checkComplete(float timeStep) {
        Hero hero = game.getHero();
        if (!hero.getWaypoints().contains(waypoint)) {
            hero.getWaypoints().add(waypoint);
            game.getObjectManager().addObjDelayed(waypoint);
        }

        if (hero.getPosition().dst(waypoint.getPosition()) < MIN_WAYPOINT_DISTANCE) {
            hero.removeWaypoint(waypoint);
            game.getObjectManager().removeObjDelayed(waypoint);
            return true;
        }
        return false;
    }
}
