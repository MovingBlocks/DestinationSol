package com.miloshpetrov.sol2.menu;

import com.badlogic.gdx.Input;
import com.miloshpetrov.sol2.SolCmp;
import com.miloshpetrov.sol2.common.Col;
import com.miloshpetrov.sol2.ui.*;

import java.util.ArrayList;
import java.util.List;

public class ResoScreen implements SolUiScreen {

  private final ArrayList<SolUiControl> myControls;
  private final SolUiControl myCloseCtrl;
  private final SolUiControl myResoCtrl;
  private final SolUiControl myFsCtrl;

  public ResoScreen(MenuLayout menuLayout) {
    myControls = new ArrayList<SolUiControl>();

    myResoCtrl = new SolUiControl(menuLayout.buttonRect(-1, 2), true, Input.Keys.ESCAPE);
    myResoCtrl.setDisplayName("Resolution");
    myControls.add(myResoCtrl);

    myFsCtrl = new SolUiControl(menuLayout.buttonRect(-1, 3), true, Input.Keys.ESCAPE);
    myFsCtrl.setDisplayName("Fullscreen");
    myControls.add(myFsCtrl);

    myCloseCtrl = new SolUiControl(menuLayout.buttonRect(-1, 4), true, Input.Keys.ESCAPE);
    myCloseCtrl.setDisplayName("Back");
    myControls.add(myCloseCtrl);
  }

  @Override
  public List<SolUiControl> getControls() {
    return myControls;
  }

  @Override
  public void updateCustom(SolCmp cmp, SolInputMan.Ptr[] ptrs, boolean clickedOutside) {
    SolInputMan im = cmp.getInputMan();
    if (myCloseCtrl.isJustOff()) {
      im.setScreen(cmp, cmp.getMenuScreens().options);
      return;
    }

    GameOptions options = cmp.getOptions();
    myResoCtrl.setDisplayName(options.x + "x" + options.y);
    if (myResoCtrl.isJustOff()) {
      options.advanceReso();
    }
    myFsCtrl.setDisplayName(options.fullscreen ? "Fullscreen" : "Windowed");
    if (myFsCtrl.isJustOff()) {
      options.advanceFullscreen();
    }
  }

  @Override
  public void drawBg(UiDrawer uiDrawer, SolCmp cmp) {
  }

  @Override
  public void drawImgs(UiDrawer uiDrawer, SolCmp cmp) {

  }

  @Override
  public void drawText(UiDrawer uiDrawer, SolCmp cmp) {
    uiDrawer.drawString("Please restart to apply changes", .5f * uiDrawer.r, .3f, FontSize.MENU, true, Col.W);
  }

  @Override
  public boolean reactsToClickOutside() {
    return false;
  }

  @Override
  public boolean isCursorOnBg(SolInputMan.Ptr ptr) {
    return false;
  }

  @Override
  public void onAdd(SolCmp cmp) {
  }

  @Override
  public void blurCustom(SolCmp cmp) {

  }

}
