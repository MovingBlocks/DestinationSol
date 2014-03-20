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
  public final ShipConfig mainStation;
  public final ShipConfig shipConfig;

  public PlayerSpawnConfig(int money, SpawnPlace spawnPlace, ShipConfig shipConfig, ShipConfig mainStation) {
    this.shipConfig = shipConfig;
    this.money = money;
    this.mySpawnPlace = spawnPlace;
    this.mainStation = mainStation;
  }

  public static PlayerSpawnConfig load(HullConfigs hullConfigs) {
    JsonReader r = new JsonReader();
    FileHandle configFile = SolFiles.readOnly(Const.CONFIGS_DIR + "playerSpawn.json");
    JsonValue mainNode = r.parse(configFile);
    JsonValue playerNode = mainNode.get("player");
    ShipConfig shipConfig = ShipConfig.load(hullConfigs, playerNode.get("ship"));
    int money = playerNode.getInt("money");
    String spawnPlaceStr = playerNode.getString("spawnPlace");
    SpawnPlace spawnPlace = SpawnPlace.forName(spawnPlaceStr);
    ShipConfig mainStation = ShipConfig.load(hullConfigs, mainNode.get("mainStation"));
    return new PlayerSpawnConfig(money, spawnPlace, shipConfig, mainStation);
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
