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

package org.destinationsol.game.input;

import org.destinationsol.common.SolMath;
import org.destinationsol.game.Faction;
import org.destinationsol.game.SolGame;
import org.destinationsol.game.ship.FarShip;
import org.destinationsol.game.ship.SolShip;

public interface Pilot {
    void update(SolGame game, SolShip ship, SolShip nearestEnemy);

    /**
     * Returns the throttle applied to the engine.
     */
    float getThrottle();

    /**
     * Returns the angle at which to orient the ship.
     */
    float getOrientation();

    boolean isShoot();

    boolean isShoot2();

    boolean collectsItems();

    boolean isAbility();

    Faction getFaction();

    boolean shootsAtObstacles();

    float getDetectionDist();

    String getMapHint();

    void updateFar(SolGame game, FarShip farShip);

    String toDebugString();

    boolean isPlayer();

    public static final class Utils {
        public static boolean isIdle(Pilot p) {
            return !(p.getThrottle() != 0 || p.isShoot() || p.isShoot2() || p.isAbility());
        }
    }
}
