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

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import org.destinationsol.Const;
import org.destinationsol.GameOptions;
import org.destinationsol.SolApplication;
import org.destinationsol.common.SolColor;
import org.destinationsol.game.SolGame;
import org.destinationsol.game.item.ItemContainer;
import org.destinationsol.game.item.SolItem;
import org.destinationsol.menu.MenuLayout;
import org.destinationsol.ui.DisplayDimensions;
import org.destinationsol.ui.FontSize;
import org.destinationsol.ui.SolInputManager;
import org.destinationsol.ui.SolUiBaseScreen;

import org.destinationsol.ui.UiDrawer;
import org.destinationsol.ui.responsiveUi.UiWindow;

import java.util.HashMap;
import java.util.List;

public class InventoryScreen extends SolUiBaseScreen {
    // TODO: Rename!
    private static final ItemContainer EMPTY_CONTAINER = new ItemContainer();
    private static final float HEADER_TEXT_OFFSET = .005f;
    private static final float SMALL_GAP = .004f;
    private static final int BUTTON_ROWS = 4;
    private static final float IMG_COL_PERC = .1f;
    private static final float EQUI_COL_PERC = .1f;
    private static final float PRICE_COL_PERC = .1f;
    private static final float AMT_COL_PERC = .1f;

//    private final SolUiControl previousControl;
//    private final SolUiControl upControl;
//    public final SolUiControl nextControl;
//    public final SolUiControl closeControl;
//    public final SolUiControl downControl;

    private final Rectangle myArea;
    private final Rectangle myListArea;
    private final Rectangle myDetailArea;
    private final Rectangle myItemCtrlArea;
    private final Vector2 myDetailHeaderPos;
    private final Vector2 myListHeaderPos;
    public final HashMap<Class<? extends InventoryOperationsScreen>, InventoryOperationsScreen> inventoryOperationsMap;

    private int myPage;
    private List<SolItem> mySelected;
    private InventoryOperationsScreen myOperations;

    public InventoryScreen(GameOptions gameOptions) {
        DisplayDimensions displayDimensions = SolApplication.displayDimensions;
        rootUiElement = new UiWindow();

        float contentW = .8f;
        float col0 = displayDimensions.getRatio() / 2 - contentW / 2;
        float row0 = .2f;
        float row = row0;
        float backgroundGap = MenuLayout.BG_BORDER;
        float bigGap = SMALL_GAP * 6;
        float headerH = .03f;

        // list header & controls
        myListHeaderPos = new Vector2(col0 + HEADER_TEXT_OFFSET, row + HEADER_TEXT_OFFSET); // offset hack
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

        // list
        float itemRowH = .04f;
        float listRow0 = row;

        myListArea = new Rectangle(col0, row, contentW, row - SMALL_GAP - listRow0);
        row += bigGap;

        // detail header & area
        myDetailHeaderPos = new Vector2(col0 + HEADER_TEXT_OFFSET, row + HEADER_TEXT_OFFSET); // offset hack
        row += headerH + SMALL_GAP;
        float itemCtrlAreaW = contentW * .4f;
        myItemCtrlArea = new Rectangle(col0 + contentW - itemCtrlAreaW, row, itemCtrlAreaW, .2f);
        myDetailArea = new Rectangle(col0, row, contentW - itemCtrlAreaW - SMALL_GAP, myItemCtrlArea.height);
        row += myDetailArea.height;

        // whole
        myArea = new Rectangle(col0 - backgroundGap, row0 - backgroundGap, contentW + backgroundGap * 2, row - row0 + backgroundGap * 2);

        inventoryOperationsMap = new HashMap<>();
        inventoryOperationsMap.put(ShowInventory.class, new ShowInventory());
//        closeControl = new SolUiControl(itemCtrl(3), true, gameOptions.getKeyClose());
//        closeControl.setDisplayName("Close");
//        controls.add(closeControl);
//        upControl = new SolUiControl(null, true, gameOptions.getKeyUp());
//        controls.add(upControl);
//        downControl = new SolUiControl(null, true, gameOptions.getKeyDown());
//        controls.add(downControl);
    }

    @Override
    public void updateCustom(SolApplication solApplication, SolInputManager.InputPointer[] inputPointers, boolean clickedOutside) {
//        if (clickedOutside) {
//            closeControl.maybeFlashPressed(solApplication.getOptions().getKeyClose());
//            return;
//        }
//        if (closeControl.isJustOff()) {
//
//            SolGame game = solApplication.getGame();
//            // Make sure the ChooseMercenaryScreen screen comes back up when we exit a mercenary related screen
//            if (myOperations == giveItemsScreen || myOperations == takeItems || (myOperations == showInventory && showInventory.getTarget() != game.getHero().getShip())) {
//                SolInputManager inputMan = solApplication.getInputManager();
//                GameScreens screens = game.getScreens();
//                InventoryScreen is = screens.inventoryScreen;
//
////                inputMan.setScreen(solApplication, screens.mainGameScreen);
//                is.setOperations(is.chooseMercenaryScreen);
//                inputMan.addScreen(solApplication, is);
//            }
////            solApplication.getInputManager().setScreen(solApplication, solApplication.getGame().getScreens().mainGameScreen);
//            return;
//        }
//        if (previousControl.isJustOff()) {
//            myPage--;
//        }
//        if (nextControl.isJustOff()) {
//            myPage++;
//        }

        ItemContainer itemContainer = myOperations.getItems(solApplication.getGame());
        if (itemContainer == null) {
            itemContainer = EMPTY_CONTAINER;
        }
        int groupCount = itemContainer.groupCount();
        int pageCount = groupCount / Const.ITEM_GROUPS_PER_PAGE;
        if (pageCount == 0 || pageCount * Const.ITEM_GROUPS_PER_PAGE < groupCount) {
            pageCount += 1;
        }
        if (myPage < 0) {
            myPage = 0;
        }
        if (myPage >= pageCount) {
            myPage = pageCount - 1;
        }

//        previousControl.setEnabled(0 < myPage);
//        nextControl.setEnabled(myPage < pageCount - 1);

        if (!itemContainer.containsGroup(mySelected)) {
            mySelected = null;
        }
        int selIdx = -1;
        int offset = myPage * Const.ITEM_GROUPS_PER_PAGE;
        boolean hNew = showingHeroItems(solApplication);

        if (selIdx < 0 && groupCount > 0) {
            mySelected = itemContainer.getGroup(offset);
        }
//        if (upControl.isJustOff() && selIdx > 0) {
//            selIdx--;
//            mySelected = itemContainer.getGroup(selIdx);
//            if (selIdx < offset) {
//                myPage--;
//            }
//        }
//        if (downControl.isJustOff() && selIdx < groupCount - 1) {
//            selIdx++;
//            mySelected = itemContainer.getGroup(selIdx);
//            if (selIdx >= offset + Const.ITEM_GROUPS_PER_PAGE) {
//                myPage++;
//            }
//        }
        if (mySelected != null) {
            itemContainer.seen(mySelected);
        }
    }

    @Override
    public boolean isCursorOnBackground(SolInputManager.InputPointer inputPointer) {
        return myArea.contains(inputPointer.x, inputPointer.y);
    }

    @Override
    public void onAdd(SolApplication solApplication) {
        myPage = 0;
        mySelected = null;
    }

    @Override
    public void drawBackground(UiDrawer uiDrawer, SolApplication solApplication) {
        uiDrawer.draw(myArea, SolColor.UI_BG);
    }

//    @Override
//    public void draw(UiDrawer uiDrawer, SolApplication solApplication) {
//        SolGame game = solApplication.getGame();
//        ItemContainer itemContainer = myOperations.getItems(game);
//        if (itemContainer == null) {
//            itemContainer = EMPTY_CONTAINER;
//        }
//
//        float imgColW = myListArea.width * IMG_COL_PERC;
//        float rowH = itemControls[0].getScreenArea().height;
//        float imgSz = imgColW < rowH ? imgColW : rowH;
//
//        uiDrawer.draw(myDetailArea, SolColor.UI_INACTIVE);
//        for (int i = 0; i < itemControls.length; i++) {
//            int groupIdx = myPage * Const.ITEM_GROUPS_PER_PAGE + i;
//            int groupCount = itemContainer.groupCount();
//            if (groupCount <= groupIdx) {
//                continue;
//            }
//            SolUiControl itemCtrl = itemControls[i];
//            List<SolItem> group = itemContainer.getGroup(groupIdx);
//            SolItem item = group.get(0);
//            TextureAtlas.AtlasRegion tex = item.getIcon(game);
//            Rectangle rect = itemCtrl.getScreenArea();
//            float rowCenterY = rect.y + rect.height / 2;
//            uiDrawer.draw(uiDrawer.whiteTexture, imgSz, imgSz, imgSz / 2, imgSz / 2, rect.x + imgColW / 2, rowCenterY, 0, item.getItemType().uiColor);
//            uiDrawer.draw(tex, imgSz, imgSz, imgSz / 2, imgSz / 2, rect.x + imgColW / 2, rowCenterY, 0, SolColor.WHITE);
//        }
//    }

    @Override
    public void draw(UiDrawer uiDrawer, SolApplication solApplication) {
        SolGame game = solApplication.getGame();
        ItemContainer itemContainer = myOperations.getItems(game);
        if (itemContainer == null) {
            itemContainer = EMPTY_CONTAINER;
        }

        float imgColW = myListArea.width * IMG_COL_PERC;
        float equiColW = myListArea.width * EQUI_COL_PERC;
        float priceWidth = myListArea.width * PRICE_COL_PERC;
        float amtWidth = myListArea.width * AMT_COL_PERC;
        float nameWidth = myListArea.width - imgColW - equiColW - priceWidth - amtWidth;


        uiDrawer.drawString(myOperations.getHeader(), myListHeaderPos.x, myListHeaderPos.y, FontSize.WINDOW, UiDrawer.TextAlignment.LEFT, false, SolColor.WHITE);
        uiDrawer.drawString("Selected Item:", myDetailHeaderPos.x, myDetailHeaderPos.y, FontSize.WINDOW, UiDrawer.TextAlignment.LEFT, false, SolColor.WHITE);
        if (mySelected != null && !mySelected.isEmpty()) {
            SolItem selItem = mySelected.get(0);
            String desc = selItem.getDisplayName() + "\n" + selItem.getDescription();
            uiDrawer.drawString(desc, myDetailArea.x + .015f, myDetailArea.y + .015f, FontSize.WINDOW, UiDrawer.TextAlignment.LEFT, false, SolColor.WHITE);
        }
    }

    @Override
    public boolean reactsToClickOutside() {
        return true;
    }

    @Override
    public void blurCustom(SolApplication solApplication) {
        if (!showingHeroItems(solApplication)) {
            return;
        }
        SolGame game = solApplication.getGame();
        ItemContainer items = myOperations.getItems(game);
        if (items != null) {
            items.markAllAsSeen();
        }
    }

    private boolean showingHeroItems(SolApplication application) {
        return false;
    }

    public Rectangle itemCtrl(int row) {
        float h = (myItemCtrlArea.height - SMALL_GAP * (BUTTON_ROWS - 1)) / BUTTON_ROWS;
        return new Rectangle(myItemCtrlArea.x, myItemCtrlArea.y + (h + SMALL_GAP) * row, myItemCtrlArea.width, h);
    }

    public List<SolItem> getSelected() {
        return mySelected;
    }

    public void setSelected(List<SolItem> selected) {
        mySelected = selected;
    }

    public SolItem getSelectedItem() {
        return mySelected == null || mySelected.isEmpty() ? null : mySelected.get(0);
    }

    public InventoryOperationsScreen getOperations() {
        return myOperations;
    }

    public void setOperations(InventoryOperationsScreen operations) {
        myOperations = operations;
    }

    public int getPage() {
        return myPage;
    }
}
