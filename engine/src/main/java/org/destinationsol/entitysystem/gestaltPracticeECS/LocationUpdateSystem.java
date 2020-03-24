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
package org.destinationsol.entitysystem.gestaltPracticeECS;

import com.badlogic.gdx.math.Vector2;
import org.destinationsol.common.In;
import org.destinationsol.entitysystem.ComponentSystem;
import org.destinationsol.entitysystem.EntitySystemManager;
import org.destinationsol.entitysystem.RegisterEventReceivers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.gestalt.entitysystem.entity.EntityIterator;
import org.terasology.gestalt.entitysystem.entity.EntityRef;
import org.terasology.gestalt.entitysystem.event.EventResult;
import org.terasology.gestalt.entitysystem.event.ReceiveEvent;

@RegisterEventReceivers
public class LocationUpdateSystem extends ComponentSystem {

    private static final Logger logger = LoggerFactory.getLogger(LocationUpdateSystem.class);

    @In
    private EntitySystemManager entitySystemManager;

    @Override
    public void preBegin() {
        entitySystemManager.getEntityManager().createEntity(new LocationComponent());
        EntityIterator iterator = entitySystemManager.getEntityManager().iterate(new LocationComponent());
        while (iterator.next()) {
            if (iterator.getEntity().getComponent(LocationComponent.class).isPresent()){
                Vector2 position = iterator.getEntity().getComponent(LocationComponent.class).get().getPosition();
                position.x = 1;
                position.y = 1;
            }
        }

    }

    @ReceiveEvent(components = LocationComponent.class)
    public EventResult onLocationUpdate(LocationUpdateEvent event, EntityRef entity) {
        logger.info("Entity " + entity.getId() + "'s coordinates are " + event.getPosition().toString());
        return EventResult.CONTINUE;
    }

//    @ReceiveEvent(components = {LocationComponent.class, AsteroidGraphicsComponent.class})
//    public EventResult onGraphicalLocationUpdate(LocationUpdateEvent event, EntityRef entity) {
//
//    }

}
