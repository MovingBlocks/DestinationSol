package org.destinationsol.game;

import com.badlogic.gdx.utils.JsonValue;
import org.destinationsol.game.item.ItemMan;
import org.destinationsol.game.ship.HullConfig;
import org.destinationsol.game.ship.HullConfigs;

import java.util.ArrayList;

public class ShipConfig {
  public final HullConfig hull;
  public final String items;
  public final int money;
  public final float density;
  public final ShipConfig guard;
  public final float dps;

  public ShipConfig(HullConfig hull, String items, int money,
    float density, ShipConfig guard, ItemMan itemMan) {
    this.hull = hull;
    this.items = items;
    this.money = money;
    this.density = density;
    this.guard = guard;
    dps = HardnessCalc.getShipConfDps(this, itemMan);
  }

  public static ArrayList<ShipConfig> loadList(JsonValue shipListJson, HullConfigs hullConfigs, ItemMan itemMan) {
    ArrayList<ShipConfig> res = new ArrayList<ShipConfig>();
    if (shipListJson == null) return res;
    for (JsonValue shipNode : shipListJson) {
      ShipConfig c = load(hullConfigs, shipNode, itemMan);
      res.add(c);
    }
    return res;
  }

  public static ShipConfig load(HullConfigs hullConfigs, JsonValue shipNode, ItemMan itemMan) {
    if (shipNode == null) return null;
    String hullName = shipNode.getString("hull");
    HullConfig hull = hullConfigs.getConfig(hullName);
    String items = shipNode.getString("items");
    int money = shipNode.getInt("money", 0);
    float density = shipNode.getFloat("density", -1);
    ShipConfig guard;
    if (shipNode.hasChild("guard")) {
      guard = load(hullConfigs, shipNode.get("guard"), itemMan);
    } else {
      guard = null;
    }
    return new ShipConfig(hull, items, money, density, guard, itemMan);
  }

}
