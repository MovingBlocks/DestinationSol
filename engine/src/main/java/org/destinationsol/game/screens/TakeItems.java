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

import java.util.ArrayList;
import java.util.List;

import org.destinationsol.GameOptions;
import org.destinationsol.SolApplication;
import org.destinationsol.game.SolGame;
import org.destinationsol.game.item.ItemContainer;
import org.destinationsol.game.item.SolItem;
import org.destinationsol.game.ship.FarShip;
import org.destinationsol.game.ship.SolShip;
import org.destinationsol.ui.SolInputManager;
import org.destinationsol.ui.SolUiControl;

public class TakeItems implements InventoryOperations {
    public final SolUiControl takeControl;
    private final ArrayList<SolUiControl> controls = new ArrayList<>();
    private FarShip target;

    TakeItems(InventoryScreen inventoryScreen, GameOptions gameOptions) {
        takeControl = new SolUiControl(inventoryScreen.itemCtrl(0), true, gameOptions.getKeyBuyItem());
        takeControl.setDisplayName("Take");
        controls.add(takeControl);
    }

    @Override
    public ItemContainer getItems(SolGame game) {
        return target.getIc();
    }

    @Override
    public String getHeader() {
        return "Take:";
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
        takeControl.setDisplayName(enabled ? "Buy" : "---");
        takeControl.setEnabled(enabled);
        if (!enabled) {
            return;
        }
        if (takeControl.isJustOff()) {
            target.getTradeContainer().getItems().remove(selItem);
            hero.getItemContainer().add(selItem);
            hero.setMoney(hero.getMoney() - selItem.getPrice());
        }
    }
    
    public void setFarShip(FarShip farship) {
        this.target = farship;
    }
}
