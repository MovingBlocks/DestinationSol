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
package org.destinationsol.game.planet;

import com.badlogic.gdx.math.Vector2;
import org.destinationsol.common.SolMath;
import org.destinationsol.game.FarObject;
import org.destinationsol.game.SolGame;
import org.destinationsol.game.SolObject;
import org.destinationsol.game.drawables.Drawable;
import org.destinationsol.game.drawables.DrawableManager;

import java.util.List;

public class FarPlanetSprites implements FarObject {
    private final Planet planet;
    private final float distance;
    private final List<Drawable> drawables;
    private final float radius;
    private final float RotationSpeedToPlanet;
    private float relativeAngleToPlanet;
    private Vector2 position;

    public FarPlanetSprites(Planet planet, float relAngleToPlanet, float dist, List<Drawable> drawables,
                            float toPlanetRotationSpeed) {
        this.planet = planet;
        relativeAngleToPlanet = relAngleToPlanet;
        distance = dist;
        this.drawables = drawables;
        radius = DrawableManager.radiusFromDrawables(this.drawables);
        RotationSpeedToPlanet = toPlanetRotationSpeed;
        position = new Vector2();
    }

    @Override
    public boolean shouldBeRemoved(SolGame game) {
        return false;
    }

    @Override
    public SolObject toObject(SolGame game) {
        return new PlanetSprites(planet, relativeAngleToPlanet, distance, drawables, RotationSpeedToPlanet);
    }

    @Override
    public void update(SolGame game) {
        relativeAngleToPlanet += RotationSpeedToPlanet * game.getTimeStep();
        if (game.getPlanetManager().getNearestPlanet() == planet) {
            SolMath.fromAl(position, planet.getAngle() + relativeAngleToPlanet, distance);
            position.add(planet.getPosition());
        }
    }

    @Override
    public float getRadius() {
        return radius;
    }

    @Override
    public Vector2 getPosition() {
        return position;
    }

    @Override
    public String toDebugString() {
        return null;
    }

    @Override
    public boolean hasBody() {
        return false;
    }
}
