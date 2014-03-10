package com.miloshpetrov.sol2.game.planet;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.miloshpetrov.sol2.game.ShipConfig;

import java.util.ArrayList;
import java.util.List;

public class PlanetConfig {
  public final String configName;
  public final float minGrav;
  public final float maxGrav;
  public final List<DecoConfig> deco;
  public final List<ShipConfig> groundEnemies;
  public final List<ShipConfig> orbitEnemies;
  public final PlanetTiles planetTiles;
  public final ArrayList<TextureAtlas.AtlasRegion> cloudTexs;

  public PlanetConfig(String configName, float minGrav, float maxGrav, List<DecoConfig> deco,
    List<ShipConfig> groundEnemies,
    List<ShipConfig> orbitEnemies, ArrayList<TextureAtlas.AtlasRegion> cloudTexs, PlanetTiles planetTiles)
  {
    this.configName = configName;
    this.minGrav = minGrav;
    this.maxGrav = maxGrav;
    this.deco = deco;
    this.groundEnemies = groundEnemies;
    this.orbitEnemies = orbitEnemies;
    this.cloudTexs = cloudTexs;
    this.planetTiles = planetTiles;
  }
}
