package com.miloshpetrov.sol2.game.item;

public class BulletClip implements SolItem {
  public static final int AMMO_PER_CLIP = 60;
  public static final String DESC = "A clip of " + AMMO_PER_CLIP + " bullets";
  public static final String TEX_NAME = "bulletClip";
  public static final BulletClip EXAMPLE = new BulletClip();

  private BulletClip() {
  }

  @Override
  public String getTexName() {
    return TEX_NAME;
  }

  @Override
  public String getDisplayName() {
    return "Bullets";
  }

  @Override
  public float getPrice() {
    return 30;
  }

  @Override
  public String getDesc() {
    return DESC;
  }

  @Override
  public SolItem copy() {
    return new BulletClip();
  }

  @Override
  public boolean isSame(SolItem item) {
    return item instanceof BulletClip;
  }
}
