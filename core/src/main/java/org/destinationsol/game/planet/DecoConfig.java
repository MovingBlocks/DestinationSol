package org.destinationsol.game.planet;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.JsonValue;
import org.destinationsol.TextureManager;
import org.destinationsol.common.SolMath;

import java.util.ArrayList;
import java.util.List;

public class DecoConfig {
  public final float density;
  public final float szMin;
  public final float szMax;
  public final Vector2 orig;
  public final boolean allowFlip;
  public final List<TextureAtlas.AtlasRegion> texs;

  public DecoConfig(float density, float szMin, float szMax, Vector2 orig, boolean allowFlip,
    List<TextureAtlas.AtlasRegion> texs) {
    this.density = density;
    this.szMin = szMin;
    this.szMax = szMax;
    this.orig = orig;
    this.allowFlip = allowFlip;
    this.texs = texs;
  }

  static List<DecoConfig> load(JsonValue planetConfig, TextureManager textureManager, FileHandle configFile) {
    ArrayList<DecoConfig> res = new ArrayList<DecoConfig>();
    for (JsonValue deco : planetConfig.get("deco")) {
      float density = deco.getFloat("density");
      float szMin = deco.getFloat("szMin");
      float szMax = deco.getFloat("szMax");
      Vector2 orig = SolMath.readV2(deco, "orig");
      boolean allowFlip = deco.getBoolean("allowFlip");
      String texName = planetConfig.getString("decoTexs") + "/" + deco.name;
      ArrayList<TextureAtlas.AtlasRegion> texs = textureManager.getPack(texName, configFile);
      DecoConfig c = new DecoConfig(density, szMin, szMax, orig, allowFlip, texs);
      res.add(c);
    }
    return res;
  }
}
