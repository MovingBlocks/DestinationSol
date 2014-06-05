package com.miloshpetrov.sol2.game.screens;

import com.badlogic.gdx.math.Rectangle;
import com.miloshpetrov.sol2.common.Col;
import com.miloshpetrov.sol2.game.SolGame;
import com.miloshpetrov.sol2.game.ship.SolShip;
import com.miloshpetrov.sol2.ui.FontSize;
import com.miloshpetrov.sol2.ui.UiDrawer;

public class DmgWarnDrawer {
  private final Rectangle myWarn;
  private boolean myShowWarn;

  public DmgWarnDrawer(float r) {
    myWarn = CollisionWarnDrawer.rect(r);
  }

  public void update(SolGame game) {
    myShowWarn = false;
    SolShip hero = game.getHero();
    if (hero == null) return;
    float l = hero.getLife();
    int ml = hero.getHull().config.maxLife;
    if (l < ml * .3f) myShowWarn = true;
  }

  public void draw(UiDrawer uiDrawer) {
    if (!myShowWarn) return;
    uiDrawer.draw(myWarn, Col.UI_WARN);
  }

  public void drawText(UiDrawer uiDrawer) {
    if (!myShowWarn) return;
    uiDrawer.drawString("Heavily Damaged", myWarn.x + myWarn.width/2, myWarn.y + myWarn.height/2, FontSize.MENU, true, Col.W);
  }
}
