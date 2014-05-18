package com.miloshpetrov.sol2.game.screens;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.miloshpetrov.sol2.Const;
import com.miloshpetrov.sol2.common.Col;
import com.miloshpetrov.sol2.game.SolGame;
import com.miloshpetrov.sol2.game.ship.SolShip;
import com.miloshpetrov.sol2.ui.FontSize;
import com.miloshpetrov.sol2.ui.UiDrawer;

public class SunWarnDrawer {
  private final Rectangle myWarn;
  private boolean myShowWarn;

  public SunWarnDrawer(float r) {
    myWarn = new Rectangle(.4f * r, 0, .2f * r, .1f);
  }

  public void update(SolGame game) {
    myShowWarn = false;
    SolShip hero = game.getHero();
    if (hero == null) return;
    Vector2 pos = hero.getPos();
    float toCenter = game.getPlanetMan().getNearestSystem(pos).getPos().dst(pos);
    if (Const.SUN_RADIUS < toCenter) return;
    myShowWarn = true;
  }

  public void draw(UiDrawer uiDrawer) {
    if (!myShowWarn) return;
    uiDrawer.draw(myWarn, Col.UI_WARN);
    uiDrawer.drawString("Sun Near", myWarn.x + myWarn.width/2, myWarn.y + myWarn.height/2, FontSize.MENU, true, Col.W);
  }
}
