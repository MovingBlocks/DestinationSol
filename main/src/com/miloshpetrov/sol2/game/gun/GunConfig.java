package com.miloshpetrov.sol2.game.gun;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.miloshpetrov.sol2.SolFiles;
import com.miloshpetrov.sol2.TexMan;
import com.miloshpetrov.sol2.common.SolMath;
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
  public final float maxReloadTime;
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
  public final int projectilesPerShot;
  public final boolean fixed;

  public GunConfig(float minAngleVar, float maxAngleVar, float angleVarDamp, float angleVarPerShot,
    float timeBetweenShots,
    float maxReloadTime, float gunLength, String displayName,
    boolean lightOnShot, int price, String descBase,
    ClipConfig clipConf, SolSound shootSound, SolSound reloadSound, TextureAtlas.AtlasRegion tex,
    TextureAtlas.AtlasRegion icon, int projectilesPerShot, boolean fixed)
  {
    this.shootSound = shootSound;
    this.reloadSound = reloadSound;

    this.tex = tex;

    this.maxAngleVar = maxAngleVar;
    this.minAngleVar = minAngleVar;
    this.angleVarDamp = angleVarDamp;
    this.angleVarPerShot = angleVarPerShot;
    this.timeBetweenShots = timeBetweenShots;
    this.maxReloadTime = maxReloadTime;
    this.gunLength = gunLength;
    this.displayName = displayName;
    this.lightOnShot = lightOnShot;
    this.price = price;
    this.clipConf = clipConf;
    this.icon = icon;
    this.projectilesPerShot = projectilesPerShot;
    this.fixed = fixed;

    this.desc = makeDesc(descBase);
    dps = calcDps();
    example = new GunItem(this, 0, 0);
  }

  private float calcDps() {
    ProjectileConfig pc = clipConf.projConfig;
    float projDmg = pc.dmg;
    if (pc.emTime > 0) projDmg = 15;
    else if (pc.density > 0) projDmg = 5;

    float projHitChance = (pc.spdLen + pc.acc) / 4;
    if (pc.guideRotSpd > 0) projHitChance += .3f;
    float sz = pc.physSize;
    if (sz > 0) projHitChance += sz * .5f;
    projHitChance = SolMath.clamp(projHitChance, .1f, 1);
    projDmg *= projHitChance;

    float shotDmg = projDmg;
    if (projectilesPerShot > 1) shotDmg *= projectilesPerShot / 2;

    float shootTimePerc = fixed ? .2f : 1f;
    return shotDmg * shootTimePerc / timeBetweenShots;
  }

  private String makeDesc(String descBase) {
    StringBuilder sb = new StringBuilder(descBase);
    sb.append("\nDmg: ").append(dps).append("/s");
    sb.append("\nReload: ").append(maxReloadTime).append("s");
    if (clipConf.infinite) {
      sb.append("\nInfinite ammo");
    }
    return sb.toString();
  }

  public static void load(TexMan texMan, ItemMan itemMan, SoundMan soundMan) {
    JsonReader r = new JsonReader();
    FileHandle configFile = SolFiles.readOnly(ItemMan.ITEM_CONFIGS_DIR + "guns.json");
    JsonValue parsed = r.parse(configFile);
    for (JsonValue sh : parsed) {
      float minAngleVar = sh.getFloat("minAngleVar", 0);
      float maxAngleVar = sh.getFloat("maxAngleVar");
      float angleVarDamp = sh.getFloat("angleVarDamp");
      float angleVarPerShot = sh.getFloat("angleVarPerShot");
      float timeBetweenShots = sh.getFloat("timeBetweenShots");
      float maxReloadTime = sh.getFloat("maxReloadTime");
      float gunLength = sh.getFloat("gunLength");
      String texName = sh.getString("texName");
      String displayName = sh.getString("displayName");
      boolean lightOnShot = sh.getBoolean("lightOnShot", false);
      int price = sh.getInt("price");
      String descBase = sh.getString("descBase");
      String clipName = sh.getString("clipName");
      ClipConfig clipConf = clipName.isEmpty() ? null : ((ClipItem)itemMan.getExample(clipName)).getConfig();
      String reloadSoundPath = sh.getString("reloadSound");
      String shootSoundPath = sh.getString("shootSound");
      SolSound reloadSound = soundMan.getSound(reloadSoundPath, configFile);
      SolSound shootSound = soundMan.getSound(shootSoundPath, configFile);
      TextureAtlas.AtlasRegion tex = texMan.getTex("guns/" + texName, configFile);
      TextureAtlas.AtlasRegion icon = texMan.getTex(TexMan.ICONS_DIR + texName, configFile);
      int projectilesPerShot = sh.getInt("projectilesPerShot", 1);
      if (projectilesPerShot < 1) throw new AssertionError("projectiles per shot");
      boolean fixed = sh.getBoolean("fixed", false);
      GunConfig c = new GunConfig(minAngleVar, maxAngleVar, angleVarDamp, angleVarPerShot, timeBetweenShots, maxReloadTime,
        gunLength, displayName, lightOnShot, price, descBase, clipConf, shootSound, reloadSound, tex, icon, projectilesPerShot, fixed);
      itemMan.registerItem(sh.name, c.example);
    }
  }
}
