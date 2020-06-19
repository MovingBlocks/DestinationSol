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

import com.badlogic.gdx.physics.box2d.Body;
import org.destinationsol.common.In;
import org.destinationsol.components.BodyLinked;
import org.destinationsol.components.ImmuneToForce;
import org.destinationsol.components.Location;
import org.destinationsol.entitysystem.EntitySystemManager;
import org.destinationsol.entitysystem.EventReceiver;
import org.destinationsol.events.ForceEvent;
import org.destinationsol.events.GenerateBodyEvent;
import org.destinationsol.events.LocationUpdateEvent;
import org.destinationsol.game.SolGame;
import org.destinationsol.game.UpdateAwareSystem;
import org.destinationsol.game.attributes.RegisterUpdateSystem;
import org.terasology.gestalt.entitysystem.entity.EntityIterator;
import org.terasology.gestalt.entitysystem.entity.EntityRef;
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
     * Sends a {@link LocationUpdateEvent} every tick to each entity with a {@link BodyLinked} component and a
     * {@link Location} component.
     */
    @Override
    public void update(SolGame game, float timeStep) {
        EntityIterator iterator = entitySystemManager.getEntityManager().iterate(new BodyLinked(), new Location());
        while (iterator.next()) {
            EntityRef entity = iterator.getEntity();

            createBodyIfNonexistent(entity);
            Body body = map.get(entity);

            LocationUpdateEvent event = new LocationUpdateEvent(body.getPosition(), body.getAngle(), body.getLinearVelocity());
            entitySystemManager.sendEvent(event, entity);
        }
    }

    /**
     * This passes the force applied by a {@link ForceEvent} to the Body associated with the entity.
     */
    @ReceiveEvent(components = {BodyLinked.class, Location.class})
    public void onForce(ForceEvent event, EntityRef entity) {
        if (!entity.hasComponent(ImmuneToForce.class)) {
            createBodyIfNonexistent(entity);
            map.get(entity).applyForceToCenter(event.getForce(), true);
        }
    }

    /**
     * if an entity with a {@link BodyLinked} doesn't have an existing body, this method creates one for it.
     *
     * @param entity the entity that should have a body associated with it
     */
    private void createBodyIfNonexistent(EntityRef entity) {
        if (!map.containsKey(entity)) {
            entitySystemManager.sendEvent(new GenerateBodyEvent(), entity);
        }
    }

}
