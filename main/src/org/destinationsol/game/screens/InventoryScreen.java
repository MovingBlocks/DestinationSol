/*
 * Copyright 2015 MovingBlocks
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

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
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
import org.destinationsol.ui.*;

import java.util.ArrayList;
import java.util.List;

public class InventoryScreen implements SolUiScreen {
  public static final ItemContainer EMPTY_CONTAINER = new ItemContainer();
  private static final int BTN_ROWS = 4;

  private static final float IMG_COL_PERC = .1f;
  private static final float EQUI_COL_PERC = .1f;
  private static final float PRICE_COL_PERC = .1f;
  private static final float AMT_COL_PERC = .1f;

  public static final float HEADER_TEXT_OFFS = .005f;

  public final ShowInventory showInventory;
  public final BuyItems buyItems;
  public final SellItems sellItems;
  public final ChangeShip changeShip;
  public final HireShips hireShips;

  private final List<SolUiControl> myControls;
  private final Rectangle myArea;
  private final Rectangle myListArea;
  private final Rectangle myDetailArea;
  private final Rectangle myItemCtrlArea;
  private final SolUiControl myPrevCtrl;
  public final SolUiControl nextCtrl;
  public final SolUiControl[] itemCtrls;
  public final SolUiControl closeCtrl;
  private final SolUiControl myUpCtrl;
  public final SolUiControl downCtrl;
  private final Vector2 myDetailHeaderPos;

  private InventoryOperations myOperations;
  private int myPage;
  private List<SolItem> mySelected;
  private final Vector2 myListHeaderPos;
  public static final float SMALL_GAP = .004f;

  public InventoryScreen(float r, GameOptions gameOptions) {
    myControls = new ArrayList<SolUiControl>();

    float contentW = .8f;
    float col0 = r / 2 - contentW / 2;
    float row0 = .2f;
    float row = row0;
    float bgGap = MenuLayout.BG_BORDER;
    float bigGap = SMALL_GAP * 6;
    float headerH = .03f;

    // list header & controls
    myListHeaderPos = new Vector2(col0 + HEADER_TEXT_OFFS, row + HEADER_TEXT_OFFS); // offset hack
    float listCtrlW = contentW * .15f;
    Rectangle nextArea = new Rectangle(col0 + contentW - listCtrlW, row, listCtrlW, headerH);
    nextCtrl = new SolUiControl(nextArea, true, gameOptions.getKeyRight());
    nextCtrl.setDisplayName(">");
    myControls.add(nextCtrl);
    Rectangle prevArea = new Rectangle(nextArea.x - SMALL_GAP - listCtrlW, row, listCtrlW, headerH);
    myPrevCtrl = new SolUiControl(prevArea, true, gameOptions.getKeyLeft());
    myPrevCtrl.setDisplayName("<");
    myControls.add(myPrevCtrl);
    row += headerH + SMALL_GAP;

    // list
    float itemRowH = .04f;
    float listRow0 = row;
    itemCtrls = new SolUiControl[Const.ITEM_GROUPS_PER_PAGE];
    for (int i = 0; i < Const.ITEM_GROUPS_PER_PAGE; i++) {
      Rectangle itemR = new Rectangle(col0, row, contentW, itemRowH);
      SolUiControl itemCtrl = new SolUiControl(itemR, true);
      itemCtrls[i] = itemCtrl;
      myControls.add(itemCtrl);
      row += itemRowH + SMALL_GAP;
    }
    myListArea = new Rectangle(col0, row, contentW, row - SMALL_GAP - listRow0);
    row += bigGap;

    // detail header & area
    myDetailHeaderPos = new Vector2(col0 + HEADER_TEXT_OFFS, row + HEADER_TEXT_OFFS); // offset hack
    row += headerH + SMALL_GAP;
    float itemCtrlAreaW = contentW * .4f;
    myItemCtrlArea = new Rectangle(col0 + contentW - itemCtrlAreaW, row, itemCtrlAreaW, .2f);
    myDetailArea = new Rectangle(col0, row, contentW - itemCtrlAreaW - SMALL_GAP, myItemCtrlArea.height);
    row += myDetailArea.height;

    // whole
    myArea = new Rectangle(col0 - bgGap, row0 - bgGap, contentW + bgGap * 2, row - row0 + bgGap * 2);

    closeCtrl = new SolUiControl(itemCtrl(3), true, gameOptions.getKeyClose());
    closeCtrl.setDisplayName("Close");
    myControls.add(closeCtrl);

    showInventory = new ShowInventory(this, gameOptions);
    buyItems = new BuyItems(this, gameOptions);
    sellItems = new SellItems(this, gameOptions);
    changeShip = new ChangeShip(this, gameOptions);
    hireShips = new HireShips(this, gameOptions);
    myUpCtrl = new SolUiControl(null, true, gameOptions.getKeyUp());
    myControls.add(myUpCtrl);
    downCtrl = new SolUiControl(null, true, gameOptions.getKeyDown());
    myControls.add(downCtrl);
  }

  @Override
  public List<SolUiControl> getControls() {
    return myControls;
  }

  @Override
  public void updateCustom(SolApplication cmp, SolInputManager.Ptr[] ptrs, boolean clickedOutside) {
    if (clickedOutside) {
      closeCtrl.maybeFlashPressed(cmp.getOptions().getKeyClose());
      return;
    }
    if (closeCtrl.isJustOff()) {
      cmp.getInputMan().setScreen(cmp, cmp.getGame().getScreens().mainScreen);
      if (myOperations != showInventory) cmp.getInputMan().addScreen(cmp, cmp.getGame().getScreens().talkScreen);
      return;
    }
    if (myPrevCtrl.isJustOff()) myPage--;
    if (nextCtrl.isJustOff()) myPage++;

    ItemContainer ic = myOperations.getItems(cmp.getGame());
    if (ic == null) ic = EMPTY_CONTAINER;
    int groupCount = ic.groupCount();
    int pageCount = groupCount / Const.ITEM_GROUPS_PER_PAGE;
    if (pageCount == 0 || pageCount * Const.ITEM_GROUPS_PER_PAGE < groupCount) pageCount += 1;
    if (myPage < 0) myPage = 0;
    if (myPage >= pageCount) myPage = pageCount - 1;

    myPrevCtrl.setEnabled(0 < myPage);
    nextCtrl.setEnabled(myPage < pageCount - 1);

    if (!ic.containsGroup(mySelected)) mySelected = null;
    int selIdx = -1;
    int offset = myPage * Const.ITEM_GROUPS_PER_PAGE;
    boolean hNew = showingHeroItems();
    for (int i = 0; i < itemCtrls.length; i++) {
      SolUiControl itemCtrl = itemCtrls[i];
      int groupIdx = offset + i;
      boolean ctrlEnabled = groupIdx < groupCount;
      itemCtrl.setEnabled(ctrlEnabled);
      if (!ctrlEnabled) continue;
      List<SolItem> group = ic.getGroup(groupIdx);
      if (hNew && ic.isNew(group)) itemCtrl.enableWarn();
      if (itemCtrl.isJustOff()) {
        mySelected = group;
      }
      if (mySelected == group) selIdx = groupIdx;
    }
    if (selIdx < 0 && groupCount > 0) {
      mySelected = ic.getGroup(offset);
    }
    if (myUpCtrl.isJustOff() && selIdx > 0) {
      selIdx--;
      mySelected = ic.getGroup(selIdx);
      if (selIdx < offset) myPage--;
    }
    if (downCtrl.isJustOff() && selIdx < groupCount - 1) {
      selIdx++;
      mySelected = ic.getGroup(selIdx);
      if (selIdx >= offset + Const.ITEM_GROUPS_PER_PAGE) myPage++;
    }
    if (mySelected != null) ic.seen(mySelected);
  }

  @Override
  public boolean isCursorOnBg(SolInputManager.Ptr ptr) {
    return myArea.contains(ptr.x, ptr.y);
  }

  @Override
  public void onAdd(SolApplication cmp) {
    if (myOperations != null) cmp.getInputMan().addScreen(cmp, myOperations);
    myPage = 0;
    mySelected = null;
  }

  @Override
  public void drawBg(UiDrawer uiDrawer, SolApplication cmp) {
    uiDrawer.draw(myArea, SolColor.UI_BG);
  }

  @Override
  public void drawImgs(UiDrawer uiDrawer, SolApplication cmp) {
    SolGame game = cmp.getGame();
    ItemContainer ic = myOperations.getItems(game);
    if (ic == null) ic = EMPTY_CONTAINER;

    float imgColW = myListArea.width * IMG_COL_PERC;
    float rowH = itemCtrls[0].getScreenArea().height;
    float imgSz = imgColW < rowH ? imgColW : rowH;

    uiDrawer.draw(myDetailArea, SolColor.UI_INACTIVE);
    for (int i = 0; i < itemCtrls.length; i++) {
      int groupIdx = myPage * Const.ITEM_GROUPS_PER_PAGE + i;
      int groupCount = ic.groupCount();
      if (groupCount <= groupIdx) continue;
      SolUiControl itemCtrl = itemCtrls[i];
      List<SolItem> group = ic.getGroup(groupIdx);
      SolItem item = group.get(0);
      TextureAtlas.AtlasRegion tex = item.getIcon(game);
      Rectangle rect = itemCtrl.getScreenArea();
      float rowCenterY = rect.y + rect.height / 2;
      uiDrawer.draw(uiDrawer.whiteTex, imgSz, imgSz, imgSz/2, imgSz/2, rect.x + imgColW/2, rowCenterY, 0, item.getItemType().uiColor);
      uiDrawer.draw(tex, imgSz, imgSz, imgSz/2, imgSz/2, rect.x + imgColW/2, rowCenterY, 0, SolColor.W);
    }
  }

  @Override
  public void drawText(UiDrawer uiDrawer, SolApplication cmp) {
    SolGame game = cmp.getGame();
    ItemContainer ic = myOperations.getItems(game);
    if (ic == null) ic = EMPTY_CONTAINER;

    float imgColW = myListArea.width * IMG_COL_PERC;
    float equiColW = myListArea.width * EQUI_COL_PERC;
    float priceWidth = myListArea.width * PRICE_COL_PERC;
    float amtWidth = myListArea.width * AMT_COL_PERC;
    float nameWidth = myListArea.width - imgColW - equiColW - priceWidth - amtWidth;
    for (int i = 0; i < itemCtrls.length; i++) {
      int groupIdx = myPage * Const.ITEM_GROUPS_PER_PAGE + i;
      int groupCount = ic.groupCount();
      if (groupCount <= groupIdx) continue;
      SolUiControl itemCtrl = itemCtrls[i];
      List<SolItem> group = ic.getGroup(groupIdx);
      SolItem item = group.get(0);
      Rectangle rect = itemCtrl.getScreenArea();
      float rowCenterY = rect.y + rect.height / 2;
      if (myOperations.isUsing(game, item)) uiDrawer.drawString("using", rect.x + imgColW + equiColW/2, rowCenterY, FontSize.WINDOW, true, SolColor.W);
      uiDrawer.drawString(item.getDisplayName(), rect.x + equiColW + imgColW + nameWidth/2, rowCenterY, FontSize.WINDOW, true, mySelected == group ? SolColor.W : SolColor.G);
      int count = ic.getCount(groupIdx);
      if (count > 1) {
        uiDrawer.drawString("x" + count, rect.x + rect.width - amtWidth/2, rowCenterY, FontSize.WINDOW, true, SolColor.W);
      }
      float mul = myOperations.getPriceMul();
      if (mul > 0) {
        float price = item.getPrice() * mul;
        uiDrawer.drawString("$" + (int)price, rect.x + rect.width - amtWidth - priceWidth/2, rowCenterY, FontSize.WINDOW, true, SolColor.LG);
      }
    }

    uiDrawer.drawString(myOperations.getHeader(), myListHeaderPos.x, myListHeaderPos.y, FontSize.WINDOW, false, SolColor.W);
    uiDrawer.drawString("Selected Item:", myDetailHeaderPos.x, myDetailHeaderPos.y, FontSize.WINDOW, false, SolColor.W);
    if (mySelected != null && !mySelected.isEmpty()) {
      SolItem selItem = mySelected.get(0);
      String desc = selItem.getDisplayName() + "\n" + selItem.getDesc();
      uiDrawer.drawString(desc, myDetailArea.x + .015f, myDetailArea.y + .015f, FontSize.WINDOW, false, SolColor.W);
    }
  }

  @Override
  public boolean reactsToClickOutside() {
    return true;
  }

  @Override
  public void blurCustom(SolApplication cmp) {
    if (!showingHeroItems()) return;
    SolGame game = cmp.getGame();
    ItemContainer items = myOperations.getItems(game);
    if (items != null) items.seenAll();
  }

  private boolean showingHeroItems() {
    return myOperations == showInventory || myOperations == sellItems;
  }

  public void setOperations(InventoryOperations operations) {
    myOperations = operations;
  }

  public Rectangle itemCtrl(int row) {
    float h = (myItemCtrlArea.height - SMALL_GAP * (BTN_ROWS - 1)) / BTN_ROWS;
    return new Rectangle(myItemCtrlArea.x, myItemCtrlArea.y + (h + SMALL_GAP) * row, myItemCtrlArea.width, h);
  }

  public List<SolItem> getSelected() {
    return mySelected;
  }

  public SolItem getSelectedItem() {
    return mySelected == null || mySelected.isEmpty() ? null : mySelected.get(0);
  }

  public void setSelected(List<SolItem> selected) {
    mySelected = selected;
  }

  public InventoryOperations getOperations() {
    return myOperations;
  }

  public int getPage() {
    return myPage;
  }

  public List<SolUiControl> getEquippedItemUIControlsForTutorial(SolGame game) {
    List<SolUiControl> controls = new ArrayList<>();
    ItemContainer ic = myOperations.getItems(game);
    if (ic == null) return controls;

    for (int i = 0; i < itemCtrls.length; i++) {
      int groupIdx = myPage * Const.ITEM_GROUPS_PER_PAGE + i;
      int groupCount = ic.groupCount();
      if (groupCount <= groupIdx) continue;
      SolUiControl itemCtrl = itemCtrls[i];
      List<SolItem> group = ic.getGroup(groupIdx);
      SolItem item = group.get(0);
      if (myOperations.isUsing(game, item))
        controls.add(itemCtrl);
    }
    return controls;
  }
}
