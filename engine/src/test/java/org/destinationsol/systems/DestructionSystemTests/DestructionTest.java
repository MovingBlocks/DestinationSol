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
package org.destinationsol.systems.DestructionSystemTests;

import org.destinationsol.entitysystem.EntitySystemManager;
import org.destinationsol.events.DestroyEvent;
import org.destinationsol.modules.ModuleManager;
import org.junit.Before;
import org.junit.Test;
import org.terasology.gestalt.entitysystem.component.management.ComponentManager;
import org.terasology.gestalt.entitysystem.entity.EntityRef;
import org.terasology.gestalt.entitysystem.entity.NullEntityRef;

import static org.junit.Assert.*;

public class DestructionTest {

    private ModuleManager moduleManager;
    private EntitySystemManager entitySystemManager;

    @Before
    public void setUp() throws Exception {
        moduleManager = new ModuleManager();
        moduleManager.init();
        entitySystemManager = new EntitySystemManager(moduleManager.getEnvironment(), new ComponentManager());
    }

    @Test
    public void testOnDestroy(){
        EntityRef entity = entitySystemManager.getEntityManager().createEntity();

        entitySystemManager.sendEvent(new DestroyEvent(), entity);

        assertFalse(entity.exists());
    }

}