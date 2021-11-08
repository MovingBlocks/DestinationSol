/*
 * Copyright 2020 The Terasology Foundation
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

public class BeltConfigManager {
    private final Map<String, BeltConfig> configs;
    private final Map<String, BeltConfig> hardConfigs;
    private HullConfigManager hullConfigManager;
    private ItemManager itemManager;

    @Inject
    public BeltConfigManager(HullConfigManager hullConfigManager, ItemManager itemManager) {
        configs = new HashMap<>();
        hardConfigs = new HashMap<>();
        this.hullConfigManager = hullConfigManager;
        this.itemManager = itemManager;
    }

    /**
     * This method is used to tell the JSON data loader to load data from the engine module.
     */
    public void loadDefaultBeltConfigs() {
        load("engine:asteroidBeltsConfig", "engine:schemaSystemsConfig", "asteroidBeltsConfig");
    }

    /**
     * This method loads in all the Belt configuration data from the specified location.
     * @param jsonPath path to the JSON data
     * @param schemaPath path to the schema
     */
    public void load(String jsonPath, String schemaPath, String assetType) {
        JSONObject rootNode = Validator.getValidatedJSON(jsonPath, schemaPath);

        for (String s : rootNode.keySet()) {
            if (!(rootNode.get(s) instanceof JSONObject)) {
                continue;
            }
            JSONObject node = rootNode.getJSONObject(s);
            String name = s;

            boolean hard = node.optBoolean("hard", false);
            Map<String, BeltConfig> configsToLoad = hard ? hardConfigs : configs;

            SpaceEnvConfig envConfig = new SpaceEnvConfig(node.getJSONObject("environment"));

            BeltConfig beltConfig = new BeltConfig(name, envConfig, hard);
            configsToLoad.put(name, beltConfig);
        }

        //TODO: determine why "engine" module is excluded
        Set<ResourceUrn> configUrnList = Assets.getAssetHelper().listAssets(Json.class, assetType, new Name("engine"));

        for (ResourceUrn configUrn : configUrnList) {
            rootNode = Validator.getValidatedJSON(configUrn.toString(), "engine:schemaSystemsConfig");

            for (String nodeValue : rootNode.keySet()) {
                if (!(rootNode.get(nodeValue) instanceof JSONObject)) {
                    continue;
                }
                JSONObject node = rootNode.getJSONObject(nodeValue);
                String name = nodeValue;

                boolean hard = node.optBoolean("hard", false);
                Map<String, BeltConfig> configsToLoad = hard ? hardConfigs : configs;

                BeltConfig config = configsToLoad.get(name);

                // TODO : Maybe add sanity checks for config?

                //Load the configs for the enemy ships used in this belt. If there are no ships in the JSONArray, the resulting list will be empty
                config.tempEnemies.addAll(ShipConfig.loadList(node.has("temporaryEnemies") ? node.getJSONArray("temporaryEnemies") : null, hullConfigManager, itemManager));
                config.innerTempEnemies.addAll(ShipConfig.loadList(node.has("innerTemporaryEnemies") ? node.getJSONArray("innerTemporaryEnemies") : null, hullConfigManager, itemManager));

                //get the config for trading for this belt
                config.tradeConfig.load(node.has("trading") ? node.getJSONObject("trading") : null, hullConfigManager, itemManager);
            }
        }
    }

    /**
     * Retrieves the BeltConfig with the specified name.
     * @param name name of the BeltConfig
     * @return the BeltConfig
     */
    public BeltConfig getBeltConfig(String name) {
        BeltConfig beltConfig = configs.get(name);
        if (beltConfig != null) {
            return beltConfig;
        }
        return hardConfigs.get(name);
    }

    /**
     * This returns a random config for a Belt from among the configs available.
     * @param hard whether or not the Belt is part of a hard SolarSystem
     * @return Config for the Belt
     */
    public BeltConfig getRandomBeltConfig(boolean hard) {
        Map<String, BeltConfig> config = hard ? hardConfigs : configs;
        return SolRandom.seededRandomElement(new ArrayList<>(config.values()));
    }

    /**
     * This adds all the ShipConfigs used in the BeltConfigs into the passed in shipConfigs list.
     * @param shipConfigs list to add the ShipConfigs from BeltConfigs to
     */
    public void addAllConfigs(ArrayList<ShipConfig> shipConfigs) {
        for (BeltConfig beltConfig : configs.values()) {
            shipConfigs.addAll(beltConfig.tempEnemies);
            shipConfigs.addAll(beltConfig.innerTempEnemies);
        }

        for (BeltConfig beltConfig : hardConfigs.values()) {
            shipConfigs.addAll(beltConfig.tempEnemies);
            shipConfigs.addAll(beltConfig.innerTempEnemies);
        }
    }
}
