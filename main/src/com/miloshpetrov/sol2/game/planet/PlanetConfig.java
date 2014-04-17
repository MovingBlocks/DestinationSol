package com.miloshpetrov.sol2.game.planet;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.JsonValue;
import com.miloshpetrov.sol2.TexMan;
import com.miloshpetrov.sol2.game.GameCols;
import com.miloshpetrov.sol2.game.ShipConfig;
import com.miloshpetrov.sol2.game.ship.HullConfigs;

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
  public final ShipConfig stationConfig;
  public final SkyConfig skyConfig;
  public final ArrayList<TextureAtlas.AtlasRegion> cloudTexs;

  public PlanetConfig(String configName, float minGrav, float maxGrav, List<DecoConfig> deco,
    List<ShipConfig> groundEnemies,
    List<ShipConfig> orbitEnemies, ArrayList<TextureAtlas.AtlasRegion> cloudTexs, PlanetTiles planetTiles,
    ShipConfig stationConfig, SkyConfig skyConfig)
  {
    this.configName = configName;
    this.minGrav = minGrav;
    this.maxGrav = maxGrav;
    this.deco = deco;
    this.groundEnemies = groundEnemies;
    this.orbitEnemies = orbitEnemies;
    this.cloudTexs = cloudTexs;
    this.planetTiles = planetTiles;
    this.stationConfig = stationConfig;
    this.skyConfig = skyConfig;
  }

  static PlanetConfig load(TexMan texMan, HullConfigs hullConfigs, FileHandle configFile, JsonValue sh, GameCols cols) {
    float minGrav = sh.getFloat("minGrav");
    float maxGrav = sh.getFloat("maxGrav");
    List<DecoConfig> deco = DecoConfig.load(sh, texMan, configFile);
    ArrayList<ShipConfig> groundEnemies = ShipConfig.loadList(sh.get("groundEnemies"), hullConfigs);
    ArrayList<ShipConfig> orbitEnemies = ShipConfig.loadList(sh.get("orbitEnemies"), hullConfigs);
    ShipConfig stationConfig = ShipConfig.load(hullConfigs, sh.get("station"));
    String cloudPackName = sh.getString("cloudTexs");
    ArrayList<TextureAtlas.AtlasRegion> cloudTexs = texMan.getPack(cloudPackName, configFile);
    String groundFolder = sh.getString("groundTexs");
    PlanetTiles planetTiles = new PlanetTiles(texMan, groundFolder, configFile);
    SkyConfig skyConfig = SkyConfig.load(sh.get("sky"), cols);
    return new PlanetConfig(sh.name, minGrav, maxGrav, deco, groundEnemies, orbitEnemies, cloudTexs, planetTiles, stationConfig, skyConfig);
  }
}
