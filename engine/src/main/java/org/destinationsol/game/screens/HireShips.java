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
import org.destinationsol.game.item.MercItem;
import org.destinationsol.game.item.SolItem;
import org.destinationsol.mercenary.MercenaryUtils;
import org.destinationsol.ui.SolInputManager;
import org.destinationsol.ui.SolUiControl;

import java.util.ArrayList;
import java.util.List;

public class HireShips implements InventoryOperations {
    private final ArrayList<SolUiControl> controls = new ArrayList<>();
    private final SolUiControl hireControl;

    HireShips(InventoryScreen inventoryScreen, GameOptions gameOptions) {
        hireControl = new SolUiControl(inventoryScreen.itemCtrl(0), true, gameOptions.getKeyHireShip());
        hireControl.setDisplayName("Hire");
        controls.add(hireControl);
    }

    @Override
    public ItemContainer getItems(SolGame game) {
        return game.getScreens().talkScreen.getTarget().getTradeContainer().getMercs();
    }

    @Override
    public String getHeader() {
        return "Mercenaries:";
    }

    @Override
    public List<SolUiControl> getControls() {
        return controls;
    }

    @Override
    public void updateCustom(SolApplication solApplication, SolInputManager.InputPointer[] inputPointers, boolean clickedOutside) {
        SolGame game = solApplication.getGame();
        InventoryScreen is = game.getScreens().inventoryScreen;
        Hero hero = game.getHero();
        TalkScreen talkScreen = game.getScreens().talkScreen;
        if (talkScreen.isTargetFar(hero)) {
            solApplication.getInputManager().setScreen(solApplication, game.getScreens().mainGameScreen);
            return;
        }
        SolItem selItem = is.getSelectedItem();
        boolean enabled = selItem != null && hero.getMoney() >= selItem.getPrice();
        hireControl.setDisplayName(enabled ? "Hire" : "---");
        hireControl.setEnabled(enabled);
        if (!enabled) {
            return;
        }
        if (hireControl.isJustOff()) {
            boolean hired = MercenaryUtils.createMerc(game, hero, (MercItem) selItem);
            if (hired) {
                hero.setMoney(hero.getMoney() - selItem.getPrice());
            }
        }
    }
}
