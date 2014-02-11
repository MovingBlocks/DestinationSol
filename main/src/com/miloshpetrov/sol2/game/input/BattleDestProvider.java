package com.miloshpetrov.sol2.game.input;

import com.badlogic.gdx.math.Vector2;
import com.miloshpetrov.sol2.common.SolMath;
import com.miloshpetrov.sol2.game.planet.Planet;
import com.miloshpetrov.sol2.game.ship.SolShip;

public class BattleDestProvider {
  public static final float BATTLE_DIST_PERC = .4f;
  private final Vector2 myDest;
  private boolean myStopNearDest;
  private final boolean myCw;

  public BattleDestProvider() {
    myDest = new Vector2();
    myCw = SolMath.test(.5f);
  }

  public Vector2 getDest(SolShip ship, SolShip enemy, float shootDist, Planet np, boolean battle) {
    float prefAngle;
    Vector2 enemyPos = enemy.getPos();
    if (!np.isNearGround(enemyPos)) {
      float toShipAngle = SolMath.angle(enemyPos, ship.getPos());
      float a = toShipAngle + 90 * SolMath.toInt(myCw);
      float len = ship.getHull().config.size/2 + .25f + enemy.getHull().config.size/2;
      SolMath.fromAl(myDest, a, len);
      myDest.add(enemyPos);
      myStopNearDest = false;
    } else {
      prefAngle = SolMath.angle(np.getPos(), enemyPos);
      myStopNearDest = true;
      SolMath.fromAl(myDest, prefAngle, BATTLE_DIST_PERC * shootDist);
      myDest.add(enemyPos);
    }
    return myDest;
  }

  public boolean shouldStopNearDest() {
    return myStopNearDest;
  }
}
