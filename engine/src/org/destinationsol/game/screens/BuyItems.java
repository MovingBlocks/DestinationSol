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

public class BuyItems implements InventoryOperations {
    public final SolUiControl buyControl;
    private final ArrayList<SolUiControl> controls = new ArrayList<>();

    BuyItems(InventoryScreen inventoryScreen, GameOptions gameOptions) {
        buyControl = new SolUiControl(inventoryScreen.itemCtrl(0), true, gameOptions.getKeyBuyItem());
        buyControl.setDisplayName("Buy");
        controls.add(buyControl);
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
    public List<SolUiControl> getControls() {
        return controls;
    }

    @Override
    public void updateCustom(SolApplication solApplication, SolInputManager.InputPointer[] inputPointers, boolean clickedOutside) {
        SolGame game = solApplication.getGame();
        InventoryScreen is = game.getScreens().inventoryScreen;
        SolShip hero = game.getHero();
        TalkScreen talkScreen = game.getScreens().talkScreen;
        SolShip target = talkScreen.getTarget();
        if (talkScreen.isTargetFar(hero)) {
            solApplication.getInputMan().setScreen(solApplication, game.getScreens().mainScreen);
            return;
        }
        SolItem selItem = is.getSelectedItem();
        boolean enabled = selItem != null && hero.getMoney() >= selItem.getPrice() && hero.getItemContainer().canAdd(selItem);
        buyControl.setDisplayName(enabled ? "Buy" : "---");
        buyControl.setEnabled(enabled);
        if (!enabled) {
            return;
        }
        if (buyControl.isJustOff()) {
            target.getTradeContainer().getItems().remove(selItem);
            hero.getItemContainer().add(selItem);
            hero.setMoney(hero.getMoney() - selItem.getPrice());
        }
    }
}
