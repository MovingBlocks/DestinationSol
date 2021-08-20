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
package org.destinationsol.game.maze;

import org.destinationsol.assets.Assets;
import org.destinationsol.assets.json.Json;
import org.destinationsol.assets.json.Validator;
import org.destinationsol.files.HullConfigManager;
import org.destinationsol.game.item.ItemManager;
import org.json.JSONObject;
import org.terasology.gestalt.assets.ResourceUrn;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * This class manages loading config files in for Mazes. It also allows for getting a random Maze config or a
 * specific Maze config by name. It can either load the default Maze configs, or specified custom configs
 */

public class MazeConfigManager {
    public final List<MazeConfig> configs;
    HullConfigManager hullConfigManager;
    ItemManager itemManager;

    public MazeConfigManager(HullConfigManager hullConfigManager, ItemManager itemManager) {
        configs = new ArrayList<>();
        this.hullConfigManager = hullConfigManager;
        this.itemManager = itemManager;

    }

    /**
     * Load the default MazeConfig JSON data from the engine module
     */
    public void loadDefaultMazeConfigs() {
        load("mazesConfig", "engine:schemaMazesConfig");
    }

    /**
     * Load in maze configs from JSON data using specified asset type and schema
     * @param assetType type name of asset to load
     * @param schema json schema
     */
    public void load(String assetType, String schema) {
        final Set<ResourceUrn> configUrns = Assets.getAssetHelper().listAssets(Json.class, assetType);
        for (ResourceUrn configUrn : configUrns) {
            JSONObject rootNode = Validator.getValidatedJSON(configUrn.toString(), schema);

            for (String s : rootNode.keySet()) {
                if (!(rootNode.get(s) instanceof JSONObject)) {
                    continue;
                }
                JSONObject mazeNode = rootNode.getJSONObject(s);
                MazeConfig c = MazeConfig.load(s, mazeNode, hullConfigManager, itemManager);
                configs.add(c);
            }
        }
    }
}
