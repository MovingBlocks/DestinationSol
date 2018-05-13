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
    public static final float MAX_RECOVER_ROT_SPD = 5f;
    public static final float RECOVER_MUL = 15f;
    public static final float RECOVER_AWAIT = 2f;

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
        Engine engine = this.myItem;
        float throttle = pilot.getThrottle();

        float engineThrust = throttle * mass * engine.getAcceleration();
        float engineImpulse = engineThrust * game.getTimeStep();

        Vector2 shipHeading = SolMath.getVec(body.getTransform().vals[Transform.COS],
                body.getTransform().vals[Transform.SIN]);

        float maxSpeed = Const.MAX_MOVE_SPD * throttle;

        // Accelerate only if speed is less than the throttle cap OR
        // the acceleration decreases the speed.
        // |v + at| < |v| when |at|^2 + 2*dot(v, at) < 0 or |at| + 2*dot(v, heading) < 0
        boolean canAccelerate = velocity.len2() <= maxSpeed * maxSpeed ||
                engineImpulse + 2 * velocity.dot(shipHeading) < 0;

        boolean engineRunning = controlsEnabled && throttle != 0 && canAccelerate;

        if (engineRunning) {
            body.applyForceToCenter(shipHeading.scl(engineThrust), true);
        }

        SolMath.free(shipHeading);

        float angularVelocity = body.getAngularVelocity() * SolMath.radDeg;

        float targetOrientation = pilot.getOrientation();

        float angularDisplacement = SolMath.norm(targetOrientation - shipAngle);

        float absAngularAcceleration = engine.getRotationAcceleration();

        float maxStoppingDistance = 0.5f * angularVelocity * angularVelocity / absAngularAcceleration;

        float angularAcceleration;

        // If we are close to where we want to aim, stop rotating
        if (Math.abs(angularDisplacement) <= Shooter.MIN_SHOOT_AAD) {
            angularAcceleration = -angularVelocity / game.getTimeStep();
        }
        // If angular speed is greater than maximum angular speed OR
        // if angular displacement is just enough to stop the body in time,
        // accelerate in the opposite direction of angular velocity to slow down
        else if (Math.abs(angularVelocity) > engine.getMaxRotationSpeed()) {
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
