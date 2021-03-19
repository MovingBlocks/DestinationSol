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
package org.destinationsol.systems.DamageSystemTests;

import org.destinationsol.entitysystem.EntitySystemManager;
import org.destinationsol.game.context.internal.ContextImpl;
import org.destinationsol.health.components.Health;
import org.destinationsol.health.events.DamageEvent;
import org.destinationsol.modules.ModuleManager;
import org.destinationsol.removal.components.SlatedForDeletion;
import org.destinationsol.removal.events.DeletionEvent;
import org.destinationsol.testsupport.AssetsHelperInitializer;
import org.destinationsol.testsupport.Box2DInitializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.terasology.gestalt.di.DefaultBeanContext;
import org.terasology.gestalt.entitysystem.component.management.ComponentManager;
import org.terasology.gestalt.entitysystem.entity.EntityRef;
import org.terasology.gestalt.module.ModuleFactory;
import org.terasology.gestalt.module.ModulePathScanner;
import org.terasology.gestalt.module.TableModuleRegistry;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * Test to ensure that an entity whose health reaches zero will be destroyed.
 */
public class ZeroHealthCausesDestructionTest implements Box2DInitializer, AssetsHelperInitializer {

    private EntitySystemManager entitySystemManager;

    @BeforeEach
    public void setUp() throws Exception {
        entitySystemManager = new EntitySystemManager(getModuleManager(),getComponentManager(), Collections.emptyList());
    }

    @Test
    public void testLethalDamageCausesDestruction() {
        EntityRef entity = entitySystemManager.getEntityManager().createEntity(new Health());
        if (entity.getComponent(Health.class).isPresent()) {
            Health health = entity.getComponent(Health.class).get();
            health.maxHealth = 50;
            health.currentHealth = 50;
            entity.setComponent(health);
        }
        DamageEvent event = new DamageEvent(50);

        entitySystemManager.sendEvent(event, entity);

        // Emulate `DeletionUpdateSystem#update`
        entitySystemManager.sendEvent(new DeletionEvent(), new SlatedForDeletion());

        assertFalse(entity.exists());
    }
}
