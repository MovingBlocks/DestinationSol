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

import org.destinationsol.entitysystem.EventReceiver;
import org.destinationsol.removal.components.SlatedForDeletion;
import org.destinationsol.removal.events.DeletionEvent;
import org.destinationsol.removal.events.ShouldBeDestroyedEvent;
import org.destinationsol.removal.events.RemovalForOptimizationEvent;
import org.terasology.gestalt.entitysystem.entity.EntityRef;
import org.terasology.gestalt.entitysystem.event.EventResult;
import org.terasology.gestalt.entitysystem.event.ReceiveEvent;

/**
 * When an entity receives a {@link ShouldBeDestroyedEvent} or a {@link RemovalForOptimizationEvent}, this system adds a
 * {@link SlatedForDeletion} component. Any system that should respond to the destruction of an entity before it happens
 * should do so before this system is called.
 * <p>
 * Every tick, the {@link DeletionUpdateSystem} sends a {@link DeletionEvent} to each entity with a SlatedForDeletion
 * component. When that happens, this system deletes those entities.
 */
public class DestructionSystem implements EventReceiver {

    /**
     * Adds a {@link SlatedForDeletion} component to an entity. That entity will be destroyed on the next tick.
     */
    @ReceiveEvent
    public EventResult onDestroy(ShouldBeDestroyedEvent event, EntityRef entity) {
        entity.setComponent(new SlatedForDeletion());
        return EventResult.COMPLETE;
    }

    /**
     * Adds a {@link SlatedForDeletion} component to an entity. That entity will be destroyed on the next tick.
     */
    @ReceiveEvent
    public EventResult onRemovalForOptimization(RemovalForOptimizationEvent event, EntityRef entity) {
        entity.setComponent(new SlatedForDeletion());
        return EventResult.COMPLETE;
    }

    /**
     * Deletes an entity that is {@link SlatedForDeletion}.
     */
    @ReceiveEvent(components = SlatedForDeletion.class)
    public EventResult onDeletion(DeletionEvent event, EntityRef entity) {
        entity.delete();
        return EventResult.COMPLETE;
    }
}
