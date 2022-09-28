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
import org.destinationsol.game.SolGame;
import org.destinationsol.game.item.ItemContainer;
import org.destinationsol.game.item.SolItem;
import org.destinationsol.ui.SolUiBaseScreen;
import org.destinationsol.ui.nui.screens.InventoryScreen;
import org.terasology.nui.widgets.UIButton;

/**
 * This is the base class for all inventory operations.
 */
public abstract class InventoryOperationsScreen extends SolUiBaseScreen {
    public abstract ItemContainer getItems(SolGame game);

    public boolean isUsing(SolGame game, SolItem item) {
        return false;
    }

    public float getPriceMul() {
        return 1;
    }

    public abstract String getHeader();

    public abstract UIButton[] getActionButtons();

    public void initialise(SolApplication solApplication, InventoryScreen inventoryScreen) {
    }

    public void update(SolApplication solApplication, InventoryScreen inventoryScreen) {
    }
}
