package com.miloshpetrov.sol2.game.planet;

import com.miloshpetrov.sol2.TexMan;

import java.util.List;

public class PlanetConfig {
  public final String configName;
  public final float minGrav;
  public final float maxGrav;
  public final List<DecoConfig> deco;
  public final List<PlanetEnemyConfig> groundEnemies;
  public final List<PlanetEnemyConfig> orbitEnemies;
  public final PlanetTiles planetTiles;

  public PlanetConfig(String configName, float minGrav, float maxGrav, List<DecoConfig> deco,
    List<PlanetEnemyConfig> groundEnemies,
    List<PlanetEnemyConfig> orbitEnemies, TexMan texMan)
  {
    this.configName = configName;
    this.minGrav = minGrav;
    this.maxGrav = maxGrav;
    this.deco = deco;
    this.groundEnemies = groundEnemies;
    this.orbitEnemies = orbitEnemies;
    planetTiles = new PlanetTiles(texMan, configName);
  }
}
