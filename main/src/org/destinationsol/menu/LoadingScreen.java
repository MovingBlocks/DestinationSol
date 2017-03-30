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

package org.destinationsol.menu;

import org.destinationsol.SolApplication;
import org.destinationsol.common.SolColor;
import org.destinationsol.ui.*;

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
  public void onAdd(SolApplication cmp) {
  }

  @Override
  public void updateCustom(SolApplication cmp, SolInputManager.Ptr[] ptrs, boolean clickedOutside) {
    cmp.startNewGame(myTut, myUsePrevShip);
  }

  @Override
  public boolean isCursorOnBg(SolInputManager.Ptr ptr) {
    return false;
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
    uiDrawer.drawStringCentered("Loading...", uiDrawer.r/2, .5f, FontSize.MENU, SolColor.W);
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
