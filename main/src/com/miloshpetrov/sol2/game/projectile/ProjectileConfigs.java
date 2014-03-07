package com.miloshpetrov.sol2.game.projectile;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.miloshpetrov.sol2.*;
import com.miloshpetrov.sol2.game.DmgType;
import com.miloshpetrov.sol2.game.sound.SolSound;
import com.miloshpetrov.sol2.game.sound.SoundMan;

import java.util.HashMap;
import java.util.Map;

public class ProjectileConfigs {

  private final Map<String, ProjectileConfig> myConfigs;

  public ProjectileConfigs(TexMan texMan, SoundMan soundMan) {
    myConfigs = new HashMap<String, ProjectileConfig>();
    JsonReader r = new JsonReader();
    FileHandle configFile = SolFiles.readOnly(Const.CONFIGS_DIR + "projectiles.json");
    JsonValue parsed = r.parse(configFile);
    for (JsonValue sh : parsed) {
      String texName = "projectiles/" + sh.getString("texName");
      TextureAtlas.AtlasRegion tex = texMan.getTex(texName, configFile);
      float sz = sh.getFloat("sz");
      float spdLen = sh.getFloat("spdLen");
      boolean explode = sh.getBoolean("explode");
      float physSize = sh.getFloat("physSize");
      boolean hasFlame = sh.getBoolean("hasFlame");
      boolean smokeOnExplosion = sh.getBoolean("smokeOnExplosion");
      boolean stretch = sh.getBoolean("stretch");
      DmgType dmgType = DmgType.forName(sh.getString("dmgType"));
      String collisionSoundPath = sh.getString("collisionSound");
      SolSound collisionSound = soundMan.getSound(collisionSoundPath, configFile);
      ProjectileConfig c = new ProjectileConfig(tex, sz, spdLen, explode, stretch, physSize, hasFlame, smokeOnExplosion, dmgType, collisionSound);
      myConfigs.put(sh.name, c);
    }
  }

  public ProjectileConfig find(String name) {
    return myConfigs.get(name);
  }
}
