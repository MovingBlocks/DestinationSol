package com.miloshpetrov.sol2.game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.miloshpetrov.sol2.*;

public class Drawer implements TexDrawer {
  public static final int RAD_TO_POINTS = 8;

  public final float r;
  private final CommonDrawer myDrawer;

  public Drawer(TexMan texMan) {
    myDrawer = new CommonDrawer(texMan);
    r = myDrawer.r;
  }

  public void begin(SolGame game) {
    myDrawer.setMtx(game.getCam().getMtx());
    myDrawer.begin();
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

  public void drawLine(float x, float y, float angle, float len, Color col, float width) {
    myDrawer.drawLine(x, y, angle, len, col, width);
  }

  public void drawLine(Vector2 p1, Vector2 p2, Color col, float width) {
    myDrawer.drawLine(p1, p2, col, width);
  }

  public void draw(ParticleEmitter emitter) {
    emitter.draw(myDrawer.getBatch());
  }

  public void drawCircle(Vector2 center, float radius, Color col, float width) {
    myDrawer.drawCircle(center, radius, col, width, (int) (radius * RAD_TO_POINTS));
  }

  public void dispose() {
    myDrawer.dispose();
  }

  public void setAdditive(boolean additive) {
    myDrawer.setAdditive(additive);
  }
}