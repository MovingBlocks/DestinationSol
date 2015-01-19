package com.miloshpetrov.sol2.game.screens;

import com.badlogic.gdx.Input;
import com.miloshpetrov.sol2.SolCmp;
import com.miloshpetrov.sol2.common.Col;
import com.miloshpetrov.sol2.game.SolGame;
import com.miloshpetrov.sol2.GameOptions;
import com.miloshpetrov.sol2.menu.MenuLayout;
import com.miloshpetrov.sol2.ui.*;

import java.util.ArrayList;
import java.util.List;

public class MenuScreen implements SolUiScreen {
  private final List<SolUiControl> myControls;
  private final SolUiControl myCloseCtrl;
  private final SolUiControl myExitCtrl;
  private final SolUiControl myRespawnCtrl;
  private final SolUiControl myVolCtrl;

  public MenuScreen(MenuLayout menuLayout) {
    myControls = new ArrayList<SolUiControl>();

    myVolCtrl = new SolUiControl(menuLayout.buttonRect(-1, 1), true);
    myVolCtrl.setDisplayName("Vol");
    myControls.add(myVolCtrl);
    myRespawnCtrl = new SolUiControl(menuLayout.buttonRect(-1, 2), true);
    myRespawnCtrl.setDisplayName("Respawn");
    myControls.add(myRespawnCtrl);
    myExitCtrl = new SolUiControl(menuLayout.buttonRect(-1, 3), true);
    myExitCtrl.setDisplayName("Exit");
    myControls.add(myExitCtrl);
    myCloseCtrl = new SolUiControl(menuLayout.buttonRect(-1, 4), true, Input.Keys.ESCAPE);
    myCloseCtrl.setDisplayName("Resume");
    myControls.add(myCloseCtrl);
  }

  @Override
  public List<SolUiControl> getControls() {
    return myControls;
  }

  @Override
  public void updateCustom(SolCmp cmp, SolInputMan.Ptr[] ptrs, boolean clickedOutside) {
    SolGame g = cmp.getGame();
    g.setPaused(true);
    SolInputMan im = cmp.getInputMan();
    GameOptions options = cmp.getOptions();
    myVolCtrl.setDisplayName("Volume: " + getVolName(options));
    if (myVolCtrl.isJustOff()) {
      options.advanceVolMul();
    }
    if (myRespawnCtrl.isJustOff()) {
      g.respawn();
      im.setScreen(cmp, g.getScreens().mainScreen);
      g.setPaused(false);
    }
    if (myExitCtrl.isJustOff()) {
      cmp.finishGame();
    }
    if (myCloseCtrl.isJustOff()) {
      g.setPaused(false);
      im.setScreen(cmp, g.getScreens().mainScreen);
    }
  }

  private String getVolName(GameOptions options) {
    float volMul = options.volMul;
    if (volMul == 0) return "Off";
    if (volMul < .4f) return "Low";
    if (volMul < .7f) return "High";
    return "Max";
  }

  @Override
  public void drawBg(UiDrawer uiDrawer, SolCmp cmp) {
    uiDrawer.draw(uiDrawer.filler, Col.UI_BG);
  }

  @Override
  public void drawImgs(UiDrawer uiDrawer, SolCmp cmp) {

  }

  @Override
  public void drawText(UiDrawer uiDrawer, SolCmp cmp) {
  }

  @Override
  public boolean reactsToClickOutside() {
    return false;
  }

  @Override
  public boolean isCursorOnBg(SolInputMan.Ptr ptr) {
    return true;
  }

  @Override
  public void onAdd(SolCmp cmp) {

  }

  @Override
  public void blurCustom(SolCmp cmp) {

  }
}
