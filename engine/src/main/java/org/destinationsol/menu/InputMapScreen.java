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
package org.destinationsol.menu;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

import java.util.List;

import org.destinationsol.Const;
import org.destinationsol.GameOptions;
import org.destinationsol.SolApplication;
import org.destinationsol.assets.Assets;
import org.destinationsol.common.SolColor;
import org.destinationsol.ui.DisplayDimensions;
import org.destinationsol.ui.FontSize;
import org.destinationsol.ui.SolInputManager;
import org.destinationsol.ui.SolUiBaseScreen;
import org.destinationsol.ui.UiDrawer;
import org.destinationsol.ui.responsiveUi.UiHeadlessButton;
import org.destinationsol.ui.responsiveUi.UiRelativeLayout;
import org.destinationsol.ui.responsiveUi.UiTextButton;
import org.destinationsol.ui.responsiveUi.UiVerticalListLayout;

import static org.destinationsol.ui.UiDrawer.UI_POSITION_BOTTOM;
import static org.destinationsol.ui.UiDrawer.UI_POSITION_TOP;
import static org.destinationsol.ui.UiDrawer.UI_POSITION_LEFT;
import static org.destinationsol.ui.UiDrawer.UI_POSITION_RIGHT;
import static org.destinationsol.ui.responsiveUi.UiTextButton.DEFAULT_BUTTON_PADDING;

/**
 * <h1>Config Screen to Change Input Mapping</h1>
 * The input mapping screen is based on the inventory screen used within the game.
 */
public class InputMapScreen extends SolUiBaseScreen {
    private static final String BUTTON_STRING_PADDING = "          ";
    final InputMapKeyboardScreen inputMapKeyboardScreen;
    final InputMapControllerScreen inputMapControllerScreen;
    final InputMapMixedScreen inputMapMixedScreen;
    private final TextureAtlas.AtlasRegion backgroundTexture;

    private DisplayDimensions displayDimensions;

    private InputMapOperations operations;
    private int page;
    private int selectedIndex;
    private UiVerticalListLayout itemsLayout;
    private boolean waitingForKey;
    private UiTextButton nextButton;
    private UiTextButton lastButton;

    InputMapScreen() {
        displayDimensions = SolApplication.displayDimensions;

        // Create the input screens
        inputMapKeyboardScreen = new InputMapKeyboardScreen();
        inputMapControllerScreen = new InputMapControllerScreen();
        inputMapMixedScreen = new InputMapMixedScreen();

        backgroundTexture = Assets.getAtlasRegion("engine:mainMenuBg", Texture.TextureFilter.Linear);
        rootUiElement = new UiRelativeLayout();
    }

    @Override
    public void updateCustom(SolApplication cmp, SolInputManager.InputPointer[] inputPointers, boolean clickedOutside) {
        GameOptions gameOptions = cmp.getOptions();

        // Selected Item Control
        List<InputConfigItem> itemsList = operations.getItems(gameOptions);
        int groupCount = itemsList.size();
        int pageCount = groupCount / Const.ITEM_GROUPS_PER_PAGE;

        // Select the item the mouse clicked
        int offset = page * Const.ITEM_GROUPS_PER_PAGE;

        if (pageCount == 0 || pageCount * Const.ITEM_GROUPS_PER_PAGE < groupCount) {
            pageCount += 1;
        }
        if (page < 0) {
            page = 0;
        }
        if (page >= pageCount) {
            page = pageCount - 1;
        }

        // Ensure Selected item is on page
        if (selectedIndex < offset || selectedIndex >= offset + Const.ITEM_GROUPS_PER_PAGE) {
            selectedIndex = offset;
        }

        if (waitingForKey && !operations.isEnterNewKey()) {
            populateItemScreen();
            waitingForKey = false;
        }

        // Inform the input screen which item is selected
        operations.setSelectedIndex(selectedIndex);
    }

    public void drawBackground(UiDrawer uiDrawer, SolApplication solApplication) {
        uiDrawer.draw(backgroundTexture, displayDimensions.getRatio(), 1, displayDimensions.getRatio() / 2,
                0.5f, displayDimensions.getRatio() / 2, 0.5f, 0, SolColor.WHITE);
    }

    @Override
    public void draw(UiDrawer uiDrawer, SolApplication solApplication) {
        // Draw the header title
        uiDrawer.drawString(operations.getHeader(),
                displayDimensions.getFloatWidthForPixelWidth(uiDrawer.getStringLength(operations.getHeader(), FontSize.WINDOW)) / 2,
                0.01f, FontSize.WINDOW, false, SolColor.WHITE);

        // Draw the detail text
        uiDrawer.drawString(operations.getDisplayDetail(),
                displayDimensions.getFloatWidthForPixelWidth(uiDrawer.getStringLength(operations.getHeader(), FontSize.WINDOW)) / 2,
                0.25f, FontSize.WINDOW, false, SolColor.WHITE);
    }

    @Override
    public void onAdd(SolApplication solApplication) {
        rootUiElement = new UiRelativeLayout();

        // Add any extra screen information as required by the input screens. E.g. buttons
        if (operations != null) {
            solApplication.getInputManager().addScreen(operations);
            solApplication.getInputManager().update(solApplication);
            GameOptions gameOptions = solApplication.getOptions();

            UiVerticalListLayout screenLayout = new UiVerticalListLayout();
            UiRelativeLayout nextButtonsLayout = new UiRelativeLayout();
            nextButton = new UiTextButton()
                    .setDisplayName(">")
                    .setOnReleaseAction(uiElement -> {
                        page++;
                        updateNavigationButtons();
                        populateItemScreen();
                    })
                    .setHeight(30)
                    .setWidth(40);
            lastButton = new UiTextButton()
                    .setDisplayName("<")
                    .setOnReleaseAction(uiElement -> {
                        page--;
                        updateNavigationButtons();
                        populateItemScreen();
                    })
                    .setHeight(30)
                    .setWidth(40);
            lastButton.setEnabled(page > 0);

            if (itemsLayout == null) {
                itemsLayout = new UiVerticalListLayout();
            } else {
                itemsLayout.clearElements();
            }
            populateItemScreen();
            screenLayout.addElement(itemsLayout);

            screenLayout.addElement(new UiHeadlessButton());

            UiTextButton defaultsButton = new UiTextButton()
                    .setDisplayName("Defaults")
                    .setHeight(30)
                    .setOnReleaseAction(uiElement -> {
                        operations.resetToDefaults(gameOptions);
                        populateItemScreen();
                    });
            screenLayout.addElement(defaultsButton);

            UiTextButton saveButton = new UiTextButton()
                    .setDisplayName("Save")
                    .setHeight(30)
                    .setOnReleaseAction(uiElement -> {
                        operations.save(gameOptions);
                        SolApplication.changeScreen(SolApplication.getMenuScreens().optionsScreen);
                    });
            screenLayout.addElement(saveButton);

            UiTextButton cancelButton = new UiTextButton()
                    .setDisplayName("Cancel")
                    .setOnReleaseAction(uiElement -> {
                        if (waitingForKey) {
                            operations.setEnterNewKey(false);
                            return;
                        }
                        SolApplication.changeScreen(SolApplication.getMenuScreens().optionsScreen);
                    })
                    .setHeight(30);
            screenLayout.addElement(cancelButton);

            nextButtonsLayout.addElement(nextButton, UI_POSITION_RIGHT, -nextButton.getWidth(), screenLayout.getY());
            nextButtonsLayout.addElement(lastButton, UI_POSITION_LEFT, lastButton.getWidth(), screenLayout.getY());

            ((UiRelativeLayout) rootUiElement).addElement(nextButtonsLayout, UI_POSITION_TOP, 0, -nextButtonsLayout.getHeight() / 2 - DEFAULT_BUTTON_PADDING * 2)
                    .addElement(screenLayout, UI_POSITION_BOTTOM, 0, -screenLayout.getHeight() / 2 - DEFAULT_BUTTON_PADDING);
        }

        page = 0;
        selectedIndex = 0;
    }

    void setOperations(InputMapOperations operations) {
        this.operations = operations;
    }

    private void populateItemScreen() {
        itemsLayout.clearElements();
        List<InputConfigItem> items = operations.getItems(SolApplication.getInstance().getOptions());
        int startIndex = (page * Const.ITEM_GROUPS_PER_PAGE);
        for (int i = startIndex; i < startIndex + Const.ITEM_GROUPS_PER_PAGE; i++) {
            if (items.size() <= i) {
                break;
            }
            final int index = i;
            UiTextButton itemButton = new UiTextButton()
                    .setDisplayName(BUTTON_STRING_PADDING + items.get(i).getDisplayName() + ": " +
                            items.get(i).getInputKey() + BUTTON_STRING_PADDING)
                    .setHeight(40)
                    .setOnReleaseAction(uiElement -> {
                        selectedIndex = index;
                        operations.setSelectedIndex(index);
                        operations.setEnterNewKey(true);
                        waitingForKey = true;
                    });
            itemButton.blur();
            itemsLayout.addElement(itemButton);
        }
        itemsLayout.recalculate();
    }

    private void updateNavigationButtons() {
        lastButton.setEnabled(page > 0);
        nextButton.setEnabled(page < operations.getItems(SolApplication.getInstance().getOptions()).size() / Const.ITEM_GROUPS_PER_PAGE);
    }
}
