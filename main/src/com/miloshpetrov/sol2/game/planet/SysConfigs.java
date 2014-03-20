package com.miloshpetrov.sol2.game.planet;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.miloshpetrov.sol2.*;
import com.miloshpetrov.sol2.common.SolMath;
import com.miloshpetrov.sol2.game.ShipConfig;
import com.miloshpetrov.sol2.game.chunk.SpaceEnvConfig;
import com.miloshpetrov.sol2.game.ship.HullConfigs;

import java.util.*;

public class SysConfigs {
  private final Map<String, SysConfig> myConfigs;
  private final Map<String, SysConfig> myBeltConfigs;

  public SysConfigs(TexMan texMan, HullConfigs hullConfigs) {
    myConfigs = new HashMap<String, SysConfig>();
    myBeltConfigs = new HashMap<String, SysConfig>();

    load(texMan, hullConfigs, myConfigs, "systems.json");
    load(texMan, hullConfigs, myBeltConfigs, "asteroidBelts.json");
  }

  private void load(TexMan texMan, HullConfigs hullConfigs, Map<String, SysConfig> configs, String configName) {
    JsonReader r = new JsonReader();
    FileHandle configFile = SolFiles.readOnly(Const.CONFIGS_DIR + configName);
    JsonValue parsed = r.parse(configFile);
    for (JsonValue sh : parsed) {
      ArrayList<ShipConfig> tempEnemies = ShipConfig.loadList(sh.get("temporaryEnemies"), hullConfigs);
      SpaceEnvConfig envConfig = new SpaceEnvConfig(sh.get("environment"), texMan, configFile);

      ArrayList<ShipConfig> constEnemies = null;
      ArrayList<ShipConfig> constAllies = null;
      if (configs == myConfigs) {
        constEnemies = ShipConfig.loadList(sh.get("constantEnemies"), hullConfigs);
        constAllies = ShipConfig.loadList(sh.get("constantAllies"), hullConfigs);
      }
      SysConfig c = new SysConfig(sh.name, tempEnemies, envConfig, constEnemies, constAllies);
      configs.put(sh.name, c);
    }
  }

  public SysConfig getRandom() {
    return SolMath.elemRnd(new ArrayList<SysConfig>(myConfigs.values()));
  }

  public SysConfig getRandomBelt() {
    return SolMath.elemRnd(new ArrayList<SysConfig>(myBeltConfigs.values()));
  }

}
