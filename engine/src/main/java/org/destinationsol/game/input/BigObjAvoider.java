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
package org.destinationsol.game.input;

import com.badlogic.gdx.math.Vector2;
import org.destinationsol.Const;
import org.destinationsol.common.SolMath;
import org.destinationsol.game.SolGame;
import org.destinationsol.game.planet.Planet;

class BigObjAvoider {

    private static final float MAX_DIST_LEN = 2 * (Const.MAX_GROUND_HEIGHT + Const.ATM_HEIGHT);
    private Vector2 myProj; // TODO replace name with something logical, if you can find out what

    BigObjAvoider() {
        myProj = new Vector2();
    }

    float avoid(SolGame game, Vector2 from, Vector2 destination, float angleToDestination) {
        float distanceToDestination = from.dst(destination);
        if (distanceToDestination > MAX_DIST_LEN) {
            distanceToDestination = MAX_DIST_LEN;
        }
        float result = angleToDestination;
        Planet planet = game.getPlanetManager().getNearestPlanet(from);
        Vector2 planetPosition = planet.getPosition();
        float planetRadius = planet.getFullHeight();
        if (destination.dst(planetPosition) < planetRadius) {
            planetRadius = planet.getGroundHeight();
        }
        myProj.set(planetPosition);
        myProj.sub(from);
        SolMath.rotate(myProj, -angleToDestination);
        if (0 < myProj.x && myProj.x < distanceToDestination) {
            if (SolMath.abs(myProj.y) < planetRadius) {
                distanceToDestination = myProj.x;
                result = angleToDestination + 45 * SolMath.toInt(myProj.y < 0);
            }
        }
        Vector2 sunPos = planet.getSystem().getPosition();
        float sunRad = Const.SUN_RADIUS;
        myProj.set(sunPos);
        myProj.sub(from);
        SolMath.rotate(myProj, -angleToDestination);
        if (0 < myProj.x && myProj.x < distanceToDestination) {
            if (SolMath.abs(myProj.y) < sunRad) {
                result = angleToDestination + 45 * SolMath.toInt(myProj.y < 0);
            }
        }
        return result;
    }
}
