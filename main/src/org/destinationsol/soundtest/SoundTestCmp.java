package org.destinationsol.soundtest;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.TimeUtils;
import org.destinationsol.Const;

import java.util.ArrayList;

class SoundTestCmp {
  private final SpriteBatch mySpriteBatch;
  private final ArrayList<TextureRegion> myTsDiv;
  private final ArrayList<TextureRegion> myTsComb;
  private final Texture myWt;

  private float myAccum;
  private float myDivSum;
  private float myCombSum;

  SoundTestCmp() {
    mySpriteBatch = new SpriteBatch();
    myTsDiv = new ArrayList<TextureRegion>();
    myTsComb = new ArrayList<TextureRegion>();
    myWt = new Texture("imgSrcs/misc/whiteTex.png");
    Texture full = new Texture("testImgs/testCombined.png");
    for (int i = 0; i < 16; i++) {
      Texture div = new Texture("testImgs/test_" + i + ".png");
      TextureRegion divReg = new TextureRegion(div, 64, 64);
      myTsDiv.add(divReg);
      TextureRegion regComb = new TextureRegion(full, i * 64, 0, 64, 64);
      myTsComb.add(regComb);
    }
  }

  public void render() {
    myAccum += Gdx.graphics.getDeltaTime();
    while (myAccum > Const.REAL_TIME_STEP) {
      update();
      myAccum -= Const.REAL_TIME_STEP;
    }
    draw();
  }

  private void draw() {
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

    mySpriteBatch.begin();
    mySpriteBatch.draw(myWt, 0, 0);
    long divided = dAndM(false);
    mySpriteBatch.draw(myWt, 0, 0);
    long combined = dAndM(true);
    mySpriteBatch.draw(myWt, 0, 0);
    mySpriteBatch.end();

    myDivSum = divided;
    myCombSum = combined;
  }

  private long dAndM(boolean combined) {
    long s = TimeUtils.nanoTime();
    ArrayList<TextureRegion> regs = combined ? myTsComb : myTsDiv;
    for (int i = 0; i < regs.size(); i++) {
      TextureRegion r = regs.get(i);
      int y = i * 32;
      if (combined) y += 64;
      mySpriteBatch.draw(r, i * 32, y);
    }
    long elapsed = TimeUtils.nanoTime() - s;
    return elapsed;
  }

  private void update() {
    System.out.println("Divided: " + myDivSum + ", combined: " + myCombSum);
  }
}
