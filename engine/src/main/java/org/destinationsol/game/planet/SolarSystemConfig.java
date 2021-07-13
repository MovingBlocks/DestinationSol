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

public class SolarSystemConfig {

    public final String name;
    public final SpaceEnvConfig envConfig;
    //These ArrayLists represent Configs for the enemy ships. When the game is populating the SolarSystem, it will look into
    //the SolarSystem config for enemy ship configs. Some enemies are temporary (they respawn) and some are constant (they don't respawn)
    public final ArrayList<ShipConfig> tempEnemies;
    public final ArrayList<ShipConfig> constEnemies;
    public final ArrayList<ShipConfig> constAllies;
    public final ArrayList<ShipConfig> innerTempEnemies;
    public final TradeConfig tradeConfig;
    public final boolean hard;

    public SolarSystemConfig(String name, SpaceEnvConfig envConfig, boolean hard) {
        this.name = name;
        this.envConfig = envConfig;
        this.hard = hard;

        //These objects are loaded in by the SolarSystemConfigManager class using JSON
        this.tradeConfig = new TradeConfig();
        this.constEnemies = new ArrayList<>();
        this.constAllies = new ArrayList<>();
        this.tempEnemies = new ArrayList<>();
        this.innerTempEnemies = new ArrayList<>();
    }

}
