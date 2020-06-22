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
import org.destinationsol.location.components.Angle;
import org.destinationsol.location.components.Position;
import org.destinationsol.location.components.Velocity;
import org.destinationsol.location.events.AngleUpdateEvent;
import org.destinationsol.location.events.PositionUpdateEvent;
import org.destinationsol.location.events.VelocityUpdateEvent;
import org.terasology.gestalt.entitysystem.entity.EntityRef;
import org.terasology.gestalt.entitysystem.event.EventResult;
import org.terasology.gestalt.entitysystem.event.ReceiveEvent;

/**
 * This system updates the location of an entity with a {@link Position}, {@link Angle}, or {@link Velocity} component
 * when it receives a {@link PositionUpdateEvent}, {@link AngleUpdateEvent}, or {@link VelocityUpdateEvent}, respectively.
 */
public class LocationSystem implements EventReceiver {

    @ReceiveEvent(components = Position.class)
    public EventResult onPositionUpdate(PositionUpdateEvent event, EntityRef entity) {
        if (entity.hasComponent(Position.class)) {
            Position position = entity.getComponent(Position.class).get();
            position.position = event.getPosition();
            entity.setComponent(position);
        }
        return EventResult.CONTINUE;
    }

    @ReceiveEvent(components = Angle.class)
    public EventResult onAngleUpdate(AngleUpdateEvent event, EntityRef entity) {
        if (entity.hasComponent(Angle.class)) {
            Angle angle = entity.getComponent(Angle.class).get();
            angle.setAngle(event.getAngle());
            entity.setComponent(angle);
        }
        return EventResult.CONTINUE;
    }

    @ReceiveEvent(components = Velocity.class)
    public EventResult onVelocityUpdate(VelocityUpdateEvent event, EntityRef entity) {
        if (entity.hasComponent(Velocity.class)) {
            Velocity velocity = entity.getComponent(Velocity.class).get();
            velocity.velocity = event.getVelocity();
            entity.setComponent(velocity);
        }
        return EventResult.CONTINUE;
    }
}
