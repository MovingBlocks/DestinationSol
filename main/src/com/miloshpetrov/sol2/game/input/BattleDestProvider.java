package com.miloshpetrov.sol2.game.input;

import com.badlogic.gdx.math.Vector2;
import com.miloshpetrov.sol2.common.SolMath;
import com.miloshpetrov.sol2.game.planet.Planet;
import com.miloshpetrov.sol2.game.ship.SolShip;

public class BattleDestProvider {
  public static final float GROUND_BATTLE_DIST_PERC = .6f;
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
    if (!np.isNearGround(enemyPos)) {
      float toShipAngle = SolMath.angle(enemyPos, ship.getPos());
      float a = toShipAngle + 90 * SolMath.toInt(myCw);
      float len = approxRad + .25f + enemyApproxRad;
      SolMath.fromAl(myDest, a, len);
      myDest.add(enemyPos);
      myStopNearDest = false;
    } else {
      prefAngle = SolMath.angle(np.getPos(), enemyPos);
      myStopNearDest = true;
      SolMath.fromAl(myDest, prefAngle, approxRad + GROUND_BATTLE_DIST_PERC * shootDist + enemyApproxRad);
      myDest.add(enemyPos);
    }
    return myDest;
  }

  public boolean shouldStopNearDest() {
    return myStopNearDest;
  }
}
