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

package org.destinationsol.game.gun;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import org.destinationsol.common.SolColor;
import org.destinationsol.common.SolMath;
import org.destinationsol.game.Faction;
import org.destinationsol.game.SolGame;
import org.destinationsol.game.SolObject;
import org.destinationsol.game.drawables.Drawable;
import org.destinationsol.game.drawables.DrawableLevel;
import org.destinationsol.game.drawables.RectSprite;
import org.destinationsol.game.item.Clip;
import org.destinationsol.game.item.Gun;
import org.destinationsol.game.item.ItemContainer;
import org.destinationsol.game.particle.LightSrc;
import org.destinationsol.game.planet.Planet;
import org.destinationsol.game.projectile.Projectile;
import org.destinationsol.game.projectile.ProjectileConfig;

import java.util.ArrayList;
import java.util.List;

public class SolGun {
    private final LightSrc myLightSrc;
    private final Vector2 myRelPos;
    private final RectSprite mySprite;
    private final Gun myItem;
    private final List<Drawable> myDrawables;
    private float myCoolDown;
    private float myCurrAngleVar;

    public SolGun(SolGame game, Gun item, Vector2 relPos, boolean underShip) {
        myItem = item;
        if (myItem.config.lightOnShot) {
            Color lightCol = SolColor.WHITE;
            ProjectileConfig projConfig = myItem.config.clipConf.projConfig;
            if (projConfig.bodyEffect != null) {
                lightCol = projConfig.bodyEffect.tint;
            } else if (projConfig.collisionEffect != null) {
                lightCol = projConfig.collisionEffect.tint;
            }
            myLightSrc = new LightSrc(.25f, true, 1f, Vector2.Zero, lightCol);
        } else {
            myLightSrc = null;
        }
        myRelPos = new Vector2(relPos);
        DrawableLevel level = underShip ? DrawableLevel.U_GUNS : DrawableLevel.GUNS;
        float texLen = myItem.config.gunLength / myItem.config.texLenPerc * 2;
        mySprite = new RectSprite(myItem.config.tex, texLen, 0, 0, new Vector2(relPos), level, 0, 0, SolColor.WHITE, false);
        myDrawables = new ArrayList<>();
        myDrawables.add(mySprite);
        if (myLightSrc != null) {
            myLightSrc.collectDras(myDrawables);
        }
    }

    public List<Drawable> getDrawables() {
        return myDrawables;
    }

    private void shoot(Vector2 gunSpd, SolGame game, float gunAngle, Vector2 muzzlePos, Faction faction, SolObject creator) {
        Vector2 baseSpd = gunSpd;
        Clip.Config cc = myItem.config.clipConf;
        if (cc.projConfig.zeroAbsSpd) {
            baseSpd = Vector2.Zero;
            Planet np = game.getPlanetMan().getNearestPlanet();
            if (np.isNearGround(muzzlePos)) {
                baseSpd = new Vector2();
                np.calcSpdAtPos(baseSpd, muzzlePos);
            }
        }

        myCurrAngleVar = SolMath.approach(myCurrAngleVar, myItem.config.maxAngleVar, myItem.config.angleVarPerShot);
        boolean multiple = cc.projectilesPerShot > 1;
        for (int i = 0; i < cc.projectilesPerShot; i++) {
            float bulletAngle = gunAngle;
            if (myCurrAngleVar > 0) {
                bulletAngle += SolMath.rnd(myCurrAngleVar);
            }
            Projectile proj = new Projectile(game, bulletAngle, muzzlePos, baseSpd, faction, cc.projConfig, multiple);
            game.getObjMan().addObjDelayed(proj);
        }
        myCoolDown += myItem.config.timeBetweenShots;
        myItem.ammo--;
        game.getSoundManager().play(game, myItem.config.shootSound, muzzlePos, creator);
    }

    public void update(ItemContainer ic, SolGame game, float gunAngle, SolObject creator, boolean shouldShoot, Faction faction) {
        float baseAngle = creator.getAngle();
        Vector2 basePos = creator.getPosition();
        float gunRelAngle = gunAngle - baseAngle;
        mySprite.relAngle = gunRelAngle;
        Vector2 muzzleRelPos = SolMath.fromAl(gunRelAngle, myItem.config.gunLength);
        muzzleRelPos.add(myRelPos);
        if (myLightSrc != null) {
            myLightSrc.setRelPos(muzzleRelPos);
        }
        Vector2 muzzlePos = SolMath.toWorld(muzzleRelPos, baseAngle, basePos);
        SolMath.free(muzzleRelPos);

        float ts = game.getTimeStep();
        if (myItem.ammo <= 0 && myItem.reloadAwait <= 0) {
            if (myItem.config.clipConf.infinite || ic != null && ic.tryConsumeItem(myItem.config.clipConf.example)) {
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
        if (shot) {
            Vector2 gunSpd = creator.getSpd();
            shoot(gunSpd, game, gunAngle, muzzlePos, faction, creator);
        } else {
            myCurrAngleVar = SolMath.approach(myCurrAngleVar, myItem.config.minAngleVar, myItem.config.angleVarDamp * ts);
        }
        if (myLightSrc != null) {
            myLightSrc.update(shot, baseAngle, game);
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
