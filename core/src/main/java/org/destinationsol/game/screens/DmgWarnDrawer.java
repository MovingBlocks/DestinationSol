package org.destinationsol.game.screens;

import org.destinationsol.game.SolGame;
import org.destinationsol.game.ship.SolShip;

public class DmgWarnDrawer extends WarnDrawer {

  public DmgWarnDrawer(float r) {
    super(r, "Heavily Damaged");
  }

  @Override
  protected boolean shouldWarn(SolGame game) {
    SolShip hero = game.getHero();
    if (hero == null) return false;
    float l = hero.getLife();
    int ml = hero.getHull().config.maxLife;
    return l < ml * .3f;
  }
}
