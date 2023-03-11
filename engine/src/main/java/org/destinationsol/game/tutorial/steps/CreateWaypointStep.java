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

import org.destinationsol.game.Hero;
import org.destinationsol.game.SolGame;
import org.destinationsol.game.screens.GameScreens;
import org.destinationsol.game.tutorial.TutorialStep;
import org.destinationsol.ui.Waypoint;
import org.destinationsol.ui.nui.widgets.UIWarnButton;
import org.terasology.nui.HorizontalAlign;

import javax.inject.Inject;

public class CreateWaypointStep extends TutorialStep {
    @Inject
    protected SolGame game;
    @Inject
    protected GameScreens gameScreens;
    private final String message;
    private UIWarnButton addWaypointButton;
    private boolean buttonPressed = false;
    private int lastWaypointCount;

    @Inject
    protected CreateWaypointStep() {
        throw new RuntimeException("Attempted to instantiate TutorialStep via DI. This is not supported.");
    }

    public CreateWaypointStep(String message) {
        this.message = message;
    }

    public void start() {
        addWaypointButton = gameScreens.mapScreen.getAddWaypointButton();

        setTutorialBoxPosition(HorizontalAlign.LEFT);
        setTutorialText(message);
        addWaypointButton.subscribe(button -> {
            addWaypointButton.enableWarn();
            buttonPressed = true;
        });
        lastWaypointCount = game.getHero().getWaypoints().size();
    }
    public boolean checkComplete(float timeStep) {
        if (!buttonPressed) {
            addWaypointButton.enableWarn();
        }

        Hero hero = game.getHero();
        if (hero.getWaypoints().size() > lastWaypointCount) {
            Waypoint waypoint = hero.getWaypoints().get(hero.getWaypoints().size()-1);
            if (waypoint.getPosition().dst(hero.getPosition()) < 100.0f) {
                return true;
            } else {
                hero.removeWaypoint(waypoint);
            }
        }
        lastWaypointCount = game.getHero().getWaypoints().size();
        return false;
    }
}
