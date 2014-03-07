package com.miloshpetrov.sol2.game.planet;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.miloshpetrov.sol2.*;
import com.miloshpetrov.sol2.common.SolMath;
import com.miloshpetrov.sol2.game.ship.HullConfig;
import com.miloshpetrov.sol2.game.ship.HullConfigs;

import java.util.*;

public class PlanetConfigs {
  private final Map<String, PlanetConfig> myConfigs;

  public PlanetConfigs(TexMan texMan, HullConfigs hullConfigs) {
    myConfigs = new HashMap<String, PlanetConfig>();

    JsonReader r = new JsonReader();
    FileHandle configFile = SolFiles.readOnly(Const.CONFIGS_DIR + "planets.json");
    JsonValue parsed = r.parse(configFile);
    for (JsonValue sh : parsed) {
      float minGrav = sh.getFloat("minGrav");
      float maxGrav = sh.getFloat("maxGrav");
      List<DecoConfig> deco = loadDecoConfigs(sh, texMan, configFile);
      ArrayList<PlanetEnemyConfig> groundEnemies = loadEnemiyConfigs(sh.get("groundEnemies"), hullConfigs);
      ArrayList<PlanetEnemyConfig> orbitEnemies = loadEnemiyConfigs(sh.get("orbitEnemies"), hullConfigs);
      String skyPackName = sh.getString("skyTexs");
      ArrayList<TextureAtlas.AtlasRegion> cloudTexs = texMan.getPack(skyPackName, configFile);
      String groundFolder = sh.getString("groundTexs");
      PlanetTiles planetTiles = new PlanetTiles(texMan, groundFolder, configFile);
      PlanetConfig c = new PlanetConfig(sh.name, minGrav, maxGrav, deco, groundEnemies, orbitEnemies, cloudTexs, planetTiles);
      myConfigs.put(sh.name, c);
    }
  }

  private ArrayList<PlanetEnemyConfig> loadEnemiyConfigs(JsonValue enemies, HullConfigs hullConfigs) {
    ArrayList<PlanetEnemyConfig> res = new ArrayList<PlanetEnemyConfig>();
    for (JsonValue e : enemies) {
      String hullName = e.getString("hull");
      HullConfig hull = hullConfigs.getConfig(hullName);
      String items = e.getString("items");
      float density = e.getFloat("density");
      PlanetEnemyConfig c = new PlanetEnemyConfig(hull, items, density);
      res.add(c);
    }
    return res;
  }

  private List<DecoConfig> loadDecoConfigs(JsonValue planetConfig, TexMan texMan, FileHandle configFile) {
    ArrayList<DecoConfig> res = new ArrayList<DecoConfig>();
    for (JsonValue deco : planetConfig.get("deco")) {
      float density = deco.getFloat("density");
      float szMin = deco.getFloat("szMin");
      float szMax = deco.getFloat("szMax");
      Vector2 orig = SolMath.readV2(deco, "orig");
      boolean allowFlip = deco.getBoolean("allowFlip");
      String texName = planetConfig.getString("decoTexs") + "/" + deco.name;
      ArrayList<TextureAtlas.AtlasRegion> texs = texMan.getPack(texName, configFile);
      DecoConfig c = new DecoConfig(density, szMin, szMax, orig, allowFlip, texs);
      res.add(c);
    }
    return res;
  }

  public PlanetConfig getConfig(String name) {
    return myConfigs.get(name);
  }

  public PlanetConfig getRandom() {
    return SolMath.elemRnd(new ArrayList<PlanetConfig>(myConfigs.values()));
  }
}
