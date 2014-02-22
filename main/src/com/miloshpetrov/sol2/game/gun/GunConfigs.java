package com.miloshpetrov.sol2.game.gun;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.miloshpetrov.sol2.TexMan;
import com.miloshpetrov.sol2.game.projectile.ProjectileFactory;

public class GunConfigs {
  public static final float BULLET_W = .03f;
  public final GunConfig weakBolter;
  public final GunConfig bolter;
  public final GunConfig slowGun;
  public final GunConfig miniGun;
  public final GunConfig rocketLauncher;

  public GunConfigs(TexMan texMan) {

    TextureAtlas.AtlasRegion bolterTex = texMan.getTex("projectiles/bolter");
    ProjectileFactory.BulletFactory weakBolterFac = new ProjectileFactory.BulletFactory(bolterTex, .12f, 5f, true, false, 0);
    weakBolter = new GunConfig(1, 30, 6, 2, .8f, 2.5f, weakBolterFac, .12f, "bolter", "Weak Bolter", true, texMan, 15, "", 12, 2); //2.5

    ProjectileFactory.BulletFactory bolterFac = new ProjectileFactory.BulletFactory(bolterTex, .15f, 5.5f, true, false, 0);
    bolter = new GunConfig(1, 30, 6, 2, .4f, 2.5f, bolterFac, .16f, "bolter", "Bolter", true, texMan, 30, "", 15, 3); //7.5

    TextureAtlas.AtlasRegion bulletTex = texMan.getTex("projectiles/bullet");
    ProjectileFactory.BulletFactory slowGunFac = new ProjectileFactory.BulletFactory(bulletTex, BULLET_W, 8f, false, true, 0);
    slowGun = new GunConfig(1, 10, 6, 1, .2f, 1, slowGunFac, .24f, "slowGun", "Slow Gun", true, texMan, 50, "", 0, 2); //10

    ProjectileFactory.BulletFactory miniGunFac = new ProjectileFactory.BulletFactory(bulletTex, BULLET_W, 8f, false, true, 0);
    miniGun = new GunConfig(1, 10, 6, 1, .1f, 1, miniGunFac, .24f, "miniGun", "Minigun", true, texMan, 150, "", 0, 2); //20

    TextureAtlas.AtlasRegion rocketTex = texMan.getTex("projectiles/rocket");
    ProjectileFactory.BulletFactory rocketFac = new ProjectileFactory.BulletFactory(rocketTex, .15f, 5.5f, true, false, .1f);
    rocketLauncher = new GunConfig(1, 30, 6, 3, .4f, 2.5f, rocketFac, .2f, "rocketLauncher", "Rocket Launcher", false, texMan, 200, "", 0, 10); //25
  }
}
