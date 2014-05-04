package com.miloshpetrov.sol2.game;

import com.badlogic.gdx.utils.JsonValue;
import com.miloshpetrov.sol2.game.ship.HullConfig;
import com.miloshpetrov.sol2.game.ship.HullConfigs;

import java.util.ArrayList;

public class ShipConfig {
  public final HullConfig hull;
  public final String items;
  public final int money;
  public final boolean hasRepairer, isMountFixed1, isMountFixed2;
  public final float density;
  public final ShipConfig guard;

  public ShipConfig(HullConfig hull, String items, int money, boolean hasRepairer, boolean isMountFixed1, boolean isMountFixed2, float density, ShipConfig guard) {
    this.hull = hull;
    this.items = items;
    this.money = money;
    this.hasRepairer = hasRepairer;
    this.isMountFixed1 = isMountFixed1;
    this.isMountFixed2 = isMountFixed2;
    this.density = density;
    this.guard = guard;
  }

  public static ArrayList<ShipConfig> loadList(JsonValue shipListJson, HullConfigs hullConfigs) {
    ArrayList<ShipConfig> res = new ArrayList<ShipConfig>();
    for (JsonValue shipNode : shipListJson) {
      ShipConfig c = load(hullConfigs, shipNode);
      res.add(c);
    }
    return res;
  }

  public static ShipConfig load(HullConfigs hullConfigs, JsonValue shipNode) {
    if (shipNode == null) return null;
    String hullName = shipNode.getString("hull");
    HullConfig hull = hullConfigs.getConfig(hullName);
    String items = shipNode.getString("items");
    int money = shipNode.getInt("money");
    boolean hasRepairer, isMountFixed1, isMountFixed2;
    hasRepairer = shipNode.getBoolean("repairer");
    isMountFixed1 = shipNode.getBoolean("mountFixed1");
    isMountFixed2 = shipNode.getBoolean("mountFixed2");
    float density = shipNode.getFloat("density", -1);
    ShipConfig guard;
    if (shipNode.hasChild("guard")) {
      guard = load(hullConfigs, shipNode.get("guard"));
    } else {
      guard = null;
    }
    return new ShipConfig(hull, items, money, hasRepairer, isMountFixed1, isMountFixed2, density, guard);
  }
}
