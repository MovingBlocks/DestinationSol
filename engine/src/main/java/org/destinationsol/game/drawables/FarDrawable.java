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

package org.destinationsol.game.drawables;

import com.badlogic.gdx.math.Vector2;
import org.destinationsol.game.FarObject;
import org.destinationsol.game.RemoveController;
import org.destinationsol.game.SolGame;
import org.destinationsol.game.SolObject;

import java.util.List;

public class FarDrawable implements FarObject {
    private final List<Drawable> myDrawables;
    private final Vector2 myPos;
    private final Vector2 mySpeed;
    private final RemoveController myRemoveController;
    private final float myRadius;
    private final boolean myHideOnPlanet;

    public FarDrawable(List<Drawable> drawables, Vector2 position, Vector2 speed, RemoveController removeController,
                       boolean hideOnPlanet) {
        myDrawables = drawables;
        myPos = position;
        mySpeed = speed;
        myRemoveController = removeController;
        myRadius = DrawableManager.radiusFromDrawables(myDrawables);
        myHideOnPlanet = hideOnPlanet;
    }

    @Override
    public boolean shouldBeRemoved(SolGame game) {
        return myRemoveController != null && myRemoveController.shouldRemove(myPos);
    }

    @Override
    public SolObject toObject(SolGame game) {
        return new DrawableObject(myDrawables, myPos, mySpeed, myRemoveController, false, myHideOnPlanet);
    }

    @Override
    public void update(SolGame game) {
    }

    @Override
    public float getRadius() {
        return myRadius;
    }

    @Override
    public Vector2 getPosition() {
        return myPos;
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
        return myDrawables;
    }
}
