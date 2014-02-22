package com.miloshpetrov.sol2.game.projectile;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.miloshpetrov.sol2.TexMan;

import java.util.HashMap;
import java.util.Map;

public class ProjectileConfigs {

  private final Map<String, ProjectileConfig> myConfigs;

  public ProjectileConfigs(TexMan texMan) {
    myConfigs = new HashMap<String, ProjectileConfig>();

    // load here
    TextureAtlas.AtlasRegion bolterTex = texMan.getTex("projectiles/bolter");
    ProjectileConfig weakBolterFac = new ProjectileConfig(bolterTex, .12f, 5f, true, false, 0, false, false);
    myConfigs.put("weakBolter", weakBolterFac);
    ProjectileConfig bolterFac = new ProjectileConfig(bolterTex, .15f, 5.5f, true, false, 0, false, false);
    myConfigs.put("bolter", bolterFac);
    TextureAtlas.AtlasRegion bulletTex = texMan.getTex("projectiles/bullet");
    ProjectileConfig slowGunFac = new ProjectileConfig(bulletTex, .03f, 8f, false, true, 0, false, false);
    myConfigs.put("slowGun", slowGunFac);
    ProjectileConfig miniGunFac = new ProjectileConfig(bulletTex, .03f, 8f, false, true, 0, false, false);
    myConfigs.put("miniGun", miniGunFac);
    TextureAtlas.AtlasRegion rocketTex = texMan.getTex("projectiles/rocket");
    ProjectileConfig rocketFac = new ProjectileConfig(rocketTex, .15f, 4f, true, false, .1f, true, true);
    myConfigs.put("rocket", rocketFac);
  }

  public ProjectileConfig find(String name) {
    return myConfigs.get(name);
  }
}
