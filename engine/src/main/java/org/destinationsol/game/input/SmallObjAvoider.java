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
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import com.badlogic.gdx.physics.box2d.World;
import org.destinationsol.common.SolMath;
import org.destinationsol.game.SolGame;
import org.destinationsol.game.SolObject;
import org.destinationsol.game.planet.Planet;
import org.destinationsol.game.ship.SolShip;

public class SmallObjAvoider {
    public static final float MANEUVER_TIME = 2f;
    public static final float MIN_RAYCAST_LEN = .5f;
    private final RayCastCallback myRayBack;
    private final Vector2 myDest;
    private SolShip myShip;
    private boolean myCollided;

    public SmallObjAvoider() {
        myRayBack = new MyRayBack();
        myDest = new Vector2();
    }

    public float avoid(SolGame game, SolShip ship, float toDestAngle, Planet np) {
        myShip = ship;
        Vector2 shipPos = ship.getPosition();
        float shipSpeed = ship.getVelocity().len();
        float ttt = ship.calcTimeToTurn(toDestAngle + 45);
        float raycastLen = shipSpeed * (ttt + MANEUVER_TIME);
        if (raycastLen < MIN_RAYCAST_LEN) {
            raycastLen = MIN_RAYCAST_LEN;
        }

        SolMath.fromAl(myDest, toDestAngle, raycastLen);
        myDest.add(shipPos);
        myCollided = false;
        World w = game.getObjectManager().getWorld();
        w.rayCast(myRayBack, shipPos, myDest);
        if (!myCollided) {
            return toDestAngle;
        }

        toDestAngle += 45;
        SolMath.fromAl(myDest, toDestAngle, raycastLen);
        myDest.add(shipPos);
        myCollided = false;
        w.rayCast(myRayBack, shipPos, myDest);
        if (!myCollided) {
            return toDestAngle;
        }

        toDestAngle -= 90;
        SolMath.fromAl(myDest, toDestAngle, raycastLen);
        myDest.add(shipPos);
        myCollided = false;
        w.rayCast(myRayBack, shipPos, myDest);
        if (!myCollided) {
            return toDestAngle;
        }

        if (np.getFullHeight() < np.getPosition().dst(shipPos)) {
            return toDestAngle - 45;
        }
        return SolMath.angle(np.getPosition(), shipPos);
    }

    private class MyRayBack implements RayCastCallback {
        @Override
        public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal, float fraction) {
            SolObject o = (SolObject) fixture.getBody().getUserData();
            if (myShip == o) {
                return -1;
            }
            myCollided = true;
            return 0;
        }
    }
}
