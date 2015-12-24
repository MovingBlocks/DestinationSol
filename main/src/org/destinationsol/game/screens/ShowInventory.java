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

public class ShowInventory implements InventoryOperations {

  private final List<SolUiControl> myControls;
  public final SolUiControl eq1Ctrl;
  public final SolUiControl eq2Ctrl;
  public final SolUiControl dropCtrl;

  public ShowInventory(InventoryScreen inventoryScreen, GameOptions gameOptions) {
    myControls = new ArrayList<SolUiControl>();

    eq1Ctrl = new SolUiControl(inventoryScreen.itemCtrl(0), true, gameOptions.getKeyEquip());
    eq1Ctrl.setDisplayName("Eq");
    myControls.add(eq1Ctrl);

    eq2Ctrl = new SolUiControl(inventoryScreen.itemCtrl(1), true, gameOptions.getKeyEquip2());
    eq2Ctrl.setDisplayName("Eq2");
    myControls.add(eq2Ctrl);

    dropCtrl = new SolUiControl(inventoryScreen.itemCtrl(2), true, gameOptions.getKeyDrop());
    dropCtrl.setDisplayName("Drop");
    myControls.add(dropCtrl);
  }

  @Override
  public List<SolUiControl> getControls() {
    return myControls;
  }

  @Override
  public void updateCustom(SolApplication cmp, SolInputManager.Ptr[] ptrs, boolean clickedOutside) {
    SolGame g = cmp.getGame();
    InventoryScreen is = g.getScreens().inventoryScreen;
    SolItem selItem = is.getSelectedItem();
    SolShip hero = g.getHero();

    eq1Ctrl.setDisplayName("---");
    eq1Ctrl.setEnabled(false);
    eq2Ctrl.setDisplayName("---");
    eq2Ctrl.setEnabled(false);
    dropCtrl.setEnabled(false);

    if (selItem == null || hero == null) {
      return;
    }


    dropCtrl.setEnabled(true);
    if (dropCtrl.isJustOff()) {
      ItemContainer ic = hero.getItemContainer();
      is.setSelected(ic.getSelectionAfterRemove(is.getSelected()));
      hero.dropItem(cmp.getGame(), selItem);
      return;
    }

    Boolean equipped1 = hero.maybeUnequip(g, selItem, false, false);
    boolean canEquip1 = hero.maybeEquip(g, selItem, false, false);
    Boolean equipped2 = hero.maybeUnequip(g, selItem, true, false);
    boolean canEquip2 = hero.maybeEquip(g, selItem, true, false);

    if (equipped1 || canEquip1) {
      eq1Ctrl.setDisplayName(equipped1 ? "Unequip" : "Equip");
      eq1Ctrl.setEnabled(true);
    }
    if (equipped2 || canEquip2) {
      eq2Ctrl.setDisplayName(equipped2 ? "Unequip" : "Set Gun 2");
      eq2Ctrl.setEnabled(true);
    }
    if (eq1Ctrl.isJustOff()) {
      if (equipped1) hero.maybeUnequip(g, selItem, false, true);
      else hero.maybeEquip(g, selItem, false, true);
    }
    if (eq2Ctrl.isJustOff()) {
      if (equipped2) hero.maybeUnequip(g, selItem, true, true);
      else hero.maybeEquip(g, selItem, true, true);
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

  @Override
  public String getHeader() {
    return "Items:";
  }
}
