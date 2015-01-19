package com.miloshpetrov.sol2.menu;

import com.miloshpetrov.sol2.SolCmp;
import com.miloshpetrov.sol2.common.Col;
import com.miloshpetrov.sol2.ui.*;

import java.util.ArrayList;
import java.util.List;

public class LoadingScreen implements SolUiScreen {
  private final ArrayList<SolUiControl> myControls;
  private boolean myTut;
  private boolean myUsePrevShip;

  public LoadingScreen() {
    myControls = new ArrayList<SolUiControl>();
  }

  @Override
  public List<SolUiControl> getControls() {
    return myControls;
  }

  @Override
  public void onAdd(SolCmp cmp) {
  }

  @Override
  public void updateCustom(SolCmp cmp, SolInputMan.Ptr[] ptrs, boolean clickedOutside) {
    cmp.startNewGame(myTut, myUsePrevShip);
  }

  @Override
  public boolean isCursorOnBg(SolInputMan.Ptr ptr) {
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
    uiDrawer.drawString("Loading...", uiDrawer.r/2, .5f, FontSize.MENU, true, Col.W);
  }

  @Override
  public boolean reactsToClickOutside() {
    return false;
  }

  public void setMode(boolean tut, boolean usePrevShip) {
    myTut = tut;
    myUsePrevShip = usePrevShip;
  }
}
