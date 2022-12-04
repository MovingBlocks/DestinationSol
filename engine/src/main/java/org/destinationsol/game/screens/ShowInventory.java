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
import org.destinationsol.game.SolGame;
import org.destinationsol.game.item.ItemContainer;
import org.destinationsol.game.item.SolItem;
import org.destinationsol.game.ship.SolShip;
import org.destinationsol.ui.nui.screens.InventoryScreen;
import org.destinationsol.ui.nui.widgets.UIWarnButton;
import org.terasology.nui.backends.libgdx.GDXInputUtil;
import org.terasology.nui.widgets.UIButton;

import java.util.List;

/**
 * This screen shows the current inventory of the targeted ship.
 * You can also equip and de-equip items here, as well as force the ship to drop those items out into space.
 */
public class ShowInventory extends InventoryOperationsScreen {
    private final UIButton[] actionButtons = new UIButton[3];

    private SolShip target;

    public ShowInventory() {
    }

    @Override
    public void initialise(SolApplication solApplication, InventoryScreen inventoryScreen) {
        GameOptions gameOptions = solApplication.getOptions();

        UIWarnButton equip1Button = new UIWarnButton();
        equip1Button.setText("Eq");
        equip1Button.setKey(GDXInputUtil.GDXToNuiKey(gameOptions.getKeyEquip()));
        equip1Button.subscribe(button -> {
            SolGame game = solApplication.getGame();
            SolItem selItem = inventoryScreen.getSelectedItem();
            if (selItem == null) {
                button.setEnabled(false);
                return;
            }

            if (target.maybeUnequip(game, selItem, false, false)) {
                target.maybeUnequip(game, selItem, false, true);
            } else {
                target.maybeEquip(game, selItem, false, true);
            }
            inventoryScreen.updateItemRows();
        });

        UIWarnButton equip2Button = new UIWarnButton();
        equip2Button.setText("Eq2");
        equip2Button.setKey(GDXInputUtil.GDXToNuiKey(gameOptions.getKeyEquip2()));
        equip2Button.subscribe(button -> {
            SolGame game = solApplication.getGame();
            SolItem selItem = inventoryScreen.getSelectedItem();
            if (target.maybeUnequip(game, selItem, true, false)) {
                target.maybeUnequip(game, selItem, true, true);
            } else {
                target.maybeEquip(game, selItem, true, true);
            }
            inventoryScreen.updateItemRows();
        });

        UIWarnButton dropButton = new UIWarnButton();
        dropButton.setText("Drop");
        dropButton.setKey(GDXInputUtil.GDXToNuiKey(gameOptions.getKeyDrop()));
        dropButton.subscribe(button -> {
            SolItem selItem = inventoryScreen.getSelectedItem();
            ItemContainer itemContainer = target.getItemContainer();
            List<SolItem> newSelection = itemContainer.getSelectionAfterRemove(inventoryScreen.getSelected());
            target.dropItem(solApplication.getGame(), selItem);
            inventoryScreen.updateItemRows();

            inventoryScreen.setSelected(newSelection);
        });

        actionButtons[0] = equip1Button;
        actionButtons[1] = equip2Button;
        actionButtons[2] = dropButton;
    }

    @Override
    public ItemContainer getItems(SolGame game) {
        return target == null ? null : target.getItemContainer();
    }

    @Override
    public boolean isUsing(SolGame game, SolItem item) {
        return target != null && target.maybeUnequip(game, item, false);
    }

    @Override
    public float getPriceMul() {
        return -1;
    }

    @Override
    public String getHeader() {
        return "Items:";
    }

    @Override
    public UIButton[] getActionButtons() {
        return actionButtons;
    }

    public UIWarnButton getEq1Control() {
        return (UIWarnButton) actionButtons[0];
    }

    public UIWarnButton getDropControl() {
        return (UIWarnButton) actionButtons[2];
    }

    @Override
    public void update(SolApplication solApplication, InventoryScreen inventoryScreen) {
        SolGame game = solApplication.getGame();
        SolItem selItem = inventoryScreen.getSelectedItem();

        UIButton equip1Button = actionButtons[0];
        UIButton equip2Button = actionButtons[1];
        UIButton dropButton = actionButtons[2];

        equip1Button.setText("---");
        equip1Button.setEnabled(false);
        equip2Button.setText("---");
        equip2Button.setEnabled(false);
        dropButton.setEnabled(false);

        if (selItem == null || target == null) {
            return;
        }

        dropButton.setEnabled(true);

        boolean equipped1 = target.maybeUnequip(game, selItem, false, false);
        boolean canEquip1 = target.maybeEquip(game, selItem, false, false);
        boolean equipped2 = target.maybeUnequip(game, selItem, true, false);
        boolean canEquip2 = target.maybeEquip(game, selItem, true, false);

        if (equipped1 || canEquip1) {
            equip1Button.setText(equipped1 ? "Unequip" : "Equip");
            equip1Button.setEnabled(true);
        }
        if (equipped2 || canEquip2) {
            equip2Button.setText(equipped2 ? "Unequip" : "Set Gun 2");
            equip2Button.setEnabled(true);
        }
    }

    /**
     * Sets the ship whose inventory we're viewing.
     * @param solship The mercenary being interacted with
     */
    public void setTarget(SolShip solship) {
        this.target = solship;
    }
    
    /**
     * Gets the ship whose inventory we're viewing.
     */
    public SolShip getTarget() {
        return this.target;
    }
}
