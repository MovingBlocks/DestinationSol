package com.miloshpetrov.sol2.game.projectile;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.miloshpetrov.sol2.SolFiles;
import com.miloshpetrov.sol2.TexMan;

import java.util.HashMap;
import java.util.Map;

public class ProjectileConfigs {

  private final Map<String, ProjectileConfig> myConfigs;

  public ProjectileConfigs(TexMan texMan) {
    myConfigs = new HashMap<String, ProjectileConfig>();
    JsonReader r = new JsonReader();
    JsonValue parsed = r.parse(SolFiles.readOnly("res/configs/projectiles.json"));
    for (JsonValue sh : parsed) {
      String texName = "projectiles/" + sh.getString("texName");
      TextureAtlas.AtlasRegion tex = texMan.getTex(texName);
      float sz = sh.getFloat("sz");
      float spdLen = sh.getFloat("spdLen");
      boolean explode = sh.getBoolean("explode");
      float physSize = sh.getFloat("physSize");
      boolean hasFlame = sh.getBoolean("hasFlame");
      boolean smokeOnExplosion = sh.getBoolean("smokeOnExplosion");
      boolean stretch = sh.getBoolean("stretch");
      ProjectileConfig c = new ProjectileConfig(tex, sz, spdLen, explode, stretch, physSize, hasFlame, smokeOnExplosion);
      myConfigs.put(sh.name, c);
    }
  }

  public ProjectileConfig find(String name) {
    return myConfigs.get(name);
  }
}
