package com.miloshpetrov.sol2.menu;

import com.badlogic.gdx.math.Rectangle;

public class MenuLayout {
  public static final float BG_BORDER = .03f;
  public final float btnW;
  public final float btnH;
  public final float colCenter;
  public final float row0;
  private final float rowH;
  private final float myPad;

  public MenuLayout(float r) {
    btnW = .25f * r;
    btnH = .1f;
    myPad = .1f * btnH;
    rowH = btnH + myPad;
    colCenter = .5f * r - btnW / 2;
    row0 = 1 - myPad - 5 * rowH;
  }

  public Rectangle buttonRect(int col, int row) {
    float x = col == -1 ? colCenter : .5f; //unfinished
    float y = row0 + rowH * row;
    return new Rectangle(x, y, btnW, btnH);
  }

  public Rectangle bg(int colCount, int startRow, int rowCount) {
    float x = colCount == -1 ? colCenter : .5f; //unfinished
    float y = row0 + rowH * startRow;
    return new Rectangle(x - BG_BORDER, y - BG_BORDER, btnW + 2 * BG_BORDER, rowH * rowCount - myPad + 2 * BG_BORDER);
  }
}
