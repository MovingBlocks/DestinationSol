package com.miloshpetrov.sol2.game.chunk;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.JsonValue;
import com.miloshpetrov.sol2.TexMan;
import com.miloshpetrov.sol2.game.ShipConfig;
import com.miloshpetrov.sol2.game.ship.HullConfigs;

import java.util.ArrayList;

public class SpaceObjConfig {
  public final ArrayList<TextureAtlas.AtlasRegion> junkTexs;
  public final float junkDensity;
  public final ArrayList<TextureAtlas.AtlasRegion> farJunkTexs;
  public final float farJunkDensity;
  public final ArrayList<ShipConfig> enemies;

  public SpaceObjConfig(JsonValue json, HullConfigs hullConfigs, TexMan texMan, FileHandle configFile) {
    String junkTexDirStr = json.getString("junkTexs");
    junkTexs = texMan.getPack(junkTexDirStr, configFile);
    junkDensity = json.getFloat("junkDensity");
    String farJunkTexDirStr = json.getString("farJunkTexs");
    farJunkTexs = texMan.getPack(farJunkTexDirStr, configFile);
    farJunkDensity = json.getFloat("farJunkDensity");
    JsonValue enemiesJson = json.get("enemies");
    enemies = ShipConfig.loadList(enemiesJson, hullConfigs);
  }
}
