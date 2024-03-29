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
import org.destinationsol.game.screens.GameScreens;
import org.destinationsol.game.tutorial.TutorialStep;
import org.destinationsol.ui.nui.screens.InventoryScreen;
import org.destinationsol.ui.nui.widgets.UIWarnButton;

import javax.inject.Inject;

/**
 * A tutorial step that completes when the player either equips or un-equips an item.
 * If {@link #equipped} is true, then the step completes when the player equips an item.
 * If {@link #equipped} is false, then the step completes when the player un-equips the item they had selected.
 */
public class CheckItemEquippedStep extends TutorialStep {
    @Inject
    protected GameScreens gameScreens;
    private final boolean equipped;
    private final String message;
    private SolItem itemToCheck;
    private UIWarnButton equipButton;
    private boolean actionPerformed;

    @Inject
    protected CheckItemEquippedStep() {
        throw new RuntimeException("Attempted to instantiate TutorialStep via DI. This is not supported.");
    }

    public CheckItemEquippedStep(boolean equipped, String message) {
        this.equipped = equipped;
        this.message = message;
    }

    public void start() {
        InventoryScreen inventoryScreen = gameScreens.inventoryScreen;
        setTutorialText(message);
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
