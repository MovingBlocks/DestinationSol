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

import org.destinationsol.assets.json.Validator;
import org.json.JSONObject;
import org.destinationsol.assets.Assets;
import org.destinationsol.assets.json.Json;
import org.destinationsol.common.SolRandom;
import org.destinationsol.files.HullConfigManager;
import org.destinationsol.game.GameColors;
import org.destinationsol.game.item.ItemManager;
import org.terasology.assets.ResourceUrn;

import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Set;

public class PlanetConfigs {
    private final Map<String, PlanetConfig> allConfigs;
    private final List<PlanetConfig> easy;
    private final List<PlanetConfig> medium;
    private final List<PlanetConfig> hard;

    public PlanetConfigs(HullConfigManager hullConfigs, GameColors cols, ItemManager itemManager) {
        allConfigs = new HashMap<>();
        easy = new ArrayList<>();
        medium = new ArrayList<>();
        hard = new ArrayList<>();

        Set<ResourceUrn> planetJsonConfigs = Assets.getAssetHelper().list(Json.class, "[a-zA-Z0-9]*:planetsConfig");

        for (ResourceUrn planetConfigJson : planetJsonConfigs) {
            String moduleName = planetConfigJson.getModuleName().toString();
            JSONObject rootNode = Validator.getValidatedJSON(planetConfigJson.toString(), "engine:schemaPlanetsConfig");

            for (String s : rootNode.keySet()) {
                JSONObject node = rootNode.getJSONObject(s);
                PlanetConfig planetConfig = PlanetConfig.load(s, node, hullConfigs, cols, itemManager, moduleName);
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

    public PlanetConfig getConfig(String name) {
        return allConfigs.get(name);
    }

    public PlanetConfig getRandom(boolean easy, boolean hard) {
        List<PlanetConfig> cfg = easy ? this.easy : hard ? this.hard : medium;
        return SolRandom.seededRandomElement(cfg);
    }

    public Map<String, PlanetConfig> getAllConfigs() {
        return allConfigs;
    }
}
