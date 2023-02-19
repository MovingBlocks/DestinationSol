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

import org.destinationsol.game.screens.ChooseMercenaryScreen;
import org.destinationsol.game.screens.GiveItemsScreen;
import org.destinationsol.game.screens.ShowInventory;
import org.destinationsol.game.screens.TakeItems;
import org.destinationsol.game.tutorial.TutorialStep;
import org.destinationsol.ui.nui.NUIManager;
import org.destinationsol.ui.nui.screens.InventoryScreen;
import org.destinationsol.ui.nui.screens.TutorialScreen;

public class ManageMercenariesGuidanceStep extends TutorialStep {
    private final TutorialScreen tutorialScreen;
    private final NUIManager nuiManager;
    private final InventoryScreen inventoryScreen;
    private final String chooseMessage;
    private final String giveMessage;
    private final String takeMessage;
    private final String equipMessage;
    private boolean giveItemsPressed;
    private boolean takeItemsPressed;
    private boolean equipItemsPressed;

    public ManageMercenariesGuidanceStep(TutorialScreen tutorialScreen, NUIManager nuiManager, InventoryScreen inventoryScreen,
                                         String chooseMessage, String giveMessage,
                                         String takeMessage, String equipMessage) {
        this.tutorialScreen = tutorialScreen;
        this.nuiManager = nuiManager;
        this.inventoryScreen = inventoryScreen;

        this.chooseMessage = chooseMessage;
        this.giveMessage = giveMessage;
        this.takeMessage = takeMessage;
        this.equipMessage = equipMessage;
    }

    @Override
    public void start() {
        tutorialScreen.setTutorialText(chooseMessage);

        ChooseMercenaryScreen chooseMercenaryScreen = inventoryScreen.getChooseMercenaryScreen();
        chooseMercenaryScreen.getGiveItemsButton().subscribe(button -> {
            giveItemsPressed = true;
        });
        chooseMercenaryScreen.getTakeItemsButton().subscribe(button -> {
            takeItemsPressed = true;
        });
        chooseMercenaryScreen.getEquipItemsButton().subscribe(button -> {
            equipItemsPressed = true;
        });
    }

    @Override
    public boolean checkComplete(float timeStep) {
        ChooseMercenaryScreen chooseMercenaryScreen = inventoryScreen.getChooseMercenaryScreen();
        GiveItemsScreen giveItemsScreen = inventoryScreen.getGiveItems();
        TakeItems takeItemsScreen = inventoryScreen.getTakeItems();
        ShowInventory equipItemsScreen = inventoryScreen.getShowInventory();

        if (inventoryScreen.getOperations() == chooseMercenaryScreen) {
            tutorialScreen.setTutorialText(chooseMessage);
            if (!giveItemsPressed) {
                chooseMercenaryScreen.getGiveItemsButton().enableWarn();
            }
            if (!takeItemsPressed) {
                chooseMercenaryScreen.getTakeItemsButton().enableWarn();
            }
            if (!equipItemsPressed) {
                chooseMercenaryScreen.getEquipItemsButton().enableWarn();
            }
        } else if (inventoryScreen.getOperations() == giveItemsScreen) {
            tutorialScreen.setTutorialText(giveMessage);
        } else if (inventoryScreen.getOperations() == takeItemsScreen) {
            tutorialScreen.setTutorialText(takeMessage);
        } else if (inventoryScreen.getOperations() == equipItemsScreen && equipItemsScreen.getTarget().isMerc()) {
            tutorialScreen.setTutorialText(equipMessage);
        }

        return !nuiManager.hasScreen(inventoryScreen);
    }
}
