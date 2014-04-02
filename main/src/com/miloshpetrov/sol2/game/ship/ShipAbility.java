package com.miloshpetrov.sol2.game.ship;

import com.miloshpetrov.sol2.game.SolGame;
import com.miloshpetrov.sol2.game.item.SolItem;

public interface ShipAbility {
  boolean update(SolGame game, SolShip owner, boolean tryToUse);
  public SolItem getChargeExample();
  public float getRechargeTime();

}
