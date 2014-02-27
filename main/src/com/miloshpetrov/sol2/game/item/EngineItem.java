package com.miloshpetrov.sol2.game.item;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.miloshpetrov.sol2.SolFiles;
import com.miloshpetrov.sol2.game.sound.SolSound;
import com.miloshpetrov.sol2.game.sound.SoundMan;

public class EngineItem implements SolItem {
  public static final String TEX_NAME = "engine";
  private final Config myConfig;

  private EngineItem(Config config) {
    myConfig = config;
  }

  public String getTexName() {
    return TEX_NAME;
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

  public float getRotAcc() { return myConfig.rotAcc; }
  public float getAac() { return myConfig.acc; }
  public float getMaxRotSpd() { return myConfig.maxRotSpd; }
  public boolean isBig() { return myConfig.big; }

  @Override
  public SolItem copy() {
    return new EngineItem(myConfig);
  }

  @Override
  public boolean isSame(SolItem item) {
    return item instanceof EngineItem && ((EngineItem) item).myConfig == myConfig;
  }

  public SolSound getWorkSound() {
    return myConfig.workSound;
  }


  public static class Config {
    public final String displayName;
    public final int price;
    public final String desc;
    public final float rotAcc;
    public final float acc;
    public final float maxRotSpd;
    public final boolean big;
    public final SolSound workSound;
    public final EngineItem example;

    private Config(String displayName, int price, String desc, float rotAcc, float acc, float maxRotSpd, boolean big, SolSound workSound){
      this.displayName = displayName;
      this.price = price;
      this.desc = desc;
      this.rotAcc = rotAcc;
      this.acc = acc;
      this.maxRotSpd = maxRotSpd;
      this.big = big;
      this.workSound = workSound;
      this.example = new EngineItem(this);
    }

    public static void loadConfigs(ItemMan itemMan, SoundMan soundMan) {
      JsonReader r = new JsonReader();
      FileHandle configFile = SolFiles.readOnly(ItemMan.ITEM_CONFIGS_DIR + "engines.json");
      JsonValue parsed = r.parse(configFile);
      for (JsonValue sh : parsed) {
        String displayName = sh.getString("displayName");
        int price = sh.getInt("price");
        String desc = sh.getString("desc");
        float rotAcc = sh.getFloat("rotAcc");
        float acc = sh.getFloat("acc");
        float maxRotSpd = sh.getFloat("maxRotSpd");
        boolean big = sh.getBoolean("big");
        String workSoundDir = sh.getString("workSound");
        SolSound workSound = soundMan.getLoopedSound(workSoundDir, configFile);
        Config config = new Config(displayName, price, desc, rotAcc, acc, maxRotSpd, big, workSound);
        itemMan.registerItem(sh.name(), config.example);
      }
    }
  }
}
