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
package org.destinationsol.game.asteroid;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import org.destinationsol.game.FarObject;
import org.destinationsol.game.RemoveController;
import org.destinationsol.game.SolGame;
import org.destinationsol.game.SolObject;

public class FarAsteroid implements FarObject {
    private final Vector2 position;
    private final float angle;
    private final RemoveController removeController;
    private final float size;
    private final Vector2 velocity;
    private final float rotationSpeed;
    private final TextureAtlas.AtlasRegion texture;

    public FarAsteroid(TextureAtlas.AtlasRegion texture, Vector2 position, float angle, RemoveController removeController,
                       float size, Vector2 velocity, float rotationSpeed) {
        this.texture = texture;
        this.position = position;
        this.angle = angle;
        this.removeController = removeController;
        this.size = size;
        this.velocity = velocity;
        this.rotationSpeed = rotationSpeed;
    }

    @Override
    public boolean shouldBeRemoved(SolGame game) {
        return removeController != null && removeController.shouldRemove(position);
    }

    @Override
    public SolObject toObject(SolGame game) {
        return game.getAsteroidBuilder().build(game, position, texture, size, angle, rotationSpeed, velocity, removeController);
    }

    @Override
    public void update(SolGame game) {
    }

    @Override
    public float getRadius() {
        return size;
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
        return true;
    }
}
