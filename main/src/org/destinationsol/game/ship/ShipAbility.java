package org.destinationsol.game.ship;

import org.destinationsol.game.AbilityCommonConfig;
import org.destinationsol.game.SolGame;

public interface ShipAbility {
  boolean update(SolGame game, SolShip owner, boolean tryToUse);
  public AbilityConfig getConfig();
  AbilityCommonConfig getCommonConfig();
  float getRadius();
}
