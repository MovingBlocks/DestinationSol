package org.destinationsol.game.screens;

import com.badlogic.gdx.math.Rectangle;
import org.destinationsol.GameOptions;
import org.destinationsol.SolApplication;
import org.destinationsol.common.SolColor;
import org.destinationsol.game.SolGame;
import org.destinationsol.game.ship.SolShip;
import org.destinationsol.game.ship.hulls.HullConfig;
import org.destinationsol.menu.MenuLayout;
import org.destinationsol.ui.SolInputManager;
import org.destinationsol.ui.SolUiControl;
import org.destinationsol.ui.SolUiScreen;
import org.destinationsol.ui.UiDrawer;

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

  public TalkScreen(MenuLayout menuLayout, GameOptions gameOptions) {
    myControls = new ArrayList<SolUiControl>();

    mySellCtrl = new SolUiControl(menuLayout.buttonRect(-1, 0), true, gameOptions.getKeySellMenu());
    mySellCtrl.setDisplayName("Sell");
    myControls.add(mySellCtrl);

    buyCtrl = new SolUiControl(menuLayout.buttonRect(-1, 1), true, gameOptions.getKeyBuyMenu());
    buyCtrl.setDisplayName("Buy");
    myControls.add(buyCtrl);

    myShipsCtrl = new SolUiControl(menuLayout.buttonRect(-1, 2), true, gameOptions.getKeyChangeShipMenu());
    myShipsCtrl.setDisplayName("Change Ship");
    myControls.add(myShipsCtrl);

    myHireCtrl = new SolUiControl(menuLayout.buttonRect(-1, 3), true, gameOptions.getKeyHireShipMenu());
    myHireCtrl.setDisplayName("Hire");
    myControls.add(myHireCtrl);

    closeCtrl = new SolUiControl(menuLayout.buttonRect(-1, 4), true, gameOptions.getKeyClose());
    closeCtrl.setDisplayName("Close");
    myControls.add(closeCtrl);

    myBg = menuLayout.bg(-1, 0, 5);
  }

  @Override
  public List<SolUiControl> getControls() {
    return myControls;
  }

  @Override
  public void updateCustom(SolApplication cmp, SolInputManager.Ptr[] ptrs, boolean clickedOutside) {
    if (clickedOutside) {
      closeCtrl.maybeFlashPressed(cmp.getOptions().getKeyClose());
      return;
    }
    SolGame g = cmp.getGame();
    SolShip hero = g.getHero();
    SolInputManager inputMan = cmp.getInputMan();
    if (closeCtrl.isJustOff() || isTargetFar(hero))
    {
      inputMan.setScreen(cmp, g.getScreens().mainScreen);
      return;
    }

    boolean station = myTarget.getHull().config.getType() == HullConfig.Type.STATION;
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
    float dst = myTarget.getPos().dst(hero.getPos()) - hero.getHull().config.getApproxRadius() - myTarget.getHull().config.getApproxRadius();
    return MAX_TALK_DIST < dst;
  }

  @Override
  public void drawBg(UiDrawer uiDrawer, SolApplication cmp) {
    uiDrawer.draw(myBg, SolColor.UI_BG);
  }

  @Override
  public void drawImgs(UiDrawer uiDrawer, SolApplication cmp) {

  }

  @Override
  public void drawText(UiDrawer uiDrawer, SolApplication cmp) {
  }

  @Override
  public boolean reactsToClickOutside() {
    return true;
  }

  @Override
  public boolean isCursorOnBg(SolInputManager.Ptr ptr) {
    return myBg.contains(ptr.x, ptr.y);
  }

  @Override
  public void onAdd(SolApplication cmp) {
  }

  @Override
  public void blurCustom(SolApplication cmp) {

  }

  public void setTarget(SolShip target) {
    myTarget = target;
  }

  public SolShip getTarget() {
    return myTarget;
  }
}
