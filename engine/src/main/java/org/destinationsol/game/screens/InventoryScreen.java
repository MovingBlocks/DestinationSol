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
import org.destinationsol.ui.SolInputManager;
import org.destinationsol.ui.SolUiBaseScreen;
import org.destinationsol.ui.responsiveUi.UiActionButton;
import org.destinationsol.ui.responsiveUi.UiHorizontalListLayout;
import org.destinationsol.ui.responsiveUi.UiItemList;
import org.destinationsol.ui.responsiveUi.UiSpacerElement;
import org.destinationsol.ui.responsiveUi.UiTextBox;
import org.destinationsol.ui.responsiveUi.UiVerticalListLayout;

import java.util.HashMap;

public class InventoryScreen extends SolUiBaseScreen {
    public final HashMap<Class<? extends InventoryOperationsScreen>, InventoryOperationsScreen> inventoryOperationsMap;
    private final UiTextBox descriptionTextBox;

    private InventoryOperationsScreen myOperation;
    private final UiItemList itemList;

    public InventoryScreen() {
        rootUiElement = new UiVerticalListLayout().setPosition(500, 500);
        itemList = new UiItemList();
        ((UiVerticalListLayout) rootUiElement).addElement(itemList);
        descriptionTextBox = new UiTextBox();
        final UiSpacerElement descriptionArea = new UiSpacerElement()
                .setFromElement(new UiTextBox().setText("-------------------------------\n\n\n\n\n\n\n\n\n\n\n\n-"))
                .setContainedElement(descriptionTextBox);
        ((UiVerticalListLayout) rootUiElement).addElement(new UiHorizontalListLayout().addElement(descriptionArea)
        .addElement(new UiVerticalListLayout().addElement(new UiActionButton().addElement(new UiTextBox().setText("Drop")).setAction(uiElement -> {

        }))));
        inventoryOperationsMap = new HashMap<>();
        inventoryOperationsMap.put(ShowInventory.class, new ShowInventory());
    }

    @Override
    public boolean isCursorOnBackground(SolInputManager.InputPointer inputPointer) {
        return rootUiElement.getScreenArea().contains(inputPointer.x, inputPointer.y);
    }

    @Override
    public void updateCustom(SolApplication solApplication, SolInputManager.InputPointer[] inputPointers, boolean clickedOutside) {
        descriptionTextBox.setText(itemList.getSelectedItem().getDescription());
    }

    @Override
    public void onAdd(SolApplication solApplication) {
        itemList.setItemContainer(myOperation.getItems(solApplication.getGame()));
        descriptionTextBox.setText(itemList.getSelectedItem().getDescription());
    }

    @Override
    public void blurCustom(SolApplication solApplication) {
        SolGame game = solApplication.getGame();
        ItemContainer items = myOperation.getItems(game);
        if (items != null) {
            items.markAllAsSeen();
        }
    }

    public void setOperations(InventoryOperationsScreen operations) {
        myOperation = operations;
    }
}
