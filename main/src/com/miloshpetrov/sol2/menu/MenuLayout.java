package com.miloshpetrov.sol2.menu;

import com.badlogic.gdx.math.Rectangle;

public class MenuLayout {
  public static final float BG_BORDER = .05f;
  public final float btnW;
  public final float btnH;
  public final float colCenter;
  public final float row0;
  private final float rowH;

  public MenuLayout(float r) {
    btnW = .2f * r;
    btnH = .1f;
    rowH = 1.2f * btnH;
    colCenter = .5f * r - btnW / 2;
    row0 = .5f - btnH / 2;
  }

  public Rectangle buttonRect(int col, int row) {
    float x = col == -1 ? colCenter : .5f; //unfinished
    float y = row0 + rowH * row;
    return new Rectangle(x, y, btnW, btnH);
  }

  public Rectangle bg(int colCount, int rowCount) {
    float x = colCount == -1 ? colCenter : .5f; //unfinished
    float y = row0;
    return new Rectangle(x - BG_BORDER, y - BG_BORDER, btnW + 2 * BG_BORDER, rowH * rowCount + 2 * BG_BORDER);
  }
}
