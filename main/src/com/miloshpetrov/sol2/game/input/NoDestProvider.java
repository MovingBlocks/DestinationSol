package com.miloshpetrov.sol2.game.input;

import com.badlogic.gdx.math.Vector2;
import com.miloshpetrov.sol2.game.SolGame;
import com.miloshpetrov.sol2.game.ship.HullConfig;

/**
 * Just stays wherever it is, but maneuvers
 */
public class NoDestProvider implements MoveDestProvider {

  public static final float DESIRED_SPD_LEN = 3f;

  public NoDestProvider() {
  }

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
    return DESIRED_SPD_LEN;
  }

  @Override
  public boolean shouldStopNearDest() {
    return false;
  }

  @Override
  public void update(SolGame game, Vector2 shipPos, float maxIdleDist, HullConfig hullConfig) {
  }

  @Override
  public Boolean shouldManeuver(boolean canShoot) {
    return canShoot ? true : null;
  }
}
