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

public class ShowInventory implements InventoryOperations {

  private final List<SolUiControl> myControls;
  public final SolUiControl eq1Ctrl;
  public final SolUiControl eq2Ctrl;
  public final SolUiControl destroyCtrl;

  public ShowInventory(InventoryScreen inventoryScreen) {
    myControls = new ArrayList<SolUiControl>();

    eq1Ctrl = new SolUiControl(inventoryScreen.itemCtrl(0), Input.Keys.SPACE);
    eq1Ctrl.setDisplayName("Eq");
    myControls.add(eq1Ctrl);

    eq2Ctrl = new SolUiControl(inventoryScreen.itemCtrl(1), Input.Keys.CONTROL_LEFT);
    eq2Ctrl.setDisplayName("Eq2");
    myControls.add(eq2Ctrl);

    destroyCtrl = new SolUiControl(inventoryScreen.itemCtrl(2), Input.Keys.D);
    destroyCtrl.setDisplayName("Destroy");
    myControls.add(destroyCtrl);
  }

  @Override
  public List<SolUiControl> getControls() {
    return myControls;
  }

  @Override
  public void updateCustom(SolCmp cmp, SolInputMan.Ptr[] ptrs) {
    SolGame g = cmp.getGame();
    InventoryScreen is = g.getScreens().inventoryScreen;
    SolItem selected = is.getSelected();
    SolShip hero = g.getHero();

    eq1Ctrl.setDisplayName("---");
    eq1Ctrl.setEnabled(false);
    eq2Ctrl.setDisplayName("---");
    eq2Ctrl.setEnabled(false);
    destroyCtrl.setEnabled(false);

    if (selected == null || hero == null) {
      return;
    }

    destroyCtrl.setEnabled(true);
    if (destroyCtrl.isJustOff()) {
      ItemContainer ic = hero.getItemContainer();
      is.setSelected(ic.getNext(selected));
      ic.remove(selected);
      return;
    }

    Boolean equipped1 = hero.maybeUnequip(g, selected, false, false);
    boolean canEquip1 = hero.maybeEquip(g, selected, false, false);
    Boolean equipped2 = hero.maybeUnequip(g, selected, true, false);
    boolean canEquip2 = hero.maybeEquip(g, selected, true, false);

    if (equipped1 || canEquip1) {
      eq1Ctrl.setDisplayName(equipped1 ? "Unequip" : canEquip2 ? "Set Primary" : "Equip");
      eq1Ctrl.setEnabled(true);
    }
    if (equipped2 || canEquip2) {
      eq2Ctrl.setDisplayName(equipped2 ? "Unequip" : canEquip1 ? "Set Secondary" : "Equip");
      eq2Ctrl.setEnabled(true);
    }
    if (eq1Ctrl.isJustOff()) {
      if (equipped1) hero.maybeUnequip(g, selected, false, true);
      else hero.maybeEquip(g, selected, false, true);
    }
    if (eq2Ctrl.isJustOff()) {
      if (equipped2) hero.maybeUnequip(g, selected, true, true);
      else hero.maybeEquip(g, selected, true, true);
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
    return -1;
  }
}
