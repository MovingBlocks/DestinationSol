package org.destinationsol.game.item;

import com.badlogic.gdx.utils.JsonValue;
import org.destinationsol.files.HullConfigManager;
import org.destinationsol.game.ShipConfig;
import org.destinationsol.game.ship.hulls.HullConfig;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TradeConfig {
  public final List<ItemConfig> items;
  public final ItemContainer hulls;
  public final ItemContainer mercs;

  public TradeConfig(List<ItemConfig> items, ItemContainer hulls, ItemContainer mercs)
  {
    this.items = items;
    this.hulls = hulls;
    this.mercs = mercs;
  }


  public static TradeConfig load(ItemManager itemManager, JsonValue tradeNode, HullConfigManager hullConfigs) {
    if (tradeNode == null) return null;
    String itemStr = tradeNode.getString("items");
    List<ItemConfig> items = itemManager.parseItems(itemStr);
    Collections.reverse(items);

    ItemContainer hulls = new ItemContainer();
    String shipStr = tradeNode.getString("ships", "");
    String[] split = shipStr.split(" ");
    for (int i = split.length - 1; i >= 0; i--) {
      String hullName = split[i];
      HullConfig hull = hullConfigs.getConfig(hullName);
      hulls.add(new ShipItem(hull));
    }

    ItemContainer mercs = new ItemContainer();
    ArrayList<ShipConfig> loadList = ShipConfig.loadList(tradeNode.get("mercenaries"), hullConfigs, itemManager);
    for (int i = loadList.size() - 1; i >= 0; i--) {
      ShipConfig merc = loadList.get(i);
      mercs.add(new MercItem(merc));
    }


    return new TradeConfig(items, hulls, mercs);
  }

}
