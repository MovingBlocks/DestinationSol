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
import org.destinationsol.common.SolMath;
import org.destinationsol.common.SolRandom;
import org.destinationsol.game.DmgType;
import org.destinationsol.game.FarObject;
import org.destinationsol.game.RemoveController;
import org.destinationsol.game.SolGame;
import org.destinationsol.game.SolObject;
import org.destinationsol.game.TimeProvider;
import org.destinationsol.game.drawables.Drawable;
import org.destinationsol.game.item.Loot;
import org.destinationsol.game.item.MoneyItem;
import org.destinationsol.game.item.SolItem;
import org.destinationsol.game.particle.DSParticleEmitter;
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

    private final Body body;
    private final Vector2 position;
    private final Vector2 speed;
    private final ArrayList<Drawable> drawables;
    private final TextureAtlas.AtlasRegion texture;
    private final RemoveController removeController;
    private final DSParticleEmitter smokeSource;
    private final DSParticleEmitter fireSource;
    private final float mass;

    private float angle;
    private float life;
    private float size;

    Asteroid(SolGame game, TextureAtlas.AtlasRegion tex, Body body, float size, RemoveController removeController, ArrayList<Drawable> drawables) {
        texture = tex;
        this.removeController = removeController;
        this.drawables = drawables;
        this.body = body;
        this.size = size;
        life = SZ_TO_LIFE * size;
        position = new Vector2();
        speed = new Vector2();
        mass = body.getMass();
        setParamsFromBody();
        List<DSParticleEmitter> effects = game.getSpecialEffects().buildBodyEffs(size / 2, game, position, speed);
        smokeSource = effects.get(0);
        fireSource = effects.get(1);
        this.drawables.addAll(smokeSource.getDrawables());
        this.drawables.addAll(fireSource.getDrawables());
    }

    @Override
    public Vector2 getPosition() {
        return position;
    }

    @Override
    public FarObject toFarObject() {
        float rotationSpeed = body.getAngularVelocity();
        return new FarAsteroid(texture, position, angle, removeController, size, speed, rotationSpeed);
    }

    @Override
    public List<Drawable> getDrawables() {
        return drawables;
    }

    @Override
    public float getAngle() {
        return angle;
    }

    @Override
    public Vector2 getSpeed() {
        return speed;
    }

    @Override
    public void handleContact(SolObject other, float absImpulse, SolGame game, Vector2 collPos) {
        float dmg;
        if (other instanceof TileObject && MIN_BURN_SZ < size) {
            dmg = life;
        } else {
            dmg = absImpulse / mass / DUR;
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
        smokeSource.setWorking(burning);
        fireSource.setWorking(burning);
        setParamsFromBody();
    }

    private boolean updateInAtm(SolGame game) {
        Planet np = game.getPlanetManager().getNearestPlanet();
        float dst = np.getPosition().dst(position);
        if (np.getFullHeight() < dst) {
            return false;
        }
        if (MIN_BURN_SZ >= size) {
            return false;
        }

        float dmg = body.getLinearVelocity().len() * SPD_TO_ATM_DMG * TimeProvider.getTimeStep();
        receiveDmg(dmg, game, null, DmgType.FIRE);
        return true;
    }

    private void setParamsFromBody() {
        position.set(body.getPosition());
        speed.set(body.getLinearVelocity());
        angle = body.getAngle() * SolMath.radDeg;
    }

    @Override
    public boolean shouldBeRemoved(SolGame game) {
        return life <= 0 || removeController != null && removeController.shouldRemove(position);
    }

    @Override
    public void onRemove(SolGame game) {
        game.getPartMan().finish(game, smokeSource, position);
        game.getPartMan().finish(game, fireSource, position);
        body.getWorld().destroyBody(body);
        if (life <= 0) {
            game.getSpecialEffects().asteroidDust(game, position, speed, size);
            float vol = SolMath.clamp(size / .5f);
            game.getSoundManager().play(game, game.getSpecialSounds().asteroidCrack, null, this, vol);
            maybeSplit(game);
        }
    }

    private void maybeSplit(SolGame game) {
        if (MIN_SPLIT_SZ > size) {
            return;
        }
        float sclSum = 0;
        while (sclSum < .7f * size * size) {
            float speedAngle = SolRandom.randomFloat(180);
            Vector2 speed = new Vector2();
            SolMath.fromAl(speed, speedAngle, SolRandom.randomFloat(0, .5f) * MAX_SPLIT_SPD);
            speed.add(speed);
            Vector2 newPos = new Vector2();
            SolMath.fromAl(newPos, speedAngle, SolRandom.randomFloat(0, size / 2));
            newPos.add(position);
            float sz = size * SolRandom.randomFloat(.25f, .5f);
            Asteroid a = game.getAsteroidBuilder().buildNew(game, newPos, speed, sz, removeController);
            game.getObjectManager().addObjDelayed(a);
            sclSum += a.size * a.size;
        }
        float thrMoney = size * 40f * SolRandom.randomFloat(.3f, 1);
        List<MoneyItem> moneyItems = game.getItemMan().moneyToItems(thrMoney);
        for (MoneyItem mi : moneyItems) {
            throwLoot(game, mi);
        }
    }

    private void throwLoot(SolGame game, SolItem item) {
        float speedAngle = SolRandom.randomFloat(180);
        Vector2 lootSpeed = new Vector2();
        SolMath.fromAl(lootSpeed, speedAngle, SolRandom.randomFloat(0, Loot.MAX_SPD));
        lootSpeed.add(speed);
        Vector2 position = new Vector2();
        SolMath.fromAl(position, speedAngle, SolRandom.randomFloat(0, size / 2));
        position.add(position);
        Loot l = game.getLootBuilder().build(game, position, item, lootSpeed, Loot.MAX_LIFE, SolRandom.randomFloat(Loot.MAX_ROT_SPD), null);
        game.getObjectManager().addObjDelayed(l);
    }

    @Override
    public void receiveDmg(float dmg, SolGame game, Vector2 position, DmgType dmgType) {
        life -= dmg;
        game.getSpecialSounds().playHit(game, this, position, dmgType);
    }

    @Override
    public boolean receivesGravity() {
        return true;
    }

    @Override
    public void receiveForce(Vector2 force, SolGame game, boolean acc) {
        if (acc) {
            force.scl(mass);
        }
        body.applyForceToCenter(force, true);
    }

    public float getLife() {
        return life;
    }
}

