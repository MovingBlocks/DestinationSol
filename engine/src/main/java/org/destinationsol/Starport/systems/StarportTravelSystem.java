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
package org.destinationsol.Starport.systems;

import org.destinationsol.Starport.components.InStarportTransit;
import org.destinationsol.body.components.BodyLinked;
import org.destinationsol.body.systems.BodyHandlerSystem;
import org.destinationsol.entitysystem.EventReceiver;
import org.destinationsol.force.events.ContactEvent;
import org.destinationsol.force.events.ForceEvent;
import org.destinationsol.force.events.ImpulseEvent;
import org.destinationsol.game.StarPort;
import org.destinationsol.health.components.Health;
import org.destinationsol.health.events.DamageEvent;
import org.destinationsol.health.systems.DamageSystem;
import org.terasology.gestalt.entitysystem.entity.EntityRef;
import org.terasology.gestalt.entitysystem.event.Before;
import org.terasology.gestalt.entitysystem.event.EventResult;
import org.terasology.gestalt.entitysystem.event.ReceiveEvent;

/**
 * This system handles basic events that happen to an entity while it is traveling through a {@link StarPort}.
 */
public class StarportTravelSystem implements EventReceiver {

    /**
     * When an entity is going through a {@link StarPort}, this prevents it from taking damage.
     */
    @ReceiveEvent(components = {InStarportTransit.class, Health.class})
    @Before(DamageSystem.class)
    public EventResult stopDamage(DamageEvent event, EntityRef entity) {
        return EventResult.COMPLETE;
    }

    /**
     * When an entity is going through a {@link StarPort}, this prevents it from being affected by contact.
     */
    @ReceiveEvent(components = InStarportTransit.class)
    public EventResult stopContact(ContactEvent event, EntityRef entity) {
        event.getContact().setEnabled(false);
        return EventResult.COMPLETE;
    }

    /**
     * When an entity is going through a {@link StarPort}, this prevents it from being affected by forces.
     */
    @ReceiveEvent(components = {InStarportTransit.class, BodyLinked.class})
    @Before(BodyHandlerSystem.class)
    public EventResult stopForce(ForceEvent event, EntityRef entity) {
        return EventResult.COMPLETE;
    }

    /**
     * When an entity is going through a {@link StarPort}, this prevents it from being affected by impulses.
     */
    @ReceiveEvent(components = {InStarportTransit.class})
    public EventResult stopImpulse(ImpulseEvent event, EntityRef entity) {
        return EventResult.COMPLETE;
    }

}
