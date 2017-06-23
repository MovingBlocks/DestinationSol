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

package org.destinationsol.game.ship.hulls;

import com.badlogic.gdx.math.Vector2;
import org.destinationsol.common.Immutable;
import org.destinationsol.common.NotNull;

/**
 * Gun slot of a ship hull, to which a gun can be attached.
 */
@Immutable
public final class GunSlot {

    private final
    @NotNull
    Vector2 position;
    private final boolean isUnderneathHull;
    private final boolean allowsRotation;

    public GunSlot(@NotNull Vector2 position, boolean isUnderneathHull, boolean allowsRotation) {
        if (position == null) {
            throw new IllegalArgumentException("Position cannot be null");
        }

        this.position = new Vector2(position);
        this.isUnderneathHull = isUnderneathHull;
        this.allowsRotation = allowsRotation;
    }

    /**
     * Returns the position, relative to the ship hull origin that owns the slot.
     *
     * @return The position, relative to the ship hull origin that owns the slot.
     */
    public
    @NotNull
    Vector2 getPosition() {
        return position;
    }

    /**
     * Returns true if the gun slot is underneath the hull.
     * Returns false if the gun slot is on the top of the hull.
     *
     * @return True if the gun slot is underneath the hull, false otherwise.
     */
    public boolean isUnderneathHull() {
        return isUnderneathHull;
    }

    /**
     * Returns true if the gun slot allows guns mounted at the slot to rotate by default.
     *
     * @return True if mounted guns can rotate by default, false otherwise.
     */
    public boolean allowsRotation() {
        return allowsRotation;
    }
}
