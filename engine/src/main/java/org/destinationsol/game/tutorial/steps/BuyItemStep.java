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

import org.destinationsol.game.screens.GameScreens;
import org.destinationsol.game.tutorial.TutorialStep;
import org.destinationsol.ui.nui.NUIManager;
import org.destinationsol.ui.nui.widgets.UIWarnButton;
import org.terasology.nui.HorizontalAlign;

import javax.inject.Inject;

public class BuyItemStep extends TutorialStep {
    @Inject
    protected NUIManager nuiManager;
    @Inject
    protected GameScreens gameScreens;
    private final String buyButtonMessage;
    private final String purchaseButtonMessage;
    private UIWarnButton buyButton;
    private UIWarnButton purchaseButton;
    private boolean buyButtonPressed = false;
    private boolean purchaseButtonPressed = false;

    @Inject
    protected BuyItemStep() {
        throw new RuntimeException("Attempted to instantiate TutorialStep via DI. This is not supported.");
    }

    public BuyItemStep(String buyButtonMessage, String purchaseButtonMessage) {
        this.buyButtonMessage = buyButtonMessage;
        this.purchaseButtonMessage = purchaseButtonMessage;
    }

    public void start() {
        buyButton = gameScreens.talkScreen.getBuyButton();;
        purchaseButton = gameScreens.inventoryScreen.getBuyItemsScreen().getBuyControl();

        setTutorialBoxPosition(HorizontalAlign.LEFT);
        setTutorialText(buyButtonMessage);
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
            setTutorialBoxPosition(HorizontalAlign.LEFT);
            setTutorialText(purchaseButtonMessage);
            purchaseButton.enableWarn();
        }

        if (buyButtonPressed && purchaseButtonPressed) {
            nuiManager.popScreen();
            return true;
        }
        return false;
    }
}
