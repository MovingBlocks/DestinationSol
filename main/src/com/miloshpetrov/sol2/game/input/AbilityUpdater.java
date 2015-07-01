/*
 * Copyright 2015 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
 
 package com.miloshpetrov.sol2.game.input;

import com.miloshpetrov.sol2.common.SolMath;
import com.miloshpetrov.sol2.game.item.SolItem;
import com.miloshpetrov.sol2.game.ship.*;

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
    if (ability.getRadius() < nearestEnemy.getPos().dst(ship.getPos())) return;
    myAbility = true;
  }

  public boolean isAbility() {
    return myAbility;
  }
}
