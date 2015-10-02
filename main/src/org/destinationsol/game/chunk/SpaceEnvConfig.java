package org.destinationsol.game.chunk;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.JsonValue;
import org.destinationsol.TextureManager;

import java.util.ArrayList;

public class SpaceEnvConfig {
  public final ArrayList<TextureAtlas.AtlasRegion> junkTexs;
  public final float junkDensity;
  public final ArrayList<TextureAtlas.AtlasRegion> farJunkTexs;
  public final float farJunkDensity;

  public SpaceEnvConfig(JsonValue json, TextureManager textureManager, FileHandle configFile) {
    String junkTexDirStr = json.getString("junkTexs");
    junkTexs = textureManager.getPack(junkTexDirStr, configFile);
    junkDensity = json.getFloat("junkDensity");
    String farJunkTexDirStr = json.getString("farJunkTexs");
    farJunkTexs = textureManager.getPack(farJunkTexDirStr, configFile);
    farJunkDensity = json.getFloat("farJunkDensity");
  }
}
