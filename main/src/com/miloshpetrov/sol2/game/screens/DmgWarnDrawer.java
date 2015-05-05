package com.miloshpetrov.sol2.game.screens;

import com.miloshpetrov.sol2.game.SolGame;
import com.miloshpetrov.sol2.game.ship.SolShip;

public class DmgWarnDrawer extends WarnDrawer {

  public DmgWarnDrawer(float r) {
    super(r, "Heavily Damaged");
  }

  @Override
  protected boolean shouldWarn(SolGame game) {
    SolShip hero = game.getHero();
    if (hero == null) return false;
    float l = hero.getLife();
    int ml = hero.getHull().config.getMaxLife();
    return l < ml * .3f;
  }
}
