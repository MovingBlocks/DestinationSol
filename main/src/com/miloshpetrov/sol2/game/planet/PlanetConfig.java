package com.miloshpetrov.sol2.game.planet;

import java.util.List;
import java.util.Map;

public class PlanetConfig {
  public final String name;
  public final float minGrav;
  public final float maxGrav;
  public final List<DecoConfig> deco;
  public final List<PlanetEnemyConfig> groundEnemies;
  public final List<PlanetEnemyConfig> orbitEnemies;
  public final Map<SurfDir, Map<SurfDir, List<Tile>>> groundTiles;

  public PlanetConfig(String name, float minGrav, float maxGrav, List<DecoConfig> deco, List<PlanetEnemyConfig> groundEnemies,
    List<PlanetEnemyConfig> orbitEnemies, Map<SurfDir, Map<SurfDir, List<Tile>>> groundTiles)
  {
    this.name = name;
    this.minGrav = minGrav;
    this.maxGrav = maxGrav;
    this.deco = deco;
    this.groundEnemies = groundEnemies;
    this.orbitEnemies = orbitEnemies;
    this.groundTiles = groundTiles;
  }
}
