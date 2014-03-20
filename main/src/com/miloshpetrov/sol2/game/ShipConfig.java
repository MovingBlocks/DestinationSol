package com.miloshpetrov.sol2.game;

import com.badlogic.gdx.utils.JsonValue;
import com.miloshpetrov.sol2.game.ship.HullConfig;
import com.miloshpetrov.sol2.game.ship.HullConfigs;

import java.util.ArrayList;

public class ShipConfig {
  public final HullConfig hull;
  public final String items;
  public final float density;

  public ShipConfig(HullConfig hull, String items, float density) {
    this.hull = hull;
    this.items = items;
    this.density = density;
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
    String hullName = shipNode.getString("hull");
    HullConfig hull = hullConfigs.getConfig(hullName);
    String items = shipNode.getString("items");
    float density = shipNode.getFloat("density", -1);
    return new ShipConfig(hull, items, density);
  }
}
