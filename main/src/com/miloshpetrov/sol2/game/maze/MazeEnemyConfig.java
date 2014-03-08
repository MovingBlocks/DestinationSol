package com.miloshpetrov.sol2.game.maze;

import com.miloshpetrov.sol2.game.ship.HullConfig;

public class MazeEnemyConfig {
  public final HullConfig hull;
  public final String items;
  public final float density;

  public MazeEnemyConfig(HullConfig hull, String items, float density) {
    this.hull = hull;
    this.items = items;
    this.density = density;
  }

  public static MazeEnemyConfig load() {
    return new MazeEnemyConfig(null, "", 3);
  }
}
