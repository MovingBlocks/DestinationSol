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

import com.badlogic.gdx.math.Vector2;
import org.destinationsol.game.SolGame;
import org.destinationsol.game.ship.SolShip;
import org.destinationsol.game.ship.hulls.HullConfig;

public interface MoveDestProvider {
    Vector2 getDest();

    boolean shouldAvoidBigObjs();

    /**
     * @return the desired spd lenght both for peaceful movement and for maneuvering
     */
    float getDesiredSpdLen();

    boolean shouldStopNearDest();

    void update(SolGame game, Vector2 shipPos, float maxIdleDist, HullConfig hullConfig, SolShip nearestEnemy);

    /**
     * if true is returned, the ship will move in battle pattern around the enemy and try to face enemy with guns
     * if false is returned, the ship will try to avoid projectiles or fly away from enemy (not implemented yet!)
     * if null is returned, the ship will move as if there's no enemy near
     * note that the ship will always shoot if there's enemy ahead of it (or if it has unfixed gun)
     */
    Boolean shouldManeuver(boolean canShoot, SolShip nearestEnemy, boolean nearGround);

    Vector2 getDestSpd();
}
