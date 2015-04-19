package com.miloshpetrov.sol2.menu;

import com.badlogic.gdx.Input;
import com.miloshpetrov.sol2.SolCmp;
import com.miloshpetrov.sol2.common.SolColor;
import com.miloshpetrov.sol2.ui.*;

import java.util.ArrayList;
import java.util.List;

public class NewShipScreen implements SolUiScreen {
  private final List<SolUiControl> myControls;
  private final SolUiControl myOkCtrl;
  public final SolUiControl myCancelCtrl;

  public NewShipScreen(MenuLayout menuLayout) {
    myControls = new ArrayList<SolUiControl>();
    myOkCtrl = new SolUiControl(menuLayout.buttonRect(-1, 1), true, Input.Keys.H);
    myOkCtrl.setDisplayName("OK");
    myControls.add(myOkCtrl);

    myCancelCtrl = new SolUiControl(menuLayout.buttonRect(-1, 4), true, Input.Keys.ESCAPE);
    myCancelCtrl.setDisplayName("Cancel");
    myControls.add(myCancelCtrl);
  }

  @Override
  public List<SolUiControl> getControls() {
    return myControls;
  }

  @Override
  public void onAdd(SolCmp cmp) {
  }

  @Override
  public void updateCustom(SolCmp cmp, SolInputManager.Ptr[] ptrs, boolean clickedOutside) {
    if (myCancelCtrl.isJustOff()) {
      cmp.getInputMan().setScreen(cmp, cmp.getMenuScreens().newGame);
      return;
    }
    if (myOkCtrl.isJustOff()) {
      cmp.loadNewGame(false, false);
    }
  }

  @Override
  public boolean isCursorOnBg(SolInputManager.Ptr ptr) {
    return false;
  }

  @Override
  public void blurCustom(SolCmp cmp) {

  }

  @Override
  public void drawBg(UiDrawer uiDrawer, SolCmp cmp) {

  }

  @Override
  public void drawImgs(UiDrawer uiDrawer, SolCmp cmp) {

  }

  @Override
  public void drawText(UiDrawer uiDrawer, SolCmp cmp) {
    uiDrawer.drawString("This will erase your previous ship", .5f * uiDrawer.r, .3f, FontSize.MENU, true, SolColor.W);
  }

  @Override
  public boolean reactsToClickOutside() {
    return false;
  }
}
