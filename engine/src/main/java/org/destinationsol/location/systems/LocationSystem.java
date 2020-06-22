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
package org.destinationsol.location.systems;

import org.destinationsol.entitysystem.EventReceiver;
import org.destinationsol.location.components.Location;
import org.destinationsol.location.events.LocationUpdateEvent;
import org.terasology.gestalt.entitysystem.entity.EntityRef;
import org.terasology.gestalt.entitysystem.event.EventResult;
import org.terasology.gestalt.entitysystem.event.ReceiveEvent;

/**
 * This system updates the location of an entity with a {@link Location} component when it receives a
 * {@link LocationUpdateEvent}.
 */
public class LocationSystem implements EventReceiver {

    @ReceiveEvent(components = Location.class)
    public EventResult onLocationUpdate(LocationUpdateEvent event, EntityRef entity) {
        if (entity.hasComponent(Location.class)) {
            Location location = entity.getComponent(Location.class).get();
            location.position = event.getPosition();
            location.angle = event.getAngle();
            location.velocity = event.getVelocity();
            entity.setComponent(location);
        }
        return EventResult.CONTINUE;
    }
}
