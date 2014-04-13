package com.miloshpetrov.sol2.game.input;

import com.badlogic.gdx.math.Vector2;
import com.miloshpetrov.sol2.common.SolMath;
import com.miloshpetrov.sol2.game.SolGame;
import com.miloshpetrov.sol2.game.planet.PlanetBind;
import com.miloshpetrov.sol2.game.ship.HullConfig;

public class StillGuard implements MoveDestProvider {

  private final PlanetBind myPlanetBind;
  private Vector2 myDest;

  public StillGuard(Vector2 target, SolGame game) {
    myDest = new Vector2(target);
    myPlanetBind = PlanetBind.tryBind(game, myDest, 0);
  }

  @Override
  public Vector2 getDest() {
    return myDest;
  }

  @Override
  public boolean shouldAvoidBigObjs() {
    return false;
  }

  @Override
  public float getDesiredSpdLen() {
    return NoDestProvider.DESIRED_SPD_LEN;
  }

  @Override
  public boolean shouldStopNearDest() {
    return true;
  }

  @Override
  public void update(SolGame game, Vector2 shipPos, float maxIdleDist, HullConfig hullConfig) {
    if (myPlanetBind != null) {
      Vector2 diff = SolMath.getVec();
      myPlanetBind.setDiff(diff, shipPos, false);
      myDest.add(diff);
      SolMath.free(diff);
    }
  }

  @Override
  public Boolean shouldManeuver(boolean canShoot) {
    return true;
  }
}
