/*
 * Copyright 2015 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
 
package com.miloshpetrov.sol2.game;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.miloshpetrov.sol2.files.FileManager;
import com.miloshpetrov.sol2.game.item.ItemMan;
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

  public static PlayerSpawnConfig load(HullConfigs hullConfigs, ItemMan itemMan) {
    JsonReader r = new JsonReader();
    FileHandle configFile = FileManager.getInstance().getConfigDirectory().child("playerSpawn.json");
    JsonValue mainNode = r.parse(configFile);
    JsonValue playerNode = mainNode.get("player");
    ShipConfig shipConfig = ShipConfig.load(hullConfigs, playerNode.get("ship"), itemMan);
    ShipConfig godShipConfig = ShipConfig.load(hullConfigs, playerNode.get("godModeShip"), itemMan);
    ShipConfig mainStation = ShipConfig.load(hullConfigs, mainNode.get("mainStation"), itemMan);
    return new PlayerSpawnConfig(shipConfig, mainStation, godShipConfig);
  }
}
