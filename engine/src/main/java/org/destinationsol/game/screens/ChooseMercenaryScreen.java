/*
 * Copyright 2018 MovingBlocks
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
package org.destinationsol.game.screens;

import org.destinationsol.SolApplication;
import org.destinationsol.game.SolGame;
import org.destinationsol.game.item.ItemContainer;
import org.destinationsol.game.item.MercItem;
import org.destinationsol.game.item.SolItem;
import org.destinationsol.game.ship.SolShip;
import org.destinationsol.ui.SolInputManager;
import org.destinationsol.ui.nui.NUIManager;
import org.destinationsol.ui.nui.screens.InventoryScreen;
import org.destinationsol.ui.nui.widgets.UIWarnButton;
import org.terasology.nui.backends.libgdx.GDXInputUtil;
import org.terasology.nui.widgets.UIButton;

/**
 * This screen shows an overview of all the mercenaries that the hero has hired.
 * You can manage each mercenary's independent inventory from here, as well as selecting their equipped items.
 */
public class ChooseMercenaryScreen extends InventoryOperationsScreen {
    private final UIButton[] actionButtons = new UIButton[3];
    private final ItemContainer EMPTY_ITEM_CONTAINER = new ItemContainer();

    public ChooseMercenaryScreen() {
    }

    @Override
    public void initialise(SolApplication solApplication, InventoryScreen inventoryScreen) {
        UIWarnButton giveButton = new UIWarnButton();
        giveButton.setText("Give Items");
        giveButton.setKey(GDXInputUtil.GDXToNuiKey(solApplication.getOptions().getKeyShoot()));
        giveButton.subscribe(button -> {
            SolItem selectedItem = inventoryScreen.getSelectedItem();
            SolInputManager inputManager = solApplication.getInputManager();
            NUIManager nuiManager = solApplication.getNuiManager();

            SolShip solship = ((MercItem) selectedItem).getSolShip();
            inputManager.setScreen(solApplication, solApplication.getGame().getScreens().oldMainGameScreen);
            nuiManager.removeScreen(inventoryScreen);
            inventoryScreen.getGiveItems().setTarget(solship);
            inventoryScreen.setOperations(inventoryScreen.getGiveItems());
            nuiManager.pushScreen(inventoryScreen);
        });
        actionButtons[0] = giveButton;

        UIWarnButton takeButton = new UIWarnButton();
        takeButton.setText("Take Items");
        takeButton.setKey(GDXInputUtil.GDXToNuiKey(solApplication.getOptions().getKeyShoot2()));
        takeButton.subscribe(button -> {
            SolItem selectedItem = inventoryScreen.getSelectedItem();
            SolInputManager inputManager = solApplication.getInputManager();
            NUIManager nuiManager = solApplication.getNuiManager();

            SolShip solship = ((MercItem) selectedItem).getSolShip();
            inputManager.setScreen(solApplication, solApplication.getGame().getScreens().oldMainGameScreen);
            inventoryScreen.getTakeItems().setTarget(solship);
            nuiManager.removeScreen(inventoryScreen);
            inventoryScreen.setOperations(inventoryScreen.getTakeItems());
            nuiManager.pushScreen(inventoryScreen);
        });
        actionButtons[1] = takeButton;

        UIWarnButton equipButton = new UIWarnButton();
        equipButton.setText("Equip Items");
        equipButton.setKey(GDXInputUtil.GDXToNuiKey(solApplication.getOptions().getKeyDrop()));
        equipButton.subscribe(button -> {
            SolItem selectedItem = inventoryScreen.getSelectedItem();
            SolInputManager inputManager = solApplication.getInputManager();
            NUIManager nuiManager = solApplication.getNuiManager();

            SolShip solship = ((MercItem) selectedItem).getSolShip();
            inputManager.setScreen(solApplication, solApplication.getGame().getScreens().oldMainGameScreen);
            nuiManager.removeScreen(inventoryScreen);
            inventoryScreen.getShowInventory().setTarget(solship);
            inventoryScreen.setOperations(inventoryScreen.getShowInventory());
            nuiManager.pushScreen(inventoryScreen);
        });
        actionButtons[2] = equipButton;
    }

    public UIWarnButton getGiveItemsButton() {
        return (UIWarnButton) actionButtons[0];
    }

    public UIWarnButton getTakeItemsButton() {
        return (UIWarnButton) actionButtons[1];
    }

    public UIWarnButton getEquipItemsButton() {
        return (UIWarnButton) actionButtons[2];
    }

    @Override
    public void update(SolApplication solApplication, InventoryScreen inventoryScreen) {
        boolean selNull = inventoryScreen.getSelectedItem() != null;

        UIButton giveButton = actionButtons[0];
        UIButton takeButton = actionButtons[1];
        UIButton equipButton = actionButtons[2];

        giveButton.setEnabled(selNull);
        takeButton.setEnabled(selNull);
        equipButton.setEnabled(selNull);
    }

    @Override
    public ItemContainer getItems(SolGame game) {
        if (game.getHero().isNonTranscendent()) {
            ItemContainer mercs = game.getHero().getMercs();
            if (mercs != null) {
                return mercs;
            }
        }
        return EMPTY_ITEM_CONTAINER;
    }

    @Override
    public String getHeader() {
        return "Mercenaries:";
    }

    @Override
    public UIButton[] getActionButtons() {
        return actionButtons;
    }
}
