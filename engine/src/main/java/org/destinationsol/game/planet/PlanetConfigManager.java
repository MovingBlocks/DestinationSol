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

import org.destinationsol.assets.Assets;
import org.destinationsol.assets.json.Json;
import org.destinationsol.assets.json.Validator;
import org.destinationsol.common.SolRandom;
import org.destinationsol.files.HullConfigManager;
import org.destinationsol.game.GameColors;
import org.destinationsol.game.item.ItemManager;
import org.json.JSONObject;
import org.terasology.gestalt.assets.ResourceUrn;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This class manages loading config files in for Planets. It also allows for getting a random Planet config or a
 * specific Planet config by name. It can either load the default Planet configs, or specified custom configs
 */
public class PlanetConfigManager {
    private HullConfigManager hullConfigManager;
    private GameColors gameColors;
    private ItemManager itemManager;
    private final Map<String, PlanetConfig> allConfigs;
    private final List<PlanetConfig> easy;
    private final List<PlanetConfig> medium;
    private final List<PlanetConfig> hard;

    public PlanetConfigManager(HullConfigManager hullConfigManager, GameColors gameColors, ItemManager itemManager) {
        this.hullConfigManager = hullConfigManager;
        this.gameColors = gameColors;
        this.itemManager = itemManager;
        allConfigs = new HashMap<>();
        easy = new ArrayList<>();
        medium = new ArrayList<>();
        hard = new ArrayList<>();
    }

    /**
     * Load the default configs for Planets from the engine module
     */
    public void loadDefaultPlanetConfigs() {
        load("planetsConfig", "engine:schemaPlanetsConfig");
    }

    /**
     * Load specified configs for Planets
     * @param asset The name of the asset type to load
     * @param schemaPath the path of the schema to load
     */
    public void load(String asset, String schemaPath) {
        Set<ResourceUrn> planetJsonConfigs = Assets.getAssetHelper().listAssets(Json.class, asset);

        for (ResourceUrn planetConfigJson : planetJsonConfigs) {
            String moduleName = planetConfigJson.getModuleName().toString();
            JSONObject rootNode = Validator.getValidatedJSON(planetConfigJson.toString(), schemaPath);

            for (String s : rootNode.keySet()) {
                JSONObject node = rootNode.getJSONObject(s);
                PlanetConfig planetConfig = PlanetConfig.load(s, node, hullConfigManager, gameColors, itemManager, moduleName);
                allConfigs.put(s, planetConfig);
                if (planetConfig.hardOnly) {
                    hard.add(planetConfig);
                } else if (planetConfig.easyOnly) {
                    easy.add(planetConfig);
                } else {
                    medium.add(planetConfig);
                }
            }
        }
    }

    /**
     * Get a particular config from the configs already loaded in.
     * @param name Name of the config to get
     * @return the particular config
     */
    public PlanetConfig getConfig(String name) {
        return allConfigs.get(name);
    }

    /**
     * This determines whether the planet gets an easy, hard, or medium config. If easy is true, it will be an easy planet.
     * If easy is false but hard is true, it will be a hard planet. Else, it will be a medium planet. If both are true,
     * it will be easy
     */
    public PlanetConfig getRandom(boolean easy, boolean hard) {
        List<PlanetConfig> cfg = easy ? this.easy : hard ? this.hard : medium;
        return SolRandom.seededRandomElement(cfg);
    }

    public Map<String, PlanetConfig> getAllConfigs() {
        return allConfigs;
    }
}
