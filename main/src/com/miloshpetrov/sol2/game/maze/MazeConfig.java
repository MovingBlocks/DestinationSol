package com.miloshpetrov.sol2.game.maze;

import com.miloshpetrov.sol2.TexMan;
import com.miloshpetrov.sol2.game.ship.HullConfigs;

import java.util.ArrayList;

public class MazeConfig {
  public final ArrayList<MazeTile> innerWalls;
  public final ArrayList<MazeTile> innerPasses;
  public final ArrayList<MazeTile> borderWalls;
  public final ArrayList<MazeTile> borderPasses;
  public final ArrayList<MazeEnemyConfig> outerEnemies;
  public final ArrayList<MazeEnemyConfig> bosses;

  public MazeConfig(ArrayList<MazeTile> innerWalls, ArrayList<MazeTile> innerPasses, ArrayList<MazeTile> borderWalls,
    ArrayList<MazeTile> borderPasses, ArrayList<MazeEnemyConfig> outerEnemies, ArrayList<MazeEnemyConfig> bosses)
  {
    this.innerWalls = innerWalls;
    this.innerPasses = innerPasses;
    this.borderWalls = borderWalls;
    this.borderPasses = borderPasses;
    this.outerEnemies = outerEnemies;
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

    ArrayList<MazeEnemyConfig> outerEnemies = new ArrayList<MazeEnemyConfig>();
    MazeEnemyConfig ec = MazeEnemyConfig.load(false, hullConfigs);
    outerEnemies.add(ec);
    ArrayList<MazeEnemyConfig> bosses = new ArrayList<MazeEnemyConfig>();
    MazeEnemyConfig bc = MazeEnemyConfig.load(true, hullConfigs);
    bosses.add(bc);
    return new MazeConfig(innerWalls, innerPasses, borderWalls, borderPasses, outerEnemies, bosses);
  }
}
