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
package org.destinationsol.gestalt;

import org.destinationsol.entitysystem.EntitySystemManager;
import org.destinationsol.game.context.internal.ContextImpl;
import org.destinationsol.modules.ModuleManager;
import org.junit.Before;
import org.junit.Test;
import org.terasology.gestalt.entitysystem.component.management.ComponentManager;
import org.terasology.gestalt.entitysystem.entity.EntityManager;

/**
 * Test to ensure that 100,000 entities can be created by the {@link EntityManager} without any issues.
 */
public class BulkEntityCreationStressTest {

    private EntitySystemManager entitySystemManager;

    @Before
    public void setUp() throws Exception {
        ModuleManager moduleManager = new ModuleManager();
        moduleManager.init();
        entitySystemManager = new EntitySystemManager(moduleManager.getEnvironment(), new ComponentManager(), new ContextImpl());
    }

    @Test
    public void testBulkEntityCreation() {
        for (int i = 0; i < 100000; i++) {
            entitySystemManager.getEntityManager().createEntity();
        }
    }
}
