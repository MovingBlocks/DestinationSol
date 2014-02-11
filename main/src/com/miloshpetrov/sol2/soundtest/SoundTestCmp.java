package com.miloshpetrov.sol2.soundtest;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.math.Vector2;
import com.miloshpetrov.sol2.Const;
import com.miloshpetrov.sol2.common.Col;
import com.miloshpetrov.sol2.ui.UiDrawer;

class SoundTestCmp {
  private static final float SPD = .5f;

  private final UiDrawer myUiDrawer;
  private final Vector2 myPos = new Vector2(.5f, .5f);

  private float myAccum;

  SoundTestCmp() {
    myUiDrawer = new UiDrawer();
  }

  // this method is called externally as often as possible
  public void render() {
    myAccum += Gdx.graphics.getDeltaTime();
    // we want to call the update() method 60 times per second or so, therefore these checks are needed
    while (myAccum > Const.REAL_TIME_STEP) {
      // in this method we update the game state
      update();
      myAccum -= Const.REAL_TIME_STEP;
    }
    // in this method we draw, this method is called as often as possible
    draw();
  }

  private void draw() {
    // clearing the screen from the previous frame
    Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
    myUiDrawer.begin();
    myUiDrawer.drawCircle(myPos, .05f, Col.W);
    myUiDrawer.end();
  }

  private void update() {
    updatePos();
    updateSound();
  }

  private void updatePos() {
    float spd = 0;
    if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
      spd = -SPD;
    } else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
      spd = SPD;
    }
    spd *= Const.REAL_TIME_STEP;
    myPos.x += spd;
  }

  private void updateSound() {

  }
}
