package com.miloshpetrov.sol2.game.item;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.miloshpetrov.sol2.SolFiles;
import com.miloshpetrov.sol2.TexMan;

public class ClipConfig {
  public final int price;
  public final String displayName;
  public final String desc;
  public final int size;
  public final ClipItem example;
  public final TextureAtlas.AtlasRegion icon;

  public ClipConfig(int price, String displayName, int size, String descSuff,
    TextureAtlas.AtlasRegion icon) {
    this.price = price;
    this.displayName = displayName;
    this.size = size;
    this.icon = icon;
    this.desc = "A clip of " + size + " " + descSuff;
    this.example = new ClipItem(this);
  }

  public static void load(ItemMan itemMan, TexMan texMan) {
    JsonReader r = new JsonReader();
    FileHandle configFile = SolFiles.readOnly(ItemMan.ITEM_CONFIGS_DIR + "clips.json");
    JsonValue parsed = r.parse(configFile);
    for (JsonValue sh : parsed) {
      String iconName = sh.getString("iconName");
      int price = sh.getInt("price");
      String displayName = sh.getString("displayName");
      String descSuf = sh.getString("descSuf");
      int size = sh.getInt("size");
      TextureAtlas.AtlasRegion icon = texMan.getTex(TexMan.ICONS_DIR + iconName, configFile);
      ClipConfig config = new ClipConfig(price, displayName, size, descSuf, icon);
      itemMan.registerItem(sh.name(), config.example);
    }
  }
}
