package com.miloshpetrov.sol2.game.planet;

import com.badlogic.gdx.math.Vector2;

public class DecoConfig {
  public final String texName;
  public final float density;
  public final float szMin;
  public final float szMax;
  public final Vector2 orig;
  public final boolean allowFlip;

  public DecoConfig(String texName, float density, float szMin, float szMax, Vector2 orig, boolean allowFlip) {
    this.texName = texName;
    this.density = density;
    this.szMin = szMin;
    this.szMax = szMax;
    this.orig = orig;
    this.allowFlip = allowFlip;
  }
}
