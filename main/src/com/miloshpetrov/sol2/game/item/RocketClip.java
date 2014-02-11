package com.miloshpetrov.sol2.game.item;


public class RocketClip implements SolItem {
  public static final int AMMO_PER_CLIP = 6;
  public static final String DESC = "A clip of " + AMMO_PER_CLIP + " rockets";
  public static final String TEX_NAME = "rocketClip";
  public static final RocketClip EXAMPLE = new RocketClip();

  private RocketClip() {
  }

  @Override
  public String getTexName() {
    return TEX_NAME;
  }

  @Override
  public String getDisplayName() {
    return "Rockets";
  }

  @Override
  public float getPrice() {
    return 70;
  }

  @Override
  public String getDesc() {
    return DESC;
  }

  @Override
  public SolItem copy() {
    return new RocketClip();
  }

  @Override
  public boolean isSame(SolItem item) {
    return item instanceof RocketClip;
  }
}
