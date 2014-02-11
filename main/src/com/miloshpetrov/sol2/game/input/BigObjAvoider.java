package com.miloshpetrov.sol2.game.input;

import com.badlogic.gdx.math.Vector2;
import com.miloshpetrov.sol2.Const;
import com.miloshpetrov.sol2.common.SolMath;
import com.miloshpetrov.sol2.game.SolGame;
import com.miloshpetrov.sol2.game.planet.Planet;

public class BigObjAvoider {

  public static final float MAX_DIST_LEN = 2 * Const.SUN_RADIUS;
  private Vector2 myPlanetProj;

  public BigObjAvoider() {
    myPlanetProj = new Vector2();
  }

  public float avoid(SolGame game, Vector2 from, Vector2 dest, float toDestAngle) {
    float toDestLen = from.dst(dest);
    if (toDestLen > MAX_DIST_LEN) toDestLen = MAX_DIST_LEN;
    float res = toDestAngle;
    for (Planet p : game.getPlanetMan().getPlanets()) {
      Vector2 pPos = p.getPos();
      myPlanetProj.set(pPos);
      myPlanetProj.sub(from);
      SolMath.rotate(myPlanetProj, -toDestAngle);
      if (myPlanetProj.x < 0 || toDestLen < myPlanetProj.x) continue;
      if (p.getFullHeight() < SolMath.abs(myPlanetProj.y)) continue;
      toDestLen = myPlanetProj.x;
      res = toDestAngle + 45 * SolMath.toInt(myPlanetProj.y < 0);
    }

    return res;
  }
}