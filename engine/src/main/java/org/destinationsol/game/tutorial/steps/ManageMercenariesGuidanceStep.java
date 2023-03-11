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
import org.destinationsol.game.screens.GameScreens;
import org.destinationsol.game.screens.GiveItemsScreen;
import org.destinationsol.game.screens.ShowInventory;
import org.destinationsol.game.screens.TakeItems;
import org.destinationsol.game.tutorial.TutorialStep;
import org.destinationsol.ui.nui.NUIManager;
import org.destinationsol.ui.nui.screens.InventoryScreen;

import javax.inject.Inject;

/**
 * A tutorial step that guides the player through the {@link ChooseMercenaryScreen}.
 * It explains what the various sub-screens do as well.
 * It completes when the player closes the screen.
 */
public class ManageMercenariesGuidanceStep extends TutorialStep {
    @Inject
    protected NUIManager nuiManager;
    @Inject
    protected GameScreens gameScreens;
    private final String chooseMessage;
    private final String giveMessage;
    private final String takeMessage;
    private final String equipMessage;
    private boolean giveItemsPressed;
    private boolean takeItemsPressed;
    private boolean equipItemsPressed;

    @Inject
    protected ManageMercenariesGuidanceStep() {
        throw new RuntimeException("Attempted to instantiate TutorialStep via DI. This is not supported.");
    }

    public ManageMercenariesGuidanceStep(String chooseMessage, String giveMessage,
                                         String takeMessage, String equipMessage) {
        this.chooseMessage = chooseMessage;
        this.giveMessage = giveMessage;
        this.takeMessage = takeMessage;
        this.equipMessage = equipMessage;
    }

    @Override
    public void start() {
        setTutorialText(chooseMessage);

        ChooseMercenaryScreen chooseMercenaryScreen = gameScreens.inventoryScreen.getChooseMercenaryScreen();
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
        InventoryScreen inventoryScreen = gameScreens.inventoryScreen;
        ChooseMercenaryScreen chooseMercenaryScreen = inventoryScreen.getChooseMercenaryScreen();
        GiveItemsScreen giveItemsScreen = inventoryScreen.getGiveItems();
        TakeItems takeItemsScreen = inventoryScreen.getTakeItems();
        ShowInventory equipItemsScreen = inventoryScreen.getShowInventory();

        if (inventoryScreen.getOperations() == chooseMercenaryScreen) {
            setTutorialText(chooseMessage);
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
            setTutorialText(giveMessage);
        } else if (inventoryScreen.getOperations() == takeItemsScreen) {
            setTutorialText(takeMessage);
        } else if (inventoryScreen.getOperations() == equipItemsScreen && equipItemsScreen.getTarget().isMerc()) {
            setTutorialText(equipMessage);
        }

        return !nuiManager.hasScreen(inventoryScreen);
    }
}
