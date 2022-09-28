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
import org.destinationsol.game.Hero;
import org.destinationsol.game.SolGame;
import org.destinationsol.game.item.ItemContainer;
import org.destinationsol.game.item.SolItem;
import org.destinationsol.game.ship.SolShip;
import org.destinationsol.ui.nui.screens.InventoryScreen;
import org.destinationsol.ui.nui.screens.TalkScreen;
import org.destinationsol.ui.nui.widgets.KeyActivatedButton;
import org.terasology.nui.backends.libgdx.GDXInputUtil;
import org.terasology.nui.widgets.UIButton;

/**
 * This screen allows the hero to sell their items in exchange for in-game currency (money).
 * The sold items are moved into the vendor's inventory.
 */
public class SellItems extends InventoryOperationsScreen {
    private static float PERC = .8f;

    private final UIButton[] actionButtons = new UIButton[1];

    public SellItems() {
    }

    @Override
    public void initialise(SolApplication solApplication, InventoryScreen inventoryScreen) {
        KeyActivatedButton sellButton = new KeyActivatedButton();
        sellButton.setText("Sell");
        sellButton.setKey(GDXInputUtil.GDXToNuiKey(solApplication.getOptions().getKeySellItem()));
        sellButton.subscribe(button -> {
            SolGame game = solApplication.getGame();
            Hero hero = game.getHero();
            SolItem selectedItem = inventoryScreen.getSelectedItem();
            TalkScreen talkScreen = game.getScreens().talkScreen;
            SolShip target = talkScreen.getTarget();

            ItemContainer itemContainer = hero.getItemContainer();
            inventoryScreen.setSelected(itemContainer.getSelectionAfterRemove(inventoryScreen.getSelected()));
            itemContainer.remove(selectedItem);
            target.getTradeContainer().getItems().add(selectedItem);
            hero.setMoney(hero.getMoney() + selectedItem.getPrice() * PERC);

            inventoryScreen.updateItemRows();
        });
        actionButtons[0] = sellButton;
    }

    @Override
    public ItemContainer getItems(SolGame game) {
        Hero hero = game.getHero();
        return hero.isTranscendent() ? null : hero.getItemContainer();
    }

    @Override
    public boolean isUsing(SolGame game, SolItem item) {
        Hero hero = game.getHero();
        return hero.isNonTranscendent() && hero.maybeUnequip(game, item, false);
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
    public UIButton[] getActionButtons() {
        return actionButtons;
    }

    @Override
    public void update(SolApplication solApplication, InventoryScreen inventoryScreen) {
        SolGame game = solApplication.getGame();
        UIButton sellButton = actionButtons[0];

        TalkScreen talkScreen = game.getScreens().talkScreen;
        SolShip target = talkScreen.getTarget();
        Hero hero = game.getHero();
        if (talkScreen.isTargetFar(hero)) {
            solApplication.getInputManager().setScreen(solApplication, game.getScreens().oldMainGameScreen);
            return;
        }
        SolItem selItem = inventoryScreen.getSelectedItem();
        if (selItem == null) {
            sellButton.setText("----");
            sellButton.setEnabled(false);
            return;
        }

        boolean isWornAndCanBeSold = isItemEquippedAndSellable(selItem, solApplication.getOptions());
        boolean enabled = isItemSellable(selItem, target);

        if (enabled && isWornAndCanBeSold) {
            sellButton.setText("Sell");
            sellButton.setEnabled(true);
        } else if (enabled) {
            sellButton.setText("Unequip it!");
            sellButton.setEnabled(false);
        } else {
            sellButton.setText("----");
            sellButton.setEnabled(false);
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
