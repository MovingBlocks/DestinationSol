package com.miloshpetrov.sol2.game.planet;

import com.miloshpetrov.sol2.game.ShipConfig;
import com.miloshpetrov.sol2.game.chunk.SpaceEnvConfig;

import java.util.ArrayList;

public class SysConfig {

  public final String name;
  public final ArrayList<ShipConfig> tempEnemies;
  public final SpaceEnvConfig envConfig;
  public final ArrayList<ShipConfig> constEnemies;
  public final ArrayList<ShipConfig> constAllies;

  public SysConfig(String name, ArrayList<ShipConfig> tempEnemies, SpaceEnvConfig envConfig,
    ArrayList<ShipConfig> constEnemies, ArrayList<ShipConfig> constAllies) {
    this.name = name;
    this.tempEnemies = tempEnemies;
    this.envConfig = envConfig;
    this.constEnemies = constEnemies;
    this.constAllies = constAllies;
  }
}
