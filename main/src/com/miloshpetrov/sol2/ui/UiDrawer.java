package com.miloshpetrov.sol2.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.*;
import com.miloshpetrov.sol2.*;

public class UiDrawer implements TexDrawer {

  public static final int UI_RAD_TO_POINTS = 300;
  private static final float FONT_SIZE = .02f;

  public final Matrix4 straightMtx;
  public final float uiLineWidth;
  private final CommonDrawer myDrawer;
  public final float r;
  public final TextureRegion whiteTex;
  public final Rectangle filler;

  public UiDrawer(TexMan texMan) {
    myDrawer = new CommonDrawer();
    r = myDrawer.r;
    whiteTex = texMan.getTex("ui/whiteTex", null);
    uiLineWidth = 1 / myDrawer.h;
    straightMtx = new Matrix4().setToOrtho2D(0, 1, myDrawer.r, -1);
    myDrawer.setMtx(straightMtx);
    filler = new Rectangle(0, 0, r, 1);
  }

  public void begin() {
    myDrawer.begin();
  }

  public void end() {
    myDrawer.end();
  }

  public void drawString(String s, float x, float y, float scale, boolean centered, Color tint) {
    myDrawer.drawString(s, x, y, scale * FONT_SIZE, centered, tint);
  }

  public void draw(TextureRegion tr, float width, float height, float origX, float origY, float x, float y,
    float rot, Color tint)
  {
    myDrawer.draw(tr, width, height, origX, origY, x, y, rot, tint);
  }

  public void draw(Rectangle rect, Color tint) {
    myDrawer.draw(whiteTex, rect, tint);
  }

  public void drawCircle(Vector2 center, float radius, Color col) {
    myDrawer.drawCircle(whiteTex, center, radius, col, uiLineWidth, (int) (radius * UI_RAD_TO_POINTS));
  }

  public void drawLine(float x, float y, float angle, float len, Color col) {
    myDrawer.drawLine(whiteTex, x, y, angle, len, col, uiLineWidth);
  }

  public void drawLine(Vector2 p1, Vector2 p2, Color col) {
    myDrawer.drawLine(whiteTex, p1, p2, col, uiLineWidth);
  }

  public void dispose() {
    myDrawer.dispose();
  }
}
