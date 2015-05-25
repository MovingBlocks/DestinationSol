package com.miloshpetrov.sol2.menu;

import com.badlogic.gdx.Input;
import com.miloshpetrov.sol2.SolApplication;
import com.miloshpetrov.sol2.game.SaveManager;
import com.miloshpetrov.sol2.ui.*;

import java.util.ArrayList;
import java.util.List;

public class NewGameScreen implements SolUiScreen {
  private final ArrayList<SolUiControl> myControls;
  private final SolUiControl myBackCtrl;
  private final SolUiControl myPrevCtrl;
  private final SolUiControl myNewCtrl;

  public NewGameScreen(MenuLayout menuLayout) {
    myControls = new ArrayList<SolUiControl>();

    myPrevCtrl = new SolUiControl(menuLayout.buttonRect(-1, 1), true, Input.Keys.SPACE);
    myPrevCtrl.setDisplayName("Previous Ship");
    myControls.add(myPrevCtrl);

    myNewCtrl = new SolUiControl(menuLayout.buttonRect(-1, 2), true);
    myNewCtrl.setDisplayName("New Ship");
    myControls.add(myNewCtrl);

    myBackCtrl = new SolUiControl(menuLayout.buttonRect(-1, 4), true, Input.Keys.ESCAPE);
    myBackCtrl.setDisplayName("Cancel");
    myControls.add(myBackCtrl);

  }

  @Override
  public List<SolUiControl> getControls() {
    return myControls;
  }

  @Override
  public void onAdd(SolApplication cmp) {
    myPrevCtrl.setEnabled(SaveManager.hasPrevShip());
  }

  @Override
  public void updateCustom(SolApplication cmp, SolInputManager.Ptr[] ptrs, boolean clickedOutside) {
    MenuScreens screens = cmp.getMenuScreens();
    SolInputManager im = cmp.getInputMan();
    if (myBackCtrl.isJustOff()) {
      im.setScreen(cmp, screens.main);
      return;
    }
    if (myPrevCtrl.isJustOff()) {
      cmp.loadNewGame(false, true);
      return;
    }
    if (myNewCtrl.isJustOff()) {
      if (!myPrevCtrl.isEnabled()) {
        cmp.loadNewGame(false, false);
      } else {
        im.setScreen(cmp, screens.newShip);
      }
    }
  }

  @Override
  public boolean isCursorOnBg(SolInputManager.Ptr ptr) {
    return true;
  }

  @Override
  public void blurCustom(SolApplication cmp) {
  }

  @Override
  public void drawBg(UiDrawer uiDrawer, SolApplication cmp) {
  }

  @Override
  public void drawImgs(UiDrawer uiDrawer, SolApplication cmp) {
  }

  @Override
  public void drawText(UiDrawer uiDrawer, SolApplication cmp) {
  }

  @Override
  public boolean reactsToClickOutside() {
    return false;
  }

}
