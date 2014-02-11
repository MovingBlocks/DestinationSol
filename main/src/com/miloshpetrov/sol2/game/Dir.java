package com.miloshpetrov.sol2.game;

public enum Dir {
  UP(-90), DOWN(90), LEFT(180), RIGHT(0);
  public final float angle;

  Dir(float angle) {

    this.angle = angle;
  }
}
