/*
 * Copyright 2021 The Terasology Foundation
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
package org.destinationsol.world;

import org.destinationsol.game.context.Context;
import org.destinationsol.game.context.internal.ContextImpl;
import org.destinationsol.modules.ModuleManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class WorldBuilderTest {
    private Context context;
    private ModuleManager moduleManager;
    WorldBuilder worldBuilder;


    @BeforeEach
    public void setUp() throws Exception {
        context = new ContextImpl();
        moduleManager = new ModuleManager();
        moduleManager.init();
        context.put(ModuleManager.class, moduleManager);
    }

    @Test
    void populatesSolSystemsList() {
        int testNumberSystems = 2;
        worldBuilder = new WorldBuilder(context, testNumberSystems);
        worldBuilder.buildWithRandomSolarSystemGenerators();
        assertTrue(worldBuilder.getSolarSystemGeneratorTypes().size() > 0);

    }

    @Test
    void populatesFeatureGeneratorsList() {
        int testNumberSystems = 2;
        worldBuilder = new WorldBuilder(context, testNumberSystems);
        worldBuilder.buildWithRandomSolarSystemGenerators();
        assertTrue(worldBuilder.getFeatureGenerators().size() > 0);
    }

    @Test
    void createsCorrectNumberOfSolarSystems() {
        int testNumberSystems = 2;
        worldBuilder = new WorldBuilder(context, testNumberSystems);
        worldBuilder.buildWithRandomSolarSystemGenerators();
        assertTrue(worldBuilder.getActiveSolarSystemGenerators().size() == 2);
    }

    @Test
    void setsContext() {
        int testNumberSystems = 2;
        worldBuilder = new WorldBuilder(context, testNumberSystems);
        assertNotNull(worldBuilder.getContext());
    }

}
