package com.miloshpetrov.sol2.game.planet;

public class SystemBelt {
  private final Float myHalfWidth;
  private final float myRadius;

  public SystemBelt(Float halfWidth, float radius) {
    myHalfWidth = halfWidth;
    myRadius = radius;
  }

  public float getRadius() {
    return myRadius;
  }

  public Float getHalfWidth() {
    return myHalfWidth;
  }
}
