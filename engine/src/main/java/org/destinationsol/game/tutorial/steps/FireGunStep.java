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
import org.destinationsol.game.screens.ShipUiControl;
import org.destinationsol.ui.nui.screens.TutorialScreen;
import org.destinationsol.ui.nui.screens.UIShipControlsScreen;
import org.destinationsol.ui.nui.widgets.UIWarnButton;

public class FireGunStep extends MessageStep {
    private UIWarnButton fireButton;

    public FireGunStep(TutorialScreen tutorialScreen, SolGame game, String message) {
        super(tutorialScreen, game, message);
    }

    public void start() {
        tutorialScreen.setTutorialText(message);

        ShipUiControl shipUiControl = game.getScreens().oldMainGameScreen.getShipControl();
        if (shipUiControl instanceof UIShipControlsScreen) {
            fireButton = ((UIShipControlsScreen) shipUiControl).getGun1Button();
        }
    }

    public boolean checkComplete(float timeStep) {
        if (fireButton != null) {
            fireButton.enableWarn();
        }

        return super.checkComplete(timeStep) && game.getHero().getPilot().isShoot();
    }
}
