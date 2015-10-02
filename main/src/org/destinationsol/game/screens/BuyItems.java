package org.destinationsol.game.screens;

import org.destinationsol.GameOptions;
import org.destinationsol.SolApplication;
import org.destinationsol.game.SolGame;
import org.destinationsol.game.item.ItemContainer;
import org.destinationsol.game.item.SolItem;
import org.destinationsol.game.ship.SolShip;
import org.destinationsol.ui.SolInputManager;
import org.destinationsol.ui.SolUiControl;
import org.destinationsol.ui.UiDrawer;

import java.util.ArrayList;
import java.util.List;

public class BuyItems implements InventoryOperations {

  private final ArrayList<SolUiControl> myControls;
  public final SolUiControl buyCtrl;

  public BuyItems(InventoryScreen inventoryScreen, GameOptions gameOptions) {
    myControls = new ArrayList<SolUiControl>();

    buyCtrl = new SolUiControl(inventoryScreen.itemCtrl(0), true, gameOptions.getKeyBuyItem());
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
  public String getHeader() {
    return "Buy:";
  }

  @Override
  public List<SolUiControl> getControls() {
    return myControls;
  }

  @Override
  public void updateCustom(SolApplication cmp, SolInputManager.Ptr[] ptrs, boolean clickedOutside) {
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
  public boolean isCursorOnBg(SolInputManager.Ptr ptr) {
    return false;
  }

  @Override
  public void onAdd(SolApplication cmp) {

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
  public void blurCustom(SolApplication cmp) {

  }
}
