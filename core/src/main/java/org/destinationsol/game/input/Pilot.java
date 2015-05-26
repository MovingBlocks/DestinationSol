package org.destinationsol.game.input;

import org.destinationsol.game.Fraction;
import org.destinationsol.game.SolGame;
import org.destinationsol.game.ship.FarShip;
import org.destinationsol.game.ship.SolShip;

public interface Pilot {
  void update(SolGame game, SolShip ship, SolShip nearestEnemy);
  boolean isUp();
  boolean isLeft();
  boolean isRight();
  boolean isShoot();
  boolean isShoot2();
  boolean collectsItems();
  boolean isAbility();
  Fraction getFraction();
  boolean shootsAtObstacles();
  float getDetectionDist();
  String getMapHint();
  void updateFar(SolGame game, FarShip farShip);
  String toDebugString();
  boolean isPlayer();

  public static final class Utils {
    public static boolean isIdle(Pilot p) {
      return !(p.isUp() || p.isShoot() || p.isShoot2() || p.isAbility());
    }
  }
}
