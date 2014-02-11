package com.miloshpetrov.sol2.soundtest;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.math.Vector2;
import com.miloshpetrov.sol2.Const;
import com.miloshpetrov.sol2.ui.UiDrawer;

class SoundTestCmp {
  private static final float SPD = .5f;

  private final UiDrawer myUiDrawer;
  private final Vector2 myPos = new Vector2(.5f, .5f);
  private Color c = new Color(myPos.x, myPos.y, 0, 1f);
  private boolean start = false;
  private float play_time = 0f;
  private float delta = 0f;
  private float radius = .05f;

  private float myAccum;

  SoundTestCmp() {
    myUiDrawer = new UiDrawer();
  }

  // this method is called externally as often as possible
  public void render() {
    delta = Gdx.graphics.getDeltaTime();
    myAccum += delta;
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
    myUiDrawer.drawCircle(myPos, radius, c);
    myUiDrawer.end();
  }

  private void update() {
    updatePos();
    updateSound();
    change_color();
    if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) //if pressed mouse button - init circle auto-morphing
    {
      start = true;
      play_time = 0f;
      radius = .05f;
    }
    if (start && play_time <= 1f) //full animation lasts 1 second
    {
      change_radius(delta);
    }
    else
    {
      start = false;
    }
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

  private void change_color() {
    c.set(myPos.x, myPos.y, 0, 1f);
  }

  private void change_radius(float d_time)
  {
    if (play_time <= .5f) //incrementing within half a second
    {
      radius += d_time * 0.1f; //amount of change in passed time
    }
    else //decrementing after
    {
      radius -= d_time * 0.1f;
    }
    play_time += d_time;
  }

  private void updateSound() {

  }
}
