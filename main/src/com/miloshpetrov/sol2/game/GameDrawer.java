package com.miloshpetrov.sol2.game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.Vector2;
import com.miloshpetrov.sol2.*;

public class GameDrawer implements TexDrawer {
  public static final int RAD_TO_POINTS = 8;

  public final float r;
  public final TextureAtlas.AtlasRegion debugWhiteTex;
  private final CommonDrawer myDrawer;

  public GameDrawer(TexMan texMan, CommonDrawer commonDrawer) {
    myDrawer = commonDrawer;
    r = myDrawer.r;
    debugWhiteTex = texMan.getTex("ui/whiteTex", null);
  }

  public void begin() {
    myDrawer.begin();
  }

  public void updateMtx(SolGame game) {
    myDrawer.setMtx(game.getCam().getMtx());
  }

  public void end() {
    myDrawer.end();
  }

  public void drawString(String s, float x, float y, float size, boolean centered, Color col) {
    myDrawer.drawString(s, x, y, size, centered, col);
  }

  public void draw(TextureRegion tr, float width, float height, float origX, float origY, float x, float y,
    float rot, Color tint)
  {
    myDrawer.draw(tr, width, height, origX, origY, x, y, rot, tint);
  }

  public void drawLine(TextureRegion tex, float x, float y, float angle, float len, Color col, float width) {
    myDrawer.drawLine(tex, x, y, angle, len, col, width);
  }

  public void drawLine(TextureRegion tex, Vector2 p1, Vector2 p2, Color col, float width) {
    myDrawer.drawLine(tex, p1, p2, col, width);
  }

  public void draw(ParticleEmitter emitter, TextureAtlas.AtlasRegion tex) {
    emitter.draw(myDrawer.getBatch(emitter.getSprite().getTexture(), tex));
  }

  public void drawCircle(TextureRegion tex, Vector2 center, float radius, Color col, float width) {
    myDrawer.drawCircle(tex, center, radius, col, width, (int) (radius * RAD_TO_POINTS));
  }

  public void setAdditive(boolean additive) {
    myDrawer.setAdditive(additive);
  }
}