package com.miloshpetrov.sol2.game.maze;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.JsonValue;
import com.miloshpetrov.sol2.TexMan;
import com.miloshpetrov.sol2.game.PathLoader;
import com.miloshpetrov.sol2.game.ShipConfig;
import com.miloshpetrov.sol2.game.chunk.SpaceEnvironmentConfig;
import com.miloshpetrov.sol2.game.ship.HullConfigs;

import java.util.ArrayList;

public class MazeConfig {
  public final ArrayList<MazeTile> innerWalls;
  public final ArrayList<MazeTile> innerPasses;
  public final ArrayList<MazeTile> borderWalls;
  public final ArrayList<MazeTile> borderPasses;
  public final ArrayList<ShipConfig> outerEnemies;
  public final ArrayList<ShipConfig> innerEnemies;
  public final ArrayList<ShipConfig> bosses;
  public final SpaceEnvironmentConfig envConfig;

  public MazeConfig(ArrayList<MazeTile> innerWalls, ArrayList<MazeTile> innerPasses, ArrayList<MazeTile> borderWalls,
    ArrayList<MazeTile> borderPasses, ArrayList<ShipConfig> outerEnemies, ArrayList<ShipConfig> innerEnemies,
    ArrayList<ShipConfig> bosses, SpaceEnvironmentConfig envConfig)
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

  public static MazeConfig load(TexMan texMan, HullConfigs hullConfigs, JsonValue mazeNode, FileHandle configFile) {
    String dirName = "mazeTiles/" + mazeNode.name + "/";
    PathLoader pathLoader = new PathLoader("mazes/" + mazeNode.name);
    PathLoader.Model paths = pathLoader.getInternalModel();

    ArrayList<MazeTile> innerWalls = new ArrayList<MazeTile>();
    buildTiles(texMan, configFile, dirName, paths, innerWalls, "innerWall", true);
    ArrayList<MazeTile> innerPasses = new ArrayList<MazeTile>();
    buildTiles(texMan, configFile, dirName, paths, innerPasses, "innerPass", false);
    ArrayList<MazeTile> borderWalls = new ArrayList<MazeTile>();
    buildTiles(texMan, configFile, dirName, paths, borderWalls, "borderWall", true);
    ArrayList<MazeTile> borderPasses = new ArrayList<MazeTile>();
    buildTiles(texMan, configFile, dirName, paths, borderPasses, "borderPass", false);

    ArrayList<ShipConfig> outerEnemies = ShipConfig.loadList(mazeNode.get("outerEnemies"), hullConfigs);
    ArrayList<ShipConfig> innerEnemies = ShipConfig.loadList(mazeNode.get("innerEnemies"), hullConfigs);
    ArrayList<ShipConfig> bosses = ShipConfig.loadList(mazeNode.get("bosses"), hullConfigs);

    SpaceEnvironmentConfig envConfig = new SpaceEnvironmentConfig(mazeNode.get("environment"), texMan, configFile);

    return new MazeConfig(innerWalls, innerPasses, borderWalls, borderPasses, outerEnemies, innerEnemies, bosses, envConfig);
  }

  private static void buildTiles(TexMan texMan, FileHandle configFile, String dirName, PathLoader.Model paths,
    ArrayList<MazeTile> list, String tileType, boolean wall)
  {
    ArrayList<TextureAtlas.AtlasRegion> iwTexs = texMan.getPack(dirName + tileType, configFile);
    for (TextureAtlas.AtlasRegion tex : iwTexs) {
      String pathEntry = tex.name + "_" + tex.index + ".png";
      MazeTile iw = MazeTile.load(tex, paths, wall, pathEntry);
      list.add(iw);
    }
  }
}
