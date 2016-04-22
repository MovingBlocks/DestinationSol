/*
 * Copyright 2015 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.destinationsol.game.item;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import org.destinationsol.TextureManager;
import org.destinationsol.files.FileManager;
import org.destinationsol.game.GameColors;
import org.destinationsol.game.SolGame;
import org.destinationsol.game.particle.EffectConfig;
import org.destinationsol.game.particle.EffectTypes;
import org.destinationsol.game.sound.SolSound;
import org.destinationsol.game.sound.SoundManager;

import java.util.HashMap;

public class EngineItem implements SolItem {
  private final Config myConfig;

  private EngineItem(Config config) {
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

  public float getRotAcc() { return myConfig.rotAcc; }
  public float getAcc() { return myConfig.acc; }
  public float getMaxRotSpd() { return myConfig.maxRotSpd; }
  public boolean isBig() { return myConfig.big; }

  @Override
  public EngineItem copy() {
    return new EngineItem(myConfig);
  }

  @Override
  public boolean isSame(SolItem item) {
    return item instanceof EngineItem && ((EngineItem) item).myConfig == myConfig;
  }

  @Override
  public TextureAtlas.AtlasRegion getIcon(SolGame game) {
    return myConfig.icon;
  }

  @Override
  public SolItemType getItemType() {
    return null;
  }

  @Override
  public String getCode() {
    return null;
  }

  @Override
  public int isEquipped() {
    return 0;
  }

  @Override
  public void setEquipped(int equipped) {

  }

  public SolSound getWorkSound() {
    return myConfig.workSound;
  }

  public EffectConfig getEffectConfig() {
    return myConfig.effectConfig;
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
    public final TextureAtlas.AtlasRegion icon;
    public final EffectConfig effectConfig;

    private Config(String displayName, int price, String desc, float rotAcc, float acc, float maxRotSpd, boolean big,
      SolSound workSound, TextureAtlas.AtlasRegion icon, EffectConfig effectConfig)
    {
      this.displayName = displayName;
      this.price = price;
      this.desc = desc;
      this.rotAcc = rotAcc;
      this.acc = acc;
      this.maxRotSpd = maxRotSpd;
      this.big = big;
      this.workSound = workSound;
      this.icon = icon;
      this.effectConfig = effectConfig;
      this.example = new EngineItem(this);
    }

    private static Config load(SoundManager soundManager, FileHandle configFile, JsonValue sh, EffectTypes effectTypes,
      TextureManager textureManager, GameColors cols)
    {
      boolean big = sh.getBoolean("big");
      float rotAcc = big ? 100f : 515f;
      float acc = 2f;
      float maxRotSpd = big ? 40f : 230f;
      String workSoundDir = sh.getString("workSound");
      SolSound workSound = soundManager.getLoopedSound(workSoundDir, configFile);
      EffectConfig effectConfig = EffectConfig.load(sh.get("effect"), effectTypes, textureManager, configFile, cols);
      return new Config(null, 0, null, rotAcc, acc, maxRotSpd, big, workSound, null, effectConfig);
    }
  }

  public static class Configs {
    private final HashMap<String, Config> myConfigs;

    public Configs(HashMap<String, Config> configs) {
      myConfigs = configs;
    }

    public static Configs load(SoundManager soundManager, TextureManager textureManager, EffectTypes effectTypes, GameColors cols) {
      HashMap<String, Config> configs = new HashMap<String, Config>();
      JsonReader r = new JsonReader();
      FileHandle configFile = FileManager.getInstance().getItemsDirectory().child("engines.json");
      JsonValue parsed = r.parse(configFile);
      for (JsonValue sh : parsed) {
        Config config = Config.load(soundManager, configFile, sh, effectTypes, textureManager, cols);
        configs.put(sh.name(), config);
      }
      return new Configs(configs);
    }

    public Config get(String name) {
      return myConfigs.get(name);
    }
  }
}
