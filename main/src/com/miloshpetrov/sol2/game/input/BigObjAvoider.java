package com.miloshpetrov.sol2.game.input;

import com.badlogic.gdx.math.Vector2;
import com.miloshpetrov.sol2.Const;
import com.miloshpetrov.sol2.common.SolMath;
import com.miloshpetrov.sol2.game.SolGame;
import com.miloshpetrov.sol2.game.planet.Planet;
import com.miloshpetrov.sol2.game.planet.SolSystem;

import java.util.ArrayList;

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
    ArrayList<Planet> planets = game.getPlanetMan().getPlanets();
    for (int i = 0, planetsSize = planets.size(); i < planetsSize; i++) {
      Planet p = planets.get(i);
      Vector2 objPos = p.getPos();
      float objRad = p.getFullHeight();
      if (dest.dst(objPos) < objRad) objRad = p.getGroundHeight();
      myProj.set(objPos);
      myProj.sub(from);
      SolMath.rotate(myProj, -toDestAngle);
      if (myProj.x < 0 || toDestLen < myProj.x) continue;
      if (objRad < SolMath.abs(myProj.y)) continue;
      toDestLen = myProj.x;
      res = toDestAngle + 45 * SolMath.toInt(myProj.y < 0);
    }

    ArrayList<SolSystem> systems = game.getPlanetMan().getSystems();
    for (int i = 0, systemsSize = systems.size(); i < systemsSize; i++) {
      SolSystem sys = systems.get(i);
      Vector2 objPos = sys.getPos();
      float objRad = Const.SUN_RADIUS;
      myProj.set(objPos);
      myProj.sub(from);
      SolMath.rotate(myProj, -toDestAngle);
      if (myProj.x < 0 || toDestLen < myProj.x) continue;
      if (objRad < SolMath.abs(myProj.y)) continue;
      toDestLen = myProj.x;
      res = toDestAngle + 45 * SolMath.toInt(myProj.y < 0);
    }


    return res;
  }
}