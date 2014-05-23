package com.miloshpetrov.sol2.game;

import com.badlogic.gdx.utils.JsonValue;
import com.miloshpetrov.sol2.game.ship.HullConfig;
import com.miloshpetrov.sol2.game.ship.HullConfigs;

import java.util.ArrayList;

public class ShipConfig {
  public final HullConfig hull;
  public final String items;
  public final int money;
  public final float density;
  public final ShipConfig guard;
  public final float hirePrice;
  public final String displayName;
  public final String desc;

  public ShipConfig(HullConfig hull, String items, int money,
    float density, ShipConfig guard, float hirePrice, String displayName, String desc) {
    this.hull = hull;
    this.items = items;
    this.money = money;
    this.density = density;
    this.guard = guard;
    this.hirePrice = hirePrice;
    this.displayName = displayName;
    this.desc = desc;
  }

  public static ArrayList<ShipConfig> loadList(JsonValue shipListJson, HullConfigs hullConfigs) {
    ArrayList<ShipConfig> res = new ArrayList<ShipConfig>();
    if (shipListJson == null) return res;
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
    int money = shipNode.getInt("money", 0);
    float density = shipNode.getFloat("density", -1);
    ShipConfig guard;
    if (shipNode.hasChild("guard")) {
      guard = load(hullConfigs, shipNode.get("guard"));
    } else {
      guard = null;
    }
    float hirePrice = shipNode.getFloat("hirePrice", 0);
    String displayName = shipNode.getString("displayName", null);
    String desc = shipNode.getString("desc", null);
    return new ShipConfig(hull, items, money, density, guard, hirePrice, displayName, desc);
  }

}
