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

public class Shooter {

    private static final float E_SPD_PERC = .6f; // 0 means that target speed is not considered, 1 means that it's fully considered
    public static final float MIN_SHOOT_AAD = 2f;
    private boolean myShoot;
    private boolean myShoot2;
    private boolean myRight;
    private boolean myLeft;

    Shooter() {
    }

    public static float calcShootAngle(Vector2 gunPos, Vector2 gunSpeed, Vector2 ePos, Vector2 eSpeed, float projSpeed,
                                       boolean sharp) {
        Vector2 eSpeedShortened = SolMath.getVec(eSpeed);
        if (!sharp) {
            eSpeedShortened.scl(E_SPD_PERC);
        }
        Vector2 relESpeed = SolMath.distVec(gunSpeed, eSpeedShortened);
        SolMath.free(eSpeedShortened);
        float rotAngle = SolMath.angle(relESpeed);
        float v = relESpeed.len();
        SolMath.free(relESpeed);
        Vector2 toE = SolMath.distVec(gunPos, ePos);
        SolMath.rotate(toE, -rotAngle);
        float x = toE.x;
        float y = toE.y;
        float a = v * v - projSpeed * projSpeed;
        float b = 2 * x * v;
        float c = x * x + y * y;
        float t = SolMath.genQuad(a, b, c);
        float res;
        if (t != t) {
            res = Float.NaN;
        } else {
            toE.x += t * v;
            res = SolMath.angle(toE) + rotAngle;
        }
        SolMath.free(toE);
        return res;
    }

    public void update(SolShip ship, Vector2 enemyPos, boolean dontRotate, boolean canShoot, Vector2 enemySpeed,
                       float enemyApproxRad) {
        myLeft = false;
        myRight = false;
        myShoot = false;
        myShoot2 = false;
        Vector2 shipPos = ship.getPosition();
        if (enemyPos == null || !canShoot) {
            return;
        }
        float toEnemyDst = enemyPos.dst(shipPos);

        Gun gun1 = processGun(ship, false);
        Gun gun2 = processGun(ship, true);
        if (gun1 == null && gun2 == null) {
            return;
        }

        float projSpeed = 0;
        Gun gun = null;
        if (gun1 != null) {
            ProjectileConfig projConfig = gun1.config.clipConf.projConfig;
            projSpeed = projConfig.speedLen + projConfig.acc; // for simplicity
            gun = gun1;
        }
        if (gun2 != null) {
            ProjectileConfig projConfig = gun2.config.clipConf.projConfig;
            float g2PS = projConfig.speedLen + projConfig.acc; // for simplicity
            if (projSpeed < g2PS) {
                projSpeed = g2PS;
                gun = gun2;
            }
        }

        Vector2 gunRelPos = ship.getHull().getGunMount(gun == gun2).getRelPos();
        Vector2 gunPos = SolMath.toWorld(gunRelPos, ship.getAngle(), shipPos);
        float shootAngle = calcShootAngle(gunPos, ship.getSpeed(), enemyPos, enemySpeed, projSpeed, false);
        SolMath.free(gunPos);
        if (shootAngle != shootAngle) {
            return;
        }
        {
            // ok this is a hack
            float toShip = SolMath.angle(enemyPos, shipPos);
            float toGun = SolMath.angle(enemyPos, gunPos);
            shootAngle += toGun - toShip;
        }
        float shipAngle = ship.getAngle();
        float maxAngleDiff = SolMath.angularWidthOfSphere(enemyApproxRad, toEnemyDst) + 10f;
        ProjectileConfig projConfig = gun.config.clipConf.projConfig;
        if (projSpeed > 0 && projConfig.guideRotationSpeed > 0) {
            maxAngleDiff += projConfig.guideRotationSpeed * toEnemyDst / projSpeed;
        }
        if (SolMath.angleDiff(shootAngle, shipAngle) < maxAngleDiff) {
            myShoot = true;
            myShoot2 = true;
            return;
        }

        if (dontRotate) {
            return;
        }
        Boolean needsToTurn = Mover.needsToTurn(shipAngle, shootAngle, ship.getRotationSpeed(), ship.getRotationAcceleration(), MIN_SHOOT_AAD);
        if (needsToTurn != null) {
            if (needsToTurn) {
                myRight = true;
            } else {
                myLeft = true;
            }
        }
    }

    // returns gun if it's fixed & can shoot
    private Gun processGun(SolShip ship, boolean second) {
        GunMount mount = ship.getHull().getGunMount(second);
        if (mount == null) {
            return null;
        }
        Gun g = mount.getGun();
        if (g == null || g.ammo <= 0) {
            return null;
        }

        if (g.config.clipConf.projConfig.zeroAbsSpeed || g.config.clipConf.projConfig.guideRotationSpeed > 0) {
            if (second) {
                myShoot2 = true;
            } else {
                myShoot = true;
            }
            return null;
        }

        if (g.config.fixed) {
            return g;
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
