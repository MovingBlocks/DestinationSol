package com.miloshpetrov.sol2.game.maze;

import java.util.ArrayList;
import java.util.List;

public class MazeConfigs {
  public final List<MazeConfig> configs;

  public MazeConfigs() {
    configs = new ArrayList<MazeConfig>();
    MazeConfig c = MazeConfig.load();
    configs.add(c);
  }

}
