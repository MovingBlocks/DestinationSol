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
import org.destinationsol.TextureManager;
import org.destinationsol.assets.Assets;
import org.destinationsol.assets.json.Json;
import org.destinationsol.common.SolMath;
import org.destinationsol.files.HullConfigManager;
import org.destinationsol.game.ShipConfig;
import org.destinationsol.game.chunk.SpaceEnvConfig;
import org.destinationsol.game.item.ItemManager;
import org.destinationsol.game.item.TradeConfig;
import org.terasology.assets.ResourceUrn;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SysConfigs {
    private final Map<String, SysConfig> myConfigs;
    private final Map<String, SysConfig> myHardConfigs;
    private final Map<String, SysConfig> myBeltConfigs;
    private final Map<String, SysConfig> myHardBeltConfigs;

    public SysConfigs(TextureManager textureManager, HullConfigManager hullConfigs, ItemManager itemManager) {
        myConfigs = new HashMap<>();
        myHardConfigs = new HashMap<>();
        myBeltConfigs = new HashMap<>();
        myHardBeltConfigs = new HashMap<>();

        load("systemsConfig", textureManager, hullConfigs, false, itemManager);
        load("asteroidBeltsConfig", textureManager, hullConfigs, true, itemManager);
    }

    private void load(String configName, TextureManager textureManager, HullConfigManager hullConfigs, boolean belts, ItemManager itemManager) {
        Json json_ = Assets.getJson(new ResourceUrn("engine:" + configName));
        JsonValue rootNode_ = json_.getJsonValue();

        for (JsonValue node : rootNode_) {
            String name = node.name;

            boolean hard = node.getBoolean("hard", false);
            Map<String, SysConfig> configs = belts ? hard ? myHardBeltConfigs : myBeltConfigs : hard ? myHardConfigs : myConfigs;

            SpaceEnvConfig envConfig = new SpaceEnvConfig(node.get("environment"), textureManager);

            SysConfig config = new SysConfig(name, new ArrayList<>(), envConfig, new ArrayList<>(), new ArrayList<>(), new TradeConfig(), new ArrayList<>(), hard);
            configs.put(name, config);
        }

        json_.dispose();

        Set<ResourceUrn> configUrnList = Assets.getAssetHelper().list(Json.class, "[a-z]*(?<!^engine):" + configName);

        for (ResourceUrn configUrn : configUrnList) {
            Json json = Assets.getJson(configUrn);
            JsonValue rootNode = json.getJsonValue();

            for (JsonValue node : rootNode) {
                String name = node.name;

                boolean hard = node.getBoolean("hard", false);
                Map<String, SysConfig> configs = belts ? hard ? myHardBeltConfigs : myBeltConfigs : hard ? myHardConfigs : myConfigs;

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
        Map<String, SysConfig> config = hard ? myHardBeltConfigs : myBeltConfigs;
        return SolMath.elemRnd(new ArrayList<SysConfig>(config.values()));
    }

    public SysConfig getConfig(String name) {
        SysConfig res = myConfigs.get(name);
        if (res != null) {
            return res;
        }
        return myHardConfigs.get(name);
    }

    public SysConfig getRandomCfg(boolean hard) {
        Map<String, SysConfig> config = hard ? myHardConfigs : myConfigs;
        return SolMath.elemRnd(new ArrayList<SysConfig>(config.values()));
    }

    public void addAllConfigs(ArrayList<ShipConfig> l) {
        for (SysConfig sc : myConfigs.values()) {
            l.addAll(sc.constAllies);
            l.addAll(sc.constEnemies);
            l.addAll(sc.tempEnemies);
            l.addAll(sc.innerTempEnemies);
        }
        for (SysConfig sc : myHardConfigs.values()) {
            l.addAll(sc.constAllies);
            l.addAll(sc.constEnemies);
            l.addAll(sc.tempEnemies);
            l.addAll(sc.innerTempEnemies);
        }
        for (SysConfig sc : myBeltConfigs.values()) {
            l.addAll(sc.tempEnemies);
        }
        for (SysConfig sc : myHardBeltConfigs.values()) {
            l.addAll(sc.tempEnemies);
        }
    }
}
