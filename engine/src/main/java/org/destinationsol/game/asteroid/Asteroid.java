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
package org.destinationsol.game.asteroid;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import org.destinationsol.common.SolMath;
import org.destinationsol.game.DmgType;
import org.destinationsol.game.FarObj;
import org.destinationsol.game.RemoveController;
import org.destinationsol.game.SolGame;
import org.destinationsol.game.SolObject;
import org.destinationsol.game.drawables.Drawable;
import org.destinationsol.game.item.Loot;
import org.destinationsol.game.item.MoneyItem;
import org.destinationsol.game.item.SolItem;
import org.destinationsol.game.particle.ParticleSrc;
import org.destinationsol.game.planet.Planet;
import org.destinationsol.game.planet.TileObject;

import java.util.ArrayList;
import java.util.List;

public class Asteroid implements SolObject {
    private static final float MIN_SPLIT_SZ = .25f;
    private static final float MIN_BURN_SZ = .3f;
    private static final float SZ_TO_LIFE = 20f;
    private static final float SPD_TO_ATM_DMG = SZ_TO_LIFE * .11f;
    private static final float MAX_SPLIT_SPD = 1f;
    private static final float DUR = .5f;

    private final Body myBody;
    private final Vector2 myPos;
    private final Vector2 mySpd;
    private final ArrayList<Drawable> myDrawables;
    private final TextureAtlas.AtlasRegion myTex;
    private final RemoveController myRemoveController;
    private final ParticleSrc mySmokeSrc;
    private final ParticleSrc myFireSrc;
    private final float myMass;

    private float myAngle;
    private float myLife;
    private float mySize;

    public Asteroid(SolGame game, TextureAtlas.AtlasRegion tex, Body body, float size, RemoveController removeController, ArrayList<Drawable> drawables) {
        myTex = tex;
        myRemoveController = removeController;
        myDrawables = drawables;
        myBody = body;
        mySize = size;
        myLife = SZ_TO_LIFE * mySize;
        myPos = new Vector2();
        mySpd = new Vector2();
        myMass = myBody.getMass();
        setParamsFromBody();
        List<ParticleSrc> effs = game.getSpecialEffects().buildBodyEffs(size / 2, game, myPos, mySpd);
        mySmokeSrc = effs.get(0);
        myFireSrc = effs.get(1);
        myDrawables.add(mySmokeSrc);
        myDrawables.add(myFireSrc);
    }

    @Override
    public Vector2 getPosition() {
        return myPos;
    }

    @Override
    public FarObj toFarObj() {
        float rotSpd = myBody.getAngularVelocity();
        return new FarAsteroid(myTex, myPos, myAngle, myRemoveController, mySize, mySpd, rotSpd);
    }

    @Override
    public List<Drawable> getDrawables() {
        return myDrawables;
    }

    @Override
    public float getAngle() {
        return myAngle;
    }

    @Override
    public Vector2 getSpd() {
        return mySpd;
    }

    @Override
    public void handleContact(SolObject other, ContactImpulse impulse, boolean isA, float absImpulse, SolGame game, Vector2 collPos) {
        float dmg;
        if (other instanceof TileObject && MIN_BURN_SZ < mySize) {
            dmg = myLife;
        } else {
            dmg = absImpulse / myMass / DUR;
        }
        receiveDmg(dmg, game, collPos, DmgType.CRASH);
    }

    @Override
    public String toDebugString() {
        return null;
    }

    @Override
    public Boolean isMetal() {
        return false;
    }

    @Override
    public boolean hasBody() {
        return true;
    }

    @Override
    public void update(SolGame game) {
        boolean burning = updateInAtm(game);
        mySmokeSrc.setWorking(burning);
        myFireSrc.setWorking(burning);
        setParamsFromBody();
    }

    private boolean updateInAtm(SolGame game) {
        Planet np = game.getPlanetMan().getNearestPlanet();
        float dst = np.getPos().dst(myPos);
        if (np.getFullHeight() < dst) {
            return false;
        }
        if (MIN_BURN_SZ >= mySize) {
            return false;
        }

        float dmg = myBody.getLinearVelocity().len() * SPD_TO_ATM_DMG * game.getTimeStep();
        receiveDmg(dmg, game, null, DmgType.FIRE);
        return true;
    }

    private void setParamsFromBody() {
        myPos.set(myBody.getPosition());
        mySpd.set(myBody.getLinearVelocity());
        myAngle = myBody.getAngle() * SolMath.radDeg;
    }

    @Override
    public boolean shouldBeRemoved(SolGame game) {
        return myLife <= 0 || myRemoveController != null && myRemoveController.shouldRemove(myPos);
    }

    @Override
    public void onRemove(SolGame game) {
        game.getPartMan().finish(game, mySmokeSrc, myPos);
        game.getPartMan().finish(game, myFireSrc, myPos);
        myBody.getWorld().destroyBody(myBody);
        if (myLife <= 0) {
            game.getSpecialEffects().asteroidDust(game, myPos, mySpd, mySize);
            float vol = SolMath.clamp(mySize / .5f);
            game.getSoundManager().play(game, game.getSpecialSounds().asteroidCrack, null, this, vol);
            maybeSplit(game);
        }
    }

    private void maybeSplit(SolGame game) {
        if (MIN_SPLIT_SZ > mySize) {
            return;
        }
        float sclSum = 0;
        while (sclSum < .7f * mySize * mySize) {
            float spdAngle = SolMath.rnd(180);
            Vector2 spd = new Vector2();
            SolMath.fromAl(spd, spdAngle, SolMath.rnd(0, .5f) * MAX_SPLIT_SPD);
            spd.add(mySpd);
            Vector2 newPos = new Vector2();
            SolMath.fromAl(newPos, spdAngle, SolMath.rnd(0, mySize / 2));
            newPos.add(myPos);
            float sz = mySize * SolMath.rnd(.25f, .5f);
            Asteroid a = game.getAsteroidBuilder().buildNew(game, newPos, spd, sz, myRemoveController);
            game.getObjMan().addObjDelayed(a);
            sclSum += a.mySize * a.mySize;
        }
        float thrMoney = mySize * 40f * SolMath.rnd(.3f, 1);
        List<MoneyItem> moneyItems = game.getItemMan().moneyToItems(thrMoney);
        for (MoneyItem mi : moneyItems) {
            throwLoot(game, mi);
        }
    }

    private void throwLoot(SolGame game, SolItem item) {
        float spdAngle = SolMath.rnd(180);
        Vector2 lootSpd = new Vector2();
        SolMath.fromAl(lootSpd, spdAngle, SolMath.rnd(0, Loot.MAX_SPD));
        lootSpd.add(mySpd);
        Vector2 pos = new Vector2();
        SolMath.fromAl(pos, spdAngle, SolMath.rnd(0, mySize / 2));
        pos.add(myPos);
        Loot l = game.getLootBuilder().build(game, pos, item, lootSpd, Loot.MAX_LIFE, SolMath.rnd(Loot.MAX_ROT_SPD), null);
        game.getObjMan().addObjDelayed(l);
    }

    @Override
    public void receiveDmg(float dmg, SolGame game, Vector2 pos, DmgType dmgType) {
        myLife -= dmg;
        game.getSpecialSounds().playHit(game, this, pos, dmgType);
    }

    @Override
    public boolean receivesGravity() {
        return true;
    }

    @Override
    public void receiveForce(Vector2 force, SolGame game, boolean acc) {
        if (acc) {
            force.scl(myMass);
        }
        myBody.applyForceToCenter(force, true);
    }

    public float getLife() {
        return myLife;
    }
}

