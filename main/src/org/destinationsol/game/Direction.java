package org.destinationsol.game;

public enum Direction {
  UP(-90), DOWN(90), LEFT(180), RIGHT(0);
  public final float angle;

  Direction(float angle) {

    this.angle = angle;
  }
}
