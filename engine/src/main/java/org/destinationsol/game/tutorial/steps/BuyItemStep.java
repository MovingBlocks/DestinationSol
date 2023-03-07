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

import org.destinationsol.game.tutorial.TutorialStep;
import org.destinationsol.ui.nui.NUIManager;
import org.destinationsol.ui.nui.screens.TutorialScreen;
import org.destinationsol.ui.nui.widgets.UIWarnButton;
import org.terasology.nui.HorizontalAlign;

public class BuyItemStep extends TutorialStep {
    private final TutorialScreen tutorialScreen;
    private final NUIManager nuiManager;
    private final UIWarnButton buyButton;
    private final UIWarnButton purchaseButton;
    private final String message;
    private boolean buyButtonPressed = false;
    private boolean purchaseButtonPressed = false;

    public BuyItemStep(TutorialScreen tutorialScreen, NUIManager nuiManager,
                       UIWarnButton buyButton, UIWarnButton purchaseButton, String message) {
        this.tutorialScreen = tutorialScreen;
        this.nuiManager = nuiManager;
        this.buyButton = buyButton;
        this.purchaseButton = purchaseButton;
        this.message = message;
    }

    public void start() {
        tutorialScreen.setTutorialText(message, HorizontalAlign.LEFT);
        buyButton.subscribe(button -> {
            buyButtonPressed = true;
        });
        purchaseButton.subscribe(button -> {
            purchaseButtonPressed = true;
        });
    }
    public boolean checkComplete(float timeStep) {
        if (!buyButtonPressed) {
            buyButton.enableWarn();
        } else {
            purchaseButton.enableWarn();
        }

        if (buyButtonPressed && purchaseButtonPressed) {
            nuiManager.popScreen();
            return true;
        }
        return false;
    }
}
