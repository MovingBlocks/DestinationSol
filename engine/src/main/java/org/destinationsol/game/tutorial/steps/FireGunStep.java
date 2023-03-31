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

import org.destinationsol.game.SolGame;
import org.destinationsol.game.screens.GameScreens;
import org.destinationsol.game.screens.ShipUiControl;
import org.destinationsol.ui.nui.screens.UIShipControlsScreen;
import org.destinationsol.ui.nui.widgets.UIWarnButton;

import javax.inject.Inject;

/**
 * A tutorial step that completes when the player ship fires its gun.
 */
public class FireGunStep extends MessageStep {
    @Inject
    protected SolGame game;
    @Inject
    protected GameScreens gameScreens;
    private UIWarnButton fireButton;

    @Inject
    protected FireGunStep() {
        throw new RuntimeException("Attempted to instantiate TutorialStep via DI. This is not supported.");
    }

    public FireGunStep(String message) {
        super(message);
    }

    public void start() {
        setTutorialText(message);

        ShipUiControl shipUiControl = gameScreens.oldMainGameScreen.getShipControl();
        if (shipUiControl instanceof UIShipControlsScreen) {
            fireButton = ((UIShipControlsScreen) shipUiControl).getGun1Button();
        }
    }

    public boolean checkComplete(float timeStep) {
        if (fireButton != null) {
            fireButton.enableWarn();
        }

        stepTimer += timeStep;
        return stepTimer > MIN_STEP_DURATION && game.getHero().getPilot().isShoot();
    }
}
