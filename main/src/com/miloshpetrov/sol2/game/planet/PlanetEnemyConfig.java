package com.miloshpetrov.sol2.game.planet;

import com.miloshpetrov.sol2.game.ship.HullConfig;

public class PlanetEnemyConfig {
  public final HullConfig hull;
  public final String items;
  public final float density;

  public PlanetEnemyConfig(HullConfig hull, String items, float density) {
    this.hull = hull;
    this.items = items;
    this.density = density;
  }
}
