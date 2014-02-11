package com.miloshpetrov.sol2.game.screens;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import com.miloshpetrov.sol2.SolCmp;
import com.miloshpetrov.sol2.game.SolGame;
import com.miloshpetrov.sol2.game.gun.GunMount;
import com.miloshpetrov.sol2.game.item.*;
import com.miloshpetrov.sol2.game.ship.*;
import com.miloshpetrov.sol2.ui.*;

import java.util.ArrayList;
import java.util.List;

public class ChangeShip implements InventoryOperations {

  private final ArrayList<SolUiControl> myControls;
  private final SolUiControl myBuyCtrl;

  public ChangeShip(InventoryScreen inventoryScreen) {
    myControls = new ArrayList<SolUiControl>();

    myBuyCtrl = new SolUiControl(inventoryScreen.itemCtrl(0), Input.Keys.SPACE);
    myBuyCtrl.setDisplayName("Change");
    myControls.add(myBuyCtrl);
  }

  @Override
  public ItemContainer getItems(SolGame game) {
    return game.getChangeShips();
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
    SolItem selected = is.getSelected();
    SolShip hero = game.getHero();
    TalkScreen talkScreen = game.getScreens().talkScreen;
    SolShip target = talkScreen.getTarget();
    if (talkScreen.isTargetFar(hero)) {
      cmp.getInputMan().setScreen(cmp, game.getScreens().mainScreen);
      return;
    }
    boolean enabled = selected != null && hero.getMoney() >= selected.getPrice();
    myBuyCtrl.setDisplayName(enabled ? "Change" : "---");
    myBuyCtrl.setEnabled(enabled);
    if (!enabled) return;
    if (myBuyCtrl.isJustOff()) {
      hero.setMoney(hero.getMoney() - selected.getPrice());
      changeShip(game, hero, (ShipItem) selected);
    }
  }

  private void changeShip(SolGame game, SolShip hero, ShipItem selected) {
    HullConfig newConfig = selected.getConfig();
    ShipHull hull = hero.getHull();
    GunMount m1 = hull.getGunMount(false);
    GunMount m2 = hull.getGunMount(true);
    EngineItem engine = hull.getEngine();
    EngineItem newEngine = hull.config.type == newConfig.type ? engine : null;
    SolShip newHero = game.getShipBuilder().build(game, hero.getPos(), new Vector2(), hero.getAngle(), 0, hero.getPilot(),
      hero.getItemContainer(), newConfig, newConfig.maxLife, m1.isFixed(), m2.isFixed(), m1.getGun(), m2.getGun(), null,
      newEngine, new ShipRepairer(), hero.getMoney(), hero.getTradeContainer(), hero.getShield(), hero.getArmor());
    game.getObjMan().removeObjDelayed(hero);
    game.getObjMan().addObjDelayed(newHero);
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
}
