package org.destinationsol.game;

public enum Fraction {
  LAANI("laani"), EHAR("ehar");
  private final String myName;

  Fraction(String name) {
    myName = name;
  }

  public String getName() {
    return myName;
  }
}
