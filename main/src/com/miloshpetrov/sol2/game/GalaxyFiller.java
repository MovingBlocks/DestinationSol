package com.miloshpetrov.sol2.game;

import com.badlogic.gdx.math.Vector2;
import com.miloshpetrov.sol2.Const;
import com.miloshpetrov.sol2.common.SolMath;
import com.miloshpetrov.sol2.game.input.*;
import com.miloshpetrov.sol2.game.planet.*;
import com.miloshpetrov.sol2.game.ship.*;

import java.util.ArrayList;
import java.util.HashSet;

public class GalaxyFiller {
  private SolShip myMainStation;

  public GalaxyFiller() {
  }

  public void fill(SolGame game) {
    PlanetMan planetMan = game.getPlanetMan();
    ArrayList<Planet> planets = planetMan.getPlanets();
    int shipAmt = planets.size() / 2;
    for (int i = 0; i < shipAmt; i++) {
      createExplorer(game, true);
      createExplorer(game, false);
      createTrader(game);
    }

    for (int i = 0; i < 3; i++) {
      createMerch(game);
    }

    HashSet<SolSystem> filled = new HashSet<SolSystem>();
    for (Planet p : planets) {
      SolSystem sys = p.getSys();
      if (filled.contains(sys)) continue;
      float r = sys.getRadius();
      float dist = p.getDist();
      if (dist < r * .3f || r * .6f < dist) continue;
      filled.add(sys);
      float stationDist = dist + p.getFullHeight() + Const.PLANET_GAP;
      Vector2 stationPos = new Vector2();
      SolMath.fromAl(stationPos, SolMath.rnd(180), stationDist);
      stationPos.add(sys.getPos());

      HullConfig config = game.getHullConfigs().station;
      float detectionDist = game.getCam().getSpaceViewDist();
      Pilot pilot = new AiPilot(new NoDestProvider(), true, Fraction.LAANI, true, "station", detectionDist);

      float angle = myMainStation == null ? 0 : SolMath.rnd(180);
      SolShip s = game.getShipBuilder().buildNew(game, stationPos, null, angle, 0, pilot, "mg rl r:1:4", config, false, false, null, true, 300f, "");
      game.getObjMan().addObjDelayed(s);
      for (int j = 0; j < 4; j++) {
        createGuard(game, s);
      }
      if (myMainStation == null) myMainStation = s;
    }

    createStarPorts(game);
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

  private void createGuard(SolGame game, SolShip target) {
    Fraction frac = target.getPilot().getFraction();
    boolean isLaani = frac == Fraction.LAANI;
    HullConfigs configs = game.getHullConfigs();
    HullConfig config = isLaani ? configs.vanguard : configs.guardie;
    Guardian dp = new Guardian(game, isLaani ? 2f : 5f, target, config);
    float detectionDist = game.getCam().getSpaceViewDist() * 2;
    Pilot pilot = new AiPilot(dp, true, frac, false, null, detectionDist);

    String items = isLaani ? "e b:1:4 r:1:2 rl|mg sBig aBig" : "e b:1:2 bo|sg s|sMed:.2 a:.2 rep:.4";
    SolShip e = game.getShipBuilder().buildNew(game, dp.getDest(), null, SolMath.rnd(180), 0, pilot, items, config, true, true, null, false, 50f, null);
    game.getObjMan().addObjDelayed(e);
  }

  private void createMerch(SolGame game) {
    Vector2 merchPos = getEmptySpace(game);
    if (merchPos == null) return;
    HullConfig config = game.getHullConfigs().bus;
    MoveDestProvider dp = new ExplorerDestProvider(game, merchPos, false, 2f, config, 1.5f);
    float detectionDist = game.getCam().getSpaceViewDist();
    Pilot pilot = new AiPilot(dp, true, Fraction.LAANI, false, "merchant", detectionDist);

    SolShip s = game.getShipBuilder().buildNew(game, merchPos, null, 0, 0, pilot, "mg rl eBig aBig sBig b:1:4 r:1:4 rep:1:4", config, false, false, null,
      true, 300f, "");
    game.getObjMan().addObjDelayed(s);
  }

  private void createExplorer(SolGame game, boolean isLaani) {
    Vector2 pos = getEmptySpace(game);
    if (pos == null) return;

    HullConfigs configs = game.getHullConfigs();
    HullConfig config = isLaani ? configs.orbiter : configs.hunter;
    MoveDestProvider dp = new ExplorerDestProvider(game, pos, true, 4f, config, .75f);
    float detectionDist = game.getCam().getSpaceViewDist();
    Pilot pilot = new AiPilot(dp, true, isLaani ? Fraction.LAANI : Fraction.EHAR, false, isLaani ? null : "hunter", detectionDist * 2);

    String items = "e b:1:4 rep:.8:2 bo|mg sMed|sBig:.5 aMed|aBig:.3";
    SolShip e = game.getShipBuilder().buildNew(game, pos, null, 0, 0, pilot, items, config, true, true, null, true, 50f, null);
    game.getObjMan().addObjDelayed(e);
  }

  private void createTrader(SolGame game) {
    Vector2 pos = getEmptySpace(game);
    if (pos == null) return;

    HullConfig config = game.getHullConfigs().truck;
    MoveDestProvider dp = new ExplorerDestProvider(game, pos, false, 2f, config, .75f);
    float detectionDist = game.getCam().getSpaceViewDist();
    Pilot pilot = new AiPilot(dp, true, Fraction.EHAR, false, "enemy trader", detectionDist);

    SolShip e = game.getShipBuilder().buildNew(game, pos, null, 0, 0, pilot, "eBig bo s|sMed", config, false, true, null, true, 200f, "");
    game.getObjMan().addObjDelayed(e);

    for (int j = 0; j < 3; j++) {
        createGuard(game, e);
    }
  }

  private Vector2 getEmptySpace(SolGame game) {
    ArrayList<SolSystem> ss = game.getPlanetMan().getSystems();
    SolSystem s = SolMath.elemRnd(ss);
    Vector2 res = new Vector2();
    Vector2 sPos = s.getPos();
    float sRadius = s.getRadius();

    for (int i = 0; i < 10; i++) {
      res.set(SolMath.rnd(sRadius), SolMath.rnd(sRadius)).add(sPos);
      if (game.isPlaceEmpty(res)) return res;
    }

    return null;
  }


  public Vector2 getPlayerSpawnPos(SolGame game) {
    Vector2 pos = new Vector2();
    SolMath.fromAl(pos, 90, myMainStation.getHull().config.size/2);
    pos.add(myMainStation.getPos());
//    Planet p = game.getPlanetMan().getPlanets().get(0);
//    pos.add(p.getPos());
//    pos.x += p.getFullHeight();
    return pos;
  }

  public SolShip getMainStation() {
    return myMainStation;
  }

}
