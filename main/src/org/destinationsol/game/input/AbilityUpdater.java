package org.destinationsol.game.input;

import org.destinationsol.common.SolMath;
import org.destinationsol.game.item.SolItem;
import org.destinationsol.game.ship.ShipAbility;
import org.destinationsol.game.ship.SolShip;

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
    if (ship.getHull().config.getMaxLife() * myAbilityUseStartPerc < ship.getLife()) return;
    SolItem ex = ability.getConfig().getChargeExample();
    if (ex != null) {
      if (ship.getItemContainer().count(ex) <= myChargesToKeep) return;
    }
    if (ability.getRadius() < nearestEnemy.getPos().dst(ship.getPos())) return;
    myAbility = true;
  }

  public boolean isAbility() {
    return myAbility;
  }
}
