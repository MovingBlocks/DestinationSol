package com.miloshpetrov.sol2.game;

public enum DmgType {
  BULLET("bullet"), ENERGY("energy"), EXPLOSION("explosion"), CRASH("crash"), FIRE("fire");
  private final String myName;

  DmgType(String name) {
    myName = name;
  }

  public static DmgType forName(String name) {
    for (DmgType dt : DmgType.values()) {
      if (dt.myName.equals(name)) return dt;
    }
    throw new AssertionError("no dmg type for name " + name);
  }
}
