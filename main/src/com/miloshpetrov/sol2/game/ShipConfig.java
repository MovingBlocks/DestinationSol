package com.miloshpetrov.sol2.game;

import com.badlogic.gdx.utils.JsonValue;
import com.miloshpetrov.sol2.files.HullConfigManager;
import com.miloshpetrov.sol2.game.item.ItemManager;
import com.miloshpetrov.sol2.game.ship.hulls.HullConfig;

import java.util.ArrayList;

public class ShipConfig {
  public final HullConfig hull;
  public final String items;
  public final int money;
  public final float density;
  public final ShipConfig guard;
  public final float dps;

  public ShipConfig(HullConfig hull, String items, int money,
    float density, ShipConfig guard, ItemManager itemManager) {
    this.hull = hull;
    this.items = items;
    this.money = money;
    this.density = density;
    this.guard = guard;
    dps = HardnessCalc.getShipConfDps(this, itemManager);
  }

  public static ArrayList<ShipConfig> loadList(JsonValue shipListJson, HullConfigManager hullConfigs, ItemManager itemManager) {
    ArrayList<ShipConfig> res = new ArrayList<ShipConfig>();
    if (shipListJson == null) return res;
    for (JsonValue shipNode : shipListJson) {
      ShipConfig c = load(hullConfigs, shipNode, itemManager);
      res.add(c);
    }
    return res;
  }

  public static ShipConfig load(HullConfigManager hullConfigs, JsonValue shipNode, ItemManager itemManager) {
    if (shipNode == null) return null;
    String hullName = shipNode.getString("hull");
    HullConfig hull = hullConfigs.getConfig(hullName);
    String items = shipNode.getString("items");
    int money = shipNode.getInt("money", 0);
    float density = shipNode.getFloat("density", -1);
    ShipConfig guard;
    if (shipNode.hasChild("guard")) {
      guard = load(hullConfigs, shipNode.get("guard"), itemManager);
    } else {
      guard = null;
    }
    return new ShipConfig(hull, items, money, density, guard, itemManager);
  }

}
