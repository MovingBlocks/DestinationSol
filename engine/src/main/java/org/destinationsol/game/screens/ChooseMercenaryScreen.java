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

import org.destinationsol.GameOptions;
import org.destinationsol.SolApplication;
import org.destinationsol.game.SolGame;
import org.destinationsol.game.item.ItemContainer;
import org.destinationsol.game.item.MercItem;
import org.destinationsol.game.item.SolItem;
import org.destinationsol.game.ship.SolShip;
import org.destinationsol.ui.SolInputManager;


public class ChooseMercenaryScreen extends InventoryOperationsScreen {
//    private final SolUiControl giveControl;
//    private final SolUiControl takeControl;
//    private final SolUiControl equipControl;
    private final ItemContainer EMPTY_ITEM_CONTAINER = new ItemContainer();

    ChooseMercenaryScreen(InventoryScreen inventoryScreen, GameOptions gameOptions) {
//        giveControl = new SolUiControl(inventoryScreen.itemCtrl(0), true, gameOptions.getKeyShoot());
//        giveControl.setDisplayName("Give Items");
//        controls.add(giveControl);
//
//        takeControl = new SolUiControl(inventoryScreen.itemCtrl(1), true, gameOptions.getKeyShoot2());
//        takeControl.setDisplayName("Take Items");
//        controls.add(takeControl);
//
//        equipControl = new SolUiControl(inventoryScreen.itemCtrl(2), true, gameOptions.getKeyDrop());
//        equipControl.setDisplayName("Equip Items");
//        controls.add(equipControl);
    }

//    @Override
//    public void updateCustom(SolApplication solApplication, SolInputManager.InputPointer[] inputPointers, boolean clickedOutside) {
//        SolGame game = solApplication.getGame();
//        InventoryScreen is = game.getScreens().inventoryScreen;
//        SolInputManager inputMan = solApplication.getInputManager();
//        GameScreens screens = game.getScreens();
//        SolItem selItem = is.getSelectedItem();
//        boolean selNull = selItem != null;
//
////        giveControl.setEnabled(selNull);
////        takeControl.setEnabled(selNull);
////        equipControl.setEnabled(selNull);
////
////        if (giveControl.isJustOff() && selNull) {
////            SolShip solship = ((MercItem) selItem).getSolShip();
////            inputMan.setScreen(solApplication, screens.mainGameScreen);
////            is.giveItemsScreen.setTarget(solship);
////            is.setOperations(is.giveItemsScreen);
////            inputMan.addScreen(solApplication, is);
////        } else if (takeControl.isJustOff() && selNull) {
////            SolShip solship = ((MercItem) selItem).getSolShip();
////            inputMan.setScreen(solApplication, screens.mainGameScreen);
////            is.takeItems.setTarget(solship);
////            is.setOperations(is.takeItems);
////            inputMan.addScreen(solApplication, is);
////        } else if (equipControl.isJustOff() && selNull) {
////            SolShip solship = ((MercItem) selItem).getSolShip();
////            inputMan.setScreen(solApplication, screens.mainGameScreen);
////            is.showInventory.setTarget(solship);
////            is.setOperations(is.showInventory);
////            inputMan.addScreen(solApplication, is);
////        }
//    }

    @Override
    public ItemContainer getItems(SolGame game) {
        ItemContainer mercs = game.getHero().getMercs();
        return mercs != null ? mercs : EMPTY_ITEM_CONTAINER;
    }

    @Override
    public String getHeader() {
        return "Mercenaries:";
    }

}
