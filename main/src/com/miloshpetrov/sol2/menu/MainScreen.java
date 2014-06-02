package com.miloshpetrov.sol2.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.miloshpetrov.sol2.SolCmp;
import com.miloshpetrov.sol2.TexMan;
import com.miloshpetrov.sol2.common.Col;
import com.miloshpetrov.sol2.game.DebugOptions;
import com.miloshpetrov.sol2.save.SaveMan;
import com.miloshpetrov.sol2.ui.*;

import java.util.ArrayList;
import java.util.List;

public class MainScreen implements SolUiScreen {

  private final ArrayList<SolUiControl> myControls;
  private final SolUiControl myTutCtrl;
  private final SolUiControl myOptionsCtrl;
  private final SolUiControl myExitCtrl;
  private final SolUiControl myNewGameCtrl;
  private final TextureAtlas.AtlasRegion myTitleTex;

  public MainScreen(MenuLayout menuLayout, SaveMan saveMan, TexMan texMan, boolean mobile) {
    myControls = new ArrayList<SolUiControl>();

    myTutCtrl = new SolUiControl(menuLayout.buttonRect(-1, 1), true);
    myTutCtrl.setDisplayName("Tutorial");
    myTutCtrl.setEnabled(!mobile);
    myControls.add(myTutCtrl);

    myNewGameCtrl = new SolUiControl(menuLayout.buttonRect(-1, 2), true, Input.Keys.SPACE);
    myNewGameCtrl.setDisplayName("New Game");
    myControls.add(myNewGameCtrl);

    myOptionsCtrl = new SolUiControl(menuLayout.buttonRect(-1, 3), true);
    myOptionsCtrl.setDisplayName("Options");
    myControls.add(myOptionsCtrl);

    myExitCtrl = new SolUiControl(menuLayout.buttonRect(-1, 4), true);
    myExitCtrl.setDisplayName("Exit");
    myControls.add(myExitCtrl);

    myTitleTex = texMan.getTex("ui/title", null);
  }

  public List<SolUiControl> getControls() {
    return myControls;
  }

  @Override
  public void updateCustom(SolCmp cmp, SolInputMan.Ptr[] ptrs) {
    if (myTutCtrl.isJustOff()) {
      cmp.startNewGame(true);
    } else if (myNewGameCtrl.isJustOff()) {
      cmp.startNewGame(false);
    } else if (myOptionsCtrl.isJustOff()) {
      cmp.getInputMan().setScreen(cmp, cmp.getMenuScreens().options);
    } else if (myExitCtrl.isJustOff()) {
      Gdx.app.exit();
    }
  }

  @Override
  public boolean isCursorOnBg(SolInputMan.Ptr ptr) {
    return false;
  }

  @Override
  public void onAdd(SolCmp cmp) {

  }

  @Override
  public void drawBg(UiDrawer uiDrawer, SolCmp cmp) {
  }

  @Override
  public void drawImgs(UiDrawer uiDrawer, SolCmp cmp) {
    float sz = .55f;
    if (!DebugOptions.PRINT_BALANCE) uiDrawer.draw(myTitleTex, sz, sz, sz/2, sz/2, uiDrawer.r/2, sz/2, 0, Col.W);
  }

  @Override
  public void drawText(UiDrawer uiDrawer, SolCmp cmp) {
  }

  @Override
  public void blurCustom(SolCmp cmp) {

  }
}
