package org.destinationsol.game.ship;

import org.destinationsol.game.item.SolItem;

public interface AbilityConfig {
  public ShipAbility build();
  public SolItem getChargeExample();
  public float getRechargeTime();
  void appendDesc(StringBuilder sb);
}
