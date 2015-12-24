package org.destinationsol.game.planet;

import org.destinationsol.game.ShipConfig;
import org.destinationsol.game.chunk.SpaceEnvConfig;
import org.destinationsol.game.item.TradeConfig;

import java.util.ArrayList;

public class SysConfig {

  public final String name;
  public final ArrayList<ShipConfig> tempEnemies;
  public final SpaceEnvConfig envConfig;
  public final ArrayList<ShipConfig> constEnemies;
  public final ArrayList<ShipConfig> constAllies;
  public final TradeConfig tradeConfig;
  public final ArrayList<ShipConfig> innerTempEnemies;
  public final boolean hard;

  public SysConfig(String name, ArrayList<ShipConfig> tempEnemies, SpaceEnvConfig envConfig,
    ArrayList<ShipConfig> constEnemies, ArrayList<ShipConfig> constAllies, TradeConfig tradeConfig,
    ArrayList<ShipConfig> innerTempEnemies, boolean hard) {
    this.name = name;
    this.tempEnemies = tempEnemies;
    this.envConfig = envConfig;
    this.constEnemies = constEnemies;
    this.constAllies = constAllies;
    this.tradeConfig = tradeConfig;
    this.innerTempEnemies = innerTempEnemies;
    this.hard = hard;
  }
}
