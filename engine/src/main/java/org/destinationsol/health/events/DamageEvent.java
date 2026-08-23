/*
 * Copyright 2020 The Terasology Foundation
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
package org.destinationsol.health.events;

import org.destinationsol.game.DmgType;
import org.terasology.gestalt.entitysystem.event.Event;

import java.util.Optional;

/**
 * Event that contains information about the damage an entity receives.
 */
public class DamageEvent implements Event {

    private final float damage;
    private final DmgType damageType;

    /**
     * Damage of no particular kind. Use {@link #DamageEvent(float, DmgType)} where the kind of damage is known, so
     * that systems which care about it (such as sound) can react to it.
     */
    public DamageEvent(float damage) {
        this(damage, null);
    }

    public DamageEvent(float damage, DmgType damageType) {
        this.damage = damage;
        this.damageType = damageType;
    }

    public float getDamage() {
        return damage;
    }

    /**
     * The kind of damage dealt, if the source of the damage specified one.
     */
    public Optional<DmgType> getDamageType() {
        return Optional.ofNullable(damageType);
    }
}
