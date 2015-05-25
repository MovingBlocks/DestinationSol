package com.miloshpetrov.sol2.game.maze;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.miloshpetrov.sol2.*;
import com.miloshpetrov.sol2.files.FileManager;
import com.miloshpetrov.sol2.game.item.ItemMan;
import com.miloshpetrov.sol2.game.ship.HullConfigs;

import java.util.ArrayList;
import java.util.List;

public class MazeConfigs {
  public final List<MazeConfig> configs;

  public MazeConfigs(TextureManager textureManager, HullConfigs hullConfigs, ItemMan itemMan) {
    configs = new ArrayList<MazeConfig>();

    JsonReader r = new JsonReader();
    FileHandle configFile = FileManager.getInstance().getConfigDirectory().child("mazes.json");
    JsonValue mazesNode = r.parse(configFile);
    for (JsonValue mazeNode : mazesNode) {
      MazeConfig c = MazeConfig.load(textureManager, hullConfigs, mazeNode, configFile, itemMan);
      configs.add(c);
    }
  }
}
