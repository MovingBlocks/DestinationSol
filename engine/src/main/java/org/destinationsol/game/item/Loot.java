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

package org.destinationsol.game.item;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import org.destinationsol.common.SolMath;
import org.destinationsol.game.DmgType;
import org.destinationsol.game.FarObject;
import org.destinationsol.game.SolGame;
import org.destinationsol.game.SolObject;
import org.destinationsol.game.drawables.Drawable;
import org.destinationsol.game.particle.LightSource;
import org.destinationsol.game.ship.SolShip;

import java.util.List;

public class Loot implements SolObject {

    public static final int MAX_ROT_SPD = 4;
    public static final float MAX_SPD = .2f;
    public static final int MAX_LIFE = 6;
    private static final float DURABILITY = 70f;
    private static final float PULL_DESIRED_SPD = 1f;
    private static final float PULL_FORCE = .1f;
    private static final float MAX_OWNER_AWAIT = 4f;
    private final SolItem item;
    private final List<Drawable> drawables;
    private final LightSource lightSource;
    private final Vector2 position;
    private final Body body;
    private final float mass;

    private SolShip owner;
    private float ownerAwait;
    private int life;
    private float angle;

    Loot(SolItem item, Body body, int life, List<Drawable> drawables, LightSource ls, SolShip owner) {
        this.body = body;
        this.life = life;
        this.item = item;
        this.drawables = drawables;
        lightSource = ls;
        this.owner = owner;
        ownerAwait = MAX_OWNER_AWAIT;
        position = new Vector2();
        mass = this.body.getMass();
        setParamsFromBody();
    }

    @Override
    public void update(SolGame game) {
        setParamsFromBody();
        lightSource.update(true, angle, game);
        if (ownerAwait > 0) {
            ownerAwait -= game.getTimeStep();
            if (ownerAwait <= 0) {
                owner = null;
            }
        }
        SolShip puller = null;
        float minDist = Float.MAX_VALUE;
        List<SolObject> objs = game.getObjectManager().getObjects();
        for (SolObject o : objs) {
            if (!(o instanceof SolShip)) {
                continue;
            }
            SolShip ship = (SolShip) o;
            if (!ship.getPilot().collectsItems()) {
                continue;
            }
            if (!(item instanceof MoneyItem) && !ship.getItemContainer().canAdd(item)) {
                continue;
            }
            float dst = ship.getPosition().dst(position);
            if (minDist < dst) {
                continue;
            }
            puller = ship;
            minDist = dst;
        }
        if (puller != null) {
            maybePulled(puller, puller.getPosition(), puller.getPullDist());
        }
    }

    private void setParamsFromBody() {
        position.set(body.getPosition());
        angle = body.getAngle() * MathUtils.radDeg;
    }

    @Override
    public boolean shouldBeRemoved(SolGame game) {
        return life <= 0;
    }

    @Override
    public void onRemove(SolGame game) {
        body.getWorld().destroyBody(body);
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

    @Override
    public Vector2 getPosition() {
        return position;
    }

    @Override
    public FarObject toFarObject() {
        return null;
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
    public Vector2 getVelocity() {
        return null;
    }

    @Override
    public void handleContact(SolObject other, float absImpulse,
                              SolGame game, Vector2 collPos) {
        float dmg = absImpulse / mass / DURABILITY;
        receiveDmg((int) dmg, game, collPos, DmgType.CRASH);
    }

    @Override
    public String toDebugString() {
        return null;
    }

    @Override
    public Boolean isMetal() {
        return true;
    }

    @Override
    public boolean hasBody() {
        return true;
    }

    public void maybePulled(SolShip ship, Vector2 pullerPos, float radius) {
        if (ship == owner) {
            return;
        }
        Vector2 toPuller = SolMath.getVec(pullerPos);
        toPuller.sub(getPosition());
        float pullerDist = toPuller.len();
        if (0 < pullerDist && pullerDist < radius) {
            toPuller.scl(PULL_DESIRED_SPD / pullerDist);
            Vector2 velocity = body.getLinearVelocity();
            Vector2 velocityDiff = SolMath.distVec(velocity, toPuller);
            float speedDiff = velocityDiff.len();
            if (speedDiff > 0) {
                velocityDiff.scl(PULL_FORCE / speedDiff);
                body.applyForceToCenter(velocityDiff, true);
            }
            SolMath.free(velocityDiff);
        }
        SolMath.free(toPuller);
    }

    public SolItem getItem() {
        return life > 0 ? item : null;
    }

    public void setLife(int life) {
        this.life = life;
    }

    public SolShip getOwner() {
        return owner;
    }

    public void pickedUp(SolGame game, SolShip ship) {
        life = 0;
        Vector2 velocity = new Vector2(ship.getPosition());
        velocity.sub(position);
        float fadeTime = .25f;
        velocity.scl(1 / fadeTime);
        velocity.add(ship.getVelocity());
        game.getPartMan().blip(game, position, angle, item.getItemType().sz, fadeTime, velocity, item.getIcon(game));
        game.getSoundManager().play(game, item.getItemType().pickUpSound, null, this);
    }
}
