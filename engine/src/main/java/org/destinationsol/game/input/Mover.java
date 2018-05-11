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
    private boolean active;
    private Vector2 myDesiredSpeed;

    private float desiredOrientation;
    private float throttle;

    Mover() {
        myBigObjAvoider = new BigObjAvoider();
        mySmallObjAvoider = new SmallObjAvoider();
        myDesiredSpeed = new Vector2();
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
                       float maxIdleDist, boolean hasEngine, boolean avoidBigObjs, float desiredSpeedLen, boolean stopNearDest,
                       Vector2 destSpeed) {
        active = false;

        if (!hasEngine || dest == null) {
            return;
        }

        Vector2 shipPos = ship.getPosition();

        float toDestLen = shipPos.dst(dest);

        if (toDestLen < maxIdleDist) {
            if (!stopNearDest) {
                return;
            }
            myDesiredSpeed.set(destSpeed);
        } else {
            updateDesiredSpeed(game, ship, dest, toDestLen, stopNearDest, np, avoidBigObjs, desiredSpeedLen, destSpeed);
        }

        Vector2 shipSpeed = ship.getSpeed();
        float speedDeviation = shipSpeed.dst(myDesiredSpeed);
        if (speedDeviation < MAX_ABS_SPD_DEV || speedDeviation < MAX_REL_SPD_DEV * shipSpeed.len()) {
            return;
        }

        active = true;

        float shipAngle = ship.getAngle();
        float rotationSpeed = ship.getRotationSpeed();
        float rotAcc = ship.getRotationAcceleration();

        desiredOrientation = SolMath.angle(shipSpeed, myDesiredSpeed);
        float angleDiff = SolMath.angleDiff(desiredOrientation, shipAngle);
        // TODO: Find a way to calculate intermediate throttle inputs if needed
        if (angleDiff < MIN_ANGLE_TO_ACC) {
            throttle = 1;
        } else {
            throttle = 0;
        }
    }

    private void updateDesiredSpeed(SolGame game, SolShip ship, Vector2 dest, float toDestLen, boolean stopNearDest,
                                  Planet np, boolean avoidBigObjs, float desiredSpeedLen, Vector2 destSpeed) {
        float toDestAngle = getToDestAngle(game, ship, dest, avoidBigObjs, np);
        if (stopNearDest) {
            float tangentSpeed = SolMath.project(ship.getSpeed(), toDestAngle);
            float turnWay = tangentSpeed * ship.calcTimeToTurn(toDestAngle + 180);
            float breakWay = tangentSpeed * tangentSpeed / ship.getAcceleration() / 2;
            boolean needsToBreak = toDestLen < .5f * tangentSpeed + turnWay + breakWay;
            if (needsToBreak) {
                myDesiredSpeed.set(destSpeed);
                return;
            }
        }
        SolMath.fromAl(myDesiredSpeed, toDestAngle, desiredSpeedLen);
    }

    public void rotateOnIdle(SolShip ship, Planet np, Vector2 dest, boolean stopNearDest, float maxIdleDist) {
        if (isActive() || dest == null) {
            return;
        }
        Vector2 shipPos = ship.getPosition();
        float shipAngle = ship.getAngle();
        float toDestLen = shipPos.dst(dest);
        boolean nearFinalDest = stopNearDest && toDestLen < maxIdleDist;
        float dstToPlanet = np.getPosition().dst(shipPos);
        if (nearFinalDest) {
            if (np.getFullHeight() < dstToPlanet) {
                return; // stopping in space, don't care of angle
            }
            // stopping on planet
            desiredOrientation = SolMath.angle(np.getPosition(), shipPos);
        } else {
            // flying somewhere
            if (dstToPlanet < np.getFullHeight() + Const.ATM_HEIGHT) {
                return; // near planet, don't care of angle
            }
            desiredOrientation = SolMath.angle(ship.getSpeed());
        }

        active = true;
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

    public boolean isActive() {
        return active;
    }

    public BigObjAvoider getBigObjAvoider() {
        return myBigObjAvoider;
    }

    public float getDesiredOrientation() {
        return desiredOrientation;
    }

    public float getThrottle() {
        return throttle;
    }
}
