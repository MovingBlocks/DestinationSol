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
    return false;
  }

  @Override
  public TextureAtlas.AtlasRegion getIcon(SolGame game) {
    return myConfig.icon;
  }

  @Override
  public SolItemType getItemType() {
    return myConfig.itemType;
  }

  public float getPerc() {
    return myConfig.perc;
  }

  public SolSound getHitSound(DmgType dmgType) {
    switch (dmgType) {
      case BULLET: return myConfig.bulletHitSound;
      case ENERGY: return myConfig.energyHitSound;
    }
    return null;
  }

  public static class Config {
    public final String displayName;
    public final int price;
    public final float perc;
    public final String desc;
    public final SolSound bulletHitSound;
    public final Armor example;
    public final TextureAtlas.AtlasRegion icon;
    public final SolSound energyHitSound;
    public final SolItemType itemType;

    private Config(String displayName, int price, float perc, SolSound bulletHitSound,
      TextureAtlas.AtlasRegion icon, SolSound energyHitSound, SolItemType itemType)
    {
      this.displayName = displayName;
      this.price = price;
      this.perc = perc;
      this.icon = icon;
      this.energyHitSound = energyHitSound;
      this.itemType = itemType;
      this.desc = "Reduces damage by " + (int)(perc * 100) + "%.\nStrong against energy weapons.";
      this.bulletHitSound = bulletHitSound;
      this.example = new Armor(this);
    }

    public static void loadConfigs(ItemMan itemMan, SoundMan soundMan, TexMan texMan, SolItemTypes types)
    {
      JsonReader r = new JsonReader();
      FileHandle configFile = SolFiles.readOnly(ItemMan.ITEM_CONFIGS_DIR + "armors.json");
      JsonValue parsed = r.parse(configFile);
      for (JsonValue sh : parsed) {
        String displayName = sh.getString("displayName");
        int price = sh.getInt("price");
        float perc = sh.getFloat("perc");
        String bulletDmgSoundDir = sh.getString("bulletHitSound");
        String energyDmgSoundDir = sh.getString("energyHitSound");
        float basePitch = sh.getFloat("baseSoundPitch", 1);
        SolSound bulletDmgSound = soundMan.getPitchedSound(bulletDmgSoundDir, configFile, basePitch);
        SolSound energyDmgSound = soundMan.getPitchedSound(energyDmgSoundDir, configFile, basePitch);
        TextureAtlas.AtlasRegion icon = texMan.getTex(TexMan.ICONS_DIR + sh.getString("icon"), configFile);
        Config config = new Config(displayName, price, perc, bulletDmgSound, icon, energyDmgSound, types.armor);
        itemMan.registerItem(sh.name(), config.example);
      }
    }
  }
}
