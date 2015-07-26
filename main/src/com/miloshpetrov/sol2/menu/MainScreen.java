package com.miloshpetrov.sol2.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Rectangle;
import com.miloshpetrov.sol2.GameOptions;
import com.miloshpetrov.sol2.SolApplication;
import com.miloshpetrov.sol2.TextureManager;
import com.miloshpetrov.sol2.common.SolColor;
import com.miloshpetrov.sol2.game.DebugOptions;
import com.miloshpetrov.sol2.ui.*;

import java.util.ArrayList;
import java.util.List;

public class MainScreen implements SolUiScreen {
  public static final float CREDITS_BTN_W = .15f;
  public static final float CREDITS_BTN_H = .07f;

  private final ArrayList<SolUiControl> myControls;
  private final SolUiControl myTutCtrl;
  private final SolUiControl myOptionsCtrl;
  private final SolUiControl myExitCtrl;
  private final SolUiControl myNewGameCtrl;
  private final SolUiControl myCreditsCtrl;
  private final TextureAtlas.AtlasRegion myTitleTex;
  private final boolean isMobile;

  public MainScreen(MenuLayout menuLayout, TextureManager textureManager, boolean mobile, float r, GameOptions gameOptions) {
    isMobile = mobile;
    myControls = new ArrayList<SolUiControl>();

    myTutCtrl = new SolUiControl(menuLayout.buttonRect(-1, 1), true, Input.Keys.T);
    myTutCtrl.setDisplayName("Tutorial");
    myControls.add(myTutCtrl);

    myNewGameCtrl = new SolUiControl(menuLayout.buttonRect(-1, 2), true, gameOptions.getKeyShoot());
    myNewGameCtrl.setDisplayName("New Game");
    myControls.add(myNewGameCtrl);

    myOptionsCtrl = new SolUiControl(mobile ? null : menuLayout.buttonRect(-1, 3), true, Input.Keys.O);
    myOptionsCtrl.setDisplayName("Options");
    myControls.add(myOptionsCtrl);

    myExitCtrl = new SolUiControl(menuLayout.buttonRect(-1, 4), true);
    myExitCtrl.setDisplayName("Exit");
    myControls.add(myExitCtrl);

    myCreditsCtrl = new SolUiControl(creditsBtnRect(r), true, Input.Keys.C);
    myCreditsCtrl.setDisplayName("Credits");
    myControls.add(myCreditsCtrl);

    myTitleTex = textureManager.getTex("ui/title", null);
  }

  public static Rectangle creditsBtnRect(float r) {
    return new Rectangle(r - CREDITS_BTN_W, 1 - CREDITS_BTN_H, CREDITS_BTN_W, CREDITS_BTN_H);
  }

  public List<SolUiControl> getControls() {
    return myControls;
  }

  @Override
  public void updateCustom(SolApplication cmp, SolInputManager.Ptr[] ptrs, boolean clickedOutside) {
    if (cmp.getOptions().controlType == GameOptions.CONTROL_CONTROLLER) {
      myTutCtrl.setEnabled(false);
    } else {
      myTutCtrl.setEnabled(true);
    }

    if (myTutCtrl.isJustOff()) {
      cmp.loadNewGame(true, false);
      return;
    }
    SolInputManager im = cmp.getInputMan();
    MenuScreens screens = cmp.getMenuScreens();
    if (myNewGameCtrl.isJustOff()) {
      im.setScreen(cmp, screens.newGame);
      return;
    }
    if (myOptionsCtrl.isJustOff()) {
      im.setScreen(cmp, screens.options);
      return;
    }
    if (myExitCtrl.isJustOff()) {
      // Save the settings on exit, but not on mobile as settings don't exist there.
      if (isMobile == false) {
        cmp.getOptions().save();
      }
      Gdx.app.exit();
      return;
    }
    if (myCreditsCtrl.isJustOff()) {
      im.setScreen(cmp, screens.credits);
    }
  }

  @Override
  public boolean isCursorOnBg(SolInputManager.Ptr ptr) {
    return false;
  }

  @Override
  public void onAdd(SolApplication cmp) {

  }

  @Override
  public void drawBg(UiDrawer uiDrawer, SolApplication cmp) {
  }

  @Override
  public void drawImgs(UiDrawer uiDrawer, SolApplication cmp) {
    float sz = .55f;
    if (!DebugOptions.PRINT_BALANCE) uiDrawer.draw(myTitleTex, sz, sz, sz/2, sz/2, uiDrawer.r/2, sz/2, 0, SolColor.W);
  }

  @Override
  public void drawText(UiDrawer uiDrawer, SolApplication cmp) {
  }

  @Override
  public boolean reactsToClickOutside() {
    return false;
  }

  @Override
  public void blurCustom(SolApplication cmp) {

  }
}
