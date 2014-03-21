package com.miloshpetrov.sol2.game.dra;

public enum DraLevel {
  NEBULAE(11), STARS(10),
  FAR_BG_3(2.5f), FAR_BG_2(2f), FAR_BG_1(1.5f),
  ATM, JUNK, DECO, PART_BG_0, PART_BG_1, BIG_BODIES, BODIES, GUNS, PART_FG_0, PROJECTILES, GROUND, CLOUDS;

  public final float depth;

  DraLevel(float depth) {
    this.depth = depth;
  }

  DraLevel() {
    this(1f);
  }
}
