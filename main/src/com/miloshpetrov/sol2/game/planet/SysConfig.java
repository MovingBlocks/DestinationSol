package com.miloshpetrov.sol2.game.planet;

import com.miloshpetrov.sol2.game.ShipConfig;
import com.miloshpetrov.sol2.game.chunk.SpaceEnvironmentConfig;

import java.util.ArrayList;

public class SysConfig {

  public final String name;
  public final ArrayList<ShipConfig> enemies;
  public final SpaceEnvironmentConfig envConfig;

  public SysConfig(String name, ArrayList<ShipConfig> enemies, SpaceEnvironmentConfig envConfig) {
    this.name = name;
    this.enemies = enemies;
    this.envConfig = envConfig;
  }
}
