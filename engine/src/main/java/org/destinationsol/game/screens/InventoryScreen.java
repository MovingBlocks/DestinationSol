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
import org.destinationsol.ui.SolInputManager;
import org.destinationsol.ui.SolUiBaseScreen;
import org.destinationsol.ui.responsiveUi.UiActionButton;
import org.destinationsol.ui.responsiveUi.UiItemList;
import org.destinationsol.ui.responsiveUi.UiRelativeLayout;
import org.destinationsol.ui.responsiveUi.UiSpacerElement;
import org.destinationsol.ui.responsiveUi.UiTextBox;
import org.destinationsol.ui.responsiveUi.UiVerticalListLayout;

import java.util.HashMap;

import static org.destinationsol.ui.UiDrawer.UI_POSITION_CENTER;

public class InventoryScreen extends SolUiBaseScreen {
    public final HashMap<Class<? extends InventoryOperationsScreen>, InventoryOperationsScreen> inventoryOperationsMap;
    private static final int DESCRIPTION_BOX_LINES_LENGTH = 24;
    private static final int DESCRIPTION_BOX_LINES_HEIGHT = 12;
    private UiTextBox descriptionTextBox;

    private InventoryOperationsScreen myOperation;
    private final UiItemList itemList;
    private UiActionButton interactButton;
    private UiTextBox interactText;
    private UiVerticalListLayout verticalListLayout;

    public InventoryScreen() {
        verticalListLayout = new UiVerticalListLayout();
        rootUiElement = new UiRelativeLayout().addElement(verticalListLayout, UI_POSITION_CENTER, 0, 0);
        itemList = new UiItemList();
        verticalListLayout.addElement(itemList);
        inventoryOperationsMap = new HashMap<>();
        inventoryOperationsMap.put(ShowInventory.class, new ShowInventory());
    }

    @Override
    public boolean isCursorOnBackground(SolInputManager.InputPointer inputPointer) {
        return rootUiElement.getScreenArea().contains(inputPointer.x, inputPointer.y);
    }

    @Override
    public void updateCustom(SolApplication solApplication, SolInputManager.InputPointer[] inputPointers, boolean clickedOutside) {
        SolItem selectedItem = itemList.getSelectedItem();
        if (myOperation != null) {
            myOperation.update(this, solApplication);
        }
        if (selectedItem != null) {
            descriptionTextBox.setText(itemList.getSelectedItem().getDescription());
        } else {
            descriptionTextBox.setText("No item selected");
        }
    }

    @Override
    public void onAdd(SolApplication solApplication) {
        itemList.setItemContainer(myOperation.getItems(solApplication.getGame()));
        if (descriptionTextBox == null) {
            descriptionTextBox = new UiTextBox();
            interactText = new UiTextBox().setText("Drop");
            interactButton = new UiActionButton().addElement(interactText);
            SolItem selectedItem = itemList.getSelectedItem();
            if (selectedItem != null) {
                descriptionTextBox.setText(itemList.getSelectedItem().getDescription());
            } else {
                descriptionTextBox.setText("No item selected");
            }
            descriptionTextBox.setPosition(0, 0);
            StringBuilder textPlaceholderBuilder = new StringBuilder(DESCRIPTION_BOX_LINES_LENGTH * DESCRIPTION_BOX_LINES_HEIGHT);
            for (int lineNo = 0; lineNo < DESCRIPTION_BOX_LINES_HEIGHT; lineNo++) {
                for (int characterNo = 0; characterNo < DESCRIPTION_BOX_LINES_LENGTH; characterNo++) {
                    textPlaceholderBuilder.append('-');
                }
                textPlaceholderBuilder.append("\n");
            }
            final UiSpacerElement descriptionArea = new UiSpacerElement()
                    .setFromElement(new UiTextBox().setText(textPlaceholderBuilder.toString()))
                    .setContainedElement(descriptionTextBox);
            verticalListLayout.addElement(descriptionArea).addElement(interactButton);
        } else {
            descriptionTextBox.setText(itemList.getSelectedItem().getDescription());
        }

        myOperation.onAdd(this);
    }

    @Override
    public void blurCustom(SolApplication solApplication) {
        SolGame game = solApplication.getGame();
        ItemContainer items = myOperation.getItems(game);
        if (items != null) {
            items.markAllAsSeen();
        }
    }

    public void refresh() {
        itemList.recalculate();
    }

    public void setOperations(InventoryOperationsScreen operations) {
        myOperation = operations;
    }

    public UiActionButton getInteractButton() {
        return interactButton;
    }

    public void setInteractText(String text) {
        interactText.setText(text);
    }

    public SolItem getSelectedItem() {
        return itemList.getSelectedItem();
    }
}
