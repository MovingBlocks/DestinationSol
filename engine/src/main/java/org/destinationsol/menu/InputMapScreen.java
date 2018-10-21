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
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
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

/**
 * <h1>Config Screen to Change Input Mapping</h1>
 * The input mapping screen is based on the inventory screen used within the game.
 */
public class InputMapScreen extends SolUiBaseScreen {
    private static final float IMG_COL_PERC = .1f;
    private static final float EQUI_COL_PERC = .1f;
    private static final float PRICE_COL_PERC = .1f;
    private static final float AMT_COL_PERC = .1f;
    private static final float SMALL_GAP = .004f;
    private static final float HEADER_TEXT_OFFSET = .005f;
    private static final int BUTTON_ROWS = 4;
    final InputMapKeyboardScreen inputMapKeyboardScreen;
    final InputMapControllerScreen inputMapControllerScreen;
    final InputMapMixedScreen inputMapMixedScreen;
    private final TextureAtlas.AtlasRegion backgroundTexture;
//    private final SolUiControl previousControl;
//    private final SolUiControl nextControl;
////    private final SolUiControl cancelControl;
//    private final SolUiControl saveControl;
//    private final SolUiControl defaultsControl;
//    private final SolUiControl upControl;
//    private final SolUiControl downControl;

    private DisplayDimensions displayDimensions;

    private final Vector2 listHeaderPos;
    private final Rectangle listArea;
    private final Rectangle detailsArea;
    private final Rectangle itemControlsArea;
    private InputMapOperations operations;
    private int page;
    private int selectedIndex;

    InputMapScreen(GameOptions gameOptions) {
        displayDimensions = SolApplication.displayDimensions;

        float contentW = .8f;
        float col0 = displayDimensions.getRatio() / 2 - contentW / 2;
        float row = 0.2f;
        float bigGap = SMALL_GAP * 6;
        float headerH = .03f;

        // List header & controls
        listHeaderPos = new Vector2(col0 + HEADER_TEXT_OFFSET, row + HEADER_TEXT_OFFSET); // offset hack
        float listCtrlW = contentW * .15f;
        Rectangle nextArea = new Rectangle(col0 + contentW - listCtrlW, row, listCtrlW, headerH);
//        nextControl = new SolUiControl(nextArea, true, gameOptions.getKeyRight());
//        nextControl.setDisplayName(">");
//        controls.add(nextControl);
        Rectangle prevArea = new Rectangle(nextArea.x - SMALL_GAP - listCtrlW, row, listCtrlW, headerH);
//        previousControl = new SolUiControl(prevArea, true, gameOptions.getKeyLeft());
//        previousControl.setDisplayName("<");
//        controls.add(previousControl);
        row += headerH + SMALL_GAP;

        // List
        float itemRowH = .04f;
        float listRow0 = row;
        for (int i = 0; i < Const.ITEM_GROUPS_PER_PAGE; i++) {
            Rectangle itemR = new Rectangle(col0, row, contentW, itemRowH);
//            SolUiControl itemCtrl = new SolUiControl(itemR, true);
//            itemControls[i] = itemCtrl;
//            controls.add(itemCtrl);
            row += itemRowH + SMALL_GAP;
        }
        listArea = new Rectangle(col0, row, contentW, row - SMALL_GAP - listRow0);
        row += bigGap;

        // Detail header & area
        row += headerH + SMALL_GAP;
        float itemCtrlAreaW = contentW * .4f;
        itemControlsArea = new Rectangle(col0 + contentW - itemCtrlAreaW, row, itemCtrlAreaW, .2f);
        detailsArea = new Rectangle(col0, row, contentW - itemCtrlAreaW - SMALL_GAP, itemControlsArea.height);
        // row += detailsArea.height;

//        // Add the buttons and controls
//        cancelControl = new SolUiControl(itemControlRectangle(3), true, gameOptions.getKeyClose());
//        cancelControl.setDisplayName("Cancel");
//        controls.add(cancelControl);
//
//        saveControl = new SolUiControl(itemControlRectangle(2), true);
//        saveControl.setDisplayName("Save");
//        controls.add(saveControl);
//
//        defaultsControl = new SolUiControl(itemControlRectangle(1), true);
//        defaultsControl.setDisplayName("Defaults");
//        controls.add(defaultsControl);
//
//        upControl = new SolUiControl(null, true, gameOptions.getKeyUp());
//        controls.add(upControl);
//        downControl = new SolUiControl(null, true, gameOptions.getKeyDown());
//        controls.add(downControl);

        // Create the input screens
        inputMapKeyboardScreen = new InputMapKeyboardScreen();
        inputMapControllerScreen = new InputMapControllerScreen();
        inputMapMixedScreen = new InputMapMixedScreen();

        backgroundTexture = Assets.getAtlasRegion("engine:mainMenuBg", Texture.TextureFilter.Linear);
    }

    @Override
    public void updateCustom(SolApplication cmp, SolInputManager.InputPointer[] inputPointers, boolean clickedOutside) {
        GameOptions gameOptions = cmp.getOptions();
        SolInputManager im = cmp.getInputManager();
        MenuScreens menuScreens = cmp.getMenuScreens();

        // Save - saves new settings and returns to the options screen
//        if (saveControl.isJustOff()) {
//            operations.save(gameOptions);
////            im.setScreen(cmp, screens.optionsScreen);
//        }
//
//        if (cancelControl.isJustOff()) {
//            if (operations.isEnterNewKey()) {
//                // Cancel - cancel the current key being entered
//                operations.setEnterNewKey(false);
//            } else {
//                // Cancel - return to options screen without saving
////                im.setScreen(cmp, screens.optionsScreen);
//            }
//        }
//
//        // Disable handling of key inputs while entering a new input key
//        if (operations.isEnterNewKey()) {
//            previousControl.setEnabled(false);
//            nextControl.setEnabled(false);
//            upControl.setEnabled(false);
//            downControl.setEnabled(false);
//            for (SolUiControl itemControl : itemControls) {
//                itemControl.setEnabled(false);
//            }
//            return;
//        } else {
//            upControl.setEnabled(true);
//            downControl.setEnabled(true);
//            for (SolUiControl itemControl : itemControls) {
//                itemControl.setEnabled(true);
//            }
//        }

        // Defaults - Reset the input keys back to their default values
//        if (defaultsControl.isJustOff()) {
//            operations.resetToDefaults(gameOptions);
//        }

        // Selected Item Control
        List<InputConfigItem> itemsList = operations.getItems(gameOptions);
        int groupCount = itemsList.size();
        int pageCount = groupCount / Const.ITEM_GROUPS_PER_PAGE;

        // Select the item the mouse clicked
        int offset = page * Const.ITEM_GROUPS_PER_PAGE;


        // Left and Right Page Control
//        if (previousControl.isJustOff()) {
//            page--;
//        }
//        if (nextControl.isJustOff()) {
//            page++;
//        }
        if (pageCount == 0 || pageCount * Const.ITEM_GROUPS_PER_PAGE < groupCount) {
            pageCount += 1;
        }
        if (page < 0) {
            page = 0;
        }
        if (page >= pageCount) {
            page = pageCount - 1;
        }
//        previousControl.setEnabled(0 < page);
//        nextControl.setEnabled(page < pageCount - 1);

        // Ensure Selected item is on page
        if (selectedIndex < offset || selectedIndex >= offset + Const.ITEM_GROUPS_PER_PAGE) {
            selectedIndex = offset;
        }

        // Up and Down Control
//        if (upControl.isJustOff()) {
//            selectedIndex--;
//            if (selectedIndex < 0) {
//                selectedIndex = 0;
//            }
//            if (selectedIndex < offset) {
//                page--;
//            }
//        }
//        if (downControl.isJustOff()) {
//            selectedIndex++;
//            if (selectedIndex >= groupCount) {
//                selectedIndex = groupCount - 1;
//            }
//            if (selectedIndex >= offset + Const.ITEM_GROUPS_PER_PAGE) {
//                page++;
//            }
//            if (page >= pageCount) {
//                page = pageCount - 1;
//            }
//        }

        // Inform the input screen which item is selected
        operations.setSelectedIndex(selectedIndex);
    }

    public void drawBackground(UiDrawer uiDrawer, SolApplication solApplication) {
        uiDrawer.draw(backgroundTexture, displayDimensions.getRatio(), 1, displayDimensions.getRatio() / 2, 0.5f, displayDimensions.getRatio() / 2, 0.5f, 0, SolColor.WHITE);
    }

    @Override
    public void draw(UiDrawer uiDrawer, SolApplication solApplication) {
        GameOptions gameOptions = solApplication.getOptions();
        List<InputConfigItem> list = operations.getItems(gameOptions);

        float imgColW = listArea.width * IMG_COL_PERC;
        float equiColW = listArea.width * EQUI_COL_PERC;
        float priceWidth = listArea.width * PRICE_COL_PERC;
        float amtWidth = listArea.width * AMT_COL_PERC;
        float nameWidth = listArea.width - imgColW - equiColW - priceWidth - amtWidth;

        // Draw the header title
        uiDrawer.drawString(operations.getHeader(), listHeaderPos.x, listHeaderPos.y, FontSize.WINDOW, false, SolColor.WHITE);

        // Draw the detail text
        uiDrawer.drawString(operations.getDisplayDetail(), detailsArea.x + .015f, detailsArea.y + .015f, FontSize.WINDOW, false, SolColor.WHITE);
    }

    @Override
    public void onAdd(SolApplication solApplication) {
        // Add any extra screen information as required by the input screens. E.g. buttons
        if (operations != null) {
            solApplication.getInputManager().addScreen(operations);
        }

        page = 0;
        selectedIndex = 0;
    }

    private Rectangle itemControlRectangle(int row) {
        float h = (itemControlsArea.height - SMALL_GAP * (BUTTON_ROWS - 1)) / BUTTON_ROWS;
        return new Rectangle(itemControlsArea.x, itemControlsArea.y + (h + SMALL_GAP) * row, itemControlsArea.width, h);
    }

    void setOperations(InputMapOperations operations) {
        this.operations = operations;
    }
}
