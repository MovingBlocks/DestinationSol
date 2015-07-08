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
    if (myRepairPoints > 0 && life < config.getMaxLife()) {
      float inc = REPAIR_SPD * ts;
      if (myRepairPoints < inc) inc = myRepairPoints;
      myRepairPoints -= inc;
      return SolMath.approach(life, inc, config.getMaxLife());
    }
    return 0;
  }

  public float getRepairPoints() {
    return myRepairPoints;
  }
}
