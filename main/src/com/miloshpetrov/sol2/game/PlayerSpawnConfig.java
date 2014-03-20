package com.miloshpetrov.sol2.game;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.miloshpetrov.sol2.Const;
import com.miloshpetrov.sol2.SolFiles;
import com.miloshpetrov.sol2.game.ship.HullConfigs;

public class PlayerSpawnConfig {

  public final int money;
  public final SpawnPlace mySpawnPlace;
  public final ShipConfig shipConfig;

  public PlayerSpawnConfig(int money, SpawnPlace spawnPlace, ShipConfig shipConfig) {
    this.shipConfig = shipConfig;
    this.money = money;
    this.mySpawnPlace = spawnPlace;
  }

  public static PlayerSpawnConfig load(HullConfigs hullConfigs) {
    JsonReader r = new JsonReader();
    FileHandle configFile = SolFiles.readOnly(Const.CONFIGS_DIR + "playerSpawn.json");
    JsonValue sh = r.parse(configFile);
    ShipConfig shipConfig = ShipConfig.load(hullConfigs, sh.get("ship"));
    int money = sh.getInt("money");
    String spawnPlaceStr = sh.getString("spawnPlace");
    SpawnPlace spawnPlace = SpawnPlace.forName(spawnPlaceStr);
    return new PlayerSpawnConfig(money, spawnPlace, shipConfig);
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
