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

public class ShowInventory implements InventoryOperations {
    private final List<SolUiControl> controls = new ArrayList<>();
    public final SolUiControl eq1Control;
    private final SolUiControl eq2Control;
    public final SolUiControl dropControl;

    ShowInventory(InventoryScreen inventoryScreen, GameOptions gameOptions) {
        eq1Control = new SolUiControl(inventoryScreen.itemCtrl(0), true, gameOptions.getKeyEquip());
        eq1Control.setDisplayName("Eq");
        controls.add(eq1Control);

        eq2Control = new SolUiControl(inventoryScreen.itemCtrl(1), true, gameOptions.getKeyEquip2());
        eq2Control.setDisplayName("Eq2");
        controls.add(eq2Control);

        dropControl = new SolUiControl(inventoryScreen.itemCtrl(2), true, gameOptions.getKeyDrop());
        dropControl.setDisplayName("Drop");
        controls.add(dropControl);
    }

    @Override
    public List<SolUiControl> getControls() {
        return controls;
    }

    @Override
    public void updateCustom(SolApplication solApplication, SolInputManager.InputPointer[] inputPointers, boolean clickedOutside) {
        SolGame g = solApplication.getGame();
        InventoryScreen is = g.getScreens().inventoryScreen;
        SolItem selItem = is.getSelectedItem();
        SolShip hero = g.getHero();

        eq1Control.setDisplayName("---");
        eq1Control.setEnabled(false);
        eq2Control.setDisplayName("---");
        eq2Control.setEnabled(false);
        dropControl.setEnabled(false);

        if (selItem == null || hero == null) {
            return;
        }

        dropControl.setEnabled(true);
        if (dropControl.isJustOff()) {
            ItemContainer ic = hero.getItemContainer();
            is.setSelected(ic.getSelectionAfterRemove(is.getSelected()));
            hero.dropItem(solApplication.getGame(), selItem);
            return;
        }

        Boolean equipped1 = hero.maybeUnequip(g, selItem, false, false);
        boolean canEquip1 = hero.maybeEquip(g, selItem, false, false);
        Boolean equipped2 = hero.maybeUnequip(g, selItem, true, false);
        boolean canEquip2 = hero.maybeEquip(g, selItem, true, false);

        if (equipped1 || canEquip1) {
            eq1Control.setDisplayName(equipped1 ? "Unequip" : "Equip");
            eq1Control.setEnabled(true);
        }
        if (equipped2 || canEquip2) {
            eq2Control.setDisplayName(equipped2 ? "Unequip" : "Set Gun 2");
            eq2Control.setEnabled(true);
        }
        if (eq1Control.isJustOff()) {
            if (equipped1) {
                hero.maybeUnequip(g, selItem, false, true);
            } else {
                hero.maybeEquip(g, selItem, false, true);
            }
        }
        if (eq2Control.isJustOff()) {
            if (equipped2) {
                hero.maybeUnequip(g, selItem, true, true);
            } else {
                hero.maybeEquip(g, selItem, true, true);
            }
        }
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
        return -1;
    }

    @Override
    public String getHeader() {
        return "Items:";
    }
}
