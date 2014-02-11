package com.miloshpetrov.sol2.game.screens;

import com.badlogic.gdx.math.Rectangle;

public class RightPaneLayout {
  public final float btnH;
  public final float btnW;
  public final float row0;
  public final float rowH;
  public final float col0;

  public RightPaneLayout(float r) {
    btnH = .07f;
    rowH = 1.3f * btnH;
    row0 = .1f;
    btnW = 2 * btnH;
    col0 = r - btnW;
  }

  public Rectangle buttonRect(int row) {
    float x = col0;
    float y = row0 + rowH * row;
    return new Rectangle(x, y, btnW, btnH);
  }
}
