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

        JsonValue playerNode = rootNode.get("player");
        ShipConfig shipConfig = ShipConfig.load(hullConfigs, playerNode.get("ship"), itemManager);
        ShipConfig godShipConfig = ShipConfig.load(hullConfigs, playerNode.get("godModeShip"), itemManager);
        ShipConfig mainStation = ShipConfig.load(hullConfigs, rootNode.get("mainStation"), itemManager);

        json.dispose();

        return new PlayerSpawnConfig(shipConfig, mainStation, godShipConfig);
    }
}
