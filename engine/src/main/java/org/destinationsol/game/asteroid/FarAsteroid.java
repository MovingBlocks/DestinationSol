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
package org.destinationsol.game.asteroid;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import org.destinationsol.common.SolMath;
import org.destinationsol.game.FarObj;
import org.destinationsol.game.RemoveController;
import org.destinationsol.game.SolGame;
import org.destinationsol.game.SolObject;

import java.util.List;

public class FarAsteroid implements FarObj {
    private Vector2 position;
    private final float angle;
    private final RemoveController removeController;
    private final float size;
    private final Vector2 speed;
    private final float rotateSpeed;
    private final TextureAtlas.AtlasRegion tex;

    public FarAsteroid(TextureAtlas.AtlasRegion tex, Vector2 position, float angle, RemoveController removeController,
                       float size, Vector2 speed, float rotateSpeed) {
        this.tex = tex;
        this.position = position;
        this.angle = angle;
        this.removeController = removeController;
        this.size = size;
        this.speed = speed;
        this.rotateSpeed = rotateSpeed;
    }

    @Override
    public boolean shouldBeRemoved(SolGame game) {
        return removeController != null && removeController.shouldRemove(position);
    }

    @Override
    public SolObject toObj(SolGame game) {
        adjustDesiredPosition(game);
        return game.getAsteroidBuilder().build(game, position, tex, size, angle, rotateSpeed, speed, removeController);
    }

    /**
     * Ensures that this asteroid does not overlap any other asteroid, by adjusting its position
     */
    private void adjustDesiredPosition(SolGame game) {
        List<SolObject> objects = game.getObjMan().getObjs();
        for (SolObject object : objects) {
            if (object instanceof Asteroid) {
                Asteroid asteroid = (Asteroid) object;
                // Check if the positions overlap
                Vector2 asteroidPosition = asteroid.getPosition();
                Vector2 distanceFromAsteroid = SolMath.distVec(asteroidPosition, position);
                float distance = SolMath.hypotenuse(distanceFromAsteroid.x, distanceFromAsteroid.y);
                if (distance <= asteroid.getSize() && distance <= size) {
                    if (asteroid.getSize() > size) {
                        distanceFromAsteroid.scl((asteroid.getSize() + .5f) / distance);
                    }
                    else {
                        distanceFromAsteroid.scl((size + .5f) / distance);
                    }
                    position = asteroidPosition.cpy().add(distanceFromAsteroid);
                    SolMath.free(SolMath.distVec(asteroidPosition, position));
                }
                SolMath.free(distanceFromAsteroid);
            }
        }
    }

    @Override
    public void update(SolGame game) {
    }

    @Override
    public float getRadius() {
        return size;
    }

    @Override
    public Vector2 getPos() {
        return position;
    }

    @Override
    public String toDebugString() {
        return null;
    }

    @Override
    public boolean hasBody() {
        return true;
    }
}