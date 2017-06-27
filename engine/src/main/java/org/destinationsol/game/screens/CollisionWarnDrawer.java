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
package org.destinationsol.game.screens;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import org.destinationsol.common.SolMath;
import org.destinationsol.game.SolGame;
import org.destinationsol.game.SolObject;
import org.destinationsol.game.ship.SolShip;

public class CollisionWarnDrawer extends WarnDrawer {
    private final CollisionRayCastCallback warnCallback = new CollisionRayCastCallback();
    private SolShip hero;

    CollisionWarnDrawer(float r) {
        super(r, "Object Near");
    }

    public boolean shouldWarn(SolGame game) {
        hero = game.getHero();
        if (hero == null) {
            return false;
        }
        Vector2 pos = hero.getPosition();
        Vector2 spd = hero.getSpd();
        float acc = hero.getAcc();
        float spdLen = spd.len();
        float spdAngle = SolMath.angle(spd);
        if (acc <= 0 || spdLen < 2 * acc) {
            return false;
        }
        // t = v/a;
        // s = att/2 = vv/a/2;
        float breakWay = spdLen * spdLen / acc / 2;
        breakWay += 2 * spdLen;
        Vector2 finalPos = SolMath.getVec(0, 0);
        SolMath.fromAl(finalPos, spdAngle, breakWay);
        finalPos.add(pos);
        warnCallback.show = false;
        game.getObjMan().getWorld().rayCast(warnCallback, pos, finalPos);
        SolMath.free(finalPos);
        return warnCallback.show;
    }

    private class CollisionRayCastCallback implements RayCastCallback {
        private boolean show;

        @Override
        public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal, float fraction) {
            SolObject o = (SolObject) fixture.getBody().getUserData();
            if (hero == o) {
                return -1;
            }
            show = true;
            return 0;
        }
    }
}
