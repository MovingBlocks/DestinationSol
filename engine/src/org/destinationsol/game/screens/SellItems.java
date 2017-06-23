/*
 * Copyright 2017 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
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
import org.destinationsol.game.item.SolItem;
import org.destinationsol.game.ship.SolShip;
import org.destinationsol.ui.SolInputManager;
import org.destinationsol.ui.SolUiControl;

import java.util.ArrayList;
import java.util.List;

public class SellItems implements InventoryOperations {
    private static float PERC = .8f;

    private final ArrayList<SolUiControl> controls = new ArrayList<>();
    private final SolUiControl sellControl;

    SellItems(InventoryScreen inventoryScreen, GameOptions gameOptions) {
        sellControl = new SolUiControl(inventoryScreen.itemCtrl(0), true, gameOptions.getKeySellItem());
        sellControl.setDisplayName("Sell");
        controls.add(sellControl);
    }

    @Override
    public ItemContainer getItems(SolGame game) {
        SolShip h = game.getHero();
        return h == null ? null : h.getItemContainer();
    }

    @Override
    public boolean isUsing(SolGame game, SolItem item) {
        SolShip h = game.getHero();
        return h != null && h.maybeUnequip(game, item, false);
    }

    @Override
    public float getPriceMul() {
        return PERC;
    }

    @Override
    public String getHeader() {
        return "Sell:";
    }

    @Override
    public List<SolUiControl> getControls() {
        return controls;
    }

    @Override
    public void updateCustom(SolApplication solApplication, SolInputManager.InputPointer[] inputPointers, boolean clickedOutside) {
        SolGame game = solApplication.getGame();
        InventoryScreen is = game.getScreens().inventoryScreen;
        TalkScreen talkScreen = game.getScreens().talkScreen;
        SolShip target = talkScreen.getTarget();
        SolShip hero = game.getHero();
        if (talkScreen.isTargetFar(hero)) {
            solApplication.getInputMan().setScreen(solApplication, game.getScreens().mainScreen);
            return;
        }
        SolItem selItem = is.getSelectedItem();
        if (selItem == null) {
            sellControl.setDisplayName("----");
            sellControl.setEnabled(false);
            return;
        }

        boolean isWornAndCanBeSold = isItemEquippedAndSellable(selItem, solApplication.getOptions());
        boolean enabled = isItemSellable(selItem, target);

        if (enabled && isWornAndCanBeSold) {
            sellControl.setDisplayName("Sell");
            sellControl.setEnabled(true);
        } else if (enabled && !isWornAndCanBeSold) {
            sellControl.setDisplayName("Unequip it!");
            sellControl.setEnabled(false);
        } else {
            sellControl.setDisplayName("----");
            sellControl.setEnabled(false);
        }

        if (!enabled || !isWornAndCanBeSold) {
            return;
        }
        if (sellControl.isJustOff()) {
            ItemContainer ic = hero.getItemContainer();
            is.setSelected(ic.getSelectionAfterRemove(is.getSelected()));
            ic.remove(selItem);
            target.getTradeContainer().getItems().add(selItem);
            hero.setMoney(hero.getMoney() + selItem.getPrice() * PERC);
        }
    }

    private boolean isItemSellable(SolItem item, SolShip target) {
        return target.getTradeContainer().getItems().canAdd(item);
    }

    // Return true if the item is not worn, or is worn and canSellEquippedItems is true
    private boolean isItemEquippedAndSellable(SolItem item, GameOptions options) {
        return (item.isEquipped() == 0 || options.canSellEquippedItems);
    }
}
