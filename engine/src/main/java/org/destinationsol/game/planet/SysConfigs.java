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
package org.destinationsol.game.planet;

import com.badlogic.gdx.utils.JsonValue;
import org.destinationsol.assets.Assets;
import org.destinationsol.assets.json.Json;
import org.destinationsol.common.SolRandom;
import org.destinationsol.files.HullConfigManager;
import org.destinationsol.game.ShipConfig;
import org.destinationsol.game.chunk.SpaceEnvConfig;
import org.destinationsol.game.item.ItemManager;
import org.destinationsol.game.item.TradeConfig;
import org.terasology.assets.ResourceUrn;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class SysConfigs {
    private final Map<String, SysConfig> configs;
    private final Map<String, SysConfig> hardConfigs;
    private final Map<String, SysConfig> beltConfigs;
    private final Map<String, SysConfig> hardBeltConfigs;

    public SysConfigs(HullConfigManager hullConfigs, ItemManager itemManager) {
        configs = new HashMap<>();
        hardConfigs = new HashMap<>();
        beltConfigs = new HashMap<>();
        hardBeltConfigs = new HashMap<>();

        load("systemsConfig", hullConfigs, false, itemManager);
        load("asteroidBeltsConfig", hullConfigs, true, itemManager);
    }

    private void load(String configName, HullConfigManager hullConfigs, boolean belts, ItemManager itemManager) {
        Json json = Assets.getJson("engine:" + configName);
        JsonValue rootNode = json.getJsonValue();

        for (JsonValue node : rootNode) {
            String name = node.name;

            boolean hard = node.getBoolean("hard", false);
            Map<String, SysConfig> configs = belts ? hard ? hardBeltConfigs : beltConfigs : hard ? hardConfigs : this.configs;

            SpaceEnvConfig envConfig = new SpaceEnvConfig(node.get("environment"));

            SysConfig config = new SysConfig(name, new ArrayList<>(), envConfig, new ArrayList<>(), new ArrayList<>(), new TradeConfig(), new ArrayList<>(), hard);
            configs.put(name, config);
        }

        json.dispose();

        Set<ResourceUrn> configUrnList = Assets.getAssetHelper().list(Json.class, "[a-z]*(?<!^engine):" + configName);

        for (ResourceUrn configUrn : configUrnList) {
            json = Assets.getJson(configUrn.toString());
            rootNode = json.getJsonValue();

            for (JsonValue node : rootNode) {
                String name = node.name;

                boolean hard = node.getBoolean("hard", false);
                Map<String, SysConfig> configs = belts ? hard ? hardBeltConfigs : beltConfigs : hard ? hardConfigs : this.configs;

                SysConfig config = configs.get(name);

                // TODO : Maybe add sanity checks for config?

                config.tempEnemies.addAll(ShipConfig.loadList(node.get("temporaryEnemies"), hullConfigs, itemManager));
                config.innerTempEnemies.addAll(ShipConfig.loadList(node.get("innerTemporaryEnemies"), hullConfigs, itemManager));

                if (!belts) {
                    config.constEnemies.addAll(ShipConfig.loadList(node.get("constantEnemies"), hullConfigs, itemManager));
                    config.constAllies.addAll(ShipConfig.loadList(node.get("constantAllies"), hullConfigs, itemManager));
                }

                config.tradeConfig.load(node.get("trading"), hullConfigs, itemManager);
            }

            json.dispose();
        }
    }

    public SysConfig getRandomBelt(boolean hard) {
        Map<String, SysConfig> config = hard ? hardBeltConfigs : beltConfigs;
        return SolRandom.seededRandomElement(new ArrayList<>(config.values()));
    }

    public SysConfig getConfig(String name) {
        SysConfig res = configs.get(name);
        if (res != null) {
            return res;
        }
        return hardConfigs.get(name);
    }

    public SysConfig getRandomCfg(boolean hard) {
        Map<String, SysConfig> config = hard ? hardConfigs : configs;
        return SolRandom.seededRandomElement(new ArrayList<>(config.values()));
    }

    public void addAllConfigs(ArrayList<ShipConfig> shipConfigs) {
        for (SysConfig sc : configs.values()) {
            shipConfigs.addAll(sc.constAllies);
            shipConfigs.addAll(sc.constEnemies);
            shipConfigs.addAll(sc.tempEnemies);
            shipConfigs.addAll(sc.innerTempEnemies);
        }

        for (SysConfig sc : hardConfigs.values()) {
            shipConfigs.addAll(sc.constAllies);
            shipConfigs.addAll(sc.constEnemies);
            shipConfigs.addAll(sc.tempEnemies);
            shipConfigs.addAll(sc.innerTempEnemies);
        }

        for (SysConfig sc : beltConfigs.values()) {
            shipConfigs.addAll(sc.tempEnemies);
        }

        for (SysConfig sc : hardBeltConfigs.values()) {
            shipConfigs.addAll(sc.tempEnemies);
        }
    }
}
