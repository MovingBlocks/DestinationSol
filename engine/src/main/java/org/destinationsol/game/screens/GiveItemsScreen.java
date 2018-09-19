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
import org.destinationsol.game.Hero;
import org.destinationsol.game.SolGame;
import org.destinationsol.game.item.ItemContainer;
import org.destinationsol.game.item.SolItem;
import org.destinationsol.game.ship.SolShip;
import org.destinationsol.ui.SolInputManager;
import org.destinationsol.ui.SolUiControl;

public class GiveItemsScreen extends InventoryOperationsScreen {
    private final SolUiControl giveControl;
    private SolShip target;

    GiveItemsScreen(InventoryScreen inventoryScreen, GameOptions gameOptions) {
        giveControl = new SolUiControl(inventoryScreen.itemCtrl(0), true, gameOptions.getKeySellItem());
        giveControl.setDisplayName("Give");
        controls.add(giveControl);
    }

    @Override
    public ItemContainer getItems(SolGame game) {
        Hero hero = game.getHero();
        return hero.getItemContainer();
    }

    @Override
    public boolean isUsing(SolGame game, SolItem item) {
        Hero hero = game.getHero();
        return hero.isNonTranscendent() && hero.maybeUnequip(game, item, false);
    }

    @Override
    public String getHeader() {
        return "Give:";
    }

    @Override
    public void updateCustom(SolApplication solApplication, SolInputManager.InputPointer[] inputPointers, boolean clickedOutside) {
        SolGame game = solApplication.getGame();
        InventoryScreen inventoryScreen = game.getScreens().inventoryScreen;
        Hero hero = game.getHero();

        SolItem selItem = inventoryScreen.getSelectedItem();
        if (selItem == null) {
            giveControl.setDisplayName("----");
            giveControl.setEnabled(false);
            return;
        }

        boolean isWornAndCanBeGiven = isItemEquippedAndGiveable(selItem, solApplication.getOptions());
        boolean enabled = isItemGiveable(selItem, target);

        if (enabled && isWornAndCanBeGiven) {
            giveControl.setDisplayName("Give");
            giveControl.setEnabled(true);
        } else if (enabled) {
            giveControl.setDisplayName("Unequip it!");
            giveControl.setEnabled(false);
        } else {
            giveControl.setDisplayName("----");
            giveControl.setEnabled(false);
        }

        if (!enabled || !isWornAndCanBeGiven) {
            return;
        }
        if (giveControl.isJustOff()) {
            ItemContainer itemContainer = hero.getItemContainer();
            inventoryScreen.setSelected(itemContainer.getSelectionAfterRemove(inventoryScreen.getSelected()));
            itemContainer.remove(selItem);
            target.getItemContainer().add(selItem);
        }
    }

    /**
     * Inventories can only carry 24 groups of up to 30 items each,
     * hence the need to check if the item can be added
     * @param item The item to give
     * @param target The mercenary being interacted with
     * @return True if the item can be given
     */
    private boolean isItemGiveable(SolItem item, SolShip target) {
        return target.getItemContainer().canAdd(item);
    }

    /**
     * Items cannot be sold if equipped unless the game option canSellEquippedItems
     * is true. This method checks both conditions and returns the appropriate boolean.
     * @param item The item to be given
     * @param options A static class holding the options set for the game
     * @return True if the item is unequipped or equipped items can be sold
     */
    private boolean isItemEquippedAndGiveable(SolItem item, GameOptions options) {
        return (item.isEquipped() == 0 || options.canSellEquippedItems);
    }

    /**
     * Sets the mercenary to give items to
     * @param solship The mercenary being interacted with
     */
    public void setTarget(SolShip solship) {
        this.target = solship;
    }
}
