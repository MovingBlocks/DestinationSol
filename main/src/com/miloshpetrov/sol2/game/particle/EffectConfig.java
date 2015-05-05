package com.miloshpetrov.sol2.game.particle;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.JsonValue;
import com.miloshpetrov.sol2.TextureManager;
import com.miloshpetrov.sol2.game.GameColors;

import java.util.ArrayList;
import java.util.List;

public class EffectConfig {
  public final EffectType effectType;
  public final float sz;
  public final TextureAtlas.AtlasRegion tex;
  public final boolean floatsUp;
  public final Color tint;

  public EffectConfig(EffectType effectType, float sz, TextureAtlas.AtlasRegion tex, boolean floatsUp, Color tint) {
    this.effectType = effectType;
    this.sz = sz;
    this.tex = tex;
    this.floatsUp = floatsUp;
    this.tint = tint;
  }

  public static EffectConfig load(JsonValue node, EffectTypes types, TextureManager textureManager, FileHandle configFile,
    GameColors cols) {
    if (node == null) return null;
    String effectFileName = node.getString("effectFile");
    EffectType effectType = types.forName(effectFileName);
    float sz = node.getFloat("size", 0);
    String texName = node.getString("tex");
    boolean floatsUp = node.getBoolean("floatsUp", false);
    Color tint = cols.load(node.getString("tint"));
    TextureAtlas.AtlasRegion tex = textureManager.getTex("smallGameObjs/particles/" + texName, configFile);
    return new EffectConfig(effectType, sz, tex, floatsUp, tint);
  }

  public static List<EffectConfig> loadList(JsonValue listNode, EffectTypes types, TextureManager textureManager, FileHandle configFile,
    GameColors cols) {
    ArrayList<EffectConfig> res = new ArrayList<EffectConfig>();
    for (JsonValue node : listNode) {
      EffectConfig ec = load(node, types, textureManager, configFile, cols);
      res.add(ec);
    }
    return res;
  }

}
