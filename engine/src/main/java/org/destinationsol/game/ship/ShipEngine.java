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

package org.destinationsol.game.ship;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import org.destinationsol.common.SolMath;
import org.destinationsol.game.SolGame;
import org.destinationsol.game.SolObject;
import org.destinationsol.game.drawables.Drawable;
import org.destinationsol.game.drawables.DrawableLevel;
import org.destinationsol.game.input.Pilot;
import org.destinationsol.game.item.Engine;
import org.destinationsol.game.particle.EffectConfig;
import org.destinationsol.game.particle.LightSrc;
import org.destinationsol.game.particle.PartMan;
import org.destinationsol.game.particle.ParticleSrc;

import java.util.ArrayList;
import java.util.List;

public class ShipEngine {
    public static final float MAX_RECOVER_ROT_SPD = 5f;
    public static final float RECOVER_MUL = 15f;
    public static final float RECOVER_AWAIT = 2f;

    private final ParticleSrc myFlameSrc1;
    private final ParticleSrc myFlameSrc2;
    private final LightSrc myLightSrc1;
    private final LightSrc myLightSrc2;
    private final Engine myItem;
    private final List<Drawable> myDrawables;
    private float myRecoverAwait;
    
    private final float acceleration;
    private final float topSpeedModifier;

    public ShipEngine(SolGame game, Engine engineItem, Vector2 e1RelPos, Vector2 e2RelPos, SolShip ship) {
        myItem = engineItem;
        myDrawables = new ArrayList<>();
        EffectConfig effectConfig = myItem.getEffectConfig();
        Vector2 shipPos = ship.getPosition();
        Vector2 shipSpd = ship.getSpd();
        myFlameSrc1 = new ParticleSrc(effectConfig, -1, DrawableLevel.PART_BG_0, e1RelPos, true, game, shipPos, shipSpd, 0);
        myDrawables.add(myFlameSrc1);
        myFlameSrc2 = new ParticleSrc(effectConfig, -1, DrawableLevel.PART_BG_0, e2RelPos, true, game, shipPos, shipSpd, 0);
        myDrawables.add(myFlameSrc2);
        float lightSz = effectConfig.sz * 2.5f;
        myLightSrc1 = new LightSrc(lightSz, true, .7f, new Vector2(e1RelPos), effectConfig.tint);
        myLightSrc1.collectDras(myDrawables);
        myLightSrc2 = new LightSrc(lightSz, true, .7f, new Vector2(e2RelPos), effectConfig.tint);
        myLightSrc2.collectDras(myDrawables);
        acceleration = engineItem.getAcc();
        topSpeedModifier = engineItem.getTopSpeedMultiplier();
    }

    public List<Drawable> getDrawables() {
        return myDrawables;
    }

    public void update(float angle, SolGame game, Pilot provider, Body body, Vector2 spd, SolObject owner,
                       boolean controlsEnabled, float mass) {
        boolean working = applyInput(game, angle, provider, body, spd, controlsEnabled, mass);

        myFlameSrc1.setWorking(working);
        myFlameSrc2.setWorking(working);

        myLightSrc1.update(working, angle, game);
        myLightSrc2.update(working, angle, game);
        if (working) {
            game.getSoundManager().play(game, myItem.getWorkSound(), myFlameSrc1.getPos(), owner); // hack with pos
        }
    }

    private boolean applyInput(SolGame cmp, float shipAngle, Pilot provider, Body body, Vector2 spd,
                               boolean controlsEnabled, float mass) {
        boolean spdOk = SolMath.canAccelerateCustom(shipAngle, spd, topSpeedModifier);
        boolean working = controlsEnabled && provider.isUp() && spdOk;

        Engine engineItem = myItem;
        if (working) {
            Vector2 v = SolMath.fromAl(shipAngle, mass * acceleration);
            body.applyForceToCenter(v, true);
            SolMath.free(v);
        }

        float timeStep = cmp.getTimeStep();
        float rotSpd = body.getAngularVelocity() * SolMath.radDeg;
        float desiredRotSpd = 0;
        float rotAcc = engineItem.getRotAcc();
        boolean leftOn = controlsEnabled && provider.isLeft();
        boolean rightOn = controlsEnabled && provider.isRight();
        float absRotSpd = SolMath.abs(rotSpd);
        if (absRotSpd < engineItem.getMaxRotSpd() && leftOn != rightOn) {
            desiredRotSpd = SolMath.toInt(rightOn) * engineItem.getMaxRotSpd();
            if (absRotSpd < MAX_RECOVER_ROT_SPD) {
                if (myRecoverAwait > 0) {
                    myRecoverAwait -= timeStep;
                }
                if (myRecoverAwait <= 0) {
                    rotAcc *= RECOVER_MUL;
                }
            }
        } else {
            myRecoverAwait = RECOVER_AWAIT;
        }
        body.setAngularVelocity(SolMath.degRad * SolMath.approach(rotSpd, desiredRotSpd, rotAcc * timeStep));
        return working;
    }

    public void onRemove(SolGame game, Vector2 basePos) {
        PartMan pm = game.getPartMan();
        pm.finish(game, myFlameSrc1, basePos);
        pm.finish(game, myFlameSrc2, basePos);
    }

    public Engine getItem() {
        return myItem;
    }
}
