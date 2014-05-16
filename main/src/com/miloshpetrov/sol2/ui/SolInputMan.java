package com.miloshpetrov.sol2.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.miloshpetrov.sol2.*;
import com.miloshpetrov.sol2.common.Col;
import com.miloshpetrov.sol2.common.SolMath;
import com.miloshpetrov.sol2.game.SolGame;
import com.miloshpetrov.sol2.menu.GameOptions;

import java.util.ArrayList;
import java.util.List;

public class SolInputMan {

  private static final int POINTER_COUNT = 4;
  private static final float CURSOR_SHOW_TIME = 3;
  public static final float CURSOR_SZ = .07f;

  private final List<SolUiScreen> myScreens;
  private final List<SolUiScreen> myToRemove;
  private final List<SolUiScreen> myToAdd;
  private final Ptr[] myPtrs;
  private final Ptr myFlashPtr;
  private final Vector2 myMousePos;
  private final Vector2 myMousePrevPos;
  private final TutMan myTutMan;
  private float myMouseIdleTime;
  private final TextureAtlas.AtlasRegion myUiCursor;

  private TextureAtlas.AtlasRegion myCurrCursor;
  private boolean myMouseOnUi;

  public SolInputMan(TexMan texMan, float r) {
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
    myUiCursor = texMan.getTex("ui/cursor", null);
    myScreens = new ArrayList<SolUiScreen>();
    myToRemove = new ArrayList<SolUiScreen>();
    myToAdd = new ArrayList<SolUiScreen>();
    myTutMan = new TutMan(r);
  }

  public void maybeFlashPressed(int keyCode) {
    for (SolUiScreen screen : myScreens) {
      boolean consumed = false;
      for (SolUiControl c : screen.getControls()) {
        if (c.maybeFlashPressed(keyCode)) consumed = true;
      }
      if (consumed) return;
    }

  }

  public void maybeFlashPressed(int x, int y) {
    setPtrPos(myFlashPtr, x, y);
    for (SolUiScreen screen : myScreens) {
      for (SolUiControl c : screen.getControls()) {
        if (c.maybeFlashPressed(myFlashPtr)) return;
      }
      if (screen.isCursorOnBg(myFlashPtr)) return;
    }

  }

  public void setScreen(SolCmp cmp, SolUiScreen screen) {
    for (SolUiScreen oldScreen : myScreens) {
      removeScreen(oldScreen);
    }
    addScreen(cmp, screen);
  }

  public void addScreen(SolCmp cmp, SolUiScreen screen) {
    myToAdd.add(screen);
    screen.onAdd(cmp);
  }

  private void removeScreen(SolUiScreen screen) {
    myToRemove.add(screen);
    for (SolUiControl c : screen.getControls()) {
      c.blur();
    }
  }

  public boolean isScreenOn(SolUiScreen screen) {
    return myScreens.contains(screen);
  }

  private static void setPtrPos(Ptr ptr, int screenX, int screenY) {
    int h = Gdx.graphics.getHeight();
    ptr.x = 1f * screenX / h;
    ptr.y = 1f * screenY / h;
  }

  public void update(SolCmp cmp) {
    boolean mobile = cmp.isMobile();
    if (!mobile) maybeFixMousePos();

    updatePtrs();

    boolean consumed = false;
    myMouseOnUi = false;
    for (SolUiScreen screen : myScreens) {
      boolean consumedNow = false;
      for (SolUiControl c : screen.getControls()) {
        c.update(myPtrs, myCurrCursor != null, !consumed);
        if (c.isOn()) {
          consumedNow = true;
        }
        Rectangle area = c.getScreenArea();
        if (area != null && area.contains(myMousePos)) {
          myMouseOnUi = true;
        }
      }
      if (consumedNow) consumed = true;
      if (!consumed) {
        for (Ptr ptr : myPtrs) {
          if (ptr.pressed && screen.isCursorOnBg(ptr)) {
            consumed = true;
            break;
          }
        }
      }
      if (screen.isCursorOnBg(myPtrs[0])) myMouseOnUi = true;
      screen.updateCustom(cmp, myPtrs);
    }

    updateCursor(cmp);
    addRemoveScreens();

    myTutMan.update(cmp);
  }

  private void addRemoveScreens() {
    for (SolUiScreen screen : myToRemove) {
      myScreens.remove(screen);
    }
    myToRemove.clear();

    for (SolUiScreen screen : myToAdd) {
      if (isScreenOn(screen)) continue;
      myScreens.add(0, screen);
    }
    myToAdd.clear();
  }

  private void updateCursor(SolCmp cmp) {
    if (cmp.isMobile()) return;
    myMousePos.set(myPtrs[0].x, myPtrs[0].y);
    if (cmp.getOptions().controlType != GameOptions.CONTROL_KB) {
      SolGame game = cmp.getGame();
      if (game == null || myMouseOnUi) {
        myCurrCursor = myUiCursor;
      } else {
        myCurrCursor = game.getScreens().mainScreen.shipControl.getInGameTex();
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
    Gdx.input.setCursorPosition(mouseX, h - mouseY);
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

  public void draw(UiDrawer uiDrawer, SolCmp cmp) {
    for (int i = myScreens.size() - 1; i >= 0; i--) {
      SolUiScreen screen = myScreens.get(i);
      screen.drawPre(uiDrawer, cmp);

      List<SolUiControl> ctrls = screen.getControls();
      for (SolUiControl ctrl : ctrls) {
        ctrl.drawButton(uiDrawer, cmp);
      }

      for (SolUiControl ctrl : ctrls) {
        ctrl.drawDisplayName(uiDrawer, cmp);
      }
      screen.drawPost(uiDrawer, cmp);
    }

    myTutMan.draw(uiDrawer, cmp);

    if (myCurrCursor != null) {
      uiDrawer.draw(myCurrCursor, CURSOR_SZ, CURSOR_SZ, CURSOR_SZ/2, CURSOR_SZ/2, myMousePos.x, myMousePos.y, 0, Col.W);
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

  public static class Ptr {
    public float x;
    public float y;
    public boolean pressed;
    public boolean prevPressed;
  }

}
