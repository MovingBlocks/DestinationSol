/*
 * Copyright 2015 MovingBlocks
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
import org.destinationsol.common.SolMath;
import org.destinationsol.game.Faction;
import org.destinationsol.game.SolGame;
import org.destinationsol.game.SolObject;
import org.destinationsol.game.dra.Dra;
import org.destinationsol.game.input.Pilot;
import org.destinationsol.game.particle.ParticleSrc;

import java.util.List;

public class ForceBeacon {

    public static final float MAX_PULL_DIST = .7f;
    private final Vector2 myRelPos;
    private final Vector2 myPrevPos;
    private final ParticleSrc myEffect;

    public ForceBeacon(SolGame game, Vector2 relPos, Vector2 basePos, Vector2 baseSpd) {
        myRelPos = relPos;
        myEffect = game.getSpecialEffects().buildForceBeacon(.6f, game, relPos, basePos, baseSpd);
        myEffect.setWorking(true);
        myPrevPos = new Vector2();
    }

    public static SolShip pullShips(SolGame game, SolObject owner, Vector2 ownPos, Vector2 ownSpd, Faction faction,
                                    float maxPullDist) {
        SolShip res = null;
        float minLen = Float.MAX_VALUE;
        List<SolObject> objs = game.getObjMan().getObjs();
        for (int i = 0, objsSize = objs.size(); i < objsSize; i++) {
            SolObject o = objs.get(i);
            if (o == owner) continue;
            if (!(o instanceof SolShip)) continue;
            SolShip ship = (SolShip) o;
            Pilot pilot = ship.getPilot();
            if (pilot.isUp() || pilot.isLeft() || pilot.isRight()) continue;
            if (game.getFactionMan().areEnemies(faction, pilot.getFaction())) continue;
            Vector2 toMe = SolMath.distVec(ship.getPosition(), ownPos);
            float toMeLen = toMe.len();
            if (toMeLen < maxPullDist) {
                if (toMeLen > 1) toMe.scl(1 / toMeLen);
                if (ownSpd != null) toMe.add(ownSpd);
                ship.getHull().getBody().setLinearVelocity(toMe);
                game.getSoundMan().play(game, game.getSpecialSounds().forceBeaconWork, null, ship);
                if (toMeLen < minLen) {
                    res = ship;
                    minLen = toMeLen;
                }
            }
            SolMath.free(toMe);
        }
        return res;
    }

    public void collectDras(List<Dra> dras) {
        dras.add(myEffect);
    }

    public void update(SolGame game, Vector2 basePos, float baseAngle, SolShip ship) {
        Vector2 pos = SolMath.toWorld(myRelPos, baseAngle, basePos);
        Vector2 spd = SolMath.distVec(myPrevPos, pos).scl(1 / game.getTimeStep());
        Faction faction = ship.getPilot().getFaction();
        pullShips(game, ship, pos, spd, faction, MAX_PULL_DIST);
        SolMath.free(spd);
        myPrevPos.set(pos);
        SolMath.free(pos);
    }
}
