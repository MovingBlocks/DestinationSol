package com.miloshpetrov.sol2.soundtest;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.TimeUtils;
import com.miloshpetrov.sol2.*;
import com.miloshpetrov.sol2.common.Col;
import com.miloshpetrov.sol2.common.SolMath;
import com.miloshpetrov.sol2.game.planet.*;
import com.miloshpetrov.sol2.ui.DebugCollector;
import com.miloshpetrov.sol2.ui.UiDrawer;

import java.util.*;

class SoundTestCmp {
  private static final float SPD = .5f;
  private static final long SAMPLE_LENGTH = 4000;
  private static long DEBUG_MUSIC_DIFF;

  private final UiDrawer myUiDrawer;
  private final Vector2 myPos;
  private final Color myColor;
  private final Map<String, List<SolSample>> mySamples;
  private final List<SolSample> myCurrSamples;
  private final Tile[][] myTileMap;

  private float myAccum;
  private long myLastPlayTime;

  SoundTestCmp() {
    myUiDrawer = new UiDrawer();
    myPos = new Vector2(.5f, .5f);
    myColor = new Color(myPos.x, myPos.y, 0, 1f);
    mySamples = new HashMap<String, List<SolSample>>();
    myCurrSamples = new ArrayList<SolSample>();

    FileHandle themeDir = SolFiles.readOnly("res/sounds/zones");
    for (FileHandle fh : themeDir.list()) {
      String name = fh.nameWithoutExtension();
      String[] parts = name.split("_");
      if (parts.length < 2) continue;
      String cat = parts[0];
      String idxStr = parts[1];
      int idx;
      try {
        idx = Integer.parseInt(idxStr);
      } catch (NumberFormatException e) {
        continue;
      }
      List<SolSample> catSamples = mySamples.get(cat);
      if (catSamples == null) {
        catSamples = new ArrayList<SolSample>();
        mySamples.put(cat, catSamples);
      }
      SolSample smp = new SolSample(cat, idx, Gdx.audio.newSound(fh));
      catSamples.add(smp);
    }

    TexMan texMan = new TexMan();
    PlanetConfig pc = new PlanetConfig("rocky", 0, 0, null, null, null, texMan);
    myTileMap = new GroundBuilder(pc, 40, 20).build();
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
    Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
    myUiDrawer.begin();
//    myUiDrawer.drawCircle(myPos, .05f, myColor);
    drawTileMap();
    DebugCollector.draw(myUiDrawer);
    myUiDrawer.end();
  }

  private void drawTileMap() {
    float sz = .03f;
    for (int col = 0; col < myTileMap.length; col++) {
      Tile[] rows = myTileMap[col];
      for (int row = 0; row < rows.length; row++) {
        Tile t = rows[row];
        if (t == null) continue;
        drawDebugTile(t, sz, col, row);
      }
    }
  }

  private void drawDebugTile(Tile t, float sz, int col, int row) {
    float x = (col + 1) * sz;
    float y = (row + 1) * sz;
//        myUiDrawer.draw(t.reg, sz*2, sz*2, 0, 0, x, y, 0, Col.W);
    float szh = sz / 2;
    if (t.from == SurfDir.FWD || t.from == SurfDir.UP) {
      if (t.from == SurfDir.UP) drawQuarterTile(szh, x-szh, y-szh);
      drawQuarterTile(szh, x-szh, y);
    }
    if (t.to == SurfDir.FWD || t.to == SurfDir.UP) {
      if (t.to == SurfDir.UP) drawQuarterTile(szh, x, y-szh);
      drawQuarterTile(szh, x, y);
    }
  }

  private void drawQuarterTile(float szh, float x, float v) {
    myUiDrawer.draw(myUiDrawer.whiteTex, szh, szh, 0, 0, x, v, 0, Col.W50);
  }

  private void update() {
    DebugCollector.update();

    updateMusic();
    updatePos();
    changeColor();
  }

  private void updateMusic() {
    debug("delay:", DEBUG_MUSIC_DIFF);
    for (SolSample s : myCurrSamples) {
      debug(s.cat, ":", s.idx);
    }

    long now = TimeUtils.millis();
    long diff = now - myLastPlayTime - SAMPLE_LENGTH;
    if (diff < -Const.REAL_TIME_STEP / 2) return;
    myLastPlayTime = now;
    DEBUG_MUSIC_DIFF = diff;

    myCurrSamples.clear();
    for (List<SolSample> catSamples : mySamples.values()) {
      SolSample sample = SolMath.elemRnd(catSamples);
//      sample.s.play();
      myCurrSamples.add(sample);
    }
    Collections.sort(myCurrSamples);
  }

  public void debug(Object ... objs) {
    DebugCollector.debug(objs);
  }

  private void updatePos() {
    float spd = 0;
    if (Gdx.input.isKeyPressed(Input.Keys.LEFT) && 0 < myPos.x) {
      spd = -SPD;
    } else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) && myPos.x < myUiDrawer.r) {
      spd = SPD;
    }
    spd *= Const.REAL_TIME_STEP;
    myPos.x += spd;

    spd = 0;
    if (Gdx.input.isKeyPressed(Input.Keys.UP) && 0 < myPos.y) {
      spd = -SPD;
    } else if (Gdx.input.isKeyPressed(Input.Keys.DOWN) && myPos.y < 1) {
      spd = SPD;
    }
    spd *= Const.REAL_TIME_STEP;
    myPos.y += spd;
  }

  private void changeColor() {
    myColor.set(myPos.x, myPos.y, 1, 1f);
  }

  private static class SolSample implements Comparable<SolSample> {
    public final String cat;
    public final int idx;
    public final Sound s;

    private SolSample(String cat, int idx, Sound s) {
      this.cat = cat;
      this.idx = idx;
      this.s = s;
    }

    @Override
    public int compareTo(SolSample o) {
      int r = cat.compareTo(o.cat);
      if (r != 0) return r;
      return idx - o.idx;
    }
  }
}
