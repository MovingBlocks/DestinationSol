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
package org.destinationsol.game;

import com.badlogic.gdx.math.Vector2;

/**
 * FarObject can be generally considered a {@link SolObject} simplified in terms of runtime code.
 * <p>
 * Generally speaking, every {@code SolObject} that gets too far away from player/camera, is transformed to a
 * {@code FarObject} or deleted, with few specific exceptions. {@code FarObject}s then persist still in the game until
 * the player/camera gets close to them again, at which point they are again transformed into their specific
 * {@code SolObjects}. {@code FarObject}s generally have no body associated with them and do not inetract with the game much.
 */
public interface FarObject {

    /**
     * Denotes whether the object should be removed as of the time of calling.
     * <p>
     * This method should return false if the object is meant to persist for the whole game, like for instance
     * {@link StarPort}. Otherwise, it usually checks for its {@link RemoveController} as to whether it should be removed.
     *
     * @param game Game this object belongs to.
     * @return Boolean denoting whether the object should be removed.
     */
    boolean shouldBeRemoved(SolGame game);

    /**
     * Creates a new {@link SolObject} similar to the one used for creation of this object by call to {@link SolObject#toFarObject()}
     * <p>
     * The created object should be by all terms similar or same as the {@code SolObject} that created this object.
     *
     * @param game Game this object belongs to.
     * @return SolObject representation of this object.
     */
    SolObject toObject(SolGame game);

    void update(SolGame game);

    float getRadius();

    Vector2 getPosition();

    String toDebugString();

    boolean hasBody();
}
