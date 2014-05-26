package com.miloshpetrov.sol2.game;

import com.badlogic.gdx.math.Vector2;
import com.miloshpetrov.sol2.Const;
import com.miloshpetrov.sol2.common.SolMath;
import com.miloshpetrov.sol2.game.input.*;
import com.miloshpetrov.sol2.game.item.TradeConfig;
import com.miloshpetrov.sol2.game.maze.Maze;
import com.miloshpetrov.sol2.game.planet.*;
import com.miloshpetrov.sol2.game.ship.*;

import java.util.ArrayList;

public class GalaxyFiller {
  private Vector2 myMainStationPos;
  private HullConfig myMainStationHc;

  public GalaxyFiller() {
  }

  private Vector2 getPosForStation(SolSystem sys, boolean mainStation) {
    Planet p;
    ArrayList<Planet> planets = sys.getPlanets();
    float angleToSun;
    if (mainStation) {
      p = planets.get(planets.size() - 2);
      angleToSun = p.getAngleToSys() + 20 * SolMath.toInt(p.getToSysRotSpd() > 0);
    } else {
      p = SolMath.elemRnd(planets);
      angleToSun = SolMath.rnd(180);
    }
    float stationDist = p.getDist() + p.getFullHeight() + Const.PLANET_GAP;
    Vector2 stationPos = new Vector2();
    SolMath.fromAl(stationPos, angleToSun, stationDist);
    stationPos.add(p.getSys().getPos());
    return stationPos;
  }

  private FarShip build(SolGame game, ShipConfig cfg, Fraction frac, boolean mainStation, SolSystem sys) {
    HullConfig hullConf = cfg.hull;

    MoveDestProvider dp;
    Vector2 pos;
    float detectionDist = Const.AI_DET_DIST;
    TradeConfig tradeConfig = null;
    if (hullConf.type == HullConfig.Type.STATION) {
      pos = getPosForStation(sys, mainStation);
      dp = new NoDestProvider();
      tradeConfig = sys.getConfig().tradeConfig;
    } else {
      pos = getEmptySpace(game, sys);
      boolean isBig = hullConf.type == HullConfig.Type.BIG;
      dp = new ExplorerDestProvider(game, pos, !isBig, hullConf, sys);
      if (isBig) {
        tradeConfig = sys.getConfig().tradeConfig;
      } else {
        detectionDist *= 1.5;
      }
    }
    Pilot pilot = new AiPilot(dp, true, frac, true, "something", detectionDist);
    float angle = mainStation ? 0 : SolMath.rnd(180);
    boolean hasRepairer;
    hasRepairer = frac == Fraction.LAANI;
    int money = cfg.money;
    FarShip s = game.getShipBuilder().buildNewFar(game, pos, null, angle, 0, pilot, cfg.items, hullConf, null, hasRepairer, money, tradeConfig);
    game.getObjMan().addFarObjNow(s);
    ShipConfig guardConf = cfg.guard;
    if (guardConf != null) {
      for (int i = 0; i < guardConf.density; i++) {
        createGuard(game, s, guardConf, frac);
      }
    }
    return s;
  }

  public void fill(SolGame game) {
    if (DebugOptions.NO_OBJS) return;
    createStarPorts(game);
    ArrayList<SolSystem> systems = game.getPlanetMan().getSystems();

    ShipConfig mainStationCfg = game.getPlayerSpawnConfig().mainStation;
    FarShip mainStation = build(game, mainStationCfg, Fraction.LAANI, true, systems.get(0));
    myMainStationPos = new Vector2(mainStation.getPos());
    myMainStationHc = mainStation.getHullConfig();

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
    Vector2 aPos = StarPort.getDesiredPos(a, b, false);
    StarPort.MyFar sp = new StarPort.MyFar(a, b, aPos, false);
    SolMath.free(aPos);
    game.getObjMan().addFarObjNow(sp);
    Vector2 bPos = StarPort.getDesiredPos(b, a, false);
    sp = new StarPort.MyFar(b, a, bPos, false);
    SolMath.free(bPos);
    game.getObjMan().addFarObjNow(sp);
  }

  private void createGuard(SolGame game, FarShip target, ShipConfig guardConf, Fraction frac) {
    Guardian dp = new Guardian(game, guardConf.hull, target.getPilot(), target.getPos(), target.getHullConfig());
    Pilot pilot = new AiPilot(dp, true, frac, false, null, Const.AI_DET_DIST);
    boolean hasRepairer = frac == Fraction.LAANI;
    int money = guardConf.money;
    FarShip e = game.getShipBuilder().buildNewFar(game, dp.getDest(), null, dp.getAngle(), 0, pilot, guardConf.items,
      guardConf.hull, null, hasRepairer, money, null);
    game.getObjMan().addFarObjNow(e);
  }

  private Vector2 getEmptySpace(SolGame game, SolSystem s) {
    Vector2 res = new Vector2();
    Vector2 sPos = s.getPos();
    float sRadius = s.getRadius();

    for (int i = 0; i < 100; i++) {
      SolMath.fromAl(res, SolMath.rnd(180), SolMath.rnd(sRadius));
      res.add(sPos);
      if (game.isPlaceEmpty(res)) return res;
    }
    throw new AssertionError("could not generate ship position");
  }

  public Vector2 getPlayerSpawnPos(SolGame game) {
    Vector2 pos = new Vector2(Const.SUN_RADIUS * 2, 0);

    if ("planet".equals(DebugOptions.SPAWN_PLACE)) {
      Planet p = game.getPlanetMan().getPlanets().get(0);
      pos.set(p.getPos());
      pos.x += p.getFullHeight();
    } else if (DebugOptions.SPAWN_PLACE.isEmpty() && myMainStationPos != null) {
      SolMath.fromAl(pos, 90, myMainStationHc.size / 2);
      pos.add(myMainStationPos);
    } else if ("maze".equals(DebugOptions.SPAWN_PLACE)) {
      Maze m = game.getPlanetMan().getMazes().get(0);
      pos.set(m.getPos());
      pos.x += m.getRadius();
    } else if ("trader".equals(DebugOptions.SPAWN_PLACE)) {
      HullConfig cfg = game.getHullConfigs().getConfig("bus");
      for (FarObj fo : game.getObjMan().getFarObjs()) {
        if (!(fo instanceof FarShip)) continue;
        if (((FarShip) fo).getHullConfig() != cfg) continue;
        pos.set(fo.getPos());
        pos.add(cfg.approxRadius * 2, 0);
        break;
      }

    }
    return pos;
  }

  public Vector2 getMainStationPos() {
    return myMainStationPos;
  }

}
