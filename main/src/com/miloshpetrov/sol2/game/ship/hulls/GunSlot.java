package com.miloshpetrov.sol2.game.ship.hulls;

import com.badlogic.gdx.math.Vector2;
import com.miloshpetrov.sol2.common.Immutable;
import com.miloshpetrov.sol2.common.NotNull;

/**
 * Gun slot of a ship hull, to which a gun can be attached.
 */
@Immutable
public final class GunSlot {

    public GunSlot(@NotNull Vector2 position, boolean isUnderneathHull, boolean allowsRotation) {
        if(position == null) {
            throw new IllegalArgumentException("Position cannot be null");
        }

        this.position = new Vector2(position);
        this.isUnderneathHull = isUnderneathHull;
        this.allowsRotation = allowsRotation;
    }

    /**
     * Returns the position, relative to the ship hull origin that owns the slot.
     * @return The position, relative to the ship hull origin that owns the slot.
     */
    public @NotNull Vector2 getPosition() {
        return position;
    }

    /**
     * Returns true if the gun slot is underneath the hull.
     * Returns false if the gun slot is on the top of the hull.
     * @return True if the gun slot is underneath the hull, false otherwise.
     */
    public boolean isUnderneathHull() {
        return isUnderneathHull;
    }

    /**
     * Returns true if the gun slot allows guns mounted at the slot to rotate by default.
     * @return True if mounted guns can rotate by default, false otherwise.
     */
    public boolean allowsRotation() {
        return allowsRotation;
    }

    private final @NotNull Vector2 position;
    private final boolean isUnderneathHull;
    private final boolean allowsRotation;
}
