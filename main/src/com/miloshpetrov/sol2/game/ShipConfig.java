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

  public static ArrayList<ShipConfig> load(JsonValue shipListJson, HullConfigs hullConfigs) {
    ArrayList<ShipConfig> res = new ArrayList<ShipConfig>();
    for (JsonValue shipNode : shipListJson) {
      String hullName = shipNode.getString("hull");
      HullConfig hull = hullConfigs.getConfig(hullName);
      String items = shipNode.getString("items");
      float density = shipNode.getFloat("density");
      ShipConfig c = new ShipConfig(hull, items, density);
      res.add(c);
    }
    return res;
  }
}
