package com.miloshpetrov.sol2.game.dra;

public enum DraLevel {
  NEBULAE(11), STARS(10),
  FAR_DECO_3(2.5f), FAR_DECO_2(2f), FAR_DECO_1(1.5f),
  SPACE_DECO, ATM, DECO, PART_BG_0, U_GUNS, BIG_BODIES, BODIES, GUNS, PART_FG_0, PART_FG_1, PROJECTILES, GROUND, CLOUDS;

  public final float depth;

  DraLevel(float depth) {
    this.depth = depth;
  }

  DraLevel() {
    this(1f);
  }
}
