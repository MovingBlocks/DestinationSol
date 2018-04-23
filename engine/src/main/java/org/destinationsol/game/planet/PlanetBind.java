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
    private final Planet planet;
    private final Vector2 relativePosition;
    private final float relativeAngle;

    private PlanetBind(Planet planet, Vector2 position, float angle) {
        this.planet = planet;
        relativePosition = new Vector2();
        float planetAngle = planet.getAngle();
        SolMath.toRel(position, relativePosition, planetAngle, planet.getPosition());
        relativeAngle = angle - planetAngle;
    }

    public static PlanetBind tryBind(SolGame game, Vector2 position, float angle) {
        Planet planet = game.getPlanetManager().getNearestPlanet(position);
        if (!planet.isNearGround(position)) {
            return null;
        }
        return new PlanetBind(planet, position, angle);
    }

    public void setDiff(Vector2 diff, Vector2 position, boolean precise) {
        SolMath.toWorld(diff, relativePosition, planet.getAngle(), planet.getPosition());
        diff.sub(position);
    }

    public float getDesiredAngle() {
        return planet.getAngle() + relativeAngle;
    }

    public Planet getPlanet() {
        return planet;
    }
}
