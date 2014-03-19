package com.miloshpetrov.sol2.game;

import com.badlogic.gdx.math.Vector2;
import com.miloshpetrov.sol2.Const;
import com.miloshpetrov.sol2.common.SolMath;
import com.miloshpetrov.sol2.game.planet.Planet;
import com.miloshpetrov.sol2.game.planet.SolSystem;

public interface CamRotStrategy {
  public float getRotation(Vector2 pos, SolGame game);

  public static class Static implements CamRotStrategy {
    public float getRotation(Vector2 pos, SolGame game) {
      return 0;
    }
  }

  public static class ToPlanet implements CamRotStrategy {

    public static final float SMOOTH_HEIGHT = 2f;

    public float getRotation(Vector2 pos, SolGame game) {
      Planet np = game.getPlanetMan().getNearestPlanet();
      float fh = np.getFullHeight();
      Vector2 npPos = np.getPos();
      if (npPos.dst(pos) < fh) {
        return forObj(pos, fh, npPos);
      }
      SolSystem sys = game.getPlanetMan().getNearestSystem(pos);
      Vector2 sysPos = sys.getPos();
      if (sysPos.dst(pos) < Const.SUN_RADIUS) {
        return forObj(pos, Const.SUN_RADIUS, sysPos);
      }
      return 0;
    }

    private float forObj(Vector2 pos, float fh, Vector2 objPos) {
      Vector2 toObj = SolMath.distVec(pos, objPos);
      float toObjLen = toObj.len();
      float toObjAngle = SolMath.norm(toObj.angle() - 90);
      float perc = (fh - toObjLen) / SMOOTH_HEIGHT;
      perc = SolMath.clamp(perc, 0, 1);
      SolMath.free(toObj);
      return toObjAngle * perc;
    }
  }
}
