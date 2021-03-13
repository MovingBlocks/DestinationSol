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
import org.destinationsol.body.components.BodyLinked;
import org.destinationsol.body.events.BodyCreatedEvent;
import org.destinationsol.body.events.BodyUpdateEvent;
import org.destinationsol.body.events.GenerateBodyEvent;
import org.destinationsol.entitysystem.EntitySystemManager;
import org.destinationsol.entitysystem.EventReceiver;
import org.destinationsol.force.components.ImmuneToForce;
import org.destinationsol.force.events.ForceEvent;
import org.destinationsol.game.UpdateAwareSystem;
import org.destinationsol.location.components.Angle;
import org.destinationsol.location.components.Position;
import org.destinationsol.location.components.Velocity;
import org.destinationsol.location.events.AngleUpdateEvent;
import org.destinationsol.location.events.PositionUpdateEvent;
import org.destinationsol.location.events.VelocityUpdateEvent;
import org.destinationsol.removal.events.DeletionEvent;
import org.destinationsol.removal.systems.DestructionSystem;
import org.terasology.gestalt.entitysystem.entity.EntityRef;
import org.terasology.gestalt.entitysystem.event.Before;
import org.terasology.gestalt.entitysystem.event.EventResult;
import org.terasology.gestalt.entitysystem.event.ReceiveEvent;

import javax.inject.Inject;
import java.util.HashMap;

/**
 * This system handles the interaction between an entity and a {@link Body}. If an entity has a {@link BodyLinked}
 * component but no actual Body object associated with it, this will send a {@link GenerateBodyEvent} so that one will
 * be created.
 * <p>
 * Bodies should only be created during an update sent by an {@link UpdateAwareSystem}. Attempting to create a body at
 * any other time may cause the game to crash.
 */
public class BodyHandlerSystem implements EventReceiver {

    @Inject
    EntitySystemManager entitySystemManager;

    @Inject
    public BodyHandlerSystem() {
    }

    private HashMap<EntityRef, Body> referenceToBodyObjects = new HashMap<>();

    /**
     * When this system receives a {@link BodyUpdateEvent} for an entity (approximately once a tick), this method sends
     * a {@link PositionUpdateEvent}, an {@link AngleUpdateEvent}, and/or a {@link VelocityUpdateEvent} to entities with
     * the respective components.
     */
    @ReceiveEvent(components = BodyLinked.class)
    public EventResult onBodyUpdate(BodyUpdateEvent event, EntityRef entity) {

        createBodyIfNonexistent(entity);
        Body body = referenceToBodyObjects.get(entity);
        BodyLinked bodyLinkedComponent = entity.getComponent(BodyLinked.class).get();
        bodyLinkedComponent.setMass(body.getMass());
        entity.setComponent(bodyLinkedComponent);

        if (entity.hasComponent(Position.class)) {
            entitySystemManager.sendEvent(new PositionUpdateEvent(body.getPosition().cpy()), entity);
        }
        if (entity.hasComponent(Angle.class)) {
            entitySystemManager.sendEvent(new AngleUpdateEvent(body.getAngle()), entity);
        }
        if (entity.hasComponent(Velocity.class)) {
            entitySystemManager.sendEvent(new VelocityUpdateEvent(body.getLinearVelocity().cpy()), entity);
        }
        return EventResult.CONTINUE;
    }


    /**
     * This passes the force applied by a {@link ForceEvent} to the Body associated with the entity.
     */
    @ReceiveEvent(components = {BodyLinked.class, Position.class})
    public EventResult onForce(ForceEvent event, EntityRef entity) {
        if (!referenceToBodyObjects.containsKey(entity)) {
            return EventResult.CANCEL;
        }

        if (entity.hasComponent(ImmuneToForce.class)) {
            return EventResult.CONTINUE;
        }

        referenceToBodyObjects.get(entity).applyForceToCenter(event.getForce(), true);
        return EventResult.CONTINUE;
    }

    /**
     * if an entity with a {@link BodyLinked} doesn't have an existing body, this method creates one for it. This should
     * only be called during an update. Attempting to create a body at any other time will cause the game to crash.
     *
     * @param entity the entity that should have a body associated with it
     */
    private void createBodyIfNonexistent(EntityRef entity) {
        if (!referenceToBodyObjects.containsKey(entity) && entity.hasComponent(BodyLinked.class)) {
            entitySystemManager.sendEvent(new GenerateBodyEvent(), entity);
        }
    }

    @ReceiveEvent(components = BodyLinked.class)
    public EventResult onBodyCreated(BodyCreatedEvent event, EntityRef entity) {
        referenceToBodyObjects.put(entity, event.getBody());
        return EventResult.CONTINUE;
    }

    /**
     * When an entity is about to be deleted, this destroys the {@link Body} associated with it and removes it from the HashMap.
     */
    @ReceiveEvent(components = BodyLinked.class)
    @Before(DestructionSystem.class)
    public EventResult onDeletion(DeletionEvent event, EntityRef entity) {
        Body body = referenceToBodyObjects.get(entity);
        referenceToBodyObjects.remove(entity);
        body.getWorld().destroyBody(body);

        return EventResult.CONTINUE;
    }
}
