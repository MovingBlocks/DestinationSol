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

package org.destinationsol.game.drawables;

import com.badlogic.gdx.math.Vector2;
import org.destinationsol.game.FarObject;
import org.destinationsol.game.RemoveController;
import org.destinationsol.game.SolGame;
import org.destinationsol.game.SolObject;

import java.util.List;

public class FarDrawable implements FarObject {
    private final List<Drawable> drawables;
    private final Vector2 position;
    private final Vector2 velocity;
    private final RemoveController removeController;
    private final float radius;
    private final boolean hideOnPlanet;

    public FarDrawable(List<Drawable> drawables, Vector2 position, Vector2 velocity, RemoveController removeController,
                       boolean hideOnPlanet) {
        this.drawables = drawables;
        this.position = position;
        this.velocity = velocity;
        this.removeController = removeController;
        radius = DrawableManager.radiusFromDrawables(this.drawables);
        this.hideOnPlanet = hideOnPlanet;
    }

    @Override
    public boolean shouldBeRemoved(SolGame game) {
        return removeController != null && removeController.shouldRemove(position);
    }

    @Override
    public SolObject toObject(SolGame game) {
        return new DrawableObject(drawables, position, velocity, removeController, false, hideOnPlanet);
    }

    @Override
    public void update(SolGame game) {
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

    public List<Drawable> getDrawables() {
        return drawables;
    }
}
