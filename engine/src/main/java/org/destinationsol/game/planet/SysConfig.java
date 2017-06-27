/*
 * Copyright 2017 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
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

public class SysConfig {

    public final String name;
    public final ArrayList<ShipConfig> tempEnemies;
    public final SpaceEnvConfig envConfig;
    public final ArrayList<ShipConfig> constEnemies;
    public final ArrayList<ShipConfig> constAllies;
    public final TradeConfig tradeConfig;
    public final ArrayList<ShipConfig> innerTempEnemies;
    public final boolean hard;

    public SysConfig(String name, ArrayList<ShipConfig> tempEnemies, SpaceEnvConfig envConfig,
                     ArrayList<ShipConfig> constEnemies, ArrayList<ShipConfig> constAllies, TradeConfig tradeConfig,
                     ArrayList<ShipConfig> innerTempEnemies, boolean hard) {
        this.name = name;
        this.tempEnemies = tempEnemies;
        this.envConfig = envConfig;
        this.constEnemies = constEnemies;
        this.constAllies = constAllies;
        this.tradeConfig = tradeConfig;
        this.innerTempEnemies = innerTempEnemies;
        this.hard = hard;
    }
}
