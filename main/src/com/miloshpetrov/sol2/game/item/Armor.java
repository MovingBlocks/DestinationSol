package com.miloshpetrov.sol2.game.item;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.miloshpetrov.sol2.SolFiles;
import com.miloshpetrov.sol2.TexMan;
import com.miloshpetrov.sol2.game.DmgType;
import com.miloshpetrov.sol2.game.SolGame;
import com.miloshpetrov.sol2.game.sound.SolSound;
import com.miloshpetrov.sol2.game.sound.SoundMan;

public class Armor implements SolItem {
  private final Config myConfig;

  private Armor(Config config) {
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
    return new Armor(myConfig);
  }

  @Override
  public boolean isSame(SolItem item) {
    return item instanceof Armor && ((Armor) item).myConfig == myConfig;
  }

  @Override
  public TextureAtlas.AtlasRegion getIcon(SolGame game) {
    return myConfig.icon;
  }

  public float getPerc() {
    return myConfig.perc;
  }

  public SolSound getDmgSound(DmgType dmgType) {
    switch (dmgType) {
      case BULLET: return myConfig.bulletDmgSound;
      case ENERGY: return myConfig.energyDmgSound;
    }
    return null;
  }

  public static class Config {
    public final String displayName;
    public final int price;
    public final float perc;
    public final String desc;
    public final SolSound bulletDmgSound;
    public final Armor example;
    public final TextureAtlas.AtlasRegion icon;
    public final SolSound energyDmgSound;

    private Config(String displayName, int price, float perc, String descBase, SolSound bulletDmgSound,
      TextureAtlas.AtlasRegion icon, SolSound energyDmgSound)
    {
      this.displayName = displayName;
      this.price = price;
      this.perc = perc;
      this.icon = icon;
      this.energyDmgSound = energyDmgSound;
      this.desc = String.format(descBase, (int)(perc * 100));
      this.bulletDmgSound = bulletDmgSound;
      this.example = new Armor(this);
    }

    public static void loadConfigs(ItemMan itemMan, SoundMan soundMan, TexMan texMan)
    {
      JsonReader r = new JsonReader();
      FileHandle configFile = SolFiles.readOnly(ItemMan.ITEM_CONFIGS_DIR + "armors.json");
      JsonValue parsed = r.parse(configFile);
      for (JsonValue sh : parsed) {
        String displayName = sh.getString("displayName");
        int price = sh.getInt("price");
        float perc = sh.getFloat("perc");
        String descBase = sh.getString("descBase");
        String bulletDmgSoundDir = sh.getString("bulletDmgSound");
        String energyDmgSoundDir = sh.getString("energyDmgSound");
        SolSound bulletDmgSound = soundMan.getSound(bulletDmgSoundDir, configFile);
        SolSound energyDmgSound = soundMan.getSound(energyDmgSoundDir, configFile);
        TextureAtlas.AtlasRegion icon = texMan.getTex(sh.getString("iconDir"), null);
        Config config = new Config(displayName, price, perc, descBase, bulletDmgSound, icon, energyDmgSound);
        itemMan.registerItem(sh.name(), config.example);
      }
    }
  }
}
