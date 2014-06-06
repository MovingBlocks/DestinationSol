package com.miloshpetrov.sol2.game.gun;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.miloshpetrov.sol2.SolFiles;
import com.miloshpetrov.sol2.TexMan;
import com.miloshpetrov.sol2.common.SolMath;
import com.miloshpetrov.sol2.game.DmgType;
import com.miloshpetrov.sol2.game.HardnessCalc;
import com.miloshpetrov.sol2.game.item.*;
import com.miloshpetrov.sol2.game.projectile.ProjectileConfig;
import com.miloshpetrov.sol2.game.sound.SolSound;
import com.miloshpetrov.sol2.game.sound.SoundMan;

public class GunConfig {
  public final float minAngleVar;
  public final float maxAngleVar;
  public final float angleVarDamp;
  public final float angleVarPerShot;
  public final float timeBetweenShots;
  public final float reloadTime;
  public final float gunLength;
  public final String displayName;
  public final TextureAtlas.AtlasRegion tex;
  public final boolean lightOnShot;
  public final int price;
  public final String desc;
  public final float dps;
  public final GunItem example;
  public final ClipConfig clipConf;
  public final SolSound shootSound;
  public final SolSound reloadSound;
  public final TextureAtlas.AtlasRegion icon;
  public final boolean fixed;
  public final float meanDps;
  public final SolItemType itemType;
  public final float texLenPerc;

  public GunConfig(float minAngleVar, float maxAngleVar, float angleVarDamp, float angleVarPerShot,
    float timeBetweenShots,
    float reloadTime, float gunLength, String displayName,
    boolean lightOnShot, int price,
    ClipConfig clipConf, SolSound shootSound, SolSound reloadSound, TextureAtlas.AtlasRegion tex,
    TextureAtlas.AtlasRegion icon, boolean fixed, SolItemType itemType, float texLenPerc)
  {
    this.shootSound = shootSound;
    this.reloadSound = reloadSound;

    this.tex = tex;

    this.maxAngleVar = maxAngleVar;
    this.minAngleVar = minAngleVar;
    this.angleVarDamp = angleVarDamp;
    this.angleVarPerShot = angleVarPerShot;
    this.timeBetweenShots = timeBetweenShots;
    this.reloadTime = reloadTime;
    this.gunLength = gunLength;
    this.displayName = displayName;
    this.lightOnShot = lightOnShot;
    this.price = price;
    this.clipConf = clipConf;
    this.icon = icon;
    this.fixed = fixed;
    this.itemType = itemType;
    this.texLenPerc = texLenPerc;

    dps = HardnessCalc.getShotDps(this, clipConf.projConfig.dmg);
    meanDps = HardnessCalc.getGunMeanDps(this);
    this.desc = makeDesc();
    example = new GunItem(this, 0, 0);
  }

  private String makeDesc() {
    StringBuilder sb = new StringBuilder();
    ProjectileConfig pc = clipConf.projConfig;
    sb.append(fixed ? "Heavy gun (shoots forward)\n" : "Light gun\n");
    if (pc.dmg > 0) {
      sb.append("Dmg: ").append(SolMath.nice(dps)).append("/s\n");
      DmgType dmgType = pc.dmgType;
      if (dmgType == DmgType.ENERGY) sb.append("Weak against armor\n");
      else if (dmgType == DmgType.BULLET) sb.append("Weak against shields\n");
    } else if (pc.emTime > 0) {
      sb.append("Disables enemy ships for ").append(SolMath.nice(pc.emTime)).append(" s\n");
    }
    if (pc.density > 0) {
      sb.append("Knocks enemies back\n");
    }
    sb.append("Reload: ").append(SolMath.nice(reloadTime)).append(" s\n");
    if (clipConf.infinite) {
      sb.append("Infinite ammo\n");
    } else {
      sb.append("Uses ").append(clipConf.plural).append("\n");
    }
    return sb.toString();
  }

  public static void load(TexMan texMan, ItemMan itemMan, SoundMan soundMan, SolItemTypes types) {
    JsonReader r = new JsonReader();
    FileHandle configFile = SolFiles.readOnly(ItemMan.ITEM_CONFIGS_DIR + "guns.json");
    JsonValue parsed = r.parse(configFile);
    for (JsonValue sh : parsed) {
      float minAngleVar = sh.getFloat("minAngleVar", 0);
      float maxAngleVar = sh.getFloat("maxAngleVar");
      float angleVarDamp = sh.getFloat("angleVarDamp");
      float angleVarPerShot = sh.getFloat("angleVarPerShot");
      float timeBetweenShots = sh.getFloat("timeBetweenShots");
      float reloadTime = sh.getFloat("reloadTime");
      float gunLength = sh.getFloat("gunLength");
      float texLenPerc = sh.getFloat("texLenPerc", 1);
      String texName = sh.getString("texName");
      String displayName = sh.getString("displayName");
      boolean lightOnShot = sh.getBoolean("lightOnShot", false);
      int price = sh.getInt("price");
      String clipName = sh.getString("clipName");
      ClipConfig clipConf = clipName.isEmpty() ? null : ((ClipItem)itemMan.getExample(clipName)).getConfig();
      String reloadSoundPath = sh.getString("reloadSound");
      SolSound reloadSound = soundMan.getSound(reloadSoundPath, configFile);
      String shootSoundPath = sh.getString("shootSound");
      float shootPitch = sh.getFloat("shootSoundPitch", 1);
      SolSound shootSound = soundMan.getPitchedSound(shootSoundPath, configFile, shootPitch);
      TextureAtlas.AtlasRegion tex = texMan.getTex("smallGameObjs/guns/" + texName, configFile);
      TextureAtlas.AtlasRegion icon = texMan.getTex(TexMan.ICONS_DIR + texName, configFile);
      boolean fixed = sh.getBoolean("fixed", false);
      GunConfig c = new GunConfig(minAngleVar, maxAngleVar, angleVarDamp, angleVarPerShot, timeBetweenShots, reloadTime,
        gunLength, displayName, lightOnShot, price, clipConf, shootSound, reloadSound, tex, icon, fixed, types.gun, texLenPerc);
      itemMan.registerItem(sh.name, c.example);
    }
  }
}
