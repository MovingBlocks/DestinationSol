/*
 * Copyright 2018 MovingBlocks
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
package org.destinationsol;

import org.terasology.entitysystem.core.Component;
import org.terasology.entitysystem.core.EntityRef;
import org.terasology.entitysystem.event.Event;
import org.terasology.entitysystem.event.EventSystem;
import org.terasology.entitysystem.event.Synchronous;

import java.util.Set;

// Shared through context
public class SolEventSystem {

    private final EventSystem system;

    SolEventSystem(EventSystem system) {
        this.system = system;
    }


    /**
     * Sends an event against an entity. This event will be processed immediately if annotated as {@link Synchronous},
     * otherwise it will be processed before next game loop.
     *
     * @param event  The event to send
     * @param entity The entity to send the event against.
     */
    void send(Event event, EntityRef entity) {
        system.send(event, entity);
    }

    /**
     * Sends an event against an entity. This event will be processed immediately if annotated as {@link Synchronous},
     * otherwise it will be processed before next game loop.
     *
     * @param event                The event to send.
     * @param entity               The entity to send the event against.
     * @param triggeringComponents The components triggering the event if any - only event handlers interested in these components will be notified.
     */
    void send(Event event, EntityRef entity, Set<Class<? extends Component>> triggeringComponents) {
        system.send(event, entity, triggeringComponents);
    }

}
