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
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import org.destinationsol.TextureManager;
import org.destinationsol.common.SolMath;
import org.destinationsol.files.FileManager;
import org.destinationsol.game.DmgType;
import org.destinationsol.game.SolGame;
import org.destinationsol.game.SolObject;
import org.destinationsol.game.ship.SolShip;
import org.destinationsol.game.sound.SolSound;
import org.destinationsol.game.sound.SoundManager;

public class Shield implements SolItem {
  public static final float SIZE_PERC = .7f;
  private static final float BULLET_DMG_FACTOR = .7f;
  private final Config myConfig;
  private float myLife;
  private float myIdleTime;

  private Shield(Config config) {
    myConfig = config;
    myLife = myConfig.maxLife;
  }

  public void update(SolGame game, SolObject owner) {
    float ts = game.getTimeStep();
    if (myIdleTime >= myConfig.myMaxIdleTime) {
      if (myLife < myConfig.maxLife) {
        float regen = myConfig.regenSpd * ts;
        myLife = SolMath.approach(myLife, myConfig.maxLife, regen);
      }
    } else {
      myIdleTime += ts;
      if (myIdleTime >= myConfig.myMaxIdleTime) {
        game.getSoundMan().play(game, myConfig.regenSound, null, owner);
      }
    }
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
    return new Shield(myConfig);
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

  @Override
  public String getCode() {
    return myConfig.code;
  }

  public float getLife() {
    return myLife;
  }

  public float getMaxLife() {
    return myConfig.maxLife;
  }

  public boolean canAbsorb(DmgType dmgType) {
    return myLife > 0 && dmgType != DmgType.FIRE && dmgType != DmgType.CRASH;
  }

  public void absorb(SolGame game, float dmg, Vector2 pos, SolShip ship, DmgType dmgType) {
    if (!canAbsorb(dmgType) || dmg <= 0) throw new AssertionError("illegal call to absorb");
    myIdleTime = 0f;
    if (dmgType == DmgType.BULLET) dmg *= BULLET_DMG_FACTOR;
    myLife -= myLife < dmg ? myLife : dmg;

    game.getPartMan().shieldSpark(game, pos, ship.getHull(), myConfig.tex, dmg / myConfig.maxLife);
    float volMul = SolMath.clamp(4 * dmg / myConfig.maxLife);
    game.getSoundMan().play(game, myConfig.absorbSound, null, ship, volMul);

  }

  public static class Config {
    public final String displayName;
    public final int price;
    public final String desc;
    public final SolSound absorbSound;
    public final SolSound regenSound;
    public final Shield example;
    public final float maxLife;
    public final float myMaxIdleTime = 2;
    public final float regenSpd;
    public final TextureAtlas.AtlasRegion icon;
    public TextureAtlas.AtlasRegion tex;
    public final SolItemType itemType;
    public final String code;

    private Config(int maxLife, String displayName, int price, SolSound absorbSound, SolSound regenSound,
      TextureAtlas.AtlasRegion icon, TextureAtlas.AtlasRegion tex, SolItemType itemType, String code) {
      this.maxLife = maxLife;
      this.displayName = displayName;
      this.price = price;
      this.absorbSound = absorbSound;
      this.regenSound = regenSound;
      this.icon = icon;
      this.tex = tex;
      this.itemType = itemType;
      this.code = code;
      regenSpd = this.maxLife / 3;
      example = new Shield(this);
      this.desc = makeDesc();
    }

    private String makeDesc() {
      StringBuilder sb = new StringBuilder();
      sb.append("Takes ").append(SolMath.nice(maxLife)).append(" dmg\n");
      sb.append("Strong against bullets\n");
      return sb.toString();
    }

    public static void loadConfigs(ItemManager itemManager, SoundManager soundManager, TextureManager textureManager, SolItemTypes types) {
      JsonReader r = new JsonReader();
      FileHandle configFile = FileManager.getInstance().getItemsDirectory().child("shields.json");
      JsonValue parsed = r.parse(configFile);
      for (JsonValue sh : parsed) {
        int maxLife = sh.getInt("maxLife");
        String displayName = sh.getString("displayName");
        int price = sh.getInt("price");
        String soundDir = sh.getString("absorbSound");
        float absorbPitch = sh.getFloat("absorbSoundPitch", 1);
        SolSound absorbSound = soundManager.getPitchedSound(soundDir, configFile, absorbPitch);
        soundDir = sh.getString("regenSound");
        SolSound regenSound = soundManager.getSound(soundDir, configFile);
        TextureAtlas.AtlasRegion icon = textureManager.getTex(TextureManager.ICONS_DIR + sh.getString("icon"), configFile);
        TextureAtlas.AtlasRegion tex = textureManager.getTex(sh.getString("tex"), configFile);
        String code = sh.name;
        Config config = new Config(maxLife, displayName, price, absorbSound, regenSound, icon, tex, types.shield, code);
        itemManager.registerItem(config.example);
      }
    }
  }
}
