package com.miloshpetrov.sol2.soundtest;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.math.Vector2;
import com.miloshpetrov.sol2.Const;
import com.miloshpetrov.sol2.ui.UiDrawer;

class SoundTestCmp {
  private static final float SPD = .5f;

  private final UiDrawer myUiDrawer;
  private Music myMusic;
  private Sound mySound1, mySound2;
  private long myS1 = 0;
  private long myS2 = 0;
  private final Vector2 myPos = new Vector2(.5f, .5f);
  private Color myColor = new Color(myPos.x, myPos.y, 0, 1f);
  private float myPlayTime = 1f;
  private float myRadius = .05f;
  private boolean MousePressed = false;

  private float myAccum;

  SoundTestCmp() {
    myUiDrawer = new UiDrawer();
    myMusic = Gdx.audio.newMusic(Gdx.files.internal("res/sounds/ambiance1.mp3"));
    mySound1 = Gdx.audio.newSound(Gdx.files.internal("res/sounds/sample1.wav"));
    mySound2 = Gdx.audio.newSound(Gdx.files.internal("res/sounds/sample2.wav"));
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
    myUiDrawer.drawCircle(myPos, myRadius, myColor);
    myUiDrawer.end();
  }

  private void update() {
    updatePos();
    //start music
    if (!myMusic.isPlaying()) {myMusic.setVolume(1); myMusic.setLooping(true); myMusic.play();};
    updateSound();
    changeColor();
    if (!(this.MousePressed) && myPlayTime < 1f) //full animation lasts 1 second
    {
      changeRadius(Const.REAL_TIME_STEP);
    }
    else myRadius = .05f;

    if (!(this.MousePressed) && Gdx.input.isButtonPressed(Input.Buttons.LEFT))
    //if pressed mouse button - init circle auto-morphing and start sound sample
    {
      myPlayTime = 0f;
      myRadius = .05f;
      this.MousePressed = true;
      myS1 = mySound1.play(.7f, myPos.x, 0f);
    }
    else this.MousePressed = false;
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

  private void changeColor() {
    myColor.set(myPos.x, myPos.y, 0, 1f);
  }

  private void changeRadius(float dTime)
  {
    if (myPlayTime < .5f) //incrementing within half a second
    {
      myRadius += dTime * 0.1f; //amount of change in passed time
    }
    else //decrementing after
    {
      myRadius -= dTime * 0.1f;
    }
    myPlayTime += dTime;
  }

  private void updateSound() {
    if(myS1 != 0) mySound1.setPitch(myS1, myPos.x); //changes pitch by position
  }
}
