package com.miloshpetrov.sol2.game.screens;

import com.badlogic.gdx.math.Rectangle;
import com.miloshpetrov.sol2.common.Col;
import com.miloshpetrov.sol2.game.SolGame;
import com.miloshpetrov.sol2.ui.FontSize;
import com.miloshpetrov.sol2.ui.UiDrawer;

public abstract class WarnDrawer {
  private final Rectangle myWarn;
  public boolean show;
  private final String myText;

  public WarnDrawer(float r, String text) {
    myWarn = rect(r);
    myText = text;
  }

  public static Rectangle rect(float r) {
    return new Rectangle(.4f * r, 0, .2f * r, .1f);
  }

  public void update(SolGame game) {
    show = shouldWarn(game);
  }

  protected abstract boolean shouldWarn(SolGame game);

  public void draw(UiDrawer uiDrawer) {
    uiDrawer.draw(myWarn, Col.UI_WARN);
  }

  public void drawText(UiDrawer uiDrawer) {
    uiDrawer.drawString(myText, myWarn.x + myWarn.width/2, myWarn.y + myWarn.height/2, FontSize.MENU, true, Col.W);
  }
}
