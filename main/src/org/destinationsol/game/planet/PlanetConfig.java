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

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.JsonValue;
import org.destinationsol.TextureManager;
import org.destinationsol.assets.AssetHelper;
import org.destinationsol.assets.Assets;
import org.destinationsol.files.HullConfigManager;
import org.destinationsol.game.GameColors;
import org.destinationsol.game.ShipConfig;
import org.destinationsol.game.item.ItemManager;
import org.destinationsol.game.item.TradeConfig;
import org.terasology.assets.ResourceUrn;

import java.util.ArrayList;
import java.util.List;

public class PlanetConfig {
    public final String configName;
    public final float minGrav;
    public final float maxGrav;
    public final List<DecoConfig> deco;
    public final List<ShipConfig> groundEnemies;
    public final List<ShipConfig> highOrbitEnemies;
    public final PlanetTiles planetTiles;
    public final ShipConfig stationConfig;
    public final SkyConfig skyConfig;
    public final List<TextureAtlas.AtlasRegion> cloudTexs;
    public final List<ShipConfig> lowOrbitEnemies;
    public final int rowCount;
    public final boolean smoothLandscape;
    public final TradeConfig tradeConfig;
    public final boolean hardOnly;
    public final boolean easyOnly;

    public PlanetConfig(String configName, float minGrav, float maxGrav, List<DecoConfig> deco, List<ShipConfig> groundEnemies,
                        List<ShipConfig> highOrbitEnemies, List<ShipConfig> lowOrbitEnemies, List<TextureAtlas.AtlasRegion> cloudTexs,
                        PlanetTiles planetTiles, ShipConfig stationConfig, SkyConfig skyConfig, int rowCount, boolean smoothLandscape,
                        TradeConfig tradeConfig, boolean hardOnly, boolean easyOnly) {
        this.configName = configName;
        this.minGrav = minGrav;
        this.maxGrav = maxGrav;
        this.deco = deco;
        this.groundEnemies = groundEnemies;
        this.highOrbitEnemies = highOrbitEnemies;
        this.lowOrbitEnemies = lowOrbitEnemies;
        this.cloudTexs = cloudTexs;
        this.planetTiles = planetTiles;
        this.stationConfig = stationConfig;
        this.skyConfig = skyConfig;
        this.rowCount = rowCount;
        this.smoothLandscape = smoothLandscape;
        this.tradeConfig = tradeConfig;
        this.hardOnly = hardOnly;
        this.easyOnly = easyOnly;
    }

    static PlanetConfig load(TextureManager textureManager, HullConfigManager hullConfigs, JsonValue rootNode,
                                GameColors cols, ItemManager itemManager) {
        float minGrav = rootNode.getFloat("minGrav");
        float maxGrav = rootNode.getFloat("maxGrav");
        List<DecoConfig> deco = DecoConfig.load(rootNode, textureManager);
        List<ShipConfig> groundEnemies = ShipConfig.loadList(rootNode.get("groundEnemies"), hullConfigs, itemManager);
        List<ShipConfig> highOrbitEnemies = ShipConfig.loadList(rootNode.get("highOrbitEnemies"), hullConfigs, itemManager);
        List<ShipConfig> lowOrbitEnemies = ShipConfig.loadList(rootNode.get("lowOrbitEnemies"), hullConfigs, itemManager);
        ShipConfig stationConfig = ShipConfig.load(hullConfigs, rootNode.get("station"), itemManager);
        String cloudPackName = rootNode.getString("cloudTexs");
        List<TextureAtlas.AtlasRegion> cloudTexs = Assets.listTexturesMatching(cloudPackName + "_.*");
        String groundFolder = rootNode.getString("groundTexs");
        PlanetTiles planetTiles = new PlanetTiles(textureManager, groundFolder);
        SkyConfig skyConfig = SkyConfig.load(rootNode.get("sky"), cols);
        int rowCount = rootNode.getInt("rowCount");
        boolean smoothLandscape = rootNode.getBoolean("smoothLandscape", false);
        TradeConfig tradeConfig = new TradeConfig();
        tradeConfig.load(rootNode.get("trading"), hullConfigs, itemManager);
        boolean hardOnly = rootNode.getBoolean("hardOnly", false);
        boolean easyOnly = rootNode.getBoolean("easyOnly", false);
        return new PlanetConfig(rootNode.name, minGrav, maxGrav, deco, groundEnemies, highOrbitEnemies, lowOrbitEnemies, cloudTexs,
                planetTiles, stationConfig, skyConfig, rowCount, smoothLandscape, tradeConfig, hardOnly, easyOnly);
    }
}
