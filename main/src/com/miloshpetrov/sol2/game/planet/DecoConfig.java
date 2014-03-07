package com.miloshpetrov.sol2.game.planet;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;

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
}
