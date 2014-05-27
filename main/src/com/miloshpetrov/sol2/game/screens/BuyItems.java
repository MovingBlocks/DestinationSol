package com.miloshpetrov.sol2.game.screens;

import com.badlogic.gdx.Input;
import com.miloshpetrov.sol2.SolCmp;
import com.miloshpetrov.sol2.game.SolGame;
import com.miloshpetrov.sol2.game.item.ItemContainer;
import com.miloshpetrov.sol2.game.item.SolItem;
import com.miloshpetrov.sol2.game.ship.SolShip;
import com.miloshpetrov.sol2.ui.*;

import java.util.ArrayList;
import java.util.List;

public class BuyItems implements InventoryOperations {

  private final ArrayList<SolUiControl> myControls;
  public final SolUiControl buyCtrl;

  public BuyItems(InventoryScreen inventoryScreen) {
    myControls = new ArrayList<SolUiControl>();

    buyCtrl = new SolUiControl(inventoryScreen.itemCtrl(0), true, Input.Keys.SPACE);
    buyCtrl.setDisplayName("Buy");
    myControls.add(buyCtrl);
  }

  @Override
  public ItemContainer getItems(SolGame game) {
    return game.getScreens().talkScreen.getTarget().getTradeContainer().getItems();
  }

  @Override
  public boolean isUsing(SolGame game, SolItem item) {
    return false;
  }

  @Override
  public float getPriceMul() {
    return 1;
  }

  @Override
  public List<SolUiControl> getControls() {
    return myControls;
  }

  @Override
  public void updateCustom(SolCmp cmp, SolInputMan.Ptr[] ptrs) {
    SolGame game = cmp.getGame();
    InventoryScreen is = game.getScreens().inventoryScreen;
    SolShip hero = game.getHero();
    TalkScreen talkScreen = game.getScreens().talkScreen;
    SolShip target = talkScreen.getTarget();
    if (talkScreen.isTargetFar(hero)) {
      cmp.getInputMan().setScreen(cmp, game.getScreens().mainScreen);
      return;
    }
    SolItem selItem = is.getSelectedItem();
    boolean enabled = selItem != null && hero.getMoney() >= selItem.getPrice() && hero.getItemContainer().canAdd(selItem);
    buyCtrl.setDisplayName(enabled ? "Buy" : "---");
    buyCtrl.setEnabled(enabled);
    if (!enabled) return;
    if (buyCtrl.isJustOff()) {
      target.getTradeContainer().getItems().remove(selItem);
      hero.getItemContainer().add(selItem);
      hero.setMoney(hero.getMoney() - selItem.getPrice());
    }
  }

  @Override
  public void drawPre(UiDrawer uiDrawer, SolCmp cmp) {

  }

  @Override
  public boolean isCursorOnBg(SolInputMan.Ptr ptr) {
    return false;
  }

  @Override
  public void onAdd(SolCmp cmp) {

  }

  @Override
  public void drawPost(UiDrawer uiDrawer, SolCmp cmp) {

  }

  @Override
  public void blurCustom(SolCmp cmp) {

  }
}
