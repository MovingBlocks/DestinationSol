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

package org.destinationsol.game.gun;

import com.badlogic.gdx.math.Vector2;
import org.destinationsol.Const;
import org.destinationsol.common.SolMath;
import org.destinationsol.game.Faction;
import org.destinationsol.game.SolGame;
import org.destinationsol.game.SolObject;
import org.destinationsol.game.drawables.Drawable;
import org.destinationsol.game.input.Shooter;
import org.destinationsol.game.item.Gun;
import org.destinationsol.game.item.ItemContainer;
import org.destinationsol.game.ship.SolShip;
import org.destinationsol.game.ship.hulls.GunSlot;
import org.destinationsol.game.ship.hulls.HullConfig;

import java.util.List;

public class GunMount {
    private final Vector2 myRelPos;
    private final boolean myFixed;
    private SolGun myGun;
    private boolean myDetected;
    private float myRelGunAngle;

    public GunMount(GunSlot gunSlot) {
        myRelPos = gunSlot.getPosition();
        myFixed = !gunSlot.allowsRotation();
    }

    public void update(ItemContainer ic, SolGame game, float shipAngle, SolShip creator, boolean shouldShoot, SolShip nearestEnemy, Faction faction) {
        if (myGun == null) {
            return;
        }
        if (!ic.contains(myGun.getItem())) {
            setGun(game, creator, null, false, 0);
            return;
        }

        if (creator.getHull().config.getType() != HullConfig.Type.STATION) {
            myRelGunAngle = 0;
        }
        myDetected = false;
        if (!myFixed && nearestEnemy != null) {
            Vector2 creatorPos = creator.getPosition();
            Vector2 nePos = nearestEnemy.getPosition();
            float dst = creatorPos.dst(nePos) - creator.getHull().config.getApproxRadius() - nearestEnemy.getHull().config.getApproxRadius();
            float detDst = game.getPlanetManager().getNearestPlanet().isNearGround(creatorPos) ? Const.AUTO_SHOOT_GROUND : Const.AUTO_SHOOT_SPACE;
            if (dst < detDst) {
                Vector2 mountPos = SolMath.toWorld(myRelPos, shipAngle, creatorPos);
                boolean player = creator.getPilot().isPlayer();
                float shootAngle = Shooter.calcShootAngle(mountPos, creator.getVelocity(), nePos, nearestEnemy.getVelocity(), myGun.getConfig().clipConf.projConfig.speed, player);
                if (shootAngle == shootAngle) {
                    myRelGunAngle = shootAngle - shipAngle;
                    myDetected = true;
                    if (player) {
                        game.getMountDetectDrawer().setNe(nearestEnemy);
                    }
                }
                SolMath.free(mountPos);
            }
        }

        float gunAngle = shipAngle + myRelGunAngle;
        myGun.update(ic, game, gunAngle, creator, shouldShoot, faction, creator);
    }

    public Gun getGun() {
        return myGun == null ? null : myGun.getItem();
    }

    public void setGun(SolGame game, SolObject o, Gun gun, boolean underShip, int slotNr) {
        List<Drawable> drawables = o.getDrawables();
        if (myGun != null) {
            List<Drawable> dras1 = myGun.getDrawables();
            drawables.removeAll(dras1);
            game.getDrawableManager().removeAll(dras1);
            myGun.getItem().setEquipped(0);
            myGun = null;
        }
        if (gun != null) {
            if (gun.config.fixed != myFixed) {
                throw new AssertionError("tried to set gun to incompatible mount");
            }
            myGun = new SolGun(gun, myRelPos, underShip);
            myGun.getItem().setEquipped(slotNr);
            List<Drawable> dras1 = myGun.getDrawables();
            drawables.addAll(dras1);
            game.getDrawableManager().addAll(dras1);
        }
    }

    public boolean isFixed() {
        return myFixed;
    }

    public Vector2 getRelPos() {
        return myRelPos;
    }

    public boolean isDetected() {
        return myDetected;
    }
}
