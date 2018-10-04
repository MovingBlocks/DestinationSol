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
package org.destinationsol.game;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

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

    /**
     * Called on every game's frame, allows for handling of object-specific runtime needs.
     * <p>
     * This method should be much simpler than the {@link SolObject#update(SolGame)}. It can be used for instance for
     * sort of simplified movement for ships.
     *
     * @param game Game this object belongs to.
     */
    void update(SolGame game);

    /**
     * Returns the approximate radius the object is taking up, computed from the point returned by {@link #getPosition()}.
     * <p>
     * This radius can be used for easy checking of space taken by object, and should be taken in account when computing a distance.
     *
     * @return Approximate radius of this object.
     */
    float getRadius();

    /**
     * Returns a position of the center of this object.
     * <p>
     * This can be considered static through the life of the object, unless it specifies a specific movement algorithm in
     * {@link #update(SolGame)}. This method is used in combination with {@link #getRadius()} to compute distance to this object.
     *
     * @return Position of this object.
     */
    Vector2 getPosition();

    /**
     * Used for retrieval of object's debug string.
     * <p>
     * Any kind of information can be used in the debug string, if you don't need/want to display any debug information,
     * you can freely have this method return null. To display debug strings in-game, set the flag {@link DebugOptions#OBJ_INFO}.
     * These strings should then be rendered in the proximity of their objects.
     *
     * @return Debug string with information about the object.
     */
    String toDebugString();

    /**
     * Denotes whether the {@link SolObject} corresponding to this object has a {@link Body} associated with it.
     * <p>
     * Generally, everything that can be touched has its {@code Body}.
     *
     * @return True if the {@code SolObject} has {@code Body} associated, false otherwise.
     */
    boolean hasBody();
}
