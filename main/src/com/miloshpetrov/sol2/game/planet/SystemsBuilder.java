package com.miloshpetrov.sol2.game.planet;

import com.badlogic.gdx.math.Vector2;
import com.miloshpetrov.sol2.Const;
import com.miloshpetrov.sol2.common.SolMath;

import java.util.List;

public class SystemsBuilder {
  public static final int SYS_COUNT = 2;
  public final static int PLANET_COUNT = 5;
  public static final float PLANET_SPD = .4f;
  private static final float GROUND_SPD = .4f;

  public List<SolSystem> build(List<SolSystem> systems, List<Planet> planets) {
    for (int i = 0; i < SYS_COUNT; i++) {
      Vector2 sysPos = SolMath.getVec();
      getNewSysPos(sysPos, systems);
      SolSystem s = createSystem(sysPos, planets);
      SolMath.free(sysPos);
      systems.add(s);
    }
    return systems;
  }

  private SolSystem createSystem(Vector2 sysPos, List<Planet> planets) {
    SolSystem s = new SolSystem(sysPos);
    float planetDist = Const.SUN_RADIUS;
    for (int j = 0; j < PLANET_COUNT; j++) {
      float groundHeight = SolMath.rnd(.5f, 1) * Const.MAX_GROUND_HEIGHT;
      float reserved = Const.PLANET_GAP + Const.ATM_HEIGHT + groundHeight;
      planetDist += reserved;
      Planet p = createPlanet(planetDist, s, groundHeight);
      planets.add(p);
      s.getPlanets().add(p);
      planetDist += reserved;
    }
    s.setRadius(planetDist);
    return s;
  }

  private void getNewSysPos(Vector2 v, List<SolSystem> systems) {
    float sysDist = 0;
    while (true) {
      for (int i = 0; i < 20; i++) {
        float angle = SolMath.rnd(180);
        SolMath.fromAl(v, angle, sysDist);
        boolean good = true;
        for (SolSystem system : systems) {
          if (system.getPos().dst(v) < system.getRadius() * 2) {
            good = false;
            break;
          }
        }
        if (good) return;
      }
      sysDist += Const.SUN_RADIUS;
    }
  }

  private Planet createPlanet(float planetDist, SolSystem s, float groundHeight) {
    float toSysRotSpd = SolMath.arcSin(PLANET_SPD / planetDist) * SolMath.toInt(SolMath.test(.5f));
    float rotSpd = SolMath.arcSin(GROUND_SPD / groundHeight)  * SolMath.toInt(SolMath.test(.5f));
    return new Planet(s, SolMath.rnd(180), planetDist, SolMath.rnd(180), toSysRotSpd, rotSpd, groundHeight, false);
  }
}
