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
import org.destinationsol.game.FarObj;
import org.destinationsol.game.RemoveController;
import org.destinationsol.game.SolGame;
import org.destinationsol.game.SolObject;

public class FarAsteroid implements FarObj {
    private final Vector2 myPos;
    private final float myAngle;
    private final RemoveController myRemoveController;
    private final float mySz;
    private final Vector2 mySpd;
    private final float myRotSpd;
    private final TextureAtlas.AtlasRegion myTex;

    public FarAsteroid(TextureAtlas.AtlasRegion tex, Vector2 pos, float angle, RemoveController removeController,
                       float sz, Vector2 spd, float rotSpd) {
        myTex = tex;
        myPos = pos;
        myAngle = angle;
        myRemoveController = removeController;
        mySz = sz;
        mySpd = spd;
        myRotSpd = rotSpd;
    }

    @Override
    public boolean shouldBeRemoved(SolGame game) {
        return myRemoveController != null && myRemoveController.shouldRemove(myPos);
    }

    @Override
    public SolObject toObj(SolGame game) {
        return game.getAsteroidBuilder().build(game, myPos, myTex, mySz, myAngle, myRotSpd, mySpd, myRemoveController);
    }

    @Override
    public void update(SolGame game) {
    }

    @Override
    public float getRadius() {
        return mySz;
    }

    @Override
    public Vector2 getPos() {
        return myPos;
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