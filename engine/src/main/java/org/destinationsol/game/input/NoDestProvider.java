/*
 * Copyright 2018 MovingBlocks
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

package org.destinationsol.game.input;

import com.badlogic.gdx.math.Vector2;
import org.destinationsol.Const;
import org.destinationsol.game.SolGame;
import org.destinationsol.game.ship.SolShip;
import org.destinationsol.game.ship.hulls.HullConfig;

/**
 * Just stays wherever it is, but maneuvers
 */
public class NoDestProvider implements MoveDestProvider {

    public NoDestProvider() {
    }

    @Override
    public Vector2 getDestination() {
        return null;
    }

    @Override
    public boolean shouldAvoidBigObjects() {
        return false;
    }

    @Override
    public float getDesiredSpeed() {
        return Const.DEFAULT_AI_SPD;
    }

    @Override
    public boolean shouldStopNearDestination() {
        return false;
    }

    @Override
    public void update(SolGame game, Vector2 shipPos, float maxIdleDist, HullConfig hullConfig, SolShip nearestEnemy) {
    }

    @Override
    public Boolean shouldManeuver(boolean canShoot, SolShip nearestEnemy, boolean nearGround) {
        return null;
    }

    @Override
    public Vector2 getDestinationVelocity() {
        return Vector2.Zero;
    }
}
