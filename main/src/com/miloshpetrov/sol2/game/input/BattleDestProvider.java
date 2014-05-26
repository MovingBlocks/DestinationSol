package com.miloshpetrov.sol2.game.input;

import com.badlogic.gdx.math.Vector2;
import com.miloshpetrov.sol2.common.SolMath;
import com.miloshpetrov.sol2.game.planet.Planet;
import com.miloshpetrov.sol2.game.ship.SolShip;

public class BattleDestProvider {
  public static final float MIN_DIR_CHANGE_AWAIT = 10f;
  public static final float MAX_DIR_CHANGE_AWAIT = 15f;
  private final Vector2 myDest;

  private boolean myStopNearDest;
  private Boolean myCw;
  private float myDirChangeAwait;

  public BattleDestProvider() {
    myDest = new Vector2();
    myCw = SolMath.test(.5f);
  }

  public Vector2 getDest(SolShip ship, SolShip enemy, float shootDist, Planet np, boolean battle, float ts) {
    myDirChangeAwait -= ts;
    if (myDirChangeAwait <= 0) {
      int rnd = SolMath.intRnd(0, 2);
      myCw = rnd == 0 ? null : rnd == 1;
      myDirChangeAwait = SolMath.rnd(MIN_DIR_CHANGE_AWAIT, MAX_DIR_CHANGE_AWAIT);
    }
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
      float a = SolMath.angle(enemyPos, shipPos);
      if (myCw != null) a += 90 * SolMath.toInt(myCw);
      float len = approxRad + .5f * shootDist + enemyApproxRad;
      SolMath.fromAl(myDest, a, len);
      myDest.add(enemyPos);
      myStopNearDest = false;
    }
    return myDest;
  }

  public boolean shouldStopNearDest() {
    return myStopNearDest;
  }
}
