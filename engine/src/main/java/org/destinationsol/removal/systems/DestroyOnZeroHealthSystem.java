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
package org.destinationsol.removal.systems;

import org.destinationsol.entitysystem.EntitySystemManager;
import org.destinationsol.entitysystem.EventReceiver;
import org.destinationsol.health.components.Health;
import org.destinationsol.removal.events.ShouldBeDestroyedEvent;
import org.destinationsol.removal.events.ZeroHealthEvent;
import org.terasology.gestalt.entitysystem.entity.EntityRef;
import org.terasology.gestalt.entitysystem.event.EventResult;
import org.terasology.gestalt.entitysystem.event.ReceiveEvent;

import javax.inject.Inject;

/**
 * When an entity's {@link Health} drops to zero, this system destroys that entity.
 */
public class DestroyOnZeroHealthSystem implements EventReceiver {

    @Inject
    EntitySystemManager entitySystemManager;

    @Inject
    public DestroyOnZeroHealthSystem() {
    }

    @ReceiveEvent
    public EventResult onZeroHealth(ZeroHealthEvent event, EntityRef entity) {
        entitySystemManager.sendEvent(new ShouldBeDestroyedEvent(), entity);
        return EventResult.COMPLETE;
    }
}
