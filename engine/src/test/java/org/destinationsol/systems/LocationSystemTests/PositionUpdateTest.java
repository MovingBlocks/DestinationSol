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
package org.destinationsol.systems.LocationSystemTests;

import com.badlogic.gdx.math.Vector2;
import org.destinationsol.entitysystem.EntitySystemManager;
import org.destinationsol.game.context.internal.ContextImpl;
import org.destinationsol.location.components.Position;
import org.destinationsol.location.events.PositionUpdateEvent;
import org.destinationsol.location.systems.LocationSystem;
import org.destinationsol.modules.ModuleManager;
import org.destinationsol.testsupport.Box2DInitializer;
import org.destinationsol.testsupport.TestModuleConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.terasology.gestalt.di.DefaultBeanContext;
import org.terasology.gestalt.di.ServiceRegistry;
import org.terasology.gestalt.entitysystem.component.management.ComponentManager;
import org.terasology.gestalt.entitysystem.entity.EntityRef;
import org.terasology.gestalt.module.ModuleFactory;
import org.terasology.gestalt.module.ModulePathScanner;
import org.terasology.gestalt.module.TableModuleRegistry;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class PositionUpdateTest implements Box2DInitializer {

    private ModuleManager moduleManager;
    private EntitySystemManager entitySystemManager;

    @BeforeEach
    public void setUp() throws Exception {
        ModuleFactory moduleFactory = new ModuleFactory();
        moduleManager = new ModuleManager(new DefaultBeanContext(), moduleFactory, new TableModuleRegistry(),
                new ModulePathScanner(moduleFactory), new TestModuleConfig());
        moduleManager.init();
        ServiceRegistry systemsRegistry = new ServiceRegistry();
        systemsRegistry.with(LocationSystem.class);
        systemsRegistry.with(EntitySystemManager.class).use(() -> entitySystemManager);
        entitySystemManager = new EntitySystemManager(moduleManager, new ComponentManager(),
                new DefaultBeanContext(systemsRegistry));
        entitySystemManager.initialise();
    }

    @Test
    public void testOnLocationUpdate() {
        EntityRef entity = entitySystemManager.getEntityManager().createEntity(new Position());
        PositionUpdateEvent event = new PositionUpdateEvent(new Vector2(1f, 2f));

        entitySystemManager.sendEvent(event, entity);

        Position position = entity.getComponent(Position.class).get();
        assertEquals(1f, position.position.x);
        assertEquals(2f, position.position.y);
    }
}
