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

import org.destinationsol.assets.Assets;
import org.destinationsol.assets.json.Json;
import org.destinationsol.assets.json.Validator;
import org.destinationsol.common.SolRandom;
import org.destinationsol.files.HullConfigManager;
import org.destinationsol.game.ShipConfig;
import org.destinationsol.game.chunk.SpaceEnvConfig;
import org.destinationsol.game.item.ItemManager;
import org.destinationsol.game.item.TradeConfig;
import org.json.JSONObject;
import org.terasology.gestalt.assets.ResourceUrn;
import org.terasology.gestalt.naming.Name;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class SolarSystemConfigManager {
    private final Map<String, SolarSystemConfig> configs;
    private final Map<String, SolarSystemConfig> hardConfigs;

    public SolarSystemConfigManager(HullConfigManager hullConfigs, ItemManager itemManager) {
        configs = new HashMap<>();
        hardConfigs = new HashMap<>();

        load(hullConfigs, false, itemManager);
    }

    private void load(HullConfigManager hullConfigs, boolean b, ItemManager itemManager) {
        JSONObject rootNode = Validator.getValidatedJSON("engine:systemsConfig", "engine:schemaSystemsConfig");

        for (String s : rootNode.keySet()) {
            if (!(rootNode.get(s) instanceof JSONObject)) {
                continue;
            }
            JSONObject node = rootNode.getJSONObject(s);
            String name = s;

            boolean hard = node.optBoolean("hard", false);
            Map<String, SolarSystemConfig> configsToLoad = hard ? hardConfigs : configs;

            SpaceEnvConfig envConfig = new SpaceEnvConfig(node.getJSONObject("environment"));

            SolarSystemConfig solarSystemConfig = new SolarSystemConfig(name, new ArrayList<>(), envConfig, new ArrayList<>(), new ArrayList<>(), new TradeConfig(), new ArrayList<>(), hard);
            configsToLoad.put(name, solarSystemConfig);
        }

        Set<ResourceUrn> configUrnList = Assets.getAssetHelper().listAssets(Json.class, "systemsConfig", new Name("engine"));

        for (ResourceUrn configUrn : configUrnList) {
            rootNode = Validator.getValidatedJSON(configUrn.toString(), "engine:schemaSystemsConfig");

            for (String s : rootNode.keySet()) {
                if (!(rootNode.get(s) instanceof JSONObject)) {
                    continue;
                }
                JSONObject node = rootNode.getJSONObject(s);
                String name = s;

                boolean hard = node.optBoolean("hard", false);
                Map<String, SolarSystemConfig> configsToLoad = hard ? hardConfigs : configs;

                SolarSystemConfig config = configsToLoad.get(name);

                // TODO : Maybe add sanity checks for config?

                config.tempEnemies.addAll(ShipConfig.loadList(node.has("temporaryEnemies") ? node.getJSONArray("temporaryEnemies") : null, hullConfigs, itemManager));
                config.innerTempEnemies.addAll(ShipConfig.loadList(node.has("innerTemporaryEnemies") ? node.getJSONArray("innerTemporaryEnemies") : null, hullConfigs, itemManager));
                config.constEnemies.addAll(ShipConfig.loadList(node.has("constantEnemies") ? node.getJSONArray("constantEnemies") : null, hullConfigs, itemManager));
                config.constAllies.addAll(ShipConfig.loadList(node.has("constantAllies") ? node.getJSONArray("constantAllies") : null, hullConfigs, itemManager));

                config.tradeConfig.load(node.has("trading") ? node.getJSONObject("trading") : null, hullConfigs, itemManager);
            }
        }
    }

    public SolarSystemConfig getSolarSystemConfig(String name) {
        SolarSystemConfig res = configs.get(name);
        if (res != null) {
            return res;
        }
        return hardConfigs.get(name);
    }

    public SolarSystemConfig getRandomSolarSystemConfig(boolean hard) {
        Map<String, SolarSystemConfig> config = hard ? hardConfigs : configs;
        return SolRandom.seededRandomElement(new ArrayList<>(config.values()));
    }

    public void addAllConfigs(ArrayList<ShipConfig> shipConfigs) {
        for (SolarSystemConfig sc : configs.values()) {
            shipConfigs.addAll(sc.constAllies);
            shipConfigs.addAll(sc.constEnemies);
            shipConfigs.addAll(sc.tempEnemies);
            shipConfigs.addAll(sc.innerTempEnemies);
        }

        for (SolarSystemConfig sc : hardConfigs.values()) {
            shipConfigs.addAll(sc.constAllies);
            shipConfigs.addAll(sc.constEnemies);
            shipConfigs.addAll(sc.tempEnemies);
            shipConfigs.addAll(sc.innerTempEnemies);
        }
    }

}
