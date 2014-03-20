package com.miloshpetrov.sol2.game.planet;

import com.badlogic.gdx.math.Vector2;
import com.miloshpetrov.sol2.Const;
import com.miloshpetrov.sol2.common.SolMath;
import com.miloshpetrov.sol2.game.maze.*;

import java.util.ArrayList;
import java.util.List;

public class SystemsBuilder {
  public static final int SYS_COUNT = 2;
  public static final int MAZE_COUNT = SYS_COUNT * 2;
  public static final int PLANET_COUNT = 5;
  public static final float PLANET_SPD = .4f;
  private static final float GROUND_SPD = .4f;
  private static final float MAX_MAZE_RADIUS = 35f;
  private static final float MAZE_GAP = 10f;
  private static final float BELT_HALF_WIDTH = 20f;

  public List<SolSystem> build(List<SolSystem> systems, List<Planet> planets, ArrayList<SystemBelt> belts,
    PlanetConfigs planetConfigs,
    MazeConfigs mazeConfigs, ArrayList<Maze> mazes, SysConfigs sysConfigs)
  {
    int sysLeft = SYS_COUNT;
    int mazesLeft = MAZE_COUNT;
    while (sysLeft > 0 || mazesLeft > 0) {
      boolean createSys = sysLeft > 0;
      if (createSys && mazesLeft > 0 && !systems.isEmpty()) createSys = SolMath.test(.5f);
      if (createSys) {
        List<Float> ghs = generatePlanetGhs();
        float sysRadius = calcSysRadius(ghs);
        Vector2 pos = getBodyPos(systems, mazes, sysRadius);
        SolSystem s = createSystem(ghs, pos, planets, belts, planetConfigs, sysRadius, sysConfigs);
        systems.add(s);
        sysLeft--;
      } else {
        MazeConfig mc = SolMath.elemRnd(mazeConfigs.configs);
        float mazeRadius = SolMath.rnd(.7f, 1) * MAX_MAZE_RADIUS;
        Vector2 pos = getBodyPos(systems, mazes, mazeRadius + MAZE_GAP);
        Maze m = new Maze(mc, pos, mazeRadius);
        mazes.add(m);
        mazesLeft--;
      }
    }
    return systems;
  }

  private List<Float> generatePlanetGhs() {
    ArrayList<Float> res = new ArrayList<Float>();
    for (int i = 0; i < PLANET_COUNT; i++) {
      float gh;
      if (SolMath.test(.8f)) {
        gh = SolMath.rnd(.5f, 1) * Const.MAX_GROUND_HEIGHT;
      } else {
        gh = -BELT_HALF_WIDTH;
      }
      res.add(gh);
    }
    return res;
  }

  private float calcSysRadius(List<Float> ghs) {
    float r = 0;
    r += Const.SUN_RADIUS;
    for (Float gh : ghs) {
      r += Const.PLANET_GAP;
      if (gh > 0) {
        r += Const.ATM_HEIGHT;
        r += gh;
        r += gh;
        r += Const.ATM_HEIGHT;
      } else {
        r -= gh;
        r -= gh;
      }
      r += Const.PLANET_GAP;
    }
    return r;
  }

  private Vector2 getBodyPos(List<SolSystem> systems, ArrayList<Maze> mazes, float bodyRadius) {
    Vector2 res = new Vector2();
    float dist = 0;
    while (true) {
      for (int i = 0; i < 20; i++) {
        float angle = SolMath.rnd(180);
        SolMath.fromAl(res, angle, dist);
        boolean good = true;
        for (SolSystem system : systems) {
          if (system.getPos().dst(res) < system.getRadius() + bodyRadius) {
            good = false;
            break;
          }
        }
        for (Maze maze : mazes) {
          if (maze.getPos().dst(res) < maze.getRadius() + bodyRadius) {
            good = false;
            break;
          }
        }
        if (good) return res;
      }
      dist += Const.SUN_RADIUS;
    }
  }

  private SolSystem createSystem(List<Float> ghs, Vector2 sysPos, List<Planet> planets, ArrayList<SystemBelt> belts,
    PlanetConfigs planetConfigs,
    float sysRadius, SysConfigs sysConfigs)
  {
    SysConfig sysConfig = sysConfigs.getRandom();
    SolSystem s = new SolSystem(sysPos, sysConfig);
    s.setRadius(sysRadius);
    float planetDist = Const.SUN_RADIUS;
    for (Float gh : ghs) {
      float reserved;
      if (gh > 0) {
        reserved = Const.PLANET_GAP + Const.ATM_HEIGHT + gh;
      } else {
        reserved = Const.PLANET_GAP - gh;
      }
      planetDist += reserved;
      if (gh > 0) {
        PlanetConfig planetConfig = planetConfigs.getRandom();
        Planet p = createPlanet(planetDist, s, gh, planetConfig);
        planets.add(p);
        s.getPlanets().add(p);
      } else {
        SysConfig beltConfig = sysConfigs.getRandomBelt();
        SystemBelt belt = new SystemBelt(-gh, planetDist, s, beltConfig);
        belts.add(belt);
        s.getBelts().add(belt);
      }
      planetDist += reserved;
    }
    if (SolMath.abs(sysRadius - planetDist) > .1f) throw new AssertionError(sysRadius + " " + planetDist);
    return s;
  }

  private Planet createPlanet(float planetDist, SolSystem s, float groundHeight, PlanetConfig planetConfig) {
    float toSysRotSpd = SolMath.arcToAngle(PLANET_SPD, planetDist) * SolMath.toInt(SolMath.test(.5f));
    float rotSpd = SolMath.arcToAngle(GROUND_SPD, groundHeight)  * SolMath.toInt(SolMath.test(.5f));
    return new Planet(s, SolMath.rnd(180), planetDist, SolMath.rnd(180), toSysRotSpd, rotSpd, groundHeight, false, planetConfig);
  }

}
