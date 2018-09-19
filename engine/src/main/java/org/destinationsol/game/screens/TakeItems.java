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

public class TakeItems extends InventoryOperationsScreen {
    public final SolUiControl takeControl;
    private SolShip target;

    TakeItems(InventoryScreen inventoryScreen, GameOptions gameOptions) {
        takeControl = new SolUiControl(inventoryScreen.itemCtrl(0), true, gameOptions.getKeyShoot());
        takeControl.setDisplayName("Take");
        controls.add(takeControl);
    }

    @Override
    public ItemContainer getItems(SolGame game) {
        return target.getItemContainer();
    }

    @Override
    public String getHeader() {
        return "Take:";
    }

    @Override
    public void updateCustom(SolApplication solApplication, SolInputManager.InputPointer[] inputPointers, boolean clickedOutside) {
        SolGame game = solApplication.getGame();
        InventoryScreen is = game.getScreens().inventoryScreen;
        Hero hero = game.getHero();
        
        SolItem selItem = is.getSelectedItem();
        boolean enabled = selItem != null && hero.getItemContainer().canAdd(selItem);
        takeControl.setDisplayName(enabled ? "Take" : "---");
        takeControl.setEnabled(enabled);
        
        if (!enabled) {
            return;
        }
        
        if (takeControl.isJustOff()) {
            target.getItemContainer().remove(selItem);
            hero.getItemContainer().add(selItem);
        }
    }
    
    /**
     * Sets the mercenary to take items from
     * @param solship The mercenary being interacted with
     */
    public void setTarget(SolShip solship) {
        this.target = solship;
    }
}
