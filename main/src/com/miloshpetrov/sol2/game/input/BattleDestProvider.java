package com.miloshpetrov.sol2.game.input;

import com.badlogic.gdx.math.Vector2;
import com.miloshpetrov.sol2.common.SolMath;
import com.miloshpetrov.sol2.game.planet.Planet;
import com.miloshpetrov.sol2.game.ship.SolShip;

public class BattleDestProvider {
  private final Vector2 myDest;
  private boolean myStopNearDest;
  private final boolean myCw;

  public BattleDestProvider() {
    myDest = new Vector2();
    myCw = SolMath.test(.5f);
  }

  public Vector2 getDest(SolShip ship, SolShip enemy, float shootDist, Planet np, boolean battle) {
    if (!battle) throw new AssertionError("can't flee yet!");
    float prefAngle;
    Vector2 enemyPos = enemy.getPos();
    float approxRad = ship.getHull().config.approxRadius;
    float enemyApproxRad = enemy.getHull().config.approxRadius;
    if (np.isNearGround(enemyPos)) {
      prefAngle = SolMath.angle(np.getPos(), enemyPos);
      myStopNearDest = false;
      SolMath.fromAl(myDest, prefAngle, .5f * shootDist + enemyApproxRad);
      myDest.add(enemyPos);
    } else {
      Vector2 shipPos = ship.getPos();
      float dst = enemyPos.dst(shipPos);
      if (dst < enemyApproxRad + approxRad + approxRad) {
        myDest.set(shipPos);
        myStopNearDest = false;
      } else {
        float toShipAngle = SolMath.angle(enemyPos, shipPos);
        float a = toShipAngle + 90 * SolMath.toInt(myCw);
        float len = approxRad + .25f + enemyApproxRad;
        SolMath.fromAl(myDest, a, len);
        myDest.add(enemyPos);
        myStopNearDest = true;
      }
    }
    return myDest;
  }

  public boolean shouldStopNearDest() {
    return myStopNearDest;
  }
}
