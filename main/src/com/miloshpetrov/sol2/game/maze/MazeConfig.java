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
 
 package com.miloshpetrov.sol2.game.maze;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.JsonValue;
import com.miloshpetrov.sol2.TextureManager;
import com.miloshpetrov.sol2.common.SolMath;
import com.miloshpetrov.sol2.game.PathLoader;
import com.miloshpetrov.sol2.game.ShipConfig;
import com.miloshpetrov.sol2.game.chunk.SpaceEnvConfig;
import com.miloshpetrov.sol2.game.item.ItemMan;
import com.miloshpetrov.sol2.game.ship.HullConfigs;

import java.util.ArrayList;
import java.util.List;

public class MazeConfig {
  public final ArrayList<MazeTile> innerWalls;
  public final ArrayList<MazeTile> innerPasses;
  public final ArrayList<MazeTile> borderWalls;
  public final ArrayList<MazeTile> borderPasses;
  public final ArrayList<ShipConfig> outerEnemies;
  public final ArrayList<ShipConfig> innerEnemies;
  public final ArrayList<ShipConfig> bosses;
  public final SpaceEnvConfig envConfig;

  public MazeConfig(ArrayList<MazeTile> innerWalls, ArrayList<MazeTile> innerPasses, ArrayList<MazeTile> borderWalls,
    ArrayList<MazeTile> borderPasses, ArrayList<ShipConfig> outerEnemies, ArrayList<ShipConfig> innerEnemies,
    ArrayList<ShipConfig> bosses, SpaceEnvConfig envConfig)
  {
    this.innerWalls = innerWalls;
    this.innerPasses = innerPasses;
    this.borderWalls = borderWalls;
    this.borderPasses = borderPasses;
    this.outerEnemies = outerEnemies;
    this.innerEnemies = innerEnemies;
    this.bosses = bosses;
    this.envConfig = envConfig;
  }

  public static MazeConfig load(TextureManager textureManager, HullConfigs hullConfigs, JsonValue mazeNode, FileHandle configFile,
    ItemMan itemMan)
  {
    String dirName = "mazeTiles/" + mazeNode.name + "/";
    PathLoader pathLoader = new PathLoader("mazes/" + mazeNode.name);
    PathLoader.Model paths = pathLoader.getInternalModel();
    List<TextureAtlas.AtlasRegion> innerBgs = textureManager.getPack(dirName + "innerBg", configFile);
    List<TextureAtlas.AtlasRegion> borderBgs = textureManager.getPack(dirName + "borderBg", configFile);
    ArrayList<TextureAtlas.AtlasRegion> wallTexs = textureManager.getPack(dirName + "wall", configFile);
    ArrayList<TextureAtlas.AtlasRegion> passTexs = textureManager.getPack(dirName + "pass", configFile);

    boolean metal = mazeNode.getBoolean("isMetal");
    ArrayList<MazeTile> innerWalls = new ArrayList<MazeTile>();
    buildTiles(paths, innerWalls, true, metal, innerBgs, wallTexs);
    ArrayList<MazeTile> innerPasses = new ArrayList<MazeTile>();
    buildTiles(paths, innerPasses, false, metal, innerBgs, passTexs);
    ArrayList<MazeTile> borderWalls = new ArrayList<MazeTile>();
    buildTiles(paths, borderWalls, true, metal, borderBgs, wallTexs);
    ArrayList<MazeTile> borderPasses = new ArrayList<MazeTile>();
    buildTiles(paths, borderPasses, false, metal, borderBgs, passTexs);

    ArrayList<ShipConfig> outerEnemies = ShipConfig.loadList(mazeNode.get("outerEnemies"), hullConfigs, itemMan);
    ArrayList<ShipConfig> innerEnemies = ShipConfig.loadList(mazeNode.get("innerEnemies"), hullConfigs, itemMan);
    ArrayList<ShipConfig> bosses = ShipConfig.loadList(mazeNode.get("bosses"), hullConfigs, itemMan);

    SpaceEnvConfig envConfig = new SpaceEnvConfig(mazeNode.get("environment"), textureManager, configFile);
    return new MazeConfig(innerWalls, innerPasses, borderWalls, borderPasses, outerEnemies, innerEnemies, bosses, envConfig);
  }

  private static void buildTiles(PathLoader.Model paths,
    ArrayList<MazeTile> list, boolean wall, boolean metal, List<TextureAtlas.AtlasRegion> bgTexs,
    ArrayList<TextureAtlas.AtlasRegion> texs)
  {
    for (TextureAtlas.AtlasRegion tex : texs) {
      String pathEntry = tex.name + "_" + tex.index + ".png";
      TextureAtlas.AtlasRegion bgTex = SolMath.elemRnd(bgTexs);
      MazeTile iw = MazeTile.load(tex, paths, wall, pathEntry, metal, bgTex);
      list.add(iw);
    }
  }
}
