package com.miloshpetrov.sol2.game.input;

import com.badlogic.gdx.math.Vector2;
import com.miloshpetrov.sol2.Const;
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

  public Vector2 getDest(SolShip ship, SolShip enemy, Planet np, boolean battle, float ts,
    boolean canShootUnfixed, boolean nearGround) {
    myDirChangeAwait -= ts;
    if (myDirChangeAwait <= 0) {
      int rnd = SolMath.intRnd(0, 2);
      myCw = rnd == 0 ? null : rnd == 1;
      myDirChangeAwait = SolMath.rnd(MIN_DIR_CHANGE_AWAIT, MAX_DIR_CHANGE_AWAIT);
    }
    if (!battle) throw new AssertionError("can't flee yet!");
    float prefAngle;
    Vector2 enemyPos = enemy.getPos();
    float approxRad = ship.getHull().config.getApproxRadius();
    float enemyApproxRad = enemy.getHull().config.getApproxRadius();

    if (nearGround) {
      prefAngle = SolMath.angle(np.getPos(), enemyPos);
      myStopNearDest = false;
      float dist = canShootUnfixed ? .9f * Const.AUTO_SHOOT_GROUND : .75f * Const.CAM_VIEW_DIST_GROUND;
      dist += approxRad + enemyApproxRad;
      SolMath.fromAl(myDest, prefAngle, dist);
      myDest.add(enemyPos);
    } else {
      Vector2 shipPos = ship.getPos();
      float a = SolMath.angle(enemyPos, shipPos);
      if (myCw != null) a += 90 * SolMath.toInt(myCw);
      float len = canShootUnfixed ? .9f * Const.AUTO_SHOOT_SPACE : .5f * Const.CAM_VIEW_DIST_SPACE;
      len += approxRad + enemyApproxRad;
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
