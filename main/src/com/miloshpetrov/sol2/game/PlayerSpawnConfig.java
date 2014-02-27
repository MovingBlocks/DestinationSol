package com.miloshpetrov.sol2.game;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.miloshpetrov.sol2.Const;
import com.miloshpetrov.sol2.SolFiles;
import com.miloshpetrov.sol2.game.ship.HullConfig;
import com.miloshpetrov.sol2.game.ship.HullConfigs;

public class PlayerSpawnConfig {

  public final HullConfig hullConfig;
  public final String items;
  public final int money;
  public final boolean nearPlanet;

  public PlayerSpawnConfig(HullConfig hullConfig, String items, int money, boolean nearPlanet) {
    this.hullConfig = hullConfig;
    this.items = items;
    this.money = money;
    this.nearPlanet = nearPlanet;
  }

  public static PlayerSpawnConfig load(HullConfigs hullConfigs) {
    JsonReader r = new JsonReader();
    FileHandle configFile = SolFiles.readOnly(Const.CONFIGS_DIR + "playerSpawn.json");
    JsonValue sh = r.parse(configFile);
    String hull = sh.getString("hull");
    HullConfig hullConfig = hullConfigs.getConfig(hull);
    String items = sh.getString("items");
    int money = sh.getInt("money");
    boolean nearPlanet = sh.getBoolean("nearPlanet");
    return new PlayerSpawnConfig(hullConfig, items, money, nearPlanet);
  }
}
