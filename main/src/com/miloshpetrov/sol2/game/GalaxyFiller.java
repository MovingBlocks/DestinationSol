package com.miloshpetrov.sol2.game;

import com.badlogic.gdx.math.Vector2;
import com.miloshpetrov.sol2.Const;
import com.miloshpetrov.sol2.common.SolMath;
import com.miloshpetrov.sol2.game.input.*;
import com.miloshpetrov.sol2.game.maze.Maze;
import com.miloshpetrov.sol2.game.planet.*;
import com.miloshpetrov.sol2.game.ship.HullConfig;
import com.miloshpetrov.sol2.game.ship.SolShip;

import java.util.ArrayList;

public class GalaxyFiller {
  private SolShip myMainStation;

  public GalaxyFiller() {
  }

  private Vector2 getPosForStation(SolSystem sys, boolean mainStation) {
    Planet p;
    ArrayList<Planet> planets = sys.getPlanets();
    if (mainStation) {
      int idx = (int) SolMath.rnd(.25f, .75f) * planets.size();
      p = planets.get(idx);
    } else {
      p = SolMath.elemRnd(planets);
    }
    float stationDist = p.getDist() + p.getFullHeight() + Const.PLANET_GAP;
    Vector2 stationPos = new Vector2();
    SolMath.fromAl(stationPos, SolMath.rnd(180), stationDist);
    stationPos.add(p.getSys().getPos());
    return stationPos;
  }

  private SolShip build(SolGame game, ShipConfig cfg, Fraction frac, boolean mainStation, SolSystem sys) {
    HullConfig hullConf = cfg.hull;

    MoveDestProvider dp;
    Vector2 pos;
    float detectionDist = game.getCam().getSpaceViewDist();
    if (hullConf.type == HullConfig.Type.STATION) {
      pos = getPosForStation(sys, mainStation);
      dp = new NoDestProvider();
    } else {
      pos = getEmptySpace(game, sys);
      boolean isBig = hullConf.type == HullConfig.Type.BIG;
      dp = new ExplorerDestProvider(game, pos, !isBig, hullConf, isBig ? 1.5f : .75f);
      if (isBig) detectionDist *= 2;
    }
    Pilot pilot = new AiPilot(dp, true, frac, true, "something", detectionDist);
    float angle = mainStation ? 0 : SolMath.rnd(180);
    boolean mountFixed1, mountFixed2, hasRepairer;
    mountFixed1 = cfg.isMountFixed1;
    mountFixed2 = cfg.isMountFixed2;
    hasRepairer = cfg.hasRepairer;
    int money = cfg.money;
    SolShip s = game.getShipBuilder().buildNew(game, pos, null, angle, 0, pilot, cfg.items, hullConf, mountFixed1, mountFixed2, null, hasRepairer, money, null);
    game.getObjMan().addObjDelayed(s);
    ShipConfig guardConf = cfg.guard;
    if (guardConf != null) {
      for (int i = 0; i < guardConf.density; i++) {
        createGuard(game, s, guardConf, frac);
      }
    }
    return s;
  }

  public void fill(SolGame game) {
    if (DebugAspects.NO_OBJS) return;
    createStarPorts(game);
    ArrayList<SolSystem> systems = game.getPlanetMan().getSystems();

    ShipConfig mainStationCfg = game.getPlayerSpawnConfig().mainStation;
    myMainStation = build(game, mainStationCfg, Fraction.LAANI, true, systems.get(0));

    for (SolSystem sys : systems) {
      SysConfig sysConfig = sys.getConfig();
      int planetCount = sys.getPlanets().size();
      for (ShipConfig shipConfig : sysConfig.constAllies) {
        int count = (int)(shipConfig.density * planetCount);
        for (int i = 0; i < count; i++) {
          build(game, shipConfig, Fraction.LAANI, false, sys);
        }
      }
      for (ShipConfig shipConfig : sysConfig.constEnemies) {
        int count = (int)(shipConfig.density * planetCount);
        for (int i = 0; i < count; i++) {
          build(game, shipConfig, Fraction.EHAR, false, sys);
        }
      }
    }
  }

  private void createStarPorts(SolGame game) {
    PlanetMan planetMan = game.getPlanetMan();
    ArrayList<Planet> biggest = new ArrayList<Planet>();
    for (SolSystem s : planetMan.getSystems()) {
      float minH = 0;
      Planet biggestP = null;
      int bi = -1;
      ArrayList<Planet> ps = s.getPlanets();
      for (int i = 0; i < ps.size(); i++) {
        Planet p = ps.get(i);
        float gh = p.getGroundHeight();
        if (minH < gh) {
          minH = gh;
          biggestP = p;
          bi = i;
        }
      }
      for (int i = 0; i < ps.size(); i++) {
        if (bi == i || bi == i - 1 || bi == i + 1) continue;
        Planet p = ps.get(i);
        link(game, p, biggestP);
      }

      for (Planet p : biggest) {
        link(game, p, biggestP);
      }
      biggest.add(biggestP);
    }

  }

  private void link(SolGame game, Planet a, Planet b) {
    if (a == b) throw new AssertionError();
    StarPort sp = game.getStarPortBuilder().build(game, a, b, false);
    game.getObjMan().addObjDelayed(sp);
    sp = game.getStarPortBuilder().build(game, b, a, true);
    game.getObjMan().addObjDelayed(sp);
  }

  private void createGuard(SolGame game, SolShip target, ShipConfig guardConf, Fraction frac) {
    Guardian dp = new Guardian(game, target, guardConf.hull);
    float detectionDist = game.getCam().getSpaceViewDist() * 2;
    Pilot pilot = new AiPilot(dp, true, frac, false, null, detectionDist);
    boolean mountFixed1 = guardConf.isMountFixed1;
    boolean mountFixed2 = guardConf.isMountFixed2;
    boolean hasRepairer = guardConf.hasRepairer;
    int money = guardConf.money;
    SolShip e = game.getShipBuilder().buildNew(game, dp.getDest(), null, dp.getAngle(), 0, pilot, guardConf.items,
      guardConf.hull, mountFixed1, mountFixed2, null, hasRepairer, money, null);
    game.getObjMan().addObjDelayed(e);
  }

  private Vector2 getEmptySpace(SolGame game, SolSystem s) {
    Vector2 res = new Vector2();
    Vector2 sPos = s.getPos();
    float sRadius = s.getRadius();

    for (int i = 0; i < 100; i++) {
      res.set(SolMath.rnd(sRadius), SolMath.rnd(sRadius)).add(sPos);
      if (game.isPlaceEmpty(res)) return res;
    }
    throw new AssertionError("could not generate ship position");
  }

  public Vector2 getPlayerSpawnPos(SolGame game, PlayerSpawnConfig.SpawnPlace spawnPlace) {
    Vector2 pos = new Vector2();

    if (spawnPlace == PlayerSpawnConfig.SpawnPlace.PLANET) {
      Planet p = game.getPlanetMan().getPlanets().get(0);
      pos.set(p.getPos());
      pos.x += p.getFullHeight();
    } else if (spawnPlace == PlayerSpawnConfig.SpawnPlace.STATION) {
      SolMath.fromAl(pos, 90, myMainStation.getHull().config.size / 2);
      pos.add(myMainStation.getPos());
    } else {
      Maze m = game.getPlanetMan().getMazes().get(0);
      pos.set(m.getPos());
      pos.x += m.getRadius();
    }
    return pos;
  }

  public SolShip getMainStation() {
    return myMainStation;
  }

}
