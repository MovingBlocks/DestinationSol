package org.destinationsol.game;

public enum Faction {
  LAANI("laani"), EHAR("ehar");
  private final String myName;

  Faction(String name) {
    myName = name;
  }

  public String getName() {
    return myName;
  }
}
