package com.miloshpetrov.sol2.game.maze;

import com.miloshpetrov.sol2.TexMan;
import com.miloshpetrov.sol2.game.ship.HullConfigs;

import java.util.ArrayList;
import java.util.List;

public class MazeConfigs {
  public final List<MazeConfig> configs;

  public MazeConfigs(TexMan texMan, HullConfigs hullConfigs) {
    configs = new ArrayList<MazeConfig>();
    MazeConfig c = MazeConfig.load(texMan, hullConfigs);
    configs.add(c);
  }

}
