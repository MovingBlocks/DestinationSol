package com.miloshpetrov.sol2.game.gun;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.miloshpetrov.sol2.TexMan;
import com.miloshpetrov.sol2.game.item.*;
import com.miloshpetrov.sol2.game.projectile.ProjectileConfig;

public class GunConfig {
  public final float minAngleVar;
  public final float maxAngleVar;
  public final float angleVarDamp;
  public final float angleVarPerShot;
  public final float timeBetweenShots;
  public final float maxReloadTime;
  public final float gunLength;
  public final String displayName;
  public final ProjectileConfig projConfig;
  public final TextureAtlas.AtlasRegion tex;
  public final boolean lightOnShot;
  public final String itemTexName;
  public final int price;
  public final String desc;
  public final int infiniteClipSize;
  public final float dmg;
  public final float dps;
  public final GunItem example;
  public final ClipConfig clipConf;

  public GunConfig(float minAngleVar, float maxAngleVar, float angleVarDamp, float angleVarPerShot,
    float timeBetweenShots,
    float maxReloadTime, ProjectileConfig projConfig, float gunLength, String texName, String displayName,
    boolean lightOnShot, TexMan texMan, int price, String descBase, int infiniteClipSize, float dmg,
    ClipConfig clipConf) {

    tex = texMan.getTex("guns/" + texName);

    this.dmg = dmg;
    this.maxAngleVar = maxAngleVar;
    this.minAngleVar = minAngleVar;
    this.angleVarDamp = angleVarDamp;
    this.angleVarPerShot = angleVarPerShot;
    this.timeBetweenShots = timeBetweenShots;
    this.maxReloadTime = maxReloadTime;
    this.projConfig = projConfig;
    this.gunLength = gunLength;
    this.displayName = displayName;
    this.lightOnShot = lightOnShot;
    this.itemTexName = texName;
    this.price = price;
    this.infiniteClipSize = infiniteClipSize;
    this.clipConf = clipConf;

    dps = dmg / timeBetweenShots;
    this.desc = makeDesc(descBase);
    example = new GunItem(this, 0, 0);
  }

  private String makeDesc(String descBase) {
    StringBuilder sb = new StringBuilder(descBase);
    sb.append("\nDamage: ").append(dps).append("/s");
    sb.append("\nReload: ").append(maxReloadTime).append("s");
    if (infiniteClipSize != 0) {
      sb.append("\nInfinite ammo");
    }
    return sb.toString();
  }

  public static void load(TexMan texMan, ItemMan itemMan) {

    // load here
    ClipConfig bulletClipConf = ((ClipItem)itemMan.getExample("b")).getConfig();
    ClipConfig rocketClipConf = ((ClipItem)itemMan.getExample("r")).getConfig();

    ProjectileConfig weakBolterFac = itemMan.projConfigs.find("weakBolter");
    GunConfig weakBolter = new GunConfig(1, 30, 6, 2, .8f, 2.5f, weakBolterFac, .12f, "bolter", "Weak Bolter", true, texMan, 15, "", 12, 2, null);

    ProjectileConfig bolterFac = itemMan.projConfigs.find("bolter");
    GunConfig bolter = new GunConfig(1, 30, 6, 2, .4f, 2.5f, bolterFac, .16f, "bolter", "Bolter", true, texMan, 30, "", 15, 3, null);

    ProjectileConfig slowGunFac = itemMan.projConfigs.find("slowGun");
    GunConfig slowGun = new GunConfig(1, 10, 6, 1, .2f, 1, slowGunFac, .24f, "slowGun", "Slow Gun", true, texMan, 50, "", 0, 2, bulletClipConf);

    ProjectileConfig miniGunFac = itemMan.projConfigs.find("miniGun");
    GunConfig miniGun = new GunConfig(1, 10, 6, 1, .1f, 1, miniGunFac, .24f, "miniGun", "Minigun", true, texMan, 150, "", 0, 2, bulletClipConf);

    ProjectileConfig rocketFac = itemMan.projConfigs.find("rocket");
    GunConfig rocketLauncher = new GunConfig(1, 30, 6, 3, .4f, 2.5f, rocketFac, .2f, "rocketLauncher", "Rocket Launcher", false, texMan, 200, "", 0, 10, rocketClipConf);

    itemMan.registerItem("wbo", weakBolter.example);
    itemMan.registerItem("bo", bolter.example);
    itemMan.registerItem("sg", slowGun.example);
    itemMan.registerItem("mg", miniGun.example);
    itemMan.registerItem("rl", rocketLauncher.example);
  }
}
