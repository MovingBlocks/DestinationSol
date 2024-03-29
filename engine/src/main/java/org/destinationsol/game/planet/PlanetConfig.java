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

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import org.destinationsol.Const;
import org.destinationsol.assets.Assets;
import org.destinationsol.files.HullConfigManager;
import org.destinationsol.game.GameColors;
import org.destinationsol.game.ShipConfig;
import org.destinationsol.game.item.ItemManager;
import org.destinationsol.game.item.TradeConfig;
import org.json.JSONObject;

import java.util.List;

public class PlanetConfig {
    public final String moduleName;
    public final String configName;
    public final float minGrav;
    public final float maxGrav;
    public final List<DecoConfig> deco;
    public final List<ShipConfig> groundEnemies;
    public final List<ShipConfig> highOrbitEnemies;
    public final PlanetTiles planetTiles;
    public final ShipConfig stationConfig;
    public final SkyConfig skyConfig;
    public final List<TextureAtlas.AtlasRegion> cloudTextures;
    public final List<ShipConfig> lowOrbitEnemies;
    public final int rowCount;
    public final boolean smoothLandscape;
    public final TradeConfig tradeConfig;
    public final boolean hardOnly;
    public final boolean easyOnly;
    //These 'builder' classes allow custom World Generators to modify values used when placing objects on the planet
    public final OrbitalEnemiesBuilder lowOrbitalEnemiesBuilder;
    public final OrbitalEnemiesBuilder highOrbitalEnemiesBuilder;
    public final CloudBuilder cloudBuilder;

    public PlanetConfig(String configName, float minGrav, float maxGrav, List<DecoConfig> deco, List<ShipConfig> groundEnemies,
                        List<ShipConfig> highOrbitEnemies, List<ShipConfig> lowOrbitEnemies, List<TextureAtlas.AtlasRegion> cloudTextures,
                        PlanetTiles planetTiles, ShipConfig stationConfig, SkyConfig skyConfig, int rowCount, boolean smoothLandscape,
                        TradeConfig tradeConfig, boolean hardOnly, boolean easyOnly, String moduleName) {
        this.configName = configName;
        this.minGrav = minGrav;
        this.maxGrav = maxGrav;
        this.deco = deco;
        this.groundEnemies = groundEnemies;
        this.highOrbitEnemies = highOrbitEnemies;
        this.lowOrbitEnemies = lowOrbitEnemies;
        this.cloudTextures = cloudTextures;
        this.planetTiles = planetTiles;
        this.stationConfig = stationConfig;
        this.skyConfig = skyConfig;
        this.rowCount = rowCount;
        this.smoothLandscape = smoothLandscape;
        this.tradeConfig = tradeConfig;
        this.hardOnly = hardOnly;
        this.easyOnly = easyOnly;
        this.moduleName = moduleName;
        this.cloudBuilder = new CloudBuilder(cloudTextures, .2f, 0f, 1f, .2f, 1f);
        this.lowOrbitalEnemiesBuilder = new OrbitalEnemiesBuilder(lowOrbitEnemies, 0, .1f, Const.AUTO_SHOOT_SPACE);
        this.highOrbitalEnemiesBuilder = new OrbitalEnemiesBuilder(highOrbitEnemies, .1f, .6f, Const.AI_DET_DIST);
    }

    static PlanetConfig load(String name, JSONObject rootNode, HullConfigManager hullConfigs, GameColors cols, ItemManager itemManager, String moduleName) {
        // JSONObject.getDouble must be used, as Android does not support JSONObject.getFloat (you cannot override the dependency either, as it is a system library).
        float minGrav = (float) rootNode.getDouble("minGrav");
        float maxGrav = (float) rootNode.getDouble("maxGrav");
        List<DecoConfig> deco = DecoConfig.load(rootNode);
        List<ShipConfig> groundEnemies = ShipConfig.loadList(rootNode.has("groundEnemies") ? rootNode.getJSONArray("groundEnemies") : null, hullConfigs, itemManager);
        List<ShipConfig> highOrbitEnemies = ShipConfig.loadList(rootNode.has("highOrbitEnemies") ? rootNode.getJSONArray("highOrbitEnemies") : null, hullConfigs, itemManager);
        List<ShipConfig> lowOrbitEnemies = ShipConfig.loadList(rootNode.has("lowOrbitEnemies") ? rootNode.getJSONArray("lowOrbitEnemies") : null, hullConfigs, itemManager);
        ShipConfig stationConfig = ShipConfig.load(hullConfigs, rootNode.has("station") ? rootNode.getJSONObject("station") : null, itemManager);
        String cloudPackName = rootNode.getString("cloudTexs");
        List<TextureAtlas.AtlasRegion> cloudTextures = Assets.listTexturesMatching(cloudPackName + "_.*");
        String groundFolder = rootNode.getString("groundTexs");
        PlanetTiles planetTiles = new PlanetTiles(groundFolder);
        SkyConfig skyConfig = SkyConfig.load(rootNode.has("sky") ? rootNode.getJSONObject("sky") : null, cols);
        int rowCount = rootNode.getInt("rowCount");
        boolean smoothLandscape = rootNode.optBoolean("smoothLandscape", false);
        TradeConfig tradeConfig = new TradeConfig();
        tradeConfig.load(rootNode.has("trading") ? rootNode.getJSONObject("trading") : null, hullConfigs, itemManager);
        boolean hardOnly = rootNode.optBoolean("hardOnly", false);
        boolean easyOnly = rootNode.optBoolean("easyOnly", false);
        return new PlanetConfig(name, minGrav, maxGrav, deco, groundEnemies, highOrbitEnemies, lowOrbitEnemies, cloudTextures,
                planetTiles, stationConfig, skyConfig, rowCount, smoothLandscape, tradeConfig, hardOnly, easyOnly, moduleName);
    }
}
