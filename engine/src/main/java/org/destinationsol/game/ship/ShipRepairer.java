/*
 * Copyright 2017 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.destinationsol.game.ship;

import org.destinationsol.common.SolMath;
import org.destinationsol.game.SolGame;
import org.destinationsol.game.item.ItemContainer;
import org.destinationsol.game.item.RepairItem;
import org.destinationsol.game.ship.hulls.HullConfig;

public class ShipRepairer {
    public static final float REPAIR_AWAIT = 2f;
    private static final float REPAIR_SPD = 5;
    private float myRepairPoints;

    public ShipRepairer() {
    }

    public float tryRepair(SolGame game, ItemContainer ic, float life, HullConfig config) {
        // Don't attempt to repair if already at full health
        if (life == config.getMaxLife()) {
            return 0;
        }

        float ts = game.getTimeStep();
        if (myRepairPoints <= 0 && ic.tryConsumeItem(game.getItemMan().getRepairExample())) {
            myRepairPoints = RepairItem.LIFE_AMT;
        }
        if (myRepairPoints > 0 && life < config.getMaxLife()) {
            float inc = REPAIR_SPD * ts;
            if (myRepairPoints < inc) {
                inc = myRepairPoints;
            }
            myRepairPoints -= inc;
            return SolMath.approach(life, inc, config.getMaxLife());
        }
        return 0;
    }

    public float getRepairPoints() {
        return myRepairPoints;
    }
}
