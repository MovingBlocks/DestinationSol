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
import org.destinationsol.common.SolMath;
import org.destinationsol.game.SolGame;
import org.destinationsol.game.planet.Planet;
import org.destinationsol.game.ship.SolShip;
import org.destinationsol.game.ship.hulls.HullConfig;

/**
 * Flies in the planet orbit
 */
public class OrbiterDestProvider implements MoveDestProvider {
    private final Planet myPlanet;
    private final float myDesiredSpeed;
    private final float myHeight;
    private final boolean myCw;
    private final Vector2 myDest;

    public OrbiterDestProvider(Planet planet, float height, boolean cw) {
        myPlanet = planet;
        myHeight = height;
        myCw = cw;
        myDesiredSpeed = SolMath.sqrt(myPlanet.getGravitationConstant() / myHeight);
        myDest = new Vector2();
    }

    @Override
    public Vector2 getDestination() {
        return myDest;
    }

    @Override
    public boolean shouldAvoidBigObjects() {
        return false;
    }

    @Override
    public float getDesiredSpeed() {
        return myDesiredSpeed;
    }

    @Override
    public boolean shouldStopNearDestination() {
        return false;
    }

    @Override
    public void update(SolGame game, Vector2 shipPos, float maxIdleDist, HullConfig hullConfig, SolShip nearestEnemy) {
        Vector2 pPos = myPlanet.getPosition();
        float destAngle = SolMath.angle(pPos, shipPos) + 5 * SolMath.toInt(myCw);
        SolMath.fromAl(myDest, destAngle, myHeight);
        myDest.add(pPos);
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
