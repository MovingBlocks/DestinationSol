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
import org.destinationsol.game.context.internal.ContextImpl;
import org.destinationsol.removal.components.SlatedForDeletion;
import org.destinationsol.removal.events.DeletionEvent;
import org.destinationsol.removal.events.ShouldBeDestroyedEvent;
import org.destinationsol.testsupport.AssetsHelperInitializer;
import org.destinationsol.testsupport.Box2DInitializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.terasology.gestalt.entitysystem.entity.EntityRef;

import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * Test to ensure that a {@link ShouldBeDestroyedEvent} on an entity will cause that entity to be removed.
 */
public class DestructionTest implements Box2DInitializer, AssetsHelperInitializer {

    private EntitySystemManager entitySystemManager;

    @BeforeEach
    public void setUp() throws Exception {
        entitySystemManager = new EntitySystemManager(getModuleManager().getEnvironment(), getComponentManager(), new ContextImpl());
    }

    @Test
    public void testOnDestroy(){
        EntityRef entity = entitySystemManager.getEntityManager().createEntity();

        entitySystemManager.sendEvent(new ShouldBeDestroyedEvent(), entity);

        // Emulate `DeletionUpdateSystem#update`
        entitySystemManager.sendEvent(new DeletionEvent(), new SlatedForDeletion());

        assertFalse(entity.exists());
    }

}