package com.miloshpetrov.sol2.game.ship;

import com.miloshpetrov.sol2.game.item.SolItem;

public interface AbilityConfig {
  public ShipAbility build();
  public SolItem getChargeExample();
  public float getRechargeTime();
  void appendDesc(StringBuilder sb);
}
