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
import org.destinationsol.game.SolGame;
import org.destinationsol.game.planet.Planet;
import org.destinationsol.game.ship.SolShip;

public class Mover {
    private static final float MIN_MOVE_AAD = 2f;
    private static final float MIN_ANGLE_TO_ACC = 5f;
    private static final float MIN_PLANET_MOVE_AAD = 2f;
    private static final float MAX_ABS_SPD_DEV = .1f;
    private static final float MAX_REL_SPD_DEV = .05f;
    private final BigObjAvoider myBigObjAvoider;
    private final SmallObjAvoider mySmallObjAvoider;
    private boolean myUp;
    private boolean myLeft;
    private boolean myRight;
    private Vector2 myDesiredVelocity;

    Mover() {
        myBigObjAvoider = new BigObjAvoider();
        mySmallObjAvoider = new SmallObjAvoider();
        myDesiredVelocity = new Vector2();
    }

    public static Boolean needsToTurn(float angle, float destAngle, float rotationSpeed, float rotAcc, float allowedAngleDiff) {
        if (SolMath.angleDiff(destAngle, angle) < allowedAngleDiff || rotAcc == 0) {
            return null;
        }

        float breakWay = rotationSpeed * rotationSpeed / rotAcc / 2;
        float angleAfterBreak = angle + breakWay * SolMath.toInt(rotationSpeed > 0);
        float relAngle = SolMath.norm(angle - destAngle);
        float relAngleAfterBreak = SolMath.norm(angleAfterBreak - destAngle);
        if (relAngle > 0 == relAngleAfterBreak > 0) {
            return relAngle < 0;
        }
        return relAngle > 0;
    }

    public void update(SolGame game, SolShip ship, Vector2 dest, Planet np,
                       float maxIdleDist, boolean hasEngine, boolean avoidBigObjs, float desiredSpeed, boolean stopNearDest,
                       Vector2 destVelocity) {
        myUp = false;
        myLeft = false;
        myRight = false;

        if (!hasEngine || dest == null) {
            return;
        }

        Vector2 shipPos = ship.getPosition();

        float toDestLen = shipPos.dst(dest);

        if (toDestLen < maxIdleDist) {
            if (!stopNearDest) {
                return;
            }
            myDesiredVelocity.set(destVelocity);
        } else {
            updateDesiredVelocity(game, ship, dest, toDestLen, stopNearDest, np, avoidBigObjs, desiredSpeed, destVelocity);
        }

        Vector2 shipVelocity = ship.getVelocity();
        float speedDeviation = shipVelocity.dst(myDesiredVelocity);
        if (speedDeviation < MAX_ABS_SPD_DEV || speedDeviation < MAX_REL_SPD_DEV * shipVelocity.len()) {
            return;
        }

        float shipAngle = ship.getAngle();
        float rotationSpeed = ship.getRotationSpeed();
        float rotAcc = ship.getRotationAcceleration();

        float desiredAngle = SolMath.angle(shipVelocity, myDesiredVelocity);
        float angleDiff = SolMath.angleDiff(desiredAngle, shipAngle);
        myUp = angleDiff < MIN_ANGLE_TO_ACC;
        Boolean ntt = needsToTurn(shipAngle, desiredAngle, rotationSpeed, rotAcc, MIN_MOVE_AAD);
        if (ntt != null) {
            if (ntt) {
                myRight = true;
            } else {
                myLeft = true;
            }
        }
    }

    private void updateDesiredVelocity(SolGame game, SolShip ship, Vector2 dest, float toDestLen, boolean stopNearDest,
                                       Planet np, boolean avoidBigObjs, float desiredSpeed, Vector2 destVelocity) {
        float toDestAngle = getToDestAngle(game, ship, dest, avoidBigObjs, np);
        if (stopNearDest) {
            float tangentSpeed = SolMath.project(ship.getVelocity(), toDestAngle);
            float turnWay = tangentSpeed * ship.calcTimeToTurn(toDestAngle + 180);
            float breakWay = tangentSpeed * tangentSpeed / ship.getAcceleration() / 2;
            boolean needsToBreak = toDestLen < .5f * tangentSpeed + turnWay + breakWay;
            if (needsToBreak) {
                myDesiredVelocity.set(destVelocity);
                return;
            }
        }
        SolMath.fromAl(myDesiredVelocity, toDestAngle, desiredSpeed);
    }

    public void rotateOnIdle(SolShip ship, Planet np, Vector2 dest, boolean stopNearDest, float maxIdleDist) {
        if (isActive() || dest == null) {
            return;
        }
        Vector2 shipPos = ship.getPosition();
        float shipAngle = ship.getAngle();
        float toDestLen = shipPos.dst(dest);
        float desiredAngle;
        float allowedAngleDiff;
        boolean nearFinalDest = stopNearDest && toDestLen < maxIdleDist;
        float dstToPlanet = np.getPosition().dst(shipPos);
        if (nearFinalDest) {
            if (np.getFullHeight() < dstToPlanet) {
                return; // stopping in space, don't care of angle
            }
            // stopping on planet
            desiredAngle = SolMath.angle(np.getPosition(), shipPos);
            allowedAngleDiff = MIN_PLANET_MOVE_AAD;
        } else {
            // flying somewhere
            if (dstToPlanet < np.getFullHeight() + Const.ATM_HEIGHT) {
                return; // near planet, don't care of angle
            }
            desiredAngle = SolMath.angle(ship.getVelocity());
            allowedAngleDiff = MIN_MOVE_AAD;
        }

        Boolean ntt = needsToTurn(shipAngle, desiredAngle, ship.getRotationSpeed(), ship.getRotationAcceleration(), allowedAngleDiff);
        if (ntt != null) {
            if (ntt) {
                myRight = true;
            } else {
                myLeft = true;
            }
        }
    }

    private float getToDestAngle(SolGame game, SolShip ship, Vector2 dest, boolean avoidBigObjs, Planet np) {
        Vector2 shipPos = ship.getPosition();
        float toDestAngle = SolMath.angle(shipPos, dest);
        if (avoidBigObjs) {
            toDestAngle = myBigObjAvoider.avoid(game, shipPos, dest, toDestAngle);
        }
        toDestAngle = mySmallObjAvoider.avoid(game, ship, toDestAngle, np);
        return toDestAngle;
    }

    public boolean isUp() {
        return myUp;
    }

    public boolean isLeft() {
        return myLeft;
    }

    public boolean isRight() {
        return myRight;
    }

    public boolean isActive() {
        return myUp || myLeft || myRight;
    }

    public BigObjAvoider getBigObjAvoider() {
        return myBigObjAvoider;
    }
}
