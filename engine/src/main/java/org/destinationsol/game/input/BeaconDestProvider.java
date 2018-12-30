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
import org.destinationsol.game.BeaconHandler;
import org.destinationsol.game.SolGame;
import org.destinationsol.game.ship.SolShip;
import org.destinationsol.game.ship.hulls.HullConfig;

public class BeaconDestProvider implements MoveDestProvider {
    public static final float STOP_AWAIT = .1f;
    private final Vector2 myDest;

    private Boolean myShouldManeuver;
    private boolean myShouldStopNearDest;
    private Vector2 myDestVelocity;

    public BeaconDestProvider() {
        myDest = new Vector2();
        myDestVelocity = new Vector2();
    }

    @Override
    public void update(SolGame game, Vector2 shipPos, float maxIdleDist, HullConfig hullConfig, SolShip nearestEnemy) {
        BeaconHandler bh = game.getBeaconHandler();
        myDest.set(bh.getPos());
        myShouldManeuver = null;
        BeaconHandler.Action a = bh.getCurrAction();
        if (nearestEnemy != null && a == BeaconHandler.Action.ATTACK) {
            if (shipPos.dst(myDest) < shipPos.dst(nearestEnemy.getPosition()) + .1f) {
                myShouldManeuver = true;
            }
        }
        myShouldStopNearDest = STOP_AWAIT < game.getTime() - bh.getClickTime();
        myDestVelocity.set(bh.getVelocity());
    }

    @Override
    public Vector2 getDestination() {
        return myDest;
    }

    @Override
    public Boolean shouldManeuver(boolean canShoot, SolShip nearestEnemy, boolean nearGround) {
        return myShouldManeuver;
    }

    @Override
    public Vector2 getDestinationVelocity() {
        return myDestVelocity;
    }

    @Override
    public boolean shouldAvoidBigObjects() {
        return true;
    }

    @Override
    public float getDesiredSpeed() {
        return Const.MAX_MOVE_SPD;
    }

    @Override
    public boolean shouldStopNearDestination() {
        return myShouldStopNearDest;
    }
}
