/*
 * Copyright 2015 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
 
package com.miloshpetrov.sol2.menu;

import com.miloshpetrov.sol2.GameOptions;
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

  public NewGameScreen(MenuLayout menuLayout, GameOptions gameOptions) {
    myControls = new ArrayList<SolUiControl>();

    myPrevCtrl = new SolUiControl(menuLayout.buttonRect(-1, 1), true, gameOptions.getKeyShoot());
    myPrevCtrl.setDisplayName("Previous Ship");
    myControls.add(myPrevCtrl);

    myNewCtrl = new SolUiControl(menuLayout.buttonRect(-1, 2), true);
    myNewCtrl.setDisplayName("New Ship");
    myControls.add(myNewCtrl);

    myBackCtrl = new SolUiControl(menuLayout.buttonRect(-1, 4), true, gameOptions.getKeyEscape());
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
