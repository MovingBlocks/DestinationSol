package org.destinationsol.game.planet;

public enum SurfaceDirection {
  UP("u"), DOWN("d"), FWD("f");

  private final String letter;

  SurfaceDirection(String letter) {
    this.letter = letter;
  }

  public String getLetter() {
    return letter;
  }
}
