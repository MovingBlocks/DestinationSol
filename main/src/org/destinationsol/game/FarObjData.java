package org.destinationsol.game;

public class FarObjData {
  public float delay;
  public final FarObj fo;
  public final float depth;

  public FarObjData(FarObj fo, float depth) {
    this.fo = fo;
    this.depth = depth;
  }
}
