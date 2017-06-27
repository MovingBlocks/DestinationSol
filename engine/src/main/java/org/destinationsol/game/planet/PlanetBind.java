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
package org.destinationsol.game.planet;

import com.badlogic.gdx.math.Vector2;
import org.destinationsol.common.SolMath;
import org.destinationsol.game.SolGame;

public class PlanetBind {
    private final Planet myPlanet;
    private final Vector2 myRelPos;
    private final float myRelAngle;

    public PlanetBind(Planet planet, Vector2 pos, float angle) {
        myPlanet = planet;
        myRelPos = new Vector2();
        float planetAngle = planet.getAngle();
        SolMath.toRel(pos, myRelPos, planetAngle, planet.getPos());
        myRelAngle = angle - planetAngle;
    }

    public static PlanetBind tryBind(SolGame game, Vector2 pos, float angle) {
        Planet np = game.getPlanetMan().getNearestPlanet(pos);
        if (!np.isNearGround(pos)) {
            return null;
        }
        return new PlanetBind(np, pos, angle);
    }

    public void setDiff(Vector2 diff, Vector2 pos, boolean precise) {
        SolMath.toWorld(diff, myRelPos, myPlanet.getAngle(), myPlanet.getPos(), precise);
        diff.sub(pos);
    }

    public float getDesiredAngle() {
        return myPlanet.getAngle() + myRelAngle;
    }

    public Planet getPlanet() {
        return myPlanet;
    }
}
