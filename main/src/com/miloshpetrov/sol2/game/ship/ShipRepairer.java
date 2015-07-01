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
 
 package com.miloshpetrov.sol2.game.ship;

import com.miloshpetrov.sol2.common.SolMath;
import com.miloshpetrov.sol2.game.SolGame;
import com.miloshpetrov.sol2.game.item.ItemContainer;
import com.miloshpetrov.sol2.game.item.RepairItem;

public class ShipRepairer {
  public static final float REPAIR_AWAIT = 2f;
  private static final float REPAIR_SPD = 5;
  private float myRepairPoints;

  public ShipRepairer() {
  }

  public float tryRepair(SolGame game, ItemContainer ic, float life, HullConfig config) {
    // Don't attempt to repair if already at full health
    if (life == config.maxLife){
      return 0;
    }

    float ts = game.getTimeStep();
    if (myRepairPoints <= 0 && ic.tryConsumeItem(game.getItemMan().getRepairExample())) {
      myRepairPoints = RepairItem.LIFE_AMT;
    }
    if (myRepairPoints > 0 && life < config.maxLife) {
      float inc = REPAIR_SPD * ts;
      if (myRepairPoints < inc) inc = myRepairPoints;
      myRepairPoints -= inc;
      return SolMath.approach(life, inc, config.maxLife);
    }
    return 0;
  }

  public float getRepairPoints() {
    return myRepairPoints;
  }
}
