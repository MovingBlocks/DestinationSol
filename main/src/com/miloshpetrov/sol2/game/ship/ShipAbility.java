package com.miloshpetrov.sol2.game.ship;

import com.miloshpetrov.sol2.game.SolGame;
import com.miloshpetrov.sol2.game.item.SolItem;

public interface ShipAbility {
  boolean update(SolGame game, boolean tryToUse);
  public SolItem getAmmoExample();
  public float getRechargeTime();
}
