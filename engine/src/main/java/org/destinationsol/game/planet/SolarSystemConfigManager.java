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
import org.json.JSONObject;
import org.terasology.gestalt.assets.ResourceUrn;
import org.terasology.gestalt.naming.Name;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class SolarSystemConfigManager {
    private final Map<String, SolarSystemConfig> configs;
    private final Map<String, SolarSystemConfig> hardConfigs;
    private HullConfigManager hullConfigManager;
    private ItemManager itemManager;

    @Inject
    public SolarSystemConfigManager(HullConfigManager hullConfigManager, ItemManager itemManager) {
        configs = new HashMap<>();
        hardConfigs = new HashMap<>();
        this.hullConfigManager = hullConfigManager;
        this.itemManager = itemManager;
    }

    /**
     * This method is used to tell the JSON data loader to load data from the engine module.
     */
    public void loadDefaultSolarSystemConfigs() {
        load("engine:systemsConfig", "engine:schemaSystemsConfig", "systemsConfig");
    }

    /**
     * This method loads in all the SolarSystem configuration data from the specified location.
     * @param jsonPath path to the JSON data
     * @param schemaPath path to the schema
     */
    public void load(String jsonPath, String schemaPath, String assetType) {
        JSONObject rootNode = Validator.getValidatedJSON(jsonPath, schemaPath);

        for (String nodeValue : rootNode.keySet()) {
            if (!(rootNode.get(nodeValue) instanceof JSONObject)) {
                continue;
            }
            JSONObject node = rootNode.getJSONObject(nodeValue);
            String name = nodeValue;

            boolean hard = node.optBoolean("hard", false);
            Map<String, SolarSystemConfig> configsToLoad = hard ? hardConfigs : configs;

            SpaceEnvConfig envConfig = new SpaceEnvConfig(node.getJSONObject("environment"));

            SolarSystemConfig solarSystemConfig = new SolarSystemConfig(name, envConfig, hard);
            configsToLoad.put(name, solarSystemConfig);
        }

        //TODO: determine why "engine" module is excluded
        Set<ResourceUrn> configUrnList = Assets.getAssetHelper().listAssets(Json.class, assetType, new Name("engine"));

        for (ResourceUrn configUrn : configUrnList) {
            rootNode = Validator.getValidatedJSON(configUrn.toString(), schemaPath);

            for (String nodeValue : rootNode.keySet()) {
                if (!(rootNode.get(nodeValue) instanceof JSONObject)) {
                    continue;
                }
                JSONObject node = rootNode.getJSONObject(nodeValue);
                String name = nodeValue;

                boolean hard = node.optBoolean("hard", false);
                Map<String, SolarSystemConfig> configsToLoad = hard ? hardConfigs : configs;

                SolarSystemConfig config = configsToLoad.get(name);


                //Load the configs for the enemy ships used in this SolarSystem. If there are no ships in the JSONArray, the resulting list will be empty
                config.tempEnemies.addAll(ShipConfig.loadList(node.has("temporaryEnemies") ? node.getJSONArray("temporaryEnemies") : null, hullConfigManager, itemManager));
                config.innerTempEnemies.addAll(ShipConfig.loadList(node.has("innerTemporaryEnemies") ? node.getJSONArray("innerTemporaryEnemies") : null, hullConfigManager, itemManager));
                config.constEnemies.addAll(ShipConfig.loadList(node.has("constantEnemies") ? node.getJSONArray("constantEnemies") : null, hullConfigManager, itemManager));
                config.constAllies.addAll(ShipConfig.loadList(node.has("constantAllies") ? node.getJSONArray("constantAllies") : null, hullConfigManager, itemManager));

                //Get the config for trading for this SolarSystem.
                config.tradeConfig.load(node.has("trading") ? node.getJSONObject("trading") : null, hullConfigManager, itemManager);
            }
        }
    }

    /**
     * Retrieves the SolarSystemConfig with the specified name.
     * @param name name of the SolarSystemConfig
     * @return the SolarSystemConfig
     */
    public SolarSystemConfig getSolarSystemConfig(String name) {
        SolarSystemConfig config = configs.get(name);
        if (config != null) {
            return config;
        }
        return hardConfigs.get(name);
    }

    /**
     * This returns a random config for a SolarSystem from among the configs available.
     * @param hard whether or not the SolarSystem is a hard SolarSystem
     * @return Config for the SolarSystem
     */
    public SolarSystemConfig getRandomSolarSystemConfig(boolean hard) {
        Map<String, SolarSystemConfig> config = hard ? hardConfigs : configs;
        return SolRandom.seededRandomElement(new ArrayList<>(config.values()));
    }

    /**
     * This adds all the ShipConfigs used in the SolarSystemConfigs into the passed in shipConfigs list.
     * @param shipConfigs list to add the ShipConfigs from SolarSystemConfigs to
     */
    public void addAllConfigs(ArrayList<ShipConfig> shipConfigs) {
        for (SolarSystemConfig solarSystemConfig : configs.values()) {
            shipConfigs.addAll(solarSystemConfig.constAllies);
            shipConfigs.addAll(solarSystemConfig.constEnemies);
            shipConfigs.addAll(solarSystemConfig.tempEnemies);
            shipConfigs.addAll(solarSystemConfig.innerTempEnemies);
        }

        for (SolarSystemConfig solarSystemConfig : hardConfigs.values()) {
            shipConfigs.addAll(solarSystemConfig.constAllies);
            shipConfigs.addAll(solarSystemConfig.constEnemies);
            shipConfigs.addAll(solarSystemConfig.tempEnemies);
            shipConfigs.addAll(solarSystemConfig.innerTempEnemies);
        }
    }

}
