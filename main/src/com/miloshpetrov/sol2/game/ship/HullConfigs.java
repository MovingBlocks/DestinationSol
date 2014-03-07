package com.miloshpetrov.sol2.game.ship;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.miloshpetrov.sol2.*;
import com.miloshpetrov.sol2.common.SolMath;

import java.util.ArrayList;
import java.util.HashMap;

public class HullConfigs {
  private final HashMap<String,HullConfig> myConfigs;

  public HullConfigs(ShipBuilder shipBuilder, TexMan texMan) {
    myConfigs = new HashMap<String, HullConfig>();

    JsonReader r = new JsonReader();
    FileHandle configFile = SolFiles.readOnly(Const.CONFIGS_DIR + "hulls.json");
    JsonValue parsed = r.parse(configFile);
    for (JsonValue sh : parsed) {
      String texName = sh.getString("texName");
      float size = sh.getFloat("size");
      int maxLife = sh.getInt("maxLife");
      Vector2 e1Pos = SolMath.readV2(sh, "e1Pos");
      Vector2 e2Pos = SolMath.readV2(sh, "e2Pos");
      Vector2 g1Pos = SolMath.readV2(sh, "g1Pos");
      Vector2 g2Pos = SolMath.readV2(sh, "g2Pos");
      ArrayList<Vector2> lightSrcPoss = SolMath.readV2List(sh, "lightSrcPoss");
      float durability = sh.getFloat("durability");
      boolean hasBase = sh.getBoolean("hasBase");
      ArrayList<Vector2> forceBeaconPoss = SolMath.readV2List(sh, "forceBeaconPoss");
      ArrayList<Vector2> doorPoss = SolMath.readV2List(sh, "doorPoss");
      HullConfig.Type type = HullConfig.Type.forValue(sh.getString("type"));
      TextureAtlas.AtlasRegion icon = texMan.getTex(TexMan.ICONS_DIR + texName);
      HullConfig c = new HullConfig(texName, size, maxLife, e1Pos, e2Pos, g1Pos, g2Pos, lightSrcPoss, durability,
        hasBase, forceBeaconPoss, doorPoss, type, icon);
      process(c, shipBuilder);
      myConfigs.put(sh.name, c);
    }
  }

  public HullConfig getConfig(String name) {
    return myConfigs.get(name);
  }

  private void process(HullConfig config, ShipBuilder shipBuilder) {
    Vector2 o = shipBuilder.getOrigin(config.texName);
    config.g1Pos.sub(o).scl(config.size);
    config.g2Pos.sub(o).scl(config.size);
    config.e1Pos.sub(o).scl(config.size);
    config.e2Pos.sub(o).scl(config.size);
    for (Vector2 pos : config.lightSrcPoss) pos.sub(o).scl(config.size);
    for (Vector2 pos : config.forceBeaconPoss) pos.sub(o).scl(config.size);
    for (Vector2 pos : config.doorPoss) pos.sub(o).scl(config.size);
  }
}
