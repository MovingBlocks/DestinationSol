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

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Transform;
import org.destinationsol.Const;
import org.destinationsol.common.SolMath;
import org.destinationsol.game.SolGame;
import org.destinationsol.game.input.Pilot;
import org.destinationsol.game.input.Shooter;
import org.destinationsol.game.item.Engine;
import org.destinationsol.game.ship.hulls.Hull;

public class ShipEngine {
    private static final float MIN_ACCELERATION_TIME = 0.1f;

    private final Engine myItem;
    private float myRecoverAwait;

    public ShipEngine(Engine engine) {
        myItem = engine;
    }

    public void update(float angle, SolGame game, Pilot pilot, Body body, Vector2 speed, boolean controlsEnabled,
                       float mass, Hull hull) {
        boolean engineRunning = applyInput(game, angle, pilot, body, speed, controlsEnabled, mass);
        game.getPartMan().updateAllHullEmittersOfType(hull, "engine", engineRunning);
    }

    private boolean applyInput(SolGame game, float shipAngle, Pilot pilot, Body body, Vector2 velocity,
                               boolean controlsEnabled, float mass) {
        boolean engineRunning = controlsEnabled && pilot.getThrottle() != 0;

        Engine e = myItem;

        // Apply force so that target velocity is reached
        // TODO: Maybe vary max speed by engine/ship
        Vector2 targetVelocity = SolMath.fromAl(shipAngle, pilot.getThrottle() * Const.MAX_MOVE_SPD);
        Vector2 velocityDelta = targetVelocity.sub(velocity);
        float speedDelta = velocityDelta.len();

        if (speedDelta != 0) {
            float maxForceMagnitude = speedDelta * mass / MIN_ACCELERATION_TIME;
            // TODO: engine should provide thrust, not acceleration
            float forceMagnitude = Math.min(mass * e.getAcceleration(), maxForceMagnitude);
            body.applyForceToCenter(velocityDelta.scl(forceMagnitude / speedDelta), true);
        }
        
        SolMath.free(targetVelocity);

        float orientation = body.getAngle() * SolMath.radDeg;
        float angularVelocity = body.getAngularVelocity() * SolMath.radDeg;

        float targetOrientation = pilot.getOrientation();

        float angularDisplacement = SolMath.norm(targetOrientation - orientation);

        float absAngularAcceleration = e.getRotationAcceleration();

        float maxStoppingDistance = 0.5f * angularVelocity * angularVelocity / absAngularAcceleration;

        float angularAcceleration;

        // If we are close to where we want to aim, stop rotating
        if (Math.abs(angularDisplacement) <= Shooter.MIN_SHOOT_AAD) {
            angularAcceleration = -angularVelocity / game.getTimeStep();
        }
        // If angular speed is greater than maximum angular speed OR
        // if angular displacement is just enough to stop the body in time,
        // accelerate in the opposite direction of angular velocity to slow down
        else if (Math.abs(angularVelocity) > e.getMaxRotationSpeed()) {
            angularAcceleration = -Math.signum(angularVelocity) * absAngularAcceleration;
        } else if (Math.abs(angularDisplacement) <= maxStoppingDistance) {
            angularAcceleration = -Math.signum(angularVelocity) * absAngularAcceleration;
        } else {
            // Otherwise, accelerate in direction of angular displacement
            angularAcceleration = Math.signum(angularDisplacement) * absAngularAcceleration;
        }

        body.applyTorque(body.getInertia() * angularAcceleration * SolMath.degRad, true);

        return engineRunning;
    }

    public Engine getItem() {
        return myItem;
    }
}
