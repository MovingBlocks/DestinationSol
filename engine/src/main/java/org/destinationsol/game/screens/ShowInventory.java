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

public class ShowInventory extends InventoryOperationsScreen {
    private SolShip target;

    @Override
    public void onAdd(InventoryScreen inventoryScreen) {
        inventoryScreen.getInteractButton().setAction(uiElement -> {
            Hero hero = SolApplication.getInstance().getGame().getHero();
            SolItem item = inventoryScreen.getSelectedItem();
            hero.getItemContainer().remove(item);
            inventoryScreen.refresh();
        });
    }

    @Override
    public void update(InventoryScreen inventoryScreen, SolApplication solApplication) {
        inventoryScreen.setInteractText("Drop");
    }

    public SolShip getTarget() {
        return target;
    }

    public void setTarget(SolShip target) {
        this.target = target;
    }

    @Override
    ItemContainer getItems(SolGame game) {
        return target == null ? null : target.getItemContainer();
    }

    @Override
    String getHeader() {
        return "Items: ";
    }
}
