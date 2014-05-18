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

public class SellItems implements InventoryOperations {

  public static float PERC = .7f;
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
  public List<SolUiControl> getControls() {
    return myControls;
  }

  @Override
  public void updateCustom(SolCmp cmp, SolInputMan.Ptr[] ptrs) {
    SolGame game = cmp.getGame();
    InventoryScreen is = game.getScreens().inventoryScreen;
    SolItem selected = is.getSelected();
    TalkScreen talkScreen = game.getScreens().talkScreen;
    SolShip target = talkScreen.getTarget();
    SolShip hero = game.getHero();
    if (talkScreen.isTargetFar(hero)) {
      cmp.getInputMan().setScreen(cmp, game.getScreens().mainScreen);
      return;
    }
    boolean enabled = selected != null && target.getTradeContainer().getItems().canAdd();
    sellCtrl.setDisplayName(enabled ? "Sell" : "---");
    sellCtrl.setEnabled(enabled);
    if (!enabled) return;
    if (sellCtrl.isJustOff()) {
      ItemContainer ic = hero.getItemContainer();
      is.setSelected(ic.getNext(selected));
      ic.remove(selected);
      target.getTradeContainer().getItems().add(selected);
      hero.setMoney(hero.getMoney() + selected.getPrice() * PERC);
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
