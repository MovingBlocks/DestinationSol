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
package org.destinationsol.body.systems;

import com.badlogic.gdx.physics.box2d.Body;
import org.destinationsol.common.In;
import org.destinationsol.body.components.BodyLinked;
import org.destinationsol.force.components.ImmuneToForce;
import org.destinationsol.location.components.Angle;
import org.destinationsol.location.components.Position;
import org.destinationsol.entitysystem.EntitySystemManager;
import org.destinationsol.entitysystem.EventReceiver;
import org.destinationsol.body.events.BodyCreatedEvent;
import org.destinationsol.force.events.ForceEvent;
import org.destinationsol.body.events.GenerateBodyEvent;
import org.destinationsol.location.components.Velocity;
import org.destinationsol.location.events.AngleUpdateEvent;
import org.destinationsol.location.events.PositionUpdateEvent;
import org.destinationsol.game.SolGame;
import org.destinationsol.game.UpdateAwareSystem;
import org.destinationsol.game.attributes.RegisterUpdateSystem;
import org.destinationsol.location.events.VelocityUpdateEvent;
import org.terasology.gestalt.entitysystem.entity.EntityIterator;
import org.terasology.gestalt.entitysystem.entity.EntityRef;
import org.terasology.gestalt.entitysystem.event.EventResult;
import org.terasology.gestalt.entitysystem.event.ReceiveEvent;

import java.util.HashMap;

/**
 * This system handles the interaction between an entity and a {@link Body}. If an entity has a {@link BodyLinked}
 * component but no actual Body object associated with it, this will send a {@link GenerateBodyEvent} so that one will
 * be created.
 */
@RegisterUpdateSystem
public class BodyHandlerSystem implements EventReceiver, UpdateAwareSystem {

    @In
    private EntitySystemManager entitySystemManager;

    private HashMap<EntityRef, Body> map = new HashMap<>();

    /**
     * Sends a {@link PositionUpdateEvent} every tick to each entity with a {@link BodyLinked} component and a
     * {@link Position} component.
     */
    @Override
    public void update(SolGame game, float timeStep) {
        EntityIterator iterator = entitySystemManager.getEntityManager().iterate(new BodyLinked(), new Position());
        while (iterator.next()) {
            EntityRef entity = iterator.getEntity();

            createBodyIfNonexistent(entity);
            Body body = map.get(entity);

            if (entity.hasComponent(Position.class)) {
                entitySystemManager.sendEvent(new PositionUpdateEvent(body.getPosition()), entity);
            }
            if (entity.hasComponent(Angle.class)) {
                entitySystemManager.sendEvent(new AngleUpdateEvent(body.getAngle()), entity);
            }
            if (entity.hasComponent(Velocity.class)) {
                entitySystemManager.sendEvent(new VelocityUpdateEvent(body.getLinearVelocity()), entity);
            }
        }
    }

    /**
     * This passes the force applied by a {@link ForceEvent} to the Body associated with the entity.
     */
    @ReceiveEvent(components = {BodyLinked.class, Position.class})
    public EventResult onForce(ForceEvent event, EntityRef entity) {
        if (!entity.hasComponent(ImmuneToForce.class)) {
            createBodyIfNonexistent(entity);
            map.get(entity).applyForceToCenter(event.getForce(), true);
        }
        return EventResult.CONTINUE;
    }

    /**
     * if an entity with a {@link BodyLinked} doesn't have an existing body, this method creates one for it.
     *
     * @param entity the entity that should have a body associated with it
     */
    private void createBodyIfNonexistent(EntityRef entity) {
        if (!map.containsKey(entity) && entity.hasComponent(BodyLinked.class)) {
            entitySystemManager.sendEvent(new GenerateBodyEvent(), entity);
        }
    }

    @ReceiveEvent(components = BodyLinked.class)
    public EventResult onBodyCreated(BodyCreatedEvent event, EntityRef entity) {
        map.put(entity, event.getBody());
        return EventResult.CONTINUE;
    }
}
