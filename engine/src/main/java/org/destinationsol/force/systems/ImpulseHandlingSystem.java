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
package org.destinationsol.force.systems;

import org.destinationsol.body.components.BodyLinked;
import org.destinationsol.common.In;
import org.destinationsol.entitysystem.EntitySystemManager;
import org.destinationsol.entitysystem.EventReceiver;
import org.destinationsol.force.components.Durability;
import org.destinationsol.force.events.ImpulseEvent;
import org.destinationsol.health.components.Health;
import org.destinationsol.health.events.DamageEvent;
import org.terasology.gestalt.entitysystem.entity.EntityRef;
import org.terasology.gestalt.entitysystem.event.EventResult;
import org.terasology.gestalt.entitysystem.event.ReceiveEvent;

/**
 * When this receives an {@link ImpulseEvent}, it sends a {@link DamageEvent} to the entity that is scaled according to
 * the entity's mass and durability.
 */
public class ImpulseHandlingSystem implements EventReceiver {

    @In
    private EntitySystemManager entitySystemManager;

    @ReceiveEvent(components = {Health.class, BodyLinked.class})
    public EventResult onImpulse(ImpulseEvent event, EntityRef entity) {

        float mass = entity.getComponent(BodyLinked.class).get().getMass();
        float damage = event.getMagnitude() / mass;

        if (entity.hasComponent(Durability.class)) {
            float durability = entity.getComponent(Durability.class).get().getDurability();
            damage /= durability;
        }

        entitySystemManager.sendEvent(new DamageEvent(damage), entity);
        return EventResult.CONTINUE;
    }
}
