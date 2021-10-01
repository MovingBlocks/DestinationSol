/*
 * Copyright 2021 The Terasology Foundation
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
package org.destinationsol.game.planet;

import org.destinationsol.game.ShipConfig;
import org.destinationsol.game.chunk.SpaceEnvConfig;
import org.destinationsol.game.item.TradeConfig;

import java.util.ArrayList;

public class BeltConfig {

    public final String name;
    //These ArrayLists represent Configs for the enemies. When the game is populating the Belt, it will look into
    //the belt config for enemy ship configs. These enemies are temporary (they respawn)
    public final ArrayList<ShipConfig> tempEnemies;
    public final ArrayList<ShipConfig> innerTempEnemies;
    public final SpaceEnvConfig envConfig;
    public final TradeConfig tradeConfig;

    public final boolean hard;

    public BeltConfig(String name, SpaceEnvConfig envConfig, boolean hard) {
        this.name = name;
        this.envConfig = envConfig;
        this.hard = hard;
        //These objects are loaded in by the BeltConfigManager class using JSON
        this.tradeConfig = new TradeConfig();
        this.tempEnemies = new ArrayList<>();
        this.innerTempEnemies = new ArrayList<>();
    }

}
