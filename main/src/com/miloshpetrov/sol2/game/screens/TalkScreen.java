package com.miloshpetrov.sol2.game.screens;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Rectangle;
import com.miloshpetrov.sol2.SolCmp;
import com.miloshpetrov.sol2.common.Col;
import com.miloshpetrov.sol2.game.SolGame;
import com.miloshpetrov.sol2.game.ship.SolShip;
import com.miloshpetrov.sol2.menu.MenuLayout;
import com.miloshpetrov.sol2.ui.*;

import java.util.ArrayList;
import java.util.List;

public class TalkScreen implements SolUiScreen {

  public static final float MAX_TALK_DIST = 2f;
  private final List<SolUiControl> myControls;
  private final SolUiControl mySellCtrl;
  private final SolUiControl myBuyCtrl;
  private final SolUiControl myShipsCtrl;
  private final SolUiControl myHireCtrl;
  private final Rectangle myBg;
  private final SolUiControl myCloseCtrl;
  private SolShip myTarget;

  public TalkScreen(MenuLayout menuLayout) {
    myControls = new ArrayList<SolUiControl>();

    mySellCtrl = new SolUiControl(menuLayout.buttonRect(-1, 0), Input.Keys.S);
    mySellCtrl.setDisplayName("Sell");
    myControls.add(mySellCtrl);

    myBuyCtrl = new SolUiControl(menuLayout.buttonRect(-1, 1), Input.Keys.B);
    myBuyCtrl.setDisplayName("Buy");
    myControls.add(myBuyCtrl);

    myShipsCtrl = new SolUiControl(menuLayout.buttonRect(-1, 2), Input.Keys.C);
    myShipsCtrl.setDisplayName("Change Ship");
    myControls.add(myShipsCtrl);

    myHireCtrl = new SolUiControl(menuLayout.buttonRect(-1, 3), Input.Keys.H);
    myHireCtrl.setDisplayName("Hire");
    myControls.add(myHireCtrl);

    myCloseCtrl = new SolUiControl(menuLayout.buttonRect(-1, 4), Input.Keys.ESCAPE);
    myCloseCtrl.setDisplayName("Close");
    myControls.add(myCloseCtrl);

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
    if (myCloseCtrl.isJustOff() || isTargetFar(hero))
    {
      inputMan.setScreen(cmp, g.getScreens().mainScreen);
      return;
    }

    boolean sellsShips = myTarget.getHull().config == g.getHullConfigs().getConfig("station");
    myShipsCtrl.setEnabled(sellsShips);
    myHireCtrl.setEnabled(sellsShips);

    InventoryScreen is = g.getScreens().inventoryScreen;
    boolean sell = mySellCtrl.isJustOff();
    boolean buy = myBuyCtrl.isJustOff();
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
  public void drawPre(UiDrawer uiDrawer, SolCmp cmp) {
    uiDrawer.draw(myBg, Col.B75);
  }

  @Override
  public boolean isCursorOnBg(SolInputMan.Ptr ptr) {
    return myBg.contains(ptr.x, ptr.y);
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

  public void setTarget(SolShip target) {
    myTarget = target;
  }

  public SolShip getTarget() {
    return myTarget;
  }
}
