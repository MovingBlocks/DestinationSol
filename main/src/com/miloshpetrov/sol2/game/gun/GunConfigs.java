package com.miloshpetrov.sol2.game.gun;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.miloshpetrov.sol2.TexMan;
import com.miloshpetrov.sol2.game.item.*;
import com.miloshpetrov.sol2.game.projectile.ProjectileConfig;

public class GunConfigs {
  public static final float BULLET_W = .03f;
  public final GunConfig weakBolter;
  public final GunConfig bolter;
  public final GunConfig slowGun;
  public final GunConfig miniGun;
  public final GunConfig rocketLauncher;

  public GunConfigs(TexMan texMan, ItemMan itemMan) {

    ClipConfig bulletClipConf = ((ClipItem)itemMan.getExample("b")).getConfig();
    ClipConfig rocketClipConf = ((ClipItem)itemMan.getExample("r")).getConfig();

    TextureAtlas.AtlasRegion bolterTex = texMan.getTex("projectiles/bolter");
    ProjectileConfig weakBolterFac = new ProjectileConfig(bolterTex, .12f, 5f, true, false, 0, false, false);
    weakBolter = new GunConfig(1, 30, 6, 2, .8f, 2.5f, weakBolterFac, .12f, "bolter", "Weak Bolter", true, texMan, 15, "", 12, 2, null); //2.5

    ProjectileConfig bolterFac = new ProjectileConfig(bolterTex, .15f, 5.5f, true, false, 0, false, false);
    bolter = new GunConfig(1, 30, 6, 2, .4f, 2.5f, bolterFac, .16f, "bolter", "Bolter", true, texMan, 30, "", 15, 3, null); //7.5

    TextureAtlas.AtlasRegion bulletTex = texMan.getTex("projectiles/bullet");
    ProjectileConfig slowGunFac = new ProjectileConfig(bulletTex, BULLET_W, 8f, false, true, 0, false, false);
    slowGun = new GunConfig(1, 10, 6, 1, .2f, 1, slowGunFac, .24f, "slowGun", "Slow Gun", true, texMan, 50, "", 0, 2, bulletClipConf); //10

    ProjectileConfig miniGunFac = new ProjectileConfig(bulletTex, BULLET_W, 8f, false, true, 0, false, false);
    miniGun = new GunConfig(1, 10, 6, 1, .1f, 1, miniGunFac, .24f, "miniGun", "Minigun", true, texMan, 150, "", 0, 2, bulletClipConf); //20

    TextureAtlas.AtlasRegion rocketTex = texMan.getTex("projectiles/rocket");
    ProjectileConfig rocketFac = new ProjectileConfig(rocketTex, .15f, 4f, true, false, .1f, true, true);
    rocketLauncher = new GunConfig(1, 30, 6, 3, .4f, 2.5f, rocketFac, .2f, "rocketLauncher", "Rocket Launcher", false, texMan, 200, "", 0, 10, rocketClipConf); //25
  }
}
