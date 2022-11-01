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
import org.destinationsol.game.item.ItemContainer;
import org.destinationsol.game.item.SolItem;
import org.destinationsol.game.ship.SolShip;
import org.destinationsol.ui.nui.screens.InventoryScreen;
import org.destinationsol.ui.nui.widgets.KeyActivatedButton;
import org.terasology.nui.backends.libgdx.GDXInputUtil;
import org.terasology.nui.widgets.UIButton;

/**
 * This screen allows the hero to take items acquired by their hired mercenaries.
 * The items taken will be transferred directly into the hero's inventory.
 */
public class TakeItems extends InventoryOperationsScreen {
    public final UIButton[] actionButtons = new UIButton[1];
    private SolShip target;

    public TakeItems() {
    }

    @Override
    public void initialise(SolApplication solApplication, InventoryScreen inventoryScreen) {
        KeyActivatedButton takeButton = new KeyActivatedButton();
        takeButton.setText("Take");
        takeButton.setKey(GDXInputUtil.GDXToNuiKey(solApplication.getOptions().getKeyShoot()));
        takeButton.subscribe(button -> {
            SolItem selectedItem = inventoryScreen.getSelectedItem();
            Hero hero = solApplication.getGame().getHero();

            target.getItemContainer().remove(selectedItem);
            hero.getItemContainer().add(selectedItem);
            inventoryScreen.updateItemRows();
        });
        actionButtons[0] = takeButton;
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
    public UIButton[] getActionButtons() {
        return actionButtons;
    }

    @Override
    public void update(SolApplication solApplication, InventoryScreen inventoryScreen) {
        SolGame game = solApplication.getGame();
        Hero hero = game.getHero();

        UIButton takeButton = actionButtons[0];

        SolItem selectedItem = inventoryScreen.getSelectedItem();
        boolean enabled = selectedItem != null && hero.getItemContainer().canAdd(selectedItem);
        takeButton.setText(enabled ? "Take" : "---");
        takeButton.setEnabled(enabled);
    }
    
    /**
     * Sets the mercenary to take items from
     * @param solship The mercenary being interacted with
     */
    public void setTarget(SolShip solship) {
        this.target = solship;
    }
}
