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

package org.destinationsol.game.item;

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
    public static final float DURABILITY = 70f;
    public static final float PULL_DESIRED_SPD = 1f;
    public static final float PULL_FORCE = .1f;
    public static final float MAX_OWNER_AWAIT = 4f;
    private final SolItem myItem;
    private final List<Drawable> myDrawables;
    private final LightSource myLightSource;
    private final Vector2 myPos;
    private final Body myBody;
    private final float myMass;

    private SolShip myOwner;
    private float myOwnerAwait;
    private int myLife;
    private float myAngle;

    public Loot(SolItem item, Body body, int life, List<Drawable> drawables, LightSource ls, SolShip owner) {
        myBody = body;
        myLife = life;
        myItem = item;
        myDrawables = drawables;
        myLightSource = ls;
        myOwner = owner;
        myOwnerAwait = MAX_OWNER_AWAIT;
        myPos = new Vector2();
        myMass = myBody.getMass();
        setParamsFromBody();
    }

    @Override
    public void update(SolGame game) {
        setParamsFromBody();
        myLightSource.update(true, myAngle, game);
        if (myOwnerAwait > 0) {
            myOwnerAwait -= game.getTimeStep();
            if (myOwnerAwait <= 0) {
                myOwner = null;
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
            if (!(myItem instanceof MoneyItem) && !ship.getItemContainer().canAdd(myItem)) {
                continue;
            }
            float dst = ship.getPosition().dst(myPos);
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
        myPos.set(myBody.getPosition());
        myAngle = myBody.getAngle() * SolMath.radDeg;
    }

    @Override
    public boolean shouldBeRemoved(SolGame game) {
        return myLife <= 0;
    }

    @Override
    public void onRemove(SolGame game) {
        myBody.getWorld().destroyBody(myBody);
    }

    @Override
    public void receiveDmg(float dmg, SolGame game, Vector2 position, DmgType dmgType) {
        myLife -= dmg;
        game.getSpecialSounds().playHit(game, this, position, dmgType);
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

    @Override
    public Vector2 getPosition() {
        return myPos;
    }

    @Override
    public FarObject toFarObject() {
        return null;
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
    public Vector2 getSpeed() {
        return null;
    }

    @Override
    public void handleContact(SolObject other, float absImpulse,
                              SolGame game, Vector2 collPos) {
        float dmg = absImpulse / myMass / DURABILITY;
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
        if (ship == myOwner) {
            return;
        }
        Vector2 toPuller = SolMath.getVec(pullerPos);
        toPuller.sub(getPosition());
        float pullerDist = toPuller.len();
        if (0 < pullerDist && pullerDist < radius) {
            toPuller.scl(PULL_DESIRED_SPD / pullerDist);
            Vector2 speed = myBody.getLinearVelocity();
            Vector2 speedDiff = SolMath.distVec(speed, toPuller);
            float speedDiffLen = speedDiff.len();
            if (speedDiffLen > 0) {
                speedDiff.scl(PULL_FORCE / speedDiffLen);
                myBody.applyForceToCenter(speedDiff, true);
            }
            SolMath.free(speedDiff);
        }
        SolMath.free(toPuller);
    }

    public SolItem getItem() {
        return myLife > 0 ? myItem : null;
    }

    public void setLife(int life) {
        myLife = life;
    }

    public SolShip getOwner() {
        return myOwner;
    }

    public void pickedUp(SolGame game, SolShip ship) {
        myLife = 0;
        Vector2 speed = new Vector2(ship.getPosition());
        speed.sub(myPos);
        float fadeTime = .25f;
        speed.scl(1 / fadeTime);
        speed.add(ship.getSpeed());
        game.getPartMan().blip(game, myPos, myAngle, myItem.getItemType().sz, fadeTime, speed, myItem.getIcon(game));
        game.getSoundManager().play(game, myItem.getItemType().pickUpSound, null, this);
    }
}
