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
import org.destinationsol.game.faction.DefaultReputationEvent;
import org.destinationsol.game.item.ItemContainer;
import org.destinationsol.game.item.SolItem;
import org.destinationsol.game.ship.SolShip;
import org.destinationsol.ui.nui.screens.InventoryScreen;
import org.destinationsol.ui.nui.screens.TalkScreen;
import org.destinationsol.ui.nui.widgets.UIWarnButton;
import org.terasology.nui.backends.libgdx.GDXInputUtil;
import org.terasology.nui.widgets.UIButton;

/**
 * This screen allows you to purchase items for the hero's ship.
 * The purchased items are moved to the hero's inventory and the cost deducted from the hero's money.
 */
public class BuyItemsScreen extends InventoryOperationsScreen {
    private final UIButton[] actionButtons = new UIButton[1];

    @Override
    public void initialise(SolApplication solApplication, InventoryScreen inventoryScreen) {
        UIWarnButton buyButton = new UIWarnButton();
        buyButton.setText("Buy");
        buyButton.setKey(GDXInputUtil.GDXToNuiKey(solApplication.getOptions().getKeyBuyItem()));
        buyButton.subscribe(button -> {
            SolGame game = solApplication.getGame();
            Hero hero = game.getHero();
            TalkScreen talkScreen = game.getScreens().talkScreen;
            SolShip target = talkScreen.getTarget();
            SolItem selectedItem = inventoryScreen.getSelectedItem();
            if (selectedItem == null) {
                return;
            }

            target.getTradeContainer().getItems().remove(selectedItem);
            hero.getItemContainer().add(selectedItem);
            hero.setMoney(hero.getMoney() - selectedItem.getPrice());
            solApplication.getGame().getFactionMan().reportEvent(hero.getFaction(), target.getFaction(), DefaultReputationEvent.BOUGHT_ITEM);

            inventoryScreen.updateItemRows();
        });
        actionButtons[0] = buyButton;
    }

    @Override
    public ItemContainer getItems(SolGame game) {
        return game.getScreens().talkScreen.getTarget().getTradeContainer().getItems();
    }

    @Override
    public String getHeader() {
        return "Buy:";
    }

    @Override
    public UIButton[] getActionButtons() {
        return actionButtons;
    }

    public UIWarnButton getBuyControl() {
        return (UIWarnButton) actionButtons[0];
    }

    @Override
    public void update(SolApplication solApplication, InventoryScreen inventoryScreen) {
        SolGame game = solApplication.getGame();
        Hero hero = game.getHero();
        TalkScreen talkScreen = game.getScreens().talkScreen;
        if (talkScreen.isTargetFar(hero)) {
            solApplication.getNuiManager().removeScreen(inventoryScreen);
            return;
        }
        SolItem selItem = inventoryScreen.getSelectedItem();
        boolean enabled = selItem != null && hero.getMoney() >= selItem.getPrice() && hero.getItemContainer().canAdd(selItem);
        UIButton buyButton = actionButtons[0];
        buyButton.setText(enabled ? "Buy" : "---");
        buyButton.setEnabled(enabled);
    }
}
