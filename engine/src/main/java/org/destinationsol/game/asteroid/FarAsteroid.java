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
        Vector2 position = adjustDesiredPosition(game, myPos, mySz);
        return game.getAsteroidBuilder().build(game, position, myTex, mySz, myAngle, myRotSpd, mySpd, myRemoveController);
    }

    /**
     * Ensures that the position of any Asteroids do not overlap
     * @param game SolGame instance
     * @param desiredPosition Vector2 position that you would like the Asteroid to spawn at
     * @param size float size of the asteroid
     * @return the new Vector2 position of the asteroid, or desired if it does not overlap
     */
    private static Vector2 adjustDesiredPosition(SolGame game, Vector2 desiredPosition, float size) {
        Vector2 newPosition = desiredPosition;
        List<SolObject> objects = game.getObjMan().getObjs();
        for (SolObject object : objects) {
            if (object instanceof Asteroid) {
                Asteroid asteroid = (Asteroid) object;
                // Check if the positions overlap
                Vector2 fromPosition = asteroid.getPosition();
                Vector2 distanceVector = SolMath.distVec(fromPosition, desiredPosition);
                float distance = SolMath.hypotenuse(distanceVector.x, distanceVector.y);
                if (distance <= asteroid.getSize() && distance <= size) {
                    if (asteroid.getSize() > size) {
                        distanceVector.scl((asteroid.getSize() + .5f) / distance);
                    }
                    else {
                        distanceVector.scl((size + .5f) / distance);
                    }
                    newPosition = fromPosition.cpy().add(distanceVector);
                    SolMath.free(SolMath.distVec(fromPosition, newPosition));
                }
                SolMath.free(distanceVector);
            }
        }
        return newPosition;
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