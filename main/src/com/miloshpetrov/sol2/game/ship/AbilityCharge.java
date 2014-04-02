package com.miloshpetrov.sol2.game.ship;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.miloshpetrov.sol2.SolFiles;
import com.miloshpetrov.sol2.TexMan;
import com.miloshpetrov.sol2.game.SolGame;
import com.miloshpetrov.sol2.game.item.ItemMan;
import com.miloshpetrov.sol2.game.item.SolItem;

public class AbilityCharge implements SolItem {
  private final Config myConfig;

  public AbilityCharge(Config config) {
    myConfig = config;
  }

  @Override
  public String getDisplayName() {
    return myConfig.displayName;
  }

  @Override
  public float getPrice() {
    return myConfig.price;
  }

  @Override
  public String getDesc() {
    return myConfig.desc;
  }

  @Override
  public SolItem copy() {
    return new AbilityCharge(myConfig);
  }

  @Override
  public boolean isSame(SolItem item) {
    return item instanceof AbilityCharge && ((AbilityCharge) item).myConfig == myConfig;
  }

  @Override
  public TextureAtlas.AtlasRegion getIcon(SolGame game) {
    return myConfig.icon;
  }


  public static class Config {
    private final TextureAtlas.AtlasRegion icon;
    private final float price;
    private final String displayName;
    private final String desc;

    public Config(TextureAtlas.AtlasRegion icon, float price, String displayName, String desc) {
      this.icon = icon;
      this.price = price;
      this.displayName = displayName;
      this.desc = desc;
    }

    public static void load(ItemMan itemMan, TexMan texMan) {
      JsonReader r = new JsonReader();
      FileHandle configFile = SolFiles.readOnly(ItemMan.ITEM_CONFIGS_DIR + "abilityCharges.json");
      JsonValue parsed = r.parse(configFile);
      for (JsonValue ammoNode : parsed) {
        String iconName = ammoNode.getString("iconName");
        TextureAtlas.AtlasRegion icon = texMan.getTex(TexMan.ICONS_DIR + iconName, configFile);
        float price = ammoNode.getFloat("price");
        String displayName = ammoNode.getString("displayName");
        String desc = ammoNode.getString("desc");
        Config c = new Config(icon, price, displayName, desc);
        AbilityCharge chargeExample = new AbilityCharge(c);
        itemMan.registerItem(ammoNode.name, chargeExample);
      }
    }
  }
}
