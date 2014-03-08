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
  public final SpawnPlace mySpawnPlace;

  public PlayerSpawnConfig(HullConfig hullConfig, String items, int money, SpawnPlace spawnPlace) {
    this.hullConfig = hullConfig;
    this.items = items;
    this.money = money;
    this.mySpawnPlace = spawnPlace;
  }

  public static PlayerSpawnConfig load(HullConfigs hullConfigs) {
    JsonReader r = new JsonReader();
    FileHandle configFile = SolFiles.readOnly(Const.CONFIGS_DIR + "playerSpawn.json");
    JsonValue sh = r.parse(configFile);
    String hull = sh.getString("hull");
    HullConfig hullConfig = hullConfigs.getConfig(hull);
    String items = sh.getString("items");
    int money = sh.getInt("money");
    String spawnPlaceStr = sh.getString("spawnPlace");
    SpawnPlace spawnPlace = SpawnPlace.forName(spawnPlaceStr);
    return new PlayerSpawnConfig(hullConfig, items, money, spawnPlace);
  }

  public static enum SpawnPlace {
    STATION("station"), PLANET("planet"), MAZE("maze");
    private final String myName;

    SpawnPlace(String name) {
      myName = name;
    }

    public static SpawnPlace forName(String name) {
      for (SpawnPlace t : SpawnPlace.values()) {
        if (t.myName.equals(name)) return t;
      }
      return null;
    }
  }
}
