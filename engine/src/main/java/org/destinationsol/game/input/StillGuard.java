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
import org.destinationsol.common.SolMath;
import org.destinationsol.game.ShipConfig;
import org.destinationsol.game.SolGame;
import org.destinationsol.game.planet.PlanetBind;
import org.destinationsol.game.ship.SolShip;
import org.destinationsol.game.ship.hulls.HullConfig;

public class StillGuard implements MoveDestProvider {

    private final PlanetBind myPlanetBind;
    private final float myDesiredSpeed;
    private Vector2 myDest;
    private Vector2 myDestVelocity;

    public StillGuard(Vector2 target, SolGame game, ShipConfig sc) {
        myDest = new Vector2(target);
        myPlanetBind = PlanetBind.tryBind(game, myDest, 0);
        myDesiredSpeed = sc.hull.getType() == HullConfig.Type.BIG ? Const.BIG_AI_SPD : Const.DEFAULT_AI_SPD;
        myDestVelocity = new Vector2();
    }

    @Override
    public Vector2 getDestination() {
        return myDest;
    }

    @Override
    public boolean shouldAvoidBigObjects() {
        return myPlanetBind != null;
    }

    @Override
    public float getDesiredSpeed() {
        return myDesiredSpeed;
    }

    @Override
    public boolean shouldStopNearDestination() {
        return true;
    }

    @Override
    public void update(SolGame game, Vector2 shipPos, float maxIdleDist, HullConfig hullConfig, SolShip nearestEnemy) {
        if (myPlanetBind != null) {
            Vector2 diff = SolMath.getVec();
            myPlanetBind.setDiff(diff, myDest, false);
            myDest.add(diff);
            SolMath.free(diff);
            myPlanetBind.getPlanet().calculateVelocityAtPosition(myDestVelocity, myDest);
        }
    }

    @Override
    public Boolean shouldManeuver(boolean canShoot, SolShip nearestEnemy, boolean nearGround) {
        return true;
    }

    @Override
    public Vector2 getDestinationVelocity() {
        return myDestVelocity;
    }
}
