package org.destinationsol.game.screens;

import com.badlogic.gdx.math.Vector2;
import org.destinationsol.Const;
import org.destinationsol.game.SolGame;
import org.destinationsol.game.ship.SolShip;

public class SunWarnDrawer extends WarnDrawer {

  public SunWarnDrawer(float r) {
    super(r, "Sun Near");
  }

  public boolean shouldWarn(SolGame game) {
    SolShip hero = game.getHero();
    if (hero == null) return false;
    Vector2 pos = hero.getPos();
    float toCenter = game.getPlanetMan().getNearestSystem(pos).getPos().dst(pos);
    return toCenter < Const.SUN_RADIUS;
  }
}
