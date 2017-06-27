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

import com.badlogic.gdx.utils.JsonValue;
import org.destinationsol.assets.Assets;
import org.destinationsol.assets.json.Json;
import org.destinationsol.common.SolMath;
import org.destinationsol.files.HullConfigManager;
import org.destinationsol.game.GameColors;
import org.destinationsol.game.item.ItemManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlanetConfigs {
    private final Map<String, PlanetConfig> myAllConfigs;
    private final List<PlanetConfig> myEasy;
    private final List<PlanetConfig> myMedium;
    private final List<PlanetConfig> myHard;

    public PlanetConfigs(HullConfigManager hullConfigs, GameColors cols, ItemManager itemManager) {
        myAllConfigs = new HashMap<>();
        myEasy = new ArrayList<>();
        myMedium = new ArrayList<>();
        myHard = new ArrayList<>();

        Assets.cacheLists();

        Json json = Assets.getJson("core:planetsConfig");
        JsonValue rootNode = json.getJsonValue();

        for (JsonValue node : rootNode) {
            PlanetConfig planetConfig = PlanetConfig.load(node, hullConfigs, cols, itemManager);
            myAllConfigs.put(node.name, planetConfig);
            if (planetConfig.hardOnly) {
                myHard.add(planetConfig);
            } else if (planetConfig.easyOnly) {
                myEasy.add(planetConfig);
            } else {
                myMedium.add(planetConfig);
            }
        }

        json.dispose();

        Assets.uncacheLists();
    }

    public PlanetConfig getConfig(String name) {
        return myAllConfigs.get(name);
    }

    public PlanetConfig getRandom(boolean easy, boolean hard) {
        List<PlanetConfig> cfg = easy ? myEasy : hard ? myHard : myMedium;
        return SolMath.elemRnd(cfg);
    }

    public Map<String, PlanetConfig> getAllConfigs() {
        return myAllConfigs;
    }
}
