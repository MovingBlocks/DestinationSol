package com.miloshpetrov.sol2.game.input;

import com.badlogic.gdx.math.Vector2;
import com.miloshpetrov.sol2.game.SolGame;
import com.miloshpetrov.sol2.game.ship.HullConfig;

public class NoDestProvider implements MoveDestProvider {
  @Override
  public Vector2 getDest() {
    return null;
  }

  @Override
  public boolean shouldAvoidBigObjs() {
    return false;
  }

  @Override
  public float getDesiredSpdLen() {
    return 0;
  }

  @Override
  public boolean shouldStopNearDest() {
    return false;
  }

  @Override
  public void update(SolGame game, Vector2 shipPos, float maxIdleDist, HullConfig hullConfig) {
  }

  @Override
  public Boolean shouldBattle(boolean canShoot) {
    return null;
  }
}
