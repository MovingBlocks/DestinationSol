package com.miloshpetrov.sol2.game.input;

import com.miloshpetrov.sol2.game.Faction;
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
  Faction getFaction();
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
