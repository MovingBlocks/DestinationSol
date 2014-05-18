package com.miloshpetrov.sol2.game.screens;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.miloshpetrov.sol2.*;
import com.miloshpetrov.sol2.common.Col;
import com.miloshpetrov.sol2.game.SolGame;
import com.miloshpetrov.sol2.game.item.ItemContainer;
import com.miloshpetrov.sol2.game.item.SolItem;
import com.miloshpetrov.sol2.menu.MenuLayout;
import com.miloshpetrov.sol2.ui.*;

import java.util.ArrayList;
import java.util.List;

public class InventoryScreen implements SolUiScreen {
  public static final ItemContainer EMPTY_CONTAINER = new ItemContainer();
  private static final int BTN_ROWS = 4;

  private static final float EQUI_COL_PERC = .1f;
  private static final float IMG_COL_PERC = .1f;
  private static final float PRICE_COL_PERC = .2f;
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
  private final SolUiControl myNextCtrl;
  private final SolUiControl[] myItemCtrls;
  private final SolUiControl myCloseCtrl;
  private final SolUiControl myUpCtrl;
  public final SolUiControl downCtrl;
  private final Vector2 myDetailHeaderPos;

  private InventoryOperations myOperations;
  private int myPage;
  private SolItem mySelected;
  private final Vector2 myListHeaderPos;
  public static final float SMALL_GAP = .004f;

  public InventoryScreen(float r) {
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
    myNextCtrl = new SolUiControl(nextArea, true, Input.Keys.RIGHT);
    myNextCtrl.setDisplayName(">");
    myControls.add(myNextCtrl);
    Rectangle prevArea = new Rectangle(nextArea.x - SMALL_GAP - listCtrlW, row, listCtrlW, headerH);
    myPrevCtrl = new SolUiControl(prevArea, true, Input.Keys.LEFT);
    myPrevCtrl.setDisplayName("<");
    myControls.add(myPrevCtrl);
    row += headerH + SMALL_GAP;

    // list
    float itemRowH = .04f;
    float listRow0 = row;
    myItemCtrls = new SolUiControl[Const.ITEMS_PER_PAGE];
    for (int i = 0; i < Const.ITEMS_PER_PAGE; i++) {
      Rectangle itemR = new Rectangle(col0, row, contentW, itemRowH);
      SolUiControl itemCtrl = new SolUiControl(itemR, true);
      myItemCtrls[i] = itemCtrl;
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

    myCloseCtrl = new SolUiControl(itemCtrl(3), true, Input.Keys.ESCAPE);
    myCloseCtrl.setDisplayName("Close");
    myControls.add(myCloseCtrl);

    showInventory = new ShowInventory(this);
    buyItems = new BuyItems(this);
    sellItems = new SellItems(this);
    changeShip = new ChangeShip(this);
    hireShips = new HireShips(this);
    myUpCtrl = new SolUiControl(null, true, Input.Keys.UP);
    myControls.add(myUpCtrl);
    downCtrl = new SolUiControl(null, true, Input.Keys.DOWN);
    myControls.add(downCtrl);
  }

  @Override
  public List<SolUiControl> getControls() {
    return myControls;
  }

  @Override
  public void updateCustom(SolCmp cmp, SolInputMan.Ptr[] ptrs) {
    if (myCloseCtrl.isJustOff()) {
      cmp.getInputMan().setScreen(cmp, cmp.getGame().getScreens().mainScreen);
    }
    if (myPrevCtrl.isJustOff()) myPage--;
    if (myNextCtrl.isJustOff()) myPage++;

    ItemContainer ic = myOperations.getItems(cmp.getGame());
    if (ic == null) ic = EMPTY_CONTAINER;
    int itemCount = ic.size();
    int pageCount = itemCount / Const.ITEMS_PER_PAGE;
    if (pageCount == 0 || pageCount * Const.ITEMS_PER_PAGE < itemCount) pageCount += 1;
    if (myPage < 0) myPage = 0;
    if (myPage >= pageCount) myPage = pageCount - 1;

    myPrevCtrl.setEnabled(0 < myPage);
    myNextCtrl.setEnabled(myPage < pageCount - 1);

    if (!ic.contains(mySelected)) mySelected = null;
    int selIdx = -1;
    int offset = myPage * Const.ITEMS_PER_PAGE;
    boolean hNew = showingHeroItems();
    for (int i = 0; i < myItemCtrls.length; i++) {
      SolUiControl itemCtrl = myItemCtrls[i];
      int itemIdx = offset + i;
      boolean ctrlEnabled = itemIdx < itemCount;
      itemCtrl.setEnabled(ctrlEnabled);
      if (!ctrlEnabled) continue;
      SolItem item = ic.get(itemIdx);
      if (hNew && ic.isNew(item)) itemCtrl.enableWarn();
      if (itemCtrl.isJustOff()) {
        mySelected = item;
      }
      if (mySelected == item) selIdx = itemIdx;
    }
    if (selIdx < 0 && itemCount > 0) {
      mySelected = ic.get(offset);
    }
    if (myUpCtrl.isJustOff() && selIdx > 0) {
      selIdx--;
      mySelected = ic.get(selIdx);
      if (selIdx < offset) myPage--;
    }
    if (downCtrl.isJustOff() && selIdx < itemCount - 1) {
      selIdx++;
      mySelected = ic.get(selIdx);
      if (selIdx >= offset + Const.ITEMS_PER_PAGE) myPage++;
    }
    if (mySelected != null) ic.seen(mySelected);
  }

  @Override
  public void drawPre(UiDrawer uiDrawer, SolCmp cmp) {
    uiDrawer.draw(myArea, Col.B75);
  }

  @Override
  public boolean isCursorOnBg(SolInputMan.Ptr ptr) {
    return myArea.contains(ptr.x, ptr.y);
  }

  @Override
  public void onAdd(SolCmp cmp) {
    if (myOperations != null) cmp.getInputMan().addScreen(cmp, myOperations);
    myPage = 0;
  }

  @Override
  public void drawPost(UiDrawer uiDrawer, SolCmp cmp) {
    uiDrawer.drawString("Items:", myListHeaderPos.x, myListHeaderPos.y, FontSize.WINDOW, false, Col.W);
    SolGame game = cmp.getGame();
    ItemContainer ic = myOperations.getItems(game);
    if (ic == null) ic = EMPTY_CONTAINER;
    float equiWidth = myListArea.width * EQUI_COL_PERC;
    float imgWidth = myListArea.width * IMG_COL_PERC;
    float rowH = myItemCtrls[0].getScreenArea().height;
    float imgSz = imgWidth < rowH ? imgWidth : rowH;
    float priceWidth = myListArea.width * PRICE_COL_PERC;
    float nameWidth = myListArea.width - equiWidth - imgSz - priceWidth;

    for (int i = 0; i < myItemCtrls.length; i++) {
      int itemIdx = myPage * Const.ITEMS_PER_PAGE + i;
      int itemCount = ic.size();
      if (itemCount <= itemIdx) continue;
      SolUiControl itemCtrl = myItemCtrls[i];
      SolItem item = ic.get(itemIdx);
      TextureAtlas.AtlasRegion tex = item.getIcon(game);
      Rectangle rect = itemCtrl.getScreenArea();
      float rowCenterY = rect.y + rect.height / 2;
      uiDrawer.draw(tex, imgSz, imgSz, imgSz/2, imgSz/2, rect.x + equiWidth + imgWidth/2, rowCenterY, 0, Col.W);
      if (myOperations.isUsing(game, item)) uiDrawer.drawString("using", rect.x + equiWidth/2, rowCenterY, FontSize.HINT, true, Col.G);
      uiDrawer.drawString(item.getDisplayName(), rect.x + equiWidth + imgWidth + nameWidth/2, rowCenterY, FontSize.WINDOW, true, mySelected == item ? Col.W : Col.G);
      float mul = myOperations.getPriceMul();
      if (mul > 0) {
        float price = item.getPrice() * mul;
        uiDrawer.drawString("$" + (int)price, rect.x + rect.width - priceWidth/2, rowCenterY, FontSize.WINDOW, true, Col.LG);
      }
    }

    uiDrawer.draw(myDetailArea, Col.UI_INACTIVE);
    uiDrawer.drawString("Selected Item:", myDetailHeaderPos.x, myDetailHeaderPos.y, FontSize.WINDOW, false, Col.W);
    if (mySelected != null) {
      uiDrawer.drawString(mySelected.getDesc(), myDetailArea.x + .015f, myDetailArea.y + .015f, FontSize.WINDOW, false, Col.W);
    }
  }

  @Override
  public void blurCustom(SolCmp cmp) {
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

  public SolItem getSelected() {
    return mySelected;
  }

  public void setSelected(SolItem selected) {
    mySelected = selected;
  }

  public InventoryOperations getOperations() {
    return myOperations;
  }

  public int getPage() {
    return myPage;
  }
}
