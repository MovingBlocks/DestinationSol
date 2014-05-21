package com.miloshpetrov.sol2.game;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.miloshpetrov.sol2.Const;
import com.miloshpetrov.sol2.SolFiles;
import com.miloshpetrov.sol2.game.ship.HullConfigs;

public class PlayerSpawnConfig {
  public final ShipConfig mainStation;
  public final ShipConfig godShipConfig;
  public final ShipConfig shipConfig;

  public PlayerSpawnConfig(ShipConfig shipConfig, ShipConfig mainStation, ShipConfig godShipConfig) {
    this.shipConfig = shipConfig;
    this.mainStation = mainStation;
    this.godShipConfig = godShipConfig;
  }

  public static PlayerSpawnConfig load(HullConfigs hullConfigs) {
    JsonReader r = new JsonReader();
    FileHandle configFile = SolFiles.readOnly(Const.CONFIGS_DIR + "playerSpawn.json");
    JsonValue mainNode = r.parse(configFile);
    JsonValue playerNode = mainNode.get("player");
    ShipConfig shipConfig = ShipConfig.load(hullConfigs, playerNode.get("ship"));
    ShipConfig godShipConfig = ShipConfig.load(hullConfigs, playerNode.get("godModeShip"));
    ShipConfig mainStation = ShipConfig.load(hullConfigs, mainNode.get("mainStation"));
    return new PlayerSpawnConfig(shipConfig, mainStation, godShipConfig);
  }
}
