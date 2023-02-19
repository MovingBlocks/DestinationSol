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
import org.destinationsol.game.input.Pilot;
import org.destinationsol.game.screens.ShipUiControl;
import org.destinationsol.game.tutorial.TutorialStep;
import org.destinationsol.ui.nui.screens.TutorialScreen;
import org.destinationsol.ui.nui.screens.UIShipControlsScreen;
import org.destinationsol.ui.nui.widgets.UIWarnButton;

public class ThrustForwardsStep extends TutorialStep {
    private final TutorialScreen tutorialScreen;
    private final SolGame game;
    private final String message;
    private final UIWarnButton thrustButton;
    private boolean didThrust = false;

    public ThrustForwardsStep(TutorialScreen tutorialScreen, SolGame game, String message) {
        this.tutorialScreen = tutorialScreen;
        this.game = game;
        this.message = message;
        ShipUiControl shipUiControl = game.getScreens().oldMainGameScreen.getShipControl();
        if (shipUiControl instanceof UIShipControlsScreen) {
            thrustButton = ((UIShipControlsScreen) shipUiControl).getForwardButton();
        } else {
            thrustButton = null;
        }
    }

    public void start() {
        tutorialScreen.setTutorialText(message);
    }

    public boolean checkComplete(float timeStep) {
        if (thrustButton != null) {
            thrustButton.enableWarn();
        }

        Hero hero = game.getHero();
        Pilot playerPilot = hero.getPilot();
        if (playerPilot.isUp()) {
            didThrust = true;
        }
        return (didThrust && hero.getShip().getVelocity().len() > 0.1f);
    }
}
