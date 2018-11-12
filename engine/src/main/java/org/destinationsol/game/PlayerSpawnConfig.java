/*
 * Copyright 2018 MovingBlocks
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
package org.destinationsol.game;

import com.badlogic.gdx.utils.JsonValue;
import org.destinationsol.common.SolException;
import org.destinationsol.assets.Assets;
import org.destinationsol.assets.json.Json;
import org.destinationsol.files.HullConfigManager;
import org.destinationsol.game.item.ItemManager;

public class PlayerSpawnConfig {
    final ShipConfig mainStation;
    final ShipConfig godShipConfig;
    final ShipConfig shipConfig;

    PlayerSpawnConfig(ShipConfig shipConfig, ShipConfig mainStation, ShipConfig godShipConfig) {
        this.shipConfig = shipConfig;
        this.mainStation = mainStation;
        this.godShipConfig = godShipConfig;
    }

    public static PlayerSpawnConfig load(HullConfigManager hullConfigs, ItemManager itemManager) {
        Json json = Assets.getJson("engine:playerSpawnConfig");
        JsonValue rootNode = json.getJsonValue();
        ShipConfig shipConfig, mainStation, godShipConfig;

        if (rootNode.get("players") != null) {
            throw new SolException("Please rename 'players' to 'player' in the JSON");
        }
        else if (rootNode.get("player") == null) {
            throw new SolException("There is no player in the JSON of " + rootNode);
        }
        else {
            JsonValue playerNode = rootNode.get("player");
            if (playerNode.get("ship") == null){
                throw new SolException("The player, " + playerNode + " ,does not have a ship!");
            }
            else if (playerNode.get("ships") != null) {
                throw new SolException("Please rename 'ships' to 'ship' in the JSON");
            }
            else {
                shipConfig = ShipConfig.load(hullConfigs, playerNode.get("ship"), itemManager);
                if (playerNode.get("godModeShip") == null) {
                    throw new SolException("The player, " + playerNode + " ,does not have a godModeShip!");
                }
                else if (playerNode.get("godModeShips") != null) {
                    throw new SolException("Please rename 'godModeShips' to 'godModeShip' in the JSON");
                }
                else {
                    godShipConfig = ShipConfig.load(hullConfigs, playerNode.get("godModeShip"), itemManager);
                    if (rootNode.get("mainStation") == null) {
                        throw new SolException("rootNode, " + rootNode + " ,does not have a main station");
                    }
                    else if (rootNode.get("mainStations") != null) {
                        throw new SolException("Please rename 'mainStations to 'mainStation' in the JSON");
                    }
                    else {
                        mainStation = ShipConfig.load(hullConfigs, rootNode.get("mainStation"), itemManager);
                    }
                }
            }  

            json.dispose();

            return new PlayerSpawnConfig(shipConfig, mainStation, godShipConfig);
        }
    }
}