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

import org.destinationsol.game.item.SolItem;
import org.destinationsol.game.tutorial.TutorialStep;
import org.destinationsol.ui.nui.screens.InventoryScreen;
import org.destinationsol.ui.nui.screens.TutorialScreen;
import org.destinationsol.ui.nui.widgets.UIWarnButton;

public class CheckItemEquippedStep extends TutorialStep {
    private final TutorialScreen tutorialScreen;
    private final InventoryScreen inventoryScreen;
    private final boolean equipped;
    private final String message;
    private SolItem itemToCheck;
    private UIWarnButton equipButton;
    private boolean actionPerformed;

    public CheckItemEquippedStep(TutorialScreen tutorialScreen, InventoryScreen inventoryScreen,
                                 boolean equipped, String message) {
        this.tutorialScreen = tutorialScreen;
        this.inventoryScreen = inventoryScreen;
        this.equipped = equipped;
        this.message = message;
    }

    public void start() {
        tutorialScreen.setTutorialText(message);
        if (equipped) {
            itemToCheck = inventoryScreen.getSelectedItem();
        }
        equipButton = inventoryScreen.getShowInventory().getEq1Control();
        equipButton.subscribe(button -> {
            if (equipped && inventoryScreen.getSelectedItem() != itemToCheck) {
                return;
            }
            actionPerformed = true;
        });
        actionPerformed = false;
    }

    public boolean checkComplete(float timeStep) {
        equipButton.enableWarn();
        return actionPerformed;
    }
}
