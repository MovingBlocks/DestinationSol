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
import org.destinationsol.game.item.SolItem;
import org.destinationsol.game.ship.SolShip;

public class BuyItemsScreen extends InventoryOperationsScreen {
    @Override
    public void onAdd(InventoryScreen inventoryScreen) {
        SolApplication.getInstance().getGame().getScreens().talkScreen.setHidden(false);
        inventoryScreen.getInteractButton().setAction(uiElement -> {
            SolShip target = SolApplication.getInstance().getGame().getScreens().talkScreen.getTarget();
            Hero hero = SolApplication.getInstance().getGame().getHero();
            SolItem item = inventoryScreen.getSelectedItem();
            target.getTradeContainer().getItems().remove(item);
            hero.getItemContainer().add(item);
            hero.setMoney(hero.getMoney() - item.getPrice());
            inventoryScreen.refresh();
        });
    }

    @Override
    public void update(InventoryScreen inventoryScreen, SolApplication solApplication) {
        Hero hero = solApplication.getGame().getHero();
        SolItem selectedItem = inventoryScreen.getSelectedItem();
        boolean itemPurchasable = (selectedItem != null
                && hero.getMoney() >= selectedItem.getPrice()
                && hero.getItemContainer().canAdd(selectedItem));
        inventoryScreen.getInteractButton().setEnabled(itemPurchasable);
        inventoryScreen.setInteractText("Buy");
    }

    @Override
    public ItemContainer getItems(SolGame game) {
        return game.getScreens().talkScreen.getTarget().getTradeContainer().getItems();
    }

    @Override
    public String getHeader() {
        return "Buy:";
    }
}
