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
package org.destinationsol.game.projectile;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import org.destinationsol.common.SolMath;
import org.destinationsol.game.SolGame;
import org.destinationsol.game.asteroid.AsteroidBuilder;
import org.destinationsol.game.ship.SolShip;

public class BallProjectileBody implements ProjectileBody {
    private final Body body;
    private final Vector2 position;
    private final Vector2 speed;
    private final float acceleration;
    private final float mass;

    private float angle;

    BallProjectileBody(SolGame game, Vector2 position, float angle, Projectile projectile,
                       Vector2 gunSpeed, float speedLen, ProjectileConfig config) {
        float density = config.density == -1 ? 1 : config.density;
        body = AsteroidBuilder.buildBall(game, position, angle, config.physSize / 2, density, config.massless);
        if (config.zeroAbsSpeed) {
            body.setAngularVelocity(15f * MathUtils.degRad);
        }

        speed = new Vector2();
        SolMath.fromAl(speed, angle, speedLen);
        speed.add(gunSpeed);
        body.setLinearVelocity(speed);
        body.setUserData(projectile);

        this.position = new Vector2();
        acceleration = config.acc;
        mass = body.getMass();
        setParamsFromBody();
    }

    private void setParamsFromBody() {
        position.set(body.getPosition());
        angle = body.getAngle() * MathUtils.radDeg;
        speed.set(body.getLinearVelocity());
    }

    @Override
    public void update(SolGame game) {
        setParamsFromBody();
        if (acceleration > 0 && SolMath.canAccelerate(angle, speed)) {
            Vector2 force = SolMath.fromAl(angle, acceleration * mass);
            body.applyForceToCenter(force, true);
            SolMath.free(force);
        }
    }

    @Override
    public Vector2 getPosition() {
        return position;
    }

    @Override
    public Vector2 getSpeed() {
        return speed;
    }

    @Override
    public void receiveForce(Vector2 force, SolGame game, boolean acc) {
        if (acc) {
            force.scl(mass);
        }
        body.applyForceToCenter(force, true);
    }

    @Override
    public void onRemove(SolGame game) {
        body.getWorld().destroyBody(body);
    }

    @Override
    public float getAngle() {
        return angle;
    }

    @Override
    public void changeAngle(float diff) {
        angle += diff;
        body.setTransform(position, angle * MathUtils.degRad);
        body.setAngularVelocity(0);
    }

    @Override
    public float getDesiredAngle(SolShip nearestEnemy) {
        float speedScalar = speed.len();
        if (speedScalar < 3) {
            speedScalar = 3;
        }
        float distanceToNearestEnemy = SolMath.angle(position, nearestEnemy.getPosition());
        Vector2 desiredSpeed = SolMath.fromAl(distanceToNearestEnemy, speedScalar);
        desiredSpeed.add(nearestEnemy.getSpeed());
        float result = SolMath.angle(speed, desiredSpeed);
        SolMath.free(desiredSpeed);
        return result;
    }
}
