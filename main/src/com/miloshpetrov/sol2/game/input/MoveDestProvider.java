package com.miloshpetrov.sol2.game.input;

import com.badlogic.gdx.math.Vector2;
import com.miloshpetrov.sol2.game.SolGame;
import com.miloshpetrov.sol2.game.ship.HullConfig;

public interface MoveDestProvider {
  Vector2 getDest();
  boolean shouldAvoidBigObjs();
  float getDesiredSpdLen();
  boolean shouldStopNearDest();
  void update(SolGame game, Vector2 shipPos, float maxIdleDist, HullConfig hullConfig);
  Boolean shouldBattle(boolean canShoot);

}
