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
 
package com.miloshpetrov.sol2.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.miloshpetrov.sol2.*;
import com.miloshpetrov.sol2.common.SolColor;
import com.miloshpetrov.sol2.common.SolMath;
import com.miloshpetrov.sol2.files.FileManager;
import com.miloshpetrov.sol2.game.SolGame;
import com.miloshpetrov.sol2.GameOptions;

import java.util.ArrayList;
import java.util.List;

public class SolInputManager {

  private static final int POINTER_COUNT = 4;
  private static final float CURSOR_SHOW_TIME = 3;
  public static final float CURSOR_SZ = .07f;
  public static final float WARN_PERC_GROWTH_TIME = 1f;

  private final List<SolUiScreen> myScreens;
  private final List<SolUiScreen> myToRemove;
  private final List<SolUiScreen> myToAdd;
  private final Ptr[] myPtrs;
  private final Ptr myFlashPtr;
  private final Vector2 myMousePos;
  private final Vector2 myMousePrevPos;
  private final Sound myHoverSound;
  private float myMouseIdleTime;
  private final TextureAtlas.AtlasRegion myUiCursor;
  private final Color myWarnCol;

  private TextureAtlas.AtlasRegion myCurrCursor;
  private boolean myMouseOnUi;
  private float myWarnPerc;
  private boolean myWarnPercGrows;
  private Boolean myScrolledUp;

  public SolInputManager(TextureManager textureManager, float r) {
    myPtrs = new Ptr[POINTER_COUNT];
    for (int i = 0; i < POINTER_COUNT; i++) {
      myPtrs[i] = new Ptr();
    }
    SolInputProcessor sip = new SolInputProcessor(this);
    Gdx.input.setInputProcessor(sip);
    myFlashPtr = new Ptr();
    myMousePos = new Vector2();
    myMousePrevPos = new Vector2();
    Gdx.input.setCursorCatched(true);
    myUiCursor = textureManager.getTex("ui/cursor", null);
    myScreens = new ArrayList<SolUiScreen>();
    myToRemove = new ArrayList<SolUiScreen>();
    myToAdd = new ArrayList<SolUiScreen>();
    myWarnCol = new Color(SolColor.UI_WARN);

    FileHandle hoverSoundFile = FileManager.getInstance().getSoundsDirectory().child("ui").child("uiHover.ogg");
    myHoverSound = Gdx.audio.newSound(hoverSoundFile);
  }

  public void maybeFlashPressed(int keyCode) {
    for (int i = 0, myScreensSize = myScreens.size(); i < myScreensSize; i++) {
      SolUiScreen screen = myScreens.get(i);
      boolean consumed = false;
      List<SolUiControl> controls = screen.getControls();
      for (int i1 = 0, controlsSize = controls.size(); i1 < controlsSize; i1++) {
        SolUiControl c = controls.get(i1);
        if (c.maybeFlashPressed(keyCode)) consumed = true;
      }
      if (consumed) return;
    }

  }

  public void maybeFlashPressed(int x, int y) {
    setPtrPos(myFlashPtr, x, y);
    for (int i = 0, myScreensSize = myScreens.size(); i < myScreensSize; i++) {
      SolUiScreen screen = myScreens.get(i);
      List<SolUiControl> controls = screen.getControls();
      for (int i1 = 0, controlsSize = controls.size(); i1 < controlsSize; i1++) {
        SolUiControl c = controls.get(i1);
        if (c.maybeFlashPressed(myFlashPtr)) return;
      }
      if (screen.isCursorOnBg(myFlashPtr)) return;
    }

  }

  public void setScreen(SolApplication cmp, SolUiScreen screen) {
    for (int i = 0, myScreensSize = myScreens.size(); i < myScreensSize; i++) {
      SolUiScreen oldScreen = myScreens.get(i);
      removeScreen(oldScreen, cmp);
    }
    addScreen(cmp, screen);
  }

  public void addScreen(SolApplication cmp, SolUiScreen screen) {
    myToAdd.add(screen);
    screen.onAdd(cmp);
  }

  private void removeScreen(SolUiScreen screen, SolApplication cmp) {
    myToRemove.add(screen);
    List<SolUiControl> controls = screen.getControls();
    for (int i = 0, controlsSize = controls.size(); i < controlsSize; i++) {
      SolUiControl c = controls.get(i);
      c.blur();
    }
    screen.blurCustom(cmp);
  }

  public boolean isScreenOn(SolUiScreen screen) {
    return myScreens.contains(screen);
  }

  private static void setPtrPos(Ptr ptr, int screenX, int screenY) {
    int h = Gdx.graphics.getHeight();
    ptr.x = 1f * screenX / h;
    ptr.y = 1f * screenY / h;
  }

  public void update(SolApplication cmp) {
    boolean mobile = cmp.isMobile();
    if (!mobile) maybeFixMousePos();

    updatePtrs();

    boolean consumed = false;
    myMouseOnUi = false;
    boolean clickOutsideReacted = false;
    for (int i = 0, myScreensSize = myScreens.size(); i < myScreensSize; i++) {
      SolUiScreen screen = myScreens.get(i);
      boolean consumedNow = false;
      List<SolUiControl> controls = screen.getControls();
      for (int i1 = 0, controlsSize = controls.size(); i1 < controlsSize; i1++) {
        SolUiControl c = controls.get(i1);
        c.update(myPtrs, myCurrCursor != null, !consumed, this, cmp);
        if (c.isOn() || c.isJustOff()) {
          consumedNow = true;
        }
        Rectangle area = c.getScreenArea();
        if (area != null && area.contains(myMousePos)) {
          myMouseOnUi = true;
        }
      }
      if (consumedNow) consumed = true;
      boolean clickedOutside = false;
      if (!consumed) {
        for (int i1 = 0, myPtrsLength = myPtrs.length; i1 < myPtrsLength; i1++) {
          Ptr ptr = myPtrs[i1];
          boolean onBg = screen.isCursorOnBg(ptr);
          if (ptr.pressed && onBg) {
            clickedOutside = false;
            consumed = true;
            break;
          }
          if (!onBg && ptr.isJustUnPressed() && !clickOutsideReacted) {
            clickedOutside = true;
          }
        }
      }
      if (clickedOutside && screen.reactsToClickOutside()) clickOutsideReacted = true;
      if (screen.isCursorOnBg(myPtrs[0])) myMouseOnUi = true;
      screen.updateCustom(cmp, myPtrs, clickedOutside);
    }

    SolGame game = cmp.getGame();
    TutorialManager tutorialManager = game == null ? null : game.getTutMan();
    if (tutorialManager != null && tutorialManager.isFinished()) {
      cmp.finishGame();
    }

    updateCursor(cmp);
    addRemoveScreens();
    updateWarnPerc();
    myScrolledUp = null;
  }

  private void updateWarnPerc() {
    float dif = SolMath.toInt(myWarnPercGrows) * Const.REAL_TIME_STEP / WARN_PERC_GROWTH_TIME;
    myWarnPerc += dif;
    if (myWarnPerc < 0 || 1 < myWarnPerc) {
      myWarnPerc = SolMath.clamp(myWarnPerc);
      myWarnPercGrows = !myWarnPercGrows;
    }
    myWarnCol.a = myWarnPerc * .5f;
  }

  private void addRemoveScreens() {
    for (int i = 0, myToRemoveSize = myToRemove.size(); i < myToRemoveSize; i++) {
      SolUiScreen screen = myToRemove.get(i);
      myScreens.remove(screen);
    }
    myToRemove.clear();

    for (int i = 0, myToAddSize = myToAdd.size(); i < myToAddSize; i++) {
      SolUiScreen screen = myToAdd.get(i);
      if (isScreenOn(screen)) continue;
      myScreens.add(0, screen);
    }
    myToAdd.clear();
  }

  private void updateCursor(SolApplication cmp) {
    if (cmp.isMobile()) return;
    myMousePos.set(myPtrs[0].x, myPtrs[0].y);
    if (cmp.getOptions().controlType != GameOptions.CONTROL_KB) {
      SolGame game = cmp.getGame();
      if (game == null || myMouseOnUi) {
        myCurrCursor = myUiCursor;
      } else {
        myCurrCursor = game.getScreens().mainScreen.shipControl.getInGameTex();
        if (myCurrCursor == null) myCurrCursor = myUiCursor;
      }
      return;
    }
    if (myMousePrevPos.epsilonEquals(myMousePos, 0)) {
      myMouseIdleTime += Const.REAL_TIME_STEP;
      myCurrCursor = myMouseIdleTime < CURSOR_SHOW_TIME ? myUiCursor : null;
    } else {
      myCurrCursor = myUiCursor;
      myMouseIdleTime = 0;
      myMousePrevPos.set(myMousePos);
    }
  }

  private void maybeFixMousePos() {
    int mouseX = Gdx.input.getX();
    int mouseY = Gdx.input.getY();
    int w = Gdx.graphics.getWidth();
    int h = Gdx.graphics.getHeight();
    mouseX = (int)SolMath.clamp(mouseX, 0, w);
    mouseY = (int)SolMath.clamp(mouseY, 0, h);
    Gdx.input.setCursorPosition(mouseX, mouseY);
  }

  private void updatePtrs() {
    for (int i = 0; i < POINTER_COUNT; i++) {
      Ptr ptr = myPtrs[i];
      int screenX = Gdx.input.getX(i);
      int screenY = Gdx.input.getY(i);
      setPtrPos(ptr, screenX, screenY);
      ptr.prevPressed = ptr.pressed;
      ptr.pressed = Gdx.input.isTouched(i);
    }
  }

  public void draw(UiDrawer uiDrawer, SolApplication cmp) {
    for (int i = myScreens.size() - 1; i >= 0; i--) {
      SolUiScreen screen = myScreens.get(i);

      uiDrawer.setTextMode(false);
      screen.drawBg(uiDrawer, cmp);
      List<SolUiControl> ctrls = screen.getControls();
      for (int i1 = 0, ctrlsSize = ctrls.size(); i1 < ctrlsSize; i1++) {
        SolUiControl ctrl = ctrls.get(i1);
        ctrl.drawButton(uiDrawer, cmp, myWarnCol);
      }
      screen.drawImgs(uiDrawer, cmp);

      uiDrawer.setTextMode(true);
      screen.drawText(uiDrawer, cmp);
      for (int i1 = 0, ctrlsSize = ctrls.size(); i1 < ctrlsSize; i1++) {
        SolUiControl ctrl = ctrls.get(i1);
        ctrl.drawDisplayName(uiDrawer);
      }
    }
    uiDrawer.setTextMode(null);

    SolGame game = cmp.getGame();
    TutorialManager tutorialManager = game == null ? null : game.getTutMan();
    if (tutorialManager != null && getTopScreen() != game.getScreens().menuScreen) tutorialManager.draw(uiDrawer);

    if (myCurrCursor != null) {
      uiDrawer.draw(myCurrCursor, CURSOR_SZ, CURSOR_SZ, CURSOR_SZ/2, CURSOR_SZ/2, myMousePos.x, myMousePos.y, 0, SolColor.W);
    }
  }

  public Vector2 getMousePos() {
    return myMousePos;
  }

  public Ptr[] getPtrs() {
    return myPtrs;
  }

  public boolean isMouseOnUi() {
    return myMouseOnUi;
  }

  public void playHover(SolApplication cmp) {
    myHoverSound.play(.7f * cmp.getOptions().volMul, .7f, 0);
  }

  public void playClick(SolApplication cmp) {
    myHoverSound.play(.7f * cmp.getOptions().volMul, .9f, 0);
  }

  public SolUiScreen getTopScreen() {
    return myScreens.isEmpty() ? null : myScreens.get(0);
  }

  public void scrolled(boolean up) {
    myScrolledUp = up;
  }

  public Boolean getScrolledUp() {
    return myScrolledUp;
  }

  public void dispose() {
    myHoverSound.dispose();
  }

  public static class Ptr {
    public float x;
    public float y;
    public boolean pressed;
    public boolean prevPressed;

    public boolean isJustPressed() {
      return pressed && !prevPressed;
    }

    public boolean isJustUnPressed() {
      return !pressed && prevPressed;
    }
  }

}
