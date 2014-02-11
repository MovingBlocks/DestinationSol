package com.miloshpetrov.sol2.game;

import com.badlogic.gdx.math.Vector2;
import com.miloshpetrov.sol2.common.SolMath;
import com.miloshpetrov.sol2.game.planet.Planet;

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
      if (np == null) return 0;
      Vector2 v = SolMath.getVec(np.getPos());
      v.sub(pos);
      float toPlanet = v.len();
      float planetAngle = SolMath.norm(v.angle() - 90);
      float perc = (np.getFullHeight()- toPlanet) / SMOOTH_HEIGHT;
      perc = SolMath.clamp(perc, 0, 1);
      SolMath.free(v);
      return planetAngle * perc;
    }
  }
}
