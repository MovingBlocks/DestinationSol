package com.miloshpetrov.sol2.game.item;

import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.miloshpetrov.sol2.SolFiles;
import com.miloshpetrov.sol2.game.sound.SolSounds;
import com.miloshpetrov.sol2.game.sound.SoundMan;

public class Armor implements SolItem {
  private final Config myConfig;

  private Armor(Config config) {
    myConfig = config;
  }

  @Override
  public String getTexName() {
    return "armor";
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

  public float getPerc() {
    return myConfig.perc;
  }

  public static class Config {
    public final String displayName;
    public final int price;
    public final float perc;
    public final String desc;
    public final SolSounds sounds;
    public final Armor example;

    private Config(String displayName, int price, float perc, String descBase, SolSounds sounds)
    {
      this.displayName = displayName;
      this.price = price;
      this.perc = perc;
      this.desc = String.format(descBase, (int)(perc * 100));
      this.sounds = sounds;
      this.example = new Armor(this);
    }

    public static void loadConfigs(ItemMan itemMan, SoundMan soundMan)
    {
      JsonReader r = new JsonReader();
      JsonValue parsed = r.parse(SolFiles.readOnly(ItemMan.ITEM_CONFIGS_DIR + "armors.json"));
      for (JsonValue sh : parsed) {
        String displayName = sh.getString("displayName");
        int price = sh.getInt("price");
        float perc = sh.getFloat("perc");
        String descBase = sh.getString("descBase");
        String soundsDir = sh.getString("sounds");
        SolSounds sounds = soundMan.getSounds(soundsDir);
        Config config = new Config(displayName, price, perc, descBase, sounds);
        itemMan.registerItem(sh.name(), config.example);
      }
    }
  }
}
