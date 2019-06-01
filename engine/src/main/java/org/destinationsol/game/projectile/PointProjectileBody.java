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
package org.destinationsol.game.projectile;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import org.destinationsol.Const;
import org.destinationsol.common.SolMath;
import org.destinationsol.game.SolGame;
import org.destinationsol.game.SolObject;
import org.destinationsol.game.ship.SolShip;

public class PointProjectileBody implements ProjectileBody {
    private final Vector2 position;
    private final Vector2 velocity;
    private final ProjectileRayBack rayBack;
    private final float acceleration;

    public PointProjectileBody(float angle, Vector2 muzzlePos, Vector2 gunVelocity, float speed,
                               Projectile projectile, SolGame game, float acceleration) {
        position = new Vector2(muzzlePos);
        velocity = new Vector2();
        SolMath.fromAl(velocity, angle, speed);
        velocity.add(gunVelocity);
        rayBack = new ProjectileRayBack(projectile, game);
        this.acceleration = acceleration;
    }

    @Override
    public void update(SolGame game) {
        if (acceleration > 0 && SolMath.canAccelerate(acceleration, velocity)) {
            float speed = velocity.len();
            if (speed < Const.MAX_MOVE_SPD) {
                velocity.scl((speed + acceleration) / speed);
            }
        }
        Vector2 prevPos = SolMath.getVec(position);
        Vector2 diff = SolMath.getVec(velocity);
        diff.scl(game.getTimeStep());
        position.add(diff);
        SolMath.free(diff);
        game.getObjectManager().getWorld().rayCast(rayBack, prevPos, position);
        SolMath.free(prevPos);
    }

    @Override
    public Vector2 getPosition() {
        return position;
    }

    @Override
    public void receiveForce(Vector2 force, SolGame game, boolean acc) {
        force.scl(game.getTimeStep());
        if (!acc) {
            force.scl(10f);
        }
        velocity.add(force);
    }

    @Override
    public Vector2 getVelocity() {
        return velocity;
    }

    @Override
    public void onRemove(SolGame game) {
    }

    @Override
    public float getAngle() {
        return SolMath.angle(velocity);
    }

    @Override
    public void changeAngle(float diff) {
        SolMath.rotate(velocity, diff);
    }

    @Override
    public float getDesiredAngle(SolShip ne) {
        return SolMath.angle(position, ne.getPosition());
    }

    private class ProjectileRayBack implements RayCastCallback {

        private final Projectile projectile;
        private final SolGame game;

        private ProjectileRayBack(Projectile projectile, SolGame game) {
            this.projectile = projectile;
            this.game = game;
        }

        @Override
        public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal, float fraction) {
            SolObject o = (SolObject) fixture.getBody().getUserData();
            boolean oIsMassless = o instanceof Projectile && ((Projectile) o).isMassless();
            if (!oIsMassless && projectile.shouldCollide(o, fixture, game.getFactionMan())) {
                position.set(point);
                projectile.setObstacle(o, game);
                return 0;
            }
            return -1;
        }
    }
}
