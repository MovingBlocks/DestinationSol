package com.miloshpetrov.sol2.game.item;

public interface SolItem {
  String getTexName();
  String getDisplayName();
  float getPrice();
  String getDesc();
  SolItem copy();
  boolean isSame(SolItem item);

}
