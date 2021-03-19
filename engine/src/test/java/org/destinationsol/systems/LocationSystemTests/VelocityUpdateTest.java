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
import org.destinationsol.location.components.Velocity;
import org.destinationsol.location.events.VelocityUpdateEvent;
import org.destinationsol.testsupport.AssetsHelperInitializer;
import org.destinationsol.testsupport.Box2DInitializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.terasology.gestalt.entitysystem.entity.EntityRef;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class VelocityUpdateTest implements AssetsHelperInitializer, Box2DInitializer {

    private EntitySystemManager entitySystemManager;

    @BeforeEach
    public void setUp() throws Exception {
        entitySystemManager = new EntitySystemManager(getModuleManager(), getComponentManager(), Collections.emptyList());
    }

    @Test
    public void testOnLocationUpdate() {
        EntityRef entity = entitySystemManager.getEntityManager().createEntity(new Velocity());
        VelocityUpdateEvent event = new VelocityUpdateEvent(new Vector2(1f, 2f));

        entitySystemManager.sendEvent(event, entity);

        Velocity velocity = entity.getComponent(Velocity.class).get();
        assertEquals(1f, velocity.velocity.x);
        assertEquals(2f, velocity.velocity.y);
    }
}
