package com.miloshpetrov.sol2.game.item;

import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.miloshpetrov.sol2.SolFiles;

public class ClipConfig {
  public final String iconName;
  public final int price;
  public final String displayName;
  public final String desc;
  public final int size;
  public final ClipItem example;

  public ClipConfig(String iconName, int price, String displayName, int size, String descSuff) {
    this.iconName = iconName;
    this.price = price;
    this.displayName = displayName;
    this.size = size;
    this.desc = "A clip of " + size + " " + descSuff;
    this.example = new ClipItem(this);
  }

  public static void load(ItemMan itemMan) {
    JsonReader r = new JsonReader();
    JsonValue parsed = r.parse(SolFiles.readOnly(ItemMan.ITEM_CONFIGS_DIR + "clips.json"));
    for (JsonValue sh : parsed) {
      String iconName = sh.getString("iconName");
      int price = sh.getInt("price");
      String displayName = sh.getString("displayName");
      String descSuf = sh.getString("descSuf");
      int size = sh.getInt("size");
      ClipConfig config = new ClipConfig(iconName, price, displayName, size, descSuf);
      itemMan.registerItem(sh.name(), config.example);
    }
  }
}
