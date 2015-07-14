/*
 * Copyright 2015 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
 
package com.miloshpetrov.sol2.game;

import com.badlogic.gdx.utils.JsonValue;
import com.miloshpetrov.sol2.game.item.ItemMan;
import com.miloshpetrov.sol2.game.ship.HullConfig;
import com.miloshpetrov.sol2.game.ship.HullConfigs;

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
