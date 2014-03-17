package com.miloshpetrov.sol2.game.maze;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.miloshpetrov.sol2.*;
import com.miloshpetrov.sol2.game.ship.HullConfigs;

import java.util.ArrayList;
import java.util.List;

public class MazeConfigs {
  public final List<MazeConfig> configs;

  public MazeConfigs(TexMan texMan, HullConfigs hullConfigs) {
    configs = new ArrayList<MazeConfig>();

    JsonReader r = new JsonReader();
    FileHandle configFile = SolFiles.readOnly(Const.CONFIGS_DIR + "mazes.json");
    JsonValue mazesNode = r.parse(configFile);
    for (JsonValue mazeNode : mazesNode) {
      MazeConfig c = MazeConfig.load(texMan, hullConfigs, mazeNode, configFile);
      configs.add(c);
    }
  }
}
