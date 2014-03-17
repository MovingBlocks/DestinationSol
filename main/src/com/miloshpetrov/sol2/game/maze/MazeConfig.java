package com.miloshpetrov.sol2.game.maze;

import com.miloshpetrov.sol2.TexMan;
import com.miloshpetrov.sol2.game.ShipConfig;
import com.miloshpetrov.sol2.game.ship.HullConfig;
import com.miloshpetrov.sol2.game.ship.HullConfigs;

import java.util.ArrayList;

public class MazeConfig {
  public final ArrayList<MazeTile> innerWalls;
  public final ArrayList<MazeTile> innerPasses;
  public final ArrayList<MazeTile> borderWalls;
  public final ArrayList<MazeTile> borderPasses;
  public final ArrayList<ShipConfig> outerEnemies;
  public final ArrayList<ShipConfig> innerEnemies;
  public final ArrayList<ShipConfig> bosses;

  public MazeConfig(ArrayList<MazeTile> innerWalls, ArrayList<MazeTile> innerPasses, ArrayList<MazeTile> borderWalls,
    ArrayList<MazeTile> borderPasses, ArrayList<ShipConfig> outerEnemies, ArrayList<ShipConfig> innerEnemies, ArrayList<ShipConfig> bosses)
  {
    this.innerWalls = innerWalls;
    this.innerPasses = innerPasses;
    this.borderWalls = borderWalls;
    this.borderPasses = borderPasses;
    this.outerEnemies = outerEnemies;
    this.innerEnemies = innerEnemies;
    this.bosses = bosses;
  }

  public static MazeConfig load(TexMan texMan, HullConfigs hullConfigs) {
    ArrayList<MazeTile> innerWalls = new ArrayList<MazeTile>();
    MazeTile iw = MazeTile.load(texMan, true, true);
    innerWalls.add(iw);
    ArrayList<MazeTile> innerPasses = new ArrayList<MazeTile>();
    MazeTile iw2 = MazeTile.load(texMan, false, true);
    innerPasses.add(iw2);
    ArrayList<MazeTile> borderWalls = new ArrayList<MazeTile>();
    MazeTile bw = MazeTile.load(texMan, true, false);
    borderWalls.add(bw);
    ArrayList<MazeTile> borderPasses = new ArrayList<MazeTile>();
    MazeTile bw2 = MazeTile.load(texMan, false, false);
    borderPasses.add(bw2);

    ArrayList<ShipConfig> outerEnemies = new ArrayList<ShipConfig>();
    ShipConfig ec = loadMazeEnemies(false, hullConfigs);
    outerEnemies.add(ec);
    ArrayList<ShipConfig> innerEnemies = new ArrayList<ShipConfig>();
    ShipConfig ic = loadMazeEnemies(false, hullConfigs);
    outerEnemies.add(ic);
    ArrayList<ShipConfig> bosses = new ArrayList<ShipConfig>();
    ShipConfig bc = loadMazeEnemies(true, hullConfigs);
    bosses.add(bc);
    return new MazeConfig(innerWalls, innerPasses, borderWalls, borderPasses, outerEnemies, innerEnemies, bosses);
  }

  public static ShipConfig loadMazeEnemies(boolean boss, HullConfigs hullConfigs) {
    HullConfig hull = hullConfigs.getConfig(boss ? "hunter" : "guardie");
    String items = "e wbo";
    return new ShipConfig(hull, items, .1f);
  }
}
