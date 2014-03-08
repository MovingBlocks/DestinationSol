package com.miloshpetrov.sol2.game.maze;

import java.util.ArrayList;

public class MazeConfig {
  public final ArrayList<MazeTile> innerWalls;
  public final ArrayList<MazeTile> innerWays;
  public final ArrayList<MazeTile> borderWalls;
  public final ArrayList<MazeTile> borderWays;
  public final ArrayList<MazeEnemyConfig> outerEnemies;
  public final ArrayList<MazeEnemyConfig> bosses;

  public MazeConfig(ArrayList<MazeTile> innerWalls, ArrayList<MazeTile> innerWays, ArrayList<MazeTile> borderWalls,
    ArrayList<MazeTile> borderWays, ArrayList<MazeEnemyConfig> outerEnemies, ArrayList<MazeEnemyConfig> bosses)
  {
    this.innerWalls = innerWalls;
    this.innerWays = innerWays;
    this.borderWalls = borderWalls;
    this.borderWays = borderWays;
    this.outerEnemies = outerEnemies;
    this.bosses = bosses;
  }

  public static MazeConfig load() {
    ArrayList<MazeTile> innerWalls = new ArrayList<MazeTile>();
    ArrayList<MazeTile> innerWays = new ArrayList<MazeTile>();
    ArrayList<MazeTile> borderWalls = new ArrayList<MazeTile>();
    ArrayList<MazeTile> borderWays = new ArrayList<MazeTile>();

    ArrayList<MazeEnemyConfig> outerEnemies = new ArrayList<MazeEnemyConfig>();
    MazeEnemyConfig ec = MazeEnemyConfig.load();
    outerEnemies.add(ec);
    ArrayList<MazeEnemyConfig> bosses = new ArrayList<MazeEnemyConfig>();
    MazeEnemyConfig bc = MazeEnemyConfig.load();
    bosses.add(bc);
    return new MazeConfig(innerWalls, innerWays, borderWalls, borderWays, outerEnemies, bosses);
  }
}
