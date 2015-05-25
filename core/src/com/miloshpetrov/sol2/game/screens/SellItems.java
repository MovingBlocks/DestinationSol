package com.miloshpetrov.sol2.game.screens;

import com.badlogic.gdx.Input;
import com.miloshpetrov.sol2.SolApplication;
import com.miloshpetrov.sol2.game.SolGame;
import com.miloshpetrov.sol2.game.item.ItemContainer;
import com.miloshpetrov.sol2.game.item.SolItem;
import com.miloshpetrov.sol2.game.ship.SolShip;
import com.miloshpetrov.sol2.ui.*;

import java.util.ArrayList;
import java.util.List;

public class SellItems implements InventoryOperations {

  public static float PERC = .8f;
  private final ArrayList<SolUiControl> myControls;
  public final SolUiControl sellCtrl;

  public SellItems(InventoryScreen inventoryScreen) {
    myControls = new ArrayList<SolUiControl>();

    sellCtrl = new SolUiControl(inventoryScreen.itemCtrl(0), true, Input.Keys.SPACE);
    sellCtrl.setDisplayName("Sell");
    myControls.add(sellCtrl);
  }

  @Override
  public ItemContainer getItems(SolGame game) {
    SolShip h = game.getHero();
    return h == null ? null : h.getItemContainer();
  }

  @Override
  public boolean isUsing(SolGame game, SolItem item) {
    SolShip h = game.getHero();
    return h != null && h.maybeUnequip(game, item, false);
  }

  @Override
  public float getPriceMul() {
    return PERC;
  }

  @Override
  public String getHeader() {
    return "Sell:";
  }

  @Override
  public List<SolUiControl> getControls() {
    return myControls;
  }

  @Override
  public void updateCustom(SolApplication cmp, SolInputManager.Ptr[] ptrs, boolean clickedOutside) {
    SolGame game = cmp.getGame();
    InventoryScreen is = game.getScreens().inventoryScreen;
    TalkScreen talkScreen = game.getScreens().talkScreen;
    SolShip target = talkScreen.getTarget();
    SolShip hero = game.getHero();
    if (talkScreen.isTargetFar(hero)) {
      cmp.getInputMan().setScreen(cmp, game.getScreens().mainScreen);
      return;
    }
    SolItem selItem = is.getSelectedItem();
    boolean enabled = selItem != null && target.getTradeContainer().getItems().canAdd(selItem);
    sellCtrl.setDisplayName(enabled ? "Sell" : "---");
    sellCtrl.setEnabled(enabled);
    if (!enabled) return;
    if (sellCtrl.isJustOff()) {
      ItemContainer ic = hero.getItemContainer();
      is.setSelected(ic.getSelectionAfterRemove(is.getSelected()));
      ic.remove(selItem);
      target.getTradeContainer().getItems().add(selItem);
      hero.setMoney(hero.getMoney() + selItem.getPrice() * PERC);
    }
  }

  @Override
  public void drawBg(UiDrawer uiDrawer, SolApplication cmp) {

  }

  @Override
  public void drawImgs(UiDrawer uiDrawer, SolApplication cmp) {

  }

  @Override
  public void drawText(UiDrawer uiDrawer, SolApplication cmp) {

  }

  @Override
  public boolean reactsToClickOutside() {
    return false;
  }

  @Override
  public boolean isCursorOnBg(SolInputManager.Ptr ptr) {
    return false;
  }

  @Override
  public void onAdd(SolApplication cmp) {

  }

  @Override
  public void blurCustom(SolApplication cmp) {

  }
}
