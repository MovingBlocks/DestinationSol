package com.miloshpetrov.sol2.game.input;

import com.miloshpetrov.sol2.common.SolMath;
import com.miloshpetrov.sol2.game.item.SolItem;
import com.miloshpetrov.sol2.game.ship.ShipAbility;
import com.miloshpetrov.sol2.game.ship.SolShip;

public class AbilityUpdater {
  private final float myAbilityUseStartPerc;
  private final int myChargesToKeep;

  private boolean myAbility;

  public AbilityUpdater() {
    myAbilityUseStartPerc = SolMath.rnd(.3f, .7f);
    myChargesToKeep = SolMath.intRnd(1, 2);
  }

  public void update(SolShip ship, SolShip nearestEnemy) {
    myAbility = false;
    if (nearestEnemy == null) return;
    ShipAbility ability = ship.getAbility();
    if (ability == null) return;
    if (ship.getHull().config.maxLife * myAbilityUseStartPerc < ship.getLife()) return;
    SolItem ex = ability.getConfig().getChargeExample();
    if (ex != null) {
      if (ship.getItemContainer().count(ex) <= myChargesToKeep) return;
    }
    myAbility = true;
  }

  public boolean isAbility() {
    return myAbility;
  }
}
