package com.miloshpetrov.sol2.game.screens;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Rectangle;
import com.miloshpetrov.sol2.SolCmp;
import com.miloshpetrov.sol2.common.Col;
import com.miloshpetrov.sol2.game.SolGame;
import com.miloshpetrov.sol2.game.ship.HullConfig;
import com.miloshpetrov.sol2.game.ship.SolShip;
import com.miloshpetrov.sol2.menu.MenuLayout;
import com.miloshpetrov.sol2.ui.*;

import java.util.ArrayList;
import java.util.List;

public class TalkScreen implements SolUiScreen {

  public static final float MAX_TALK_DIST = 1f;
  private final List<SolUiControl> myControls;
  private final SolUiControl mySellCtrl;
  public final SolUiControl buyCtrl;
  private final SolUiControl myShipsCtrl;
  private final SolUiControl myHireCtrl;
  private final Rectangle myBg;
  public final SolUiControl closeCtrl;
  private SolShip myTarget;

  public TalkScreen(MenuLayout menuLayout) {
    myControls = new ArrayList<SolUiControl>();

    mySellCtrl = new SolUiControl(menuLayout.buttonRect(-1, 0), true, Input.Keys.S);
    mySellCtrl.setDisplayName("Sell");
    myControls.add(mySellCtrl);

    buyCtrl = new SolUiControl(menuLayout.buttonRect(-1, 1), true, Input.Keys.B);
    buyCtrl.setDisplayName("Buy");
    myControls.add(buyCtrl);

    myShipsCtrl = new SolUiControl(menuLayout.buttonRect(-1, 2), true, Input.Keys.C);
    myShipsCtrl.setDisplayName("Change Ship");
    myControls.add(myShipsCtrl);

    myHireCtrl = new SolUiControl(menuLayout.buttonRect(-1, 3), true, Input.Keys.H);
    myHireCtrl.setDisplayName("Hire");
    myControls.add(myHireCtrl);

    closeCtrl = new SolUiControl(menuLayout.buttonRect(-1, 4), true, Input.Keys.ESCAPE);
    closeCtrl.setDisplayName("Close");
    myControls.add(closeCtrl);

    myBg = menuLayout.bg(-1, 5);
  }

  @Override
  public List<SolUiControl> getControls() {
    return myControls;
  }

  @Override
  public void updateCustom(SolCmp cmp, SolInputMan.Ptr[] ptrs) {
    SolGame g = cmp.getGame();
    SolShip hero = g.getHero();
    SolInputMan inputMan = cmp.getInputMan();
    if (closeCtrl.isJustOff() || isTargetFar(hero))
    {
      inputMan.setScreen(cmp, g.getScreens().mainScreen);
      return;
    }

    boolean station = myTarget.getHull().config.type == HullConfig.Type.STATION;
    myShipsCtrl.setEnabled(station);
    myHireCtrl.setEnabled(station);

    InventoryScreen is = g.getScreens().inventoryScreen;
    boolean sell = mySellCtrl.isJustOff();
    boolean buy = buyCtrl.isJustOff();
    boolean sellShips = myShipsCtrl.isJustOff();
    boolean hire = myHireCtrl.isJustOff();
    if (sell || buy || sellShips || hire) {
      is.setOperations(sell ? is.sellItems : buy ? is.buyItems : sellShips ? is.changeShip : is.hireShips);
      inputMan.setScreen(cmp, g.getScreens().mainScreen);
      inputMan.addScreen(cmp, is);
    }
  }

  public boolean isTargetFar(SolShip hero) {
    if (hero == null || myTarget == null || myTarget.getLife() <= 0) return true;
    float dst = myTarget.getPos().dst(hero.getPos()) - hero.getHull().config.approxRadius - myTarget.getHull().config.approxRadius;
    return MAX_TALK_DIST < dst;
  }

  @Override
  public void drawBg(UiDrawer uiDrawer, SolCmp cmp) {
    uiDrawer.draw(myBg, Col.B75);
  }

  @Override
  public void drawImgs(UiDrawer uiDrawer, SolCmp cmp) {

  }

  @Override
  public void drawText(UiDrawer uiDrawer, SolCmp cmp) {
  }

  @Override
  public boolean isCursorOnBg(SolInputMan.Ptr ptr) {
    return myBg.contains(ptr.x, ptr.y);
  }

  @Override
  public void onAdd(SolCmp cmp) {
  }

  @Override
  public void blurCustom(SolCmp cmp) {

  }

  public void setTarget(SolShip target) {
    myTarget = target;
  }

  public SolShip getTarget() {
    return myTarget;
  }
}
