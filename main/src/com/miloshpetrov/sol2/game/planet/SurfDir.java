package com.miloshpetrov.sol2.game.planet;

public enum SurfDir {
  UP("u"), DOWN("d"), FWD("f");

  private final String letter;

  SurfDir(String letter) {
    this.letter = letter;
  }

  public String getLetter() {
    return letter;
  }
}
