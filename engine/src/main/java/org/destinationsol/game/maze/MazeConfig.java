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

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import org.json.JSONObject;
import org.destinationsol.assets.Assets;
import org.destinationsol.common.SolRandom;
import org.destinationsol.files.HullConfigManager;
import org.destinationsol.game.CollisionMeshLoader;
import org.destinationsol.game.ShipConfig;
import org.destinationsol.game.chunk.SpaceEnvConfig;
import org.destinationsol.game.item.ItemManager;

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
                      ArrayList<ShipConfig> bosses, SpaceEnvConfig envConfig) {
        this.innerWalls = innerWalls;
        this.innerPasses = innerPasses;
        this.borderWalls = borderWalls;
        this.borderPasses = borderPasses;
        this.outerEnemies = outerEnemies;
        this.innerEnemies = innerEnemies;
        this.bosses = bosses;
        this.envConfig = envConfig;
    }

    public static MazeConfig load(String name, JSONObject mazeNode, HullConfigManager hullConfigs, ItemManager itemManager) {
        CollisionMeshLoader collisionMeshLoader = new CollisionMeshLoader("core:" + name + "Maze");
        CollisionMeshLoader.Model paths = collisionMeshLoader.getInternalModel();
        List<TextureAtlas.AtlasRegion> innerBackgrounds = Assets.listTexturesMatching("core:" + name + "MazeInnerBg_.*");
        List<TextureAtlas.AtlasRegion> borderBackgrounds = Assets.listTexturesMatching("core:" + name + "MazeBorderBg_.*");
        List<TextureAtlas.AtlasRegion> wallTextures = Assets.listTexturesMatching("core:" + name + "MazeWall_.*");
        List<TextureAtlas.AtlasRegion> passTextures = Assets.listTexturesMatching("core:" + name + "MazePass_.*");

        boolean metal = mazeNode.getBoolean("isMetal");
        ArrayList<MazeTile> innerWalls = new ArrayList<>();
        buildTiles(paths, innerWalls, true, metal, innerBackgrounds, wallTextures);
        ArrayList<MazeTile> innerPasses = new ArrayList<>();
        buildTiles(paths, innerPasses, false, metal, innerBackgrounds, passTextures);
        ArrayList<MazeTile> borderWalls = new ArrayList<>();
        buildTiles(paths, borderWalls, true, metal, borderBackgrounds, wallTextures);
        ArrayList<MazeTile> borderPasses = new ArrayList<>();
        buildTiles(paths, borderPasses, false, metal, borderBackgrounds, passTextures);

        ArrayList<ShipConfig> outerEnemies = ShipConfig.loadList(mazeNode.getJSONArray("outerEnemies"), hullConfigs, itemManager);
        ArrayList<ShipConfig> innerEnemies = ShipConfig.loadList(mazeNode.getJSONArray("innerEnemies"), hullConfigs, itemManager);
        ArrayList<ShipConfig> bosses = ShipConfig.loadList(mazeNode.getJSONArray("bosses"), hullConfigs, itemManager);

        SpaceEnvConfig envConfig = new SpaceEnvConfig(mazeNode.getJSONObject("environment"));
        return new MazeConfig(innerWalls, innerPasses, borderWalls, borderPasses, outerEnemies, innerEnemies, bosses, envConfig);
    }

    private static void buildTiles(CollisionMeshLoader.Model paths, List<MazeTile> list, boolean wall, boolean metal,
                                       List<TextureAtlas.AtlasRegion> backgroundTextures, List<TextureAtlas.AtlasRegion> texs) {
        for (TextureAtlas.AtlasRegion tex : texs) {
            TextureAtlas.AtlasRegion backgroundTexture = SolRandom.seededRandomElement(backgroundTextures);
            MazeTile iw = MazeTile.load(tex, paths.rigidBodies.get(tex.name), wall, metal, backgroundTexture);
            list.add(iw);
        }
    }
}
