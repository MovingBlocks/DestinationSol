package com.miloshpetrov.sol2.game.input;

import com.miloshpetrov.sol2.game.Fraction;
import com.miloshpetrov.sol2.game.SolGame;
import com.miloshpetrov.sol2.game.ship.FarShip;
import com.miloshpetrov.sol2.game.ship.SolShip;

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

  public static final class Utils {
    public static boolean isIdle(Pilot p) {
      return !(p.isUp() || p.isLeft() || p.isRight() || p.isShoot() || p.isShoot2() || p.isAbility());
    }
  }
}
