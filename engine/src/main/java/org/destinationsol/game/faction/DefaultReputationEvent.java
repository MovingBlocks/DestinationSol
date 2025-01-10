/*
 * Copyright 2025 The Terasology Foundation
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
 * Defines events impacting reputation that are likely to be used by the base game.
 */
public enum DefaultReputationEvent implements ReputationEvent {
    /**
     * A ship (or projectile fired by a ship) has caused damage to another.
     */
    DAMAGED_SHIP(-1),
    /**
     * A ship (or projectile fired by a ship) has destroyed another.
     */
    DESTROYED_SHIP(-20),
    /**
     * A ship has bought an item from a station.
     * (Note: this only applies to the player for now.)
     */
    BOUGHT_ITEM(1);

    /**
     * The default impact on reputation this event will have in absence of a faction-specific value.
     */
    private final int defaultReputationImpact;

    DefaultReputationEvent(int defaultReputationImpact) {
        this.defaultReputationImpact = defaultReputationImpact;
    }

    /**
     * Returns the default impact on reputation this event will have in absence of a faction-specific value.
     * @return the default impact on reputation this event will have in absence of a faction-specific value.
     */
    @Override
    public int getDefaultReputationImpact() {
        return defaultReputationImpact;
    }
}
