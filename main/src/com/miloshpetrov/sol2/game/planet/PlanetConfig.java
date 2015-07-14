/*
 * Copyright 2015 MovingBlocks
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
 
package com.miloshpetrov.sol2.game.planet;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.JsonValue;
import com.miloshpetrov.sol2.TextureManager;
import com.miloshpetrov.sol2.game.GameCols;
import com.miloshpetrov.sol2.game.ShipConfig;
import com.miloshpetrov.sol2.game.item.ItemMan;
import com.miloshpetrov.sol2.game.item.TradeConfig;
import com.miloshpetrov.sol2.game.ship.HullConfigs;

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
  public final ArrayList<TextureAtlas.AtlasRegion> cloudTexs;
  public final ArrayList<ShipConfig> lowOrbitEnemies;
  public final int rowCount;
  public final boolean smoothLandscape;
  public final TradeConfig tradeConfig;
  public final boolean hardOnly;
  public final boolean easyOnly;

  public PlanetConfig(String configName, float minGrav, float maxGrav, List<DecoConfig> deco,
    List<ShipConfig> groundEnemies,
    List<ShipConfig> highOrbitEnemies, ArrayList<ShipConfig> lowOrbitEnemies,
    ArrayList<TextureAtlas.AtlasRegion> cloudTexs, PlanetTiles planetTiles,
    ShipConfig stationConfig, SkyConfig skyConfig, int rowCount, boolean smoothLandscape, TradeConfig tradeConfig,
    boolean hardOnly, boolean easyOnly)
  {
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

  static PlanetConfig load(TextureManager textureManager, HullConfigs hullConfigs, FileHandle configFile, JsonValue sh, GameCols cols,
    ItemMan itemMan) {
    float minGrav = sh.getFloat("minGrav");
    float maxGrav = sh.getFloat("maxGrav");
    List<DecoConfig> deco = DecoConfig.load(sh, textureManager, configFile);
    ArrayList<ShipConfig> groundEnemies = ShipConfig.loadList(sh.get("groundEnemies"), hullConfigs, itemMan);
    ArrayList<ShipConfig> highOrbitEnemies = ShipConfig.loadList(sh.get("highOrbitEnemies"), hullConfigs, itemMan);
    ArrayList<ShipConfig> lowOrbitEnemies = ShipConfig.loadList(sh.get("lowOrbitEnemies"), hullConfigs, itemMan);
    ShipConfig stationConfig = ShipConfig.load(hullConfigs, sh.get("station"), itemMan);
    String cloudPackName = sh.getString("cloudTexs");
    ArrayList<TextureAtlas.AtlasRegion> cloudTexs = textureManager.getPack(cloudPackName, configFile);
    String groundFolder = sh.getString("groundTexs");
    PlanetTiles planetTiles = new PlanetTiles(textureManager, groundFolder, configFile);
    SkyConfig skyConfig = SkyConfig.load(sh.get("sky"), cols);
    int rowCount = sh.getInt("rowCount");
    boolean smoothLandscape = sh.getBoolean("smoothLandscape", false);
    TradeConfig tradeConfig = TradeConfig.load(itemMan, sh.get("trading"), hullConfigs);
    boolean hardOnly = sh.getBoolean("hardOnly", false);
    boolean easyOnly = sh.getBoolean("easyOnly", false);
    return new PlanetConfig(sh.name, minGrav, maxGrav, deco, groundEnemies, highOrbitEnemies, lowOrbitEnemies, cloudTexs,
      planetTiles, stationConfig, skyConfig, rowCount, smoothLandscape, tradeConfig, hardOnly, easyOnly);
  }
}
