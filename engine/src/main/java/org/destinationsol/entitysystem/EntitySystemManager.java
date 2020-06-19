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
package org.destinationsol.entitysystem;

import com.google.common.collect.Lists;
import org.terasology.gestalt.entitysystem.component.Component;
import org.terasology.gestalt.entitysystem.component.management.ComponentManager;
import org.terasology.gestalt.entitysystem.component.store.ArrayComponentStore;
import org.terasology.gestalt.entitysystem.component.store.ComponentStore;
import org.terasology.gestalt.entitysystem.component.store.ConcurrentComponentStore;
import org.terasology.gestalt.entitysystem.entity.EntityIterator;
import org.terasology.gestalt.entitysystem.entity.EntityManager;
import org.terasology.gestalt.entitysystem.entity.EntityRef;
import org.terasology.gestalt.entitysystem.entity.manager.CoreEntityManager;
import org.terasology.gestalt.entitysystem.event.Event;
import org.terasology.gestalt.entitysystem.event.EventSystem;
import org.terasology.gestalt.entitysystem.event.impl.EventReceiverMethodSupport;
import org.terasology.gestalt.entitysystem.event.impl.EventSystemImpl;
import org.terasology.gestalt.entitysystem.prefab.GeneratedFromRecipeComponent;
import org.terasology.gestalt.module.ModuleEnvironment;

import java.util.List;

public class EntitySystemManager {

    private static EntityManager entityManager;
    private EventSystem eventSystem = new EventSystemImpl();
    private static EventReceiverMethodSupport eventReceiverMethodSupport = new EventReceiverMethodSupport();

    public EntitySystemManager(ModuleEnvironment environment, ComponentManager componentManager) {

        List<ComponentStore<?>> stores = Lists.newArrayList();
        for (Class<? extends Component> componentType : environment.getSubtypesOf(Component.class)) {
            stores.add(
                    new ConcurrentComponentStore<>(new ArrayComponentStore<>(componentManager.getType(componentType))));
        }
        stores.add(new ConcurrentComponentStore<>(
                new ArrayComponentStore<>(componentManager.getType(GeneratedFromRecipeComponent.class))));

        entityManager = new CoreEntityManager(stores);

        for (Class<? extends EventReceiver> eventReceiver : environment.getSubtypesOf(EventReceiver.class)) {
            try {
                eventReceiverMethodSupport.register(eventReceiver.newInstance(), eventSystem);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void sendEvent(Event event, Component... components) {
        EntityIterator iterator = entityManager.iterate(components);
        while (iterator.next()) {
            eventSystem.send(event, iterator.getEntity());
        }
        eventSystem.processEvents();
    }

    public void sendEvent(Event event, EntityRef entity){
        eventSystem.send(event, entity);
        eventSystem.processEvents();
    }

    public EntityManager getEntityManager() {
        return entityManager;
    }
}
