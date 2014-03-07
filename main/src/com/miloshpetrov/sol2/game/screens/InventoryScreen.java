package com.miloshpetrov.sol2.game.screens;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Rectangle;
import com.miloshpetrov.sol2.*;
import com.miloshpetrov.sol2.common.Col;
import com.miloshpetrov.sol2.game.SolGame;
import com.miloshpetrov.sol2.game.item.ItemContainer;
import com.miloshpetrov.sol2.game.item.SolItem;
import com.miloshpetrov.sol2.game.ship.SolShip;
import com.miloshpetrov.sol2.ui.*;

import java.util.ArrayList;
import java.util.List;

public class InventoryScreen implements SolUiScreen {
  public static final float LIST_PERC = .6f;
  public static final float LIST_CTRL_PERC = .1f;
  public static final float DETAIL_H_PERC = .3f;
  public static final float DETAIL_W_PERC = .7f;
  private static final int BTN_ROWS = 4;

  private static final float EQUI_COL_PERC = .1f;
  private static final float IMG_COL_PERC = .1f;
  private static final float PRICE_COL_PERC = .2f;

  public final ShowInventory showInventory;
  public final BuyItems buyItems;
  public final SellItems sellItems;
  public final ChangeShip changeShip;

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

  private InventoryOperations myOperations;
  private int myPage;
  private SolItem mySelected;

  public InventoryScreen(float r) {
    myArea = new Rectangle(r/2 - .4f, .2f, .8f, .6f);
    myListArea = new Rectangle(myArea.x, myArea.y, myArea.width, myArea.height * LIST_PERC);
    float listCtrlH = myArea.height * LIST_CTRL_PERC;
    myDetailArea = new Rectangle(myArea.x, myArea.y + myListArea.height + listCtrlH, myArea.width * DETAIL_W_PERC, myArea.height * DETAIL_H_PERC);
    myItemCtrlArea = new Rectangle(myDetailArea.x + myDetailArea.width, myDetailArea.y, myArea.width * (1 - DETAIL_W_PERC), myDetailArea.height);

    myControls = new ArrayList<SolUiControl>();

    float listCtrlW = myListArea.width * .2f;
    Rectangle presArea = new Rectangle(myListArea.x + myListArea.width * .6f, myListArea.y + myListArea.height, listCtrlW, listCtrlH / 2);
    myPrevCtrl = new SolUiControl(presArea, Input.Keys.LEFT);
    myPrevCtrl.setDisplayName("<");
    myControls.add(myPrevCtrl);

    Rectangle nextArea = new Rectangle(myListArea.x + myListArea.width * .6f + listCtrlW, myListArea.y + myListArea.height, listCtrlW, listCtrlH / 2);
    myNextCtrl = new SolUiControl(nextArea, Input.Keys.RIGHT);
    myNextCtrl.setDisplayName(">");
    myControls.add(myNextCtrl);

    myCloseCtrl = new SolUiControl(itemCtrl(3), Input.Keys.ESCAPE);
    myCloseCtrl.setDisplayName("Close");
    myControls.add(myCloseCtrl);

    myUpCtrl = new SolUiControl(null, Input.Keys.UP);
    myControls.add(myUpCtrl);

    downCtrl = new SolUiControl(null, Input.Keys.DOWN);
    myControls.add(downCtrl);

    myItemCtrls = new SolUiControl[Const.ITEMS_PER_PAGE];
    float itemH = myListArea.height / Const.ITEMS_PER_PAGE;
    for (int i = 0; i < Const.ITEMS_PER_PAGE; i++) {
      Rectangle itemR = new Rectangle(myListArea.x, myListArea.y + itemH * i, myListArea.width, itemH);
      SolUiControl itemCtrl = new SolUiControl(itemR);
      myItemCtrls[i] = itemCtrl;
      myControls.add(itemCtrl);
    }

    showInventory = new ShowInventory(this);
    buyItems = new BuyItems(this);
    sellItems = new SellItems(this);
    changeShip = new ChangeShip(this);
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
    for (int i = 0; i < myItemCtrls.length; i++) {
      SolUiControl itemCtrl = myItemCtrls[i];
      int itemIdx = offset + i;
      boolean ctrlEnabled = itemIdx < itemCount;
      itemCtrl.setEnabled(ctrlEnabled);
      if (!ctrlEnabled) continue;
      SolItem item = ic.get(itemIdx);
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
    SolGame game = cmp.getGame();
    ItemContainer ic = myOperations.getItems(game);
    TexMan texMan = cmp.getTexMan();
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

    if (mySelected != null) {
      uiDrawer.drawString(mySelected.getDesc(), myDetailArea.x + myDetailArea.width/2, myDetailArea.y + myDetailArea.height/2, FontSize.WINDOW, true, Col.W);
    }

    SolShip h = cmp.getGame().getHero();
    if (h != null) {
      int money = (int) h.getMoney();
      uiDrawer.drawString("$" + money, myListArea.x, myListArea.y + myListArea.height + myNextCtrl.getScreenArea().height/2, FontSize.WINDOW, false, Col.W);
    }
  }

  public void setOperations(InventoryOperations operations) {
    myOperations = operations;
  }

  public Rectangle itemCtrl(int row) {
    float h = myItemCtrlArea.height / BTN_ROWS;
    return new Rectangle(myItemCtrlArea.x, myItemCtrlArea.y + h * row, myItemCtrlArea.width, h);
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
