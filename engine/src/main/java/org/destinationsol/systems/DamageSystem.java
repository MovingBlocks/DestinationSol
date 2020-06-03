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
package org.destinationsol.systems;

import org.destinationsol.components.HealthComponent;
import org.destinationsol.entitysystem.RegisterEventReceivers;
import org.destinationsol.events.DamageEvent;
import org.terasology.gestalt.entitysystem.entity.EntityRef;
import org.terasology.gestalt.entitysystem.event.EventResult;
import org.terasology.gestalt.entitysystem.event.ReceiveEvent;

@RegisterEventReceivers
public class DamageSystem {

    @ReceiveEvent(components = HealthComponent.class)
    public EventResult onDamage(DamageEvent event, EntityRef entity) {
        if (event.getDamage() <= 0){
            return EventResult.CONTINUE;
        }
        entity.getComponent(HealthComponent.class).ifPresent(health -> {
            int newHealthAmount = health.getCurrentHealth() - event.getDamage();
            if (newHealthAmount < 0) {
                newHealthAmount = 0;
            }
            health.setCurrentHealth(newHealthAmount);
            entity.setComponent(health);
        });
        return EventResult.CONTINUE;
    }
}
