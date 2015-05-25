package com.miloshpetrov.sol2.game.input;

import com.badlogic.gdx.math.Vector2;
import com.miloshpetrov.sol2.Const;
import com.miloshpetrov.sol2.common.SolMath;
import com.miloshpetrov.sol2.game.SolGame;
import com.miloshpetrov.sol2.game.planet.Planet;

public class BigObjAvoider {

  public static final float MAX_DIST_LEN = 2 * (Const.MAX_GROUND_HEIGHT + Const.ATM_HEIGHT);
  private Vector2 myProj;

  public BigObjAvoider() {
    myProj = new Vector2();
  }

  public float avoid(SolGame game, Vector2 from, Vector2 dest, float toDestAngle) {
    float toDestLen = from.dst(dest);
    if (toDestLen > MAX_DIST_LEN) toDestLen = MAX_DIST_LEN;
    float res = toDestAngle;
    Planet p = game.getPlanetMan().getNearestPlanet(from);
    Vector2 pPos = p.getPos();
    float pRad = p.getFullHeight();
    if (dest.dst(pPos) < pRad) pRad = p.getGroundHeight();
    myProj.set(pPos);
    myProj.sub(from);
    SolMath.rotate(myProj, -toDestAngle);
    if (0 < myProj.x && myProj.x < toDestLen) {
      if (SolMath.abs(myProj.y) < pRad) {
        toDestLen = myProj.x;
        res = toDestAngle + 45 * SolMath.toInt(myProj.y < 0);
      }
    }
    Vector2 sunPos = p.getSys().getPos();
    float sunRad = Const.SUN_RADIUS;
    myProj.set(sunPos);
    myProj.sub(from);
    SolMath.rotate(myProj, -toDestAngle);
    if (0 < myProj.x && myProj.x < toDestLen) {
      if (SolMath.abs(myProj.y) < sunRad) {
        res = toDestAngle + 45 * SolMath.toInt(myProj.y < 0);
      }
    }
    return res;
  }
}