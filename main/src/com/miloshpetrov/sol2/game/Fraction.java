package com.miloshpetrov.sol2.game;

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
