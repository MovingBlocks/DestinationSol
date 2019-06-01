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

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import org.destinationsol.common.SolColor;
import org.destinationsol.common.SolMath;
import org.destinationsol.common.SolRandom;
import org.destinationsol.game.Faction;
import org.destinationsol.game.SolGame;
import org.destinationsol.game.SolObject;
import org.destinationsol.game.drawables.Drawable;
import org.destinationsol.game.drawables.DrawableLevel;
import org.destinationsol.game.drawables.RectSprite;
import org.destinationsol.game.item.Clip;
import org.destinationsol.game.item.Gun;
import org.destinationsol.game.item.ItemContainer;
import org.destinationsol.game.particle.LightSource;
import org.destinationsol.game.planet.Planet;
import org.destinationsol.game.projectile.Projectile;
import org.destinationsol.game.projectile.ProjectileConfig;
import org.destinationsol.game.ship.SolShip;

import java.util.ArrayList;
import java.util.List;

public class SolGun {
    private final LightSource myLightSource;
    private final Vector2 myRelPos;
    private final RectSprite mySprite;
    private final Gun myItem;
    private final List<Drawable> myDrawables;
    private float myCoolDown;
    private float myCurrAngleVar;

    public SolGun(Gun item, Vector2 relPos, boolean underShip) {
        myItem = item;
        if (myItem.config.lightOnShot) {
            Color lightCol = SolColor.WHITE;
            ProjectileConfig projConfig = myItem.config.clipConf.projConfig;
            if (projConfig.bodyEffect != null) {
                lightCol = projConfig.bodyEffect.tint;
            } else if (projConfig.collisionEffect != null) {
                lightCol = projConfig.collisionEffect.tint;
            }
            myLightSource = new LightSource(.25f, true, 1f, Vector2.Zero, lightCol);
        } else {
            myLightSource = null;
        }
        myRelPos = new Vector2(relPos);
        DrawableLevel level = underShip ? DrawableLevel.U_GUNS : DrawableLevel.GUNS;
        float texLen = myItem.config.gunLength / myItem.config.texLenPercentage * 2;
        mySprite = new RectSprite(myItem.config.tex, texLen, 0, 0, new Vector2(relPos), level, 0, 0, SolColor.WHITE, false);
        myDrawables = new ArrayList<>();
        myDrawables.add(mySprite);
        if (myLightSource != null) {
            myLightSource.collectDrawables(myDrawables);
        }
    }

    public List<Drawable> getDrawables() {
        return myDrawables;
    }

    private void shoot(Vector2 gunVelocity, SolGame game, float gunAngle, Vector2 muzzlePos, Faction faction, SolObject creator, SolShip ship) {
        Vector2 baseVelocity = gunVelocity;
        Clip.Config cc = myItem.config.clipConf;
        if (cc.projConfig.zeroAbsSpeed) {
            baseVelocity = Vector2.Zero;
            Planet np = game.getPlanetManager().getNearestPlanet();
            if (np.isNearGround(muzzlePos)) {
                baseVelocity = new Vector2();
                np.calculateVelocityAtPosition(baseVelocity, muzzlePos);
            }
        }

        myCurrAngleVar = SolMath.approach(myCurrAngleVar, myItem.config.maxAngleVar, myItem.config.angleVarPerShot);
        boolean multiple = cc.projectilesPerShot > 1;
        for (int i = 0; i < cc.projectilesPerShot; i++) {
            float bulletAngle = gunAngle;
            if (myCurrAngleVar > 0) {
                bulletAngle += SolRandom.randomFloat(myCurrAngleVar);
            }
            Projectile proj = new Projectile(game, bulletAngle, muzzlePos, baseVelocity, faction, cc.projConfig, multiple, ship);
            game.getObjectManager().addObjDelayed(proj);
        }
        myCoolDown += myItem.config.timeBetweenShots;
        myItem.ammo--;
        game.getSoundManager().play(game, myItem.config.shootSound, muzzlePos, creator);
    }

    public void update(ItemContainer itemContainer, SolGame game, float gunAngle, SolObject creator, boolean shouldShoot, Faction faction, SolShip ship) {
        float baseAngle = creator.getAngle();
        Vector2 basePos = creator.getPosition();
        float gunRelAngle = gunAngle - baseAngle;
        mySprite.relativeAngle = gunRelAngle;
        Vector2 muzzleRelPos = SolMath.fromAl(gunRelAngle, myItem.config.gunLength);
        muzzleRelPos.add(myRelPos);
        if (myLightSource != null) {
            myLightSource.setRelativePosition(muzzleRelPos);
        }
        Vector2 muzzlePos = SolMath.toWorld(muzzleRelPos, baseAngle, basePos);
        SolMath.free(muzzleRelPos);

        float ts = game.getTimeStep();
        if (myItem.ammo <= 0 && myItem.reloadAwait <= 0) {
            if (myItem.config.clipConf.infinite || itemContainer != null && itemContainer.tryConsumeItem(myItem.config.clipConf.example)) {
                myItem.reloadAwait = myItem.config.reloadTime + .0001f;
                game.getSoundManager().play(game, myItem.config.reloadSound, null, creator);
            }
        } else if (myItem.reloadAwait > 0) {
            myItem.reloadAwait -= ts;
            if (myItem.reloadAwait <= 0) {
                myItem.ammo = myItem.config.clipConf.size;
            }
        }

        if (myCoolDown > 0) {
            myCoolDown -= ts;
        }

        boolean shot = shouldShoot && myCoolDown <= 0 && myItem.ammo > 0;
        game.getPartMan().updateAllHullEmittersOfType(ship, "shoot", shot);
        if (shot) {
            Vector2 gunVelocity = creator.getVelocity();
            shoot(gunVelocity, game, gunAngle, muzzlePos, faction, creator, ship);
        } else {
            myCurrAngleVar = SolMath.approach(myCurrAngleVar, myItem.config.minAngleVar, myItem.config.angleVarDamp * ts);
        }
        if (myLightSource != null) {
            myLightSource.update(shot, baseAngle, game);
        }
        SolMath.free(muzzlePos);
    }

    public Gun.Config getConfig() {
        return myItem.config;
    }

    public Gun getItem() {
        return myItem;
    }
}
