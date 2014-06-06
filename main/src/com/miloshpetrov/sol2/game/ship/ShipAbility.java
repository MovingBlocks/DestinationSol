package com.miloshpetrov.sol2.game.ship;

import com.miloshpetrov.sol2.game.AbilityCommonConfig;
import com.miloshpetrov.sol2.game.SolGame;

public interface ShipAbility {
  boolean update(SolGame game, SolShip owner, boolean tryToUse);
  public AbilityConfig getConfig();
  AbilityCommonConfig getCommonConfig();
  float getRadius();
}
