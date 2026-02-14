/*
 * Copyright 2026 The Terasology Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.destinationsol.game.faction;

/**
 * A reputation-affecting event raised when a ship (or projectile fired by a ship) causes damage to another.
 */
public class DamageInflictedReputationEvent implements ReputationEvent {
    // This constant is calculated based on backwards compatibility with the old "1 hit = -1 reputation" logic,
    // assuming a normal bullet clip that deals 1.6 units of damage (same as the Heavy Machine Gun at the time).
    public static final float DAMAGE_REPUTATION_MODIFIER = 1.0f / 1.6f;
    private final float damageTaken;

    public DamageInflictedReputationEvent(float damageTaken) {
        this.damageTaken = damageTaken;
    }

    /**
     * Returns the quantity of damage caused by the instigator.
     * @return the quantity of damage caused by the instigator.
     */
    public float getDamageTaken() {
        return damageTaken;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public float getDefaultReputationImpact() {
        return -(damageTaken * DAMAGE_REPUTATION_MODIFIER);
    }
}
