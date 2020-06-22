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
package org.destinationsol.health.systems;

import org.destinationsol.health.components.Health;
import org.destinationsol.entitysystem.EventReceiver;
import org.destinationsol.health.events.DamageEvent;
import org.terasology.gestalt.entitysystem.entity.EntityRef;
import org.terasology.gestalt.entitysystem.event.EventResult;
import org.terasology.gestalt.entitysystem.event.ReceiveEvent;

/**
 * When a damage event happens to an entity with a health component, this system reads the damage from that event and
 * lowers its health by that amount. If it would lower the health to less than zero, it's reduced to zero instead. If
 * the damage is a negative amount, nothing happens.
 */
public class DamageSystem implements EventReceiver {

    /**
     * Handles a damage event done to an entity with a Health component.
     *
     * @param event  the damage event that is occurring
     * @param entity the entity that the damage is happening to
     * @return the event should be processed by other systems, if there are
     */
    @ReceiveEvent(components = Health.class)
    public EventResult onDamage(DamageEvent event, EntityRef entity) {
        if (event.getDamage() <= 0) {
            return EventResult.CONTINUE;
        }
        if (entity.getComponent(Health.class).isPresent()) {
            Health health = entity.getComponent(Health.class).get();
            int newHealthAmount = health.currentHealth - event.getDamage();
            if (newHealthAmount < 0) {
                newHealthAmount = 0;
            }
            health.currentHealth = newHealthAmount;
            entity.setComponent(health);
        }
        return EventResult.CONTINUE;
    }
}
