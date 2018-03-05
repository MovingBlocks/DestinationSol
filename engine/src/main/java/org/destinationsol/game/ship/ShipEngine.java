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
import org.destinationsol.game.input.Pilot;
import org.destinationsol.game.item.Engine;
import org.destinationsol.game.ship.hulls.Hull;

public class ShipEngine {
    public static final float MAX_RECOVER_ROT_SPD = 5f;
    public static final float RECOVER_MUL = 15f;
    public static final float RECOVER_AWAIT = 2f;

    private final Engine myItem;
    private float myRecoverAwait;

    public ShipEngine(Engine engine) {
        myItem = engine;
    }

    public void update(float angle, SolGame game, Pilot provider, Body body, Vector2 spd, SolObject owner,
                       boolean controlsEnabled, float mass, Hull hull) {

        boolean working = applyInput(game, angle, provider, body, spd, controlsEnabled, mass);
        game.getPartMan().updateAllHullEmittersOfType(hull, "engine", working);
        if (working) {
            game.getSoundManager().play(game, myItem.getWorkSound(), owner.getPosition(), owner);
        }
    }

    private boolean applyInput(SolGame cmp, float shipAngle, Pilot provider, Body body, Vector2 spd,
                               boolean controlsEnabled, float mass) {
        boolean spdOk = SolMath.canAccelerate(shipAngle, spd);
        boolean working = controlsEnabled && provider.isUp() && spdOk;

        Engine e = myItem;
        if (working) {
            Vector2 v = SolMath.fromAl(shipAngle, mass * e.getAcc());
            body.applyForceToCenter(v, true);
            SolMath.free(v);
        }

        float ts = cmp.getTimeStep();
        float rotSpd = body.getAngularVelocity() * SolMath.radDeg;
        float desiredRotSpd = 0;
        float rotAcc = e.getRotAcc();
        boolean l = controlsEnabled && provider.isLeft();
        boolean r = controlsEnabled && provider.isRight();
        float absRotSpd = SolMath.abs(rotSpd);
        if (absRotSpd < e.getMaxRotSpd() && l != r) {
            desiredRotSpd = SolMath.toInt(r) * e.getMaxRotSpd();
            if (absRotSpd < MAX_RECOVER_ROT_SPD) {
                if (myRecoverAwait > 0) {
                    myRecoverAwait -= ts;
                }
                if (myRecoverAwait <= 0) {
                    rotAcc *= RECOVER_MUL;
                }
            }
        } else {
            myRecoverAwait = RECOVER_AWAIT;
        }
        body.setAngularVelocity(SolMath.degRad * SolMath.approach(rotSpd, desiredRotSpd, rotAcc * ts));
        return working;
    }

    public Engine getItem() {
        return myItem;
    }
}
