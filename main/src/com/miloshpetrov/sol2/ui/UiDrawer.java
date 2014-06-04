package com.miloshpetrov.sol2.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.*;
import com.miloshpetrov.sol2.*;

public class UiDrawer {

  public static final int UI_RAD_TO_POINTS = 300;
  private static final float FONT_SIZE = .02f;

  public final Matrix4 straightMtx;
  public final float uiLineWidth;
  private final CommonDrawer myDrawer;
  public final float r;
  public final TextureRegion whiteTex;
  public final Rectangle filler;
  private Boolean myTextMode;

  public UiDrawer(TexMan texMan, CommonDrawer commonDrawer) {
    myDrawer = commonDrawer;
    r = myDrawer.r;
    whiteTex = texMan.getTex("ui/whiteTex", null);
    uiLineWidth = 1 / myDrawer.h;
    straightMtx = new Matrix4().setToOrtho2D(0, 1, myDrawer.r, -1);
    myDrawer.setMtx(straightMtx);
    filler = new Rectangle(0, 0, r, 1);
  }

  public void updateMtx() {
    myDrawer.setMtx(straightMtx);
  }

  public void drawString(String s, float x, float y, float scale, boolean centered, Color tint) {
    if (myTextMode != null && !myTextMode) throw new AssertionError();
    myDrawer.drawString(s, x, y, scale * FONT_SIZE, centered, tint);
  }

  private void check() {
    if (myTextMode != null && myTextMode) throw new AssertionError();
  }

  public void draw(TextureRegion tr, float width, float height, float origX, float origY, float x, float y,
    float rot, Color tint)
  {
    check();
    myDrawer.draw(tr, width, height, origX, origY, x, y, rot, tint);
  }

  public void draw(Rectangle rect, Color tint) {
    check();
    myDrawer.draw(whiteTex, rect, tint);
  }

  public void drawCircle(Vector2 center, float radius, Color col) {
    check();
    myDrawer.drawCircle(whiteTex, center, radius, col, uiLineWidth, (int) (radius * UI_RAD_TO_POINTS));
  }

  public void drawLine(float x, float y, float angle, float len, Color col) {
    check();
    myDrawer.drawLine(whiteTex, x, y, angle, len, col, uiLineWidth);
  }

  public void drawLine(Vector2 p1, Vector2 p2, Color col) {
    check();
    myDrawer.drawLine(whiteTex, p1, p2, col, uiLineWidth, false);
  }

  public void setTextMode(Boolean textMode) {
    myTextMode = textMode;
  }
}
