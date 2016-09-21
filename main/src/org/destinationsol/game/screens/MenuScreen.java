/*
 * Copyright 2015 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.destinationsol.game.screens;

import org.destinationsol.GameOptions;
import org.destinationsol.SolApplication;
import org.destinationsol.common.SolColor;
import org.destinationsol.game.SolGame;
import org.destinationsol.menu.MenuLayout;
import org.destinationsol.ui.SolInputManager;
import org.destinationsol.ui.SolUiControl;
import org.destinationsol.ui.SolUiScreen;
import org.destinationsol.ui.UiDrawer;

import java.util.ArrayList;
import java.util.List;

public class MenuScreen implements SolUiScreen {
  private final List<SolUiControl> myControls;
  private final SolUiControl myCloseCtrl;
  private final SolUiControl myExitCtrl;
  private final SolUiControl myRespawnCtrl;
  private final SolUiControl mySoundVolCtrl;
  private final SolUiControl myMusVolCtrl;
  private final SolUiControl myDoNotSellEquippedControl;

  public MenuScreen(MenuLayout menuLayout, GameOptions gameOptions) {
    myControls = new ArrayList<SolUiControl>();

    myDoNotSellEquippedControl = new SolUiControl(menuLayout.buttonRect(-1, -1), true);
    myDoNotSellEquippedControl.setDisplayName("Can't sell equipped items");
    myControls.add(myDoNotSellEquippedControl);
    mySoundVolCtrl = new SolUiControl(menuLayout.buttonRect(-1, 1), true);
    mySoundVolCtrl.setDisplayName("Sound Vol");
    myControls.add(mySoundVolCtrl);
    myMusVolCtrl = new SolUiControl(menuLayout.buttonRect(-1, 0), true);
    myMusVolCtrl.setDisplayName("Music Vol");
    myControls.add(myMusVolCtrl);
    myRespawnCtrl = new SolUiControl(menuLayout.buttonRect(-1, 2), true);
    myRespawnCtrl.setDisplayName("Respawn");
    myControls.add(myRespawnCtrl);
    myExitCtrl = new SolUiControl(menuLayout.buttonRect(-1, 3), true);
    myExitCtrl.setDisplayName("Exit");
    myControls.add(myExitCtrl);
    myCloseCtrl = new SolUiControl(menuLayout.buttonRect(-1, 4), true, gameOptions.getKeyClose());
    myCloseCtrl.setDisplayName("Resume");
    myControls.add(myCloseCtrl);
  }

  @Override
  public List<SolUiControl> getControls() {
    return myControls;
  }

  @Override
  public void updateCustom(SolApplication cmp, SolInputManager.Ptr[] ptrs, boolean clickedOutside) {
    SolGame g = cmp.getGame();
    g.setPaused(true);
    SolInputManager im = cmp.getInputMan();
    GameOptions options = cmp.getOptions();
    mySoundVolCtrl.setDisplayName("Sound Volume: " + getVolName(options));
    if (mySoundVolCtrl.isJustOff()) {
      options.advanceSoundVolMul();
    }
    myMusVolCtrl.setDisplayName("Music Volume: " + getMusName(options));
    if(myMusVolCtrl.isJustOff()){
    	options.advanceMusicVolMul();
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
    myDoNotSellEquippedControl.setDisplayName("Can't sell equipped items: " +
            (options.canSellEquippedItems ? "No" : "Yes"));
    if (myDoNotSellEquippedControl.isJustOff()) {
      options.canSellEquippedItems = !options.canSellEquippedItems;
    }
  }

  private String getVolName(GameOptions options) {
    float volMul = options.volMul;
    if (volMul == 0) return "Off";
    else if (volMul < .4f) return "Low";
    else if (volMul < .7f) return "High";
    else {return "Max";}
  }
  private String getMusName(GameOptions options)
  {
	  float musMul = options.musicMul;
	  if (musMul == 0) return "Off";
	  if (musMul < .4f) return "Low";
	  if (musMul < .7f) return "High";
	  return "Max";
  }
  @Override
  public void drawBg(UiDrawer uiDrawer, SolApplication cmp) {
    uiDrawer.draw(uiDrawer.filler, SolColor.UI_BG);
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

  @Override
  public boolean isCursorOnBg(SolInputManager.Ptr ptr) {
    return true;
  }

  @Override
  public void onAdd(SolApplication cmp) {

  }

  @Override
  public void blurCustom(SolApplication cmp) {

  }
}
