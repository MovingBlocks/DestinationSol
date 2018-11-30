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
import org.destinationsol.game.Hero;
import org.destinationsol.game.SolGame;
import org.destinationsol.game.item.ItemContainer;
import org.destinationsol.game.item.MercItem;
import org.destinationsol.game.item.SolItem;
import org.destinationsol.mercenary.MercenaryUtils;

public class HireShipsScreen extends InventoryOperationsScreen {
    @Override
    public void onAdd(InventoryScreen inventoryScreen) {
        SolApplication.getInstance().getGame().getScreens().talkScreen.setHidden(false);
        inventoryScreen.getInteractButton().setAction(uiElement -> {
            SolGame game = SolApplication.getInstance().getGame();
            Hero hero = game.getHero();
            SolItem item = inventoryScreen.getSelectedItem();

            if (!(item instanceof MercItem)) {
                return;
            }

            boolean hired = MercenaryUtils.createMerc(game, hero, (MercItem) item);
            if (hired) {
                hero.setMoney(hero.getMoney() - item.getPrice());
            }
            inventoryScreen.refresh();
        });
    }

    @Override
    void update(InventoryScreen inventoryScreen, SolApplication solApplication) {
        Hero hero = solApplication.getGame().getHero();
        SolItem selectedItem = inventoryScreen.getSelectedItem();
        boolean itemPurchasable = (selectedItem != null && hero.getMoney() >= selectedItem.getPrice());
        inventoryScreen.getInteractButton().setEnabled(itemPurchasable);
        inventoryScreen.setInteractText("Hire");
    }

    @Override
    ItemContainer getItems(SolGame game) {
        return game.getScreens().talkScreen.getTarget().getTradeContainer().getMercs();
    }

    @Override
    public String getHeader() {
        return "Mercenaries:";
    }

//    @Override
//    public void updateCustom(SolApplication solApplication, SolInputManager.InputPointer[] inputPointers, boolean clickedOutside) {
//        SolGame game = solApplication.getGame();
//        InventoryScreen is = game.getScreens().inventoryScreen;
//        Hero hero = game.getHero();
//        TalkScreen talkScreen = game.getScreens().talkScreen;
////        if (talkScreen.isTargetFar(hero)) {
////            solApplication.getInputManager().setScreen(solApplication, game.getScreens().mainGameScreen);
////            return;
////        }
////        SolItem selItem = is.getSelectedItem();
////        boolean enabled = selItem != null && hero.getMoney() >= selItem.getPrice();
////        hireControl.setDisplayName(enabled ? "Hire" : "---");
////        hireControl.setEnabled(enabled);
////        if (!enabled) {
////            return;
////        }
////        if (hireControl.isJustOff()) {
////            boolean hired = MercenaryUtils.createMerc(game, hero, (MercItem) selItem);
////            if (hired) {
////                hero.setMoney(hero.getMoney() - selItem.getPrice());
////            }
////        }
//    }
}
