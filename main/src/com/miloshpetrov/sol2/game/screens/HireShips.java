package com.miloshpetrov.sol2.game.screens;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import com.miloshpetrov.sol2.Const;
import com.miloshpetrov.sol2.SolCmp;
import com.miloshpetrov.sol2.common.SolMath;
import com.miloshpetrov.sol2.game.*;
import com.miloshpetrov.sol2.game.input.AiPilot;
import com.miloshpetrov.sol2.game.input.Guardian;
import com.miloshpetrov.sol2.game.item.*;
import com.miloshpetrov.sol2.game.ship.HullConfig;
import com.miloshpetrov.sol2.game.ship.SolShip;
import com.miloshpetrov.sol2.ui.*;

import java.util.ArrayList;
import java.util.List;

public class HireShips implements InventoryOperations {

  private final ArrayList<SolUiControl> myControls;
  private final SolUiControl myBuyCtrl;

  public HireShips(InventoryScreen inventoryScreen) {
    myControls = new ArrayList<SolUiControl>();

    myBuyCtrl = new SolUiControl(inventoryScreen.itemCtrl(0), true, Input.Keys.SPACE);
    myBuyCtrl.setDisplayName("Hire");
    myControls.add(myBuyCtrl);
  }

  @Override
  public ItemContainer getItems(SolGame game) {
    return game.getScreens().talkScreen.getTarget().getTradeContainer().getMercs();
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
    if (talkScreen.isTargetFar(hero)) {
      cmp.getInputMan().setScreen(cmp, game.getScreens().mainScreen);
      return;
    }
    boolean enabled = selected != null && hero.getMoney() >= selected.getPrice();
    myBuyCtrl.setDisplayName(enabled ? "Hire" : "---");
    myBuyCtrl.setEnabled(enabled);
    if (!enabled) return;
    if (myBuyCtrl.isJustOff()) {
      hero.setMoney(hero.getMoney() - selected.getPrice());
      hireShip(game, hero, (MercItem) selected);
    }
  }

  private void hireShip(SolGame game, SolShip hero, MercItem selected) {
    ShipConfig config = selected.getConfig();
    Guardian dp = new Guardian(game, config.hull, hero.getPilot(), hero.getPos(), hero.getHull().config);
    AiPilot pilot = new AiPilot(dp, true, Fraction.LAANI, false, "Merc", Const.AI_DET_DIST);
    Vector2 pos = getPos(game, hero, config.hull);
    SolShip merc = game.getShipBuilder().buildNew(game, pos, new Vector2(), 0, 0, pilot, config.items, config.hull, null, true, config.money, null);
    game.getObjMan().addObjDelayed(merc);
  }

  private Vector2 getPos(SolGame game, SolShip hero, HullConfig hull) {
    Vector2 pos = new Vector2();
    float dist = hero.getHull().config.approxRadius + Guardian.DIST + hull.approxRadius;
    for (int i = 0; i < 50; i++) {
      SolMath.fromAl(pos, SolMath.rnd(180), dist);
      pos.add(hero.getPos());
      if (game.isPlaceEmpty(pos)) break;
    }
    return pos;
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
