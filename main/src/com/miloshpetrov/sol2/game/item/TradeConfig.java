package com.miloshpetrov.sol2.game.item;

import com.badlogic.gdx.utils.JsonValue;
import com.miloshpetrov.sol2.game.ShipConfig;
import com.miloshpetrov.sol2.game.ship.HullConfig;
import com.miloshpetrov.sol2.game.ship.HullConfigs;

public class TradeConfig {
  public final ItemContainer items;
  public final ItemContainer hulls;
  public final ItemContainer mercs;

  public TradeConfig(ItemContainer items, ItemContainer hulls, ItemContainer mercs)
  {
    this.items = items;
    this.hulls = hulls;
    this.mercs = mercs;
  }


  public static TradeConfig load(ItemMan itemMan, JsonValue tradeNode, HullConfigs hullConfigs) {
    if (tradeNode == null) return null;
    String itemStr = tradeNode.getString("items");
    ItemContainer items = new ItemContainer();
    itemMan.fillContainer(items, itemStr);

    ItemContainer hulls = new ItemContainer();
    String shipStr = tradeNode.getString("ships", "");
    for (String hullName : shipStr.split(" ")) {
      HullConfig hull = hullConfigs.getConfig(hullName);
      hulls.add(new ShipItem(hull));
    }

    ItemContainer mercs = new ItemContainer();
    for (ShipConfig merc : ShipConfig.loadList(tradeNode.get("mercenaries"), hullConfigs, itemMan)) {
      mercs.add(new MercItem(merc));
    }


    return new TradeConfig(items, hulls, mercs);
  }

}
