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
import org.destinationsol.common.SolMath;
import org.destinationsol.game.gun.GunMount;
import org.destinationsol.game.item.Gun;
import org.destinationsol.game.projectile.ProjectileConfig;
import org.destinationsol.game.ship.SolShip;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Shooter {

    public static final float E_SPD_PERC = .6f; // 0 means that target speed is not considered, 1 means that it's fully considered
    public static final float MIN_SHOOT_AAD = 2f;
    private boolean myShoot;
    private boolean myShoot2;
    private boolean myRight;
    private boolean myLeft;

    public Shooter() {
    }

    public static float calcShootAngle(@NotNull Vector2 gunPosition, @NotNull Vector2 gunSpeed, @NotNull Vector2 enemyPosition,
                                       @NotNull Vector2 enemySpeed, float projSpd, boolean sharp) {
        Vector2 temporalEnemySpeed = SolMath.getVec(enemySpeed);
        if (!sharp) {
            temporalEnemySpeed.scl(E_SPD_PERC);
        }
        Vector2 relativeEnemySpeed = SolMath.distVec(gunSpeed, temporalEnemySpeed);
        SolMath.free(temporalEnemySpeed);
        float enemyMovementAngle = SolMath.angle(relativeEnemySpeed);
        float v = relativeEnemySpeed.len();
        SolMath.free(relativeEnemySpeed);
        Vector2 distanceToEnemy = SolMath.distVec(gunPosition, enemyPosition);
        SolMath.rotate(distanceToEnemy, -enemyMovementAngle);
        float x = distanceToEnemy.x;
        float y = distanceToEnemy.y;
        float a = v * v - projSpd * projSpd;
        float b = 2 * x * v;
        float c = x * x + y * y;
        float t = SolMath.genQuad(a, b, c);
        float result;
        if (t != t) {
            result = Float.NaN;
        } else {
            distanceToEnemy.x += t * v;
            result = SolMath.angle(distanceToEnemy) + enemyMovementAngle;
        }
        SolMath.free(distanceToEnemy);
        return result;
    }

    public void update(@NotNull SolShip ship, @Nullable Vector2 enemyPos, boolean nonRotatable, boolean canShoot,
                       @NotNull Vector2 enemySpd, float enemyApproxRad) {
        myLeft = false;
        myRight = false;
        myShoot = false;
        myShoot2 = false;
        Vector2 shipPos = ship.getPosition();
        if (enemyPos == null || !canShoot) {
            return;
        }
        float distanceToEnemy = SolMath.distVec(shipPos, enemyPos).len();

        Gun gun1 = processGun(ship, false);
        Gun gun2 = processGun(ship, true);
        if (gun1 == null && gun2 == null) {
            return;
        }

        float projectileSpeed = 0;
        Gun gun = null;
        if (gun1 != null) {
            ProjectileConfig projConfig = gun1.config.clipConf.projConfig;
            projectileSpeed = projConfig.spdLen + projConfig.acc; // for simplicity
            gun = gun1;
        }
        if (gun2 != null) {
            ProjectileConfig projConfig = gun2.config.clipConf.projConfig;
            float gun2ProjectileSpeed = projConfig.spdLen + projConfig.acc; // for simplicity
            if (projectileSpeed < gun2ProjectileSpeed || gun == null) { // nullcheck is here mainly to suppress NPE warnings
                projectileSpeed = gun2ProjectileSpeed;
                gun = gun2;
            }
        }

        Vector2 gunRelativePosition = ship.getHull().getGunMount(gun == gun2).getRelPos();
        Vector2 gunAbsolutePosition = SolMath.toWorld(gunRelativePosition, ship.getAngle(), shipPos);
        float shootAngle = calcShootAngle(gunAbsolutePosition, ship.getSpd(), enemyPos, enemySpd, projectileSpeed, false);
        SolMath.free(gunAbsolutePosition);
        if (shootAngle != shootAngle) { // Float.NaN
            return;
        }
        {
            // ok this is a hack
            float toShip = SolMath.angle(enemyPos, shipPos);
            float toGun = SolMath.angle(enemyPos, gunAbsolutePosition);
            shootAngle += toGun - toShip;
        }
        float shipAngle = ship.getAngle();
        float maxAngleDiff = SolMath.angularWidthOfSphere(enemyApproxRad, distanceToEnemy) + 10f;
        ProjectileConfig projConfig = gun.config.clipConf.projConfig;
        if (projectileSpeed > 0 && projConfig.guideRotSpd > 0) {
            maxAngleDiff += projConfig.guideRotSpd * distanceToEnemy / projectileSpeed;
        }
        if (SolMath.angleDiff(shootAngle, shipAngle) < maxAngleDiff) {
            myShoot = true;
            myShoot2 = true;
            return;
        }

        if (nonRotatable) {
            return;
        }
        Boolean needsToTurn = Mover.needsToTurn(shipAngle, shootAngle, ship.getRotSpd(), ship.getRotAcc(), MIN_SHOOT_AAD);
        if (needsToTurn != null) {
            if (needsToTurn) {
                myRight = true;
            } else {
                myLeft = true;
            }
        }
    }

    // returns gun if it's fixed & can shoot
    private @Nullable Gun processGun(@NotNull SolShip ship, boolean second) {
        GunMount mount = ship.getHull().getGunMount(second);
        if (mount == null) {
            return null;
        }
        Gun gun = mount.getGun();
        if (gun == null || gun.ammo <= 0) {
            return null;
        }

        if (gun.config.clipConf.projConfig.zeroAbsSpd || gun.config.clipConf.projConfig.guideRotSpd > 0) {
            if (second) {
                myShoot2 = true;
            } else {
                myShoot = true;
            }
            return null;
        }

        if (gun.config.fixed) {
            return gun;
        }

        if (mount.isDetected()) {
            if (second) {
                myShoot2 = true;
            } else {
                myShoot = true;
            }
        }
        return null;
    }

    public boolean isShoot() {
        return myShoot;
    }

    public boolean isShoot2() {
        return myShoot2;
    }

    public boolean isLeft() {
        return myLeft;
    }

    public boolean isRight() {
        return myRight;
    }
}
