package org.destinationsol.game;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import org.destinationsol.files.FileManager;
import org.destinationsol.files.HullConfigManager;
import org.destinationsol.game.item.ItemManager;

public class PlayerSpawnConfig {
  public final ShipConfig mainStation;
  public final ShipConfig godShipConfig;
  public final ShipConfig shipConfig;

  public PlayerSpawnConfig(ShipConfig shipConfig, ShipConfig mainStation, ShipConfig godShipConfig) {
    this.shipConfig = shipConfig;
    this.mainStation = mainStation;
    this.godShipConfig = godShipConfig;
  }

  public static PlayerSpawnConfig load(HullConfigManager hullConfigs, ItemManager itemManager) {
    JsonReader r = new JsonReader();
    FileHandle configFile = FileManager.getInstance().getConfigDirectory().child("playerSpawn.json");
    JsonValue mainNode = r.parse(configFile);
    JsonValue playerNode = mainNode.get("player");
    ShipConfig shipConfig = ShipConfig.load(hullConfigs, playerNode.get("ship"), itemManager);
    ShipConfig godShipConfig = ShipConfig.load(hullConfigs, playerNode.get("godModeShip"), itemManager);
    ShipConfig mainStation = ShipConfig.load(hullConfigs, mainNode.get("mainStation"), itemManager);
    return new PlayerSpawnConfig(shipConfig, mainStation, godShipConfig);
  }
}
