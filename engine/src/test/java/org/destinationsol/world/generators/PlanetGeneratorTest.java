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
package org.destinationsol.world.generators;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import org.destinationsol.assets.sound.OggSoundManager;
import org.destinationsol.files.HullConfigManager;
import org.destinationsol.game.AbilityCommonConfigs;
import org.destinationsol.game.GameColors;
import org.destinationsol.game.context.Context;
import org.destinationsol.game.context.internal.ContextImpl;
import org.destinationsol.game.item.ItemManager;
import org.destinationsol.game.particle.EffectTypes;
import org.destinationsol.game.planet.PlanetConfigs;
import org.destinationsol.game.planet.SolarSystemConfigManager;
import org.destinationsol.modules.ModuleManager;
import org.destinationsol.testingUtilities.MockGL;
import org.destinationsol.testsupport.AssetsHelperInitializer;
import org.destinationsol.world.WorldBuilder;
import org.junit.jupiter.api.BeforeEach;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PlanetGeneratorTest implements AssetsHelperInitializer {
    private Context context;
    private ModuleManager moduleManager;
    private WorldBuilder worldBuilder;
    private GameColors gameColors;
    private EffectTypes effectTypes;
    private OggSoundManager soundManager;
    private AbilityCommonConfigs abilityCommonConfigs;
    private SolarSystemGenerator solarSystemGenerator;
    private PlanetGenerator planetGenerator;

    @BeforeEach
    public void setUp() throws Exception {
        context = new ContextImpl();
        moduleManager = getModuleManager();
        moduleManager.init();
        context.put(ModuleManager.class, moduleManager);

        setupMockGL();

        //TODO: Determine how to load in PlanetConfigs class without CollisionMeshLoader error
        setupConfigManagers();

        worldBuilder = new WorldBuilder(context, 1);

        ArrayList<SolarSystemGenerator> solarSystemGenerators = worldBuilder.initializeRandomSolarSystemGenerators();
        solarSystemGenerator = solarSystemGenerators.get(0);
        setupSolarSystemGenerator();
        planetGenerator = (PlanetGeneratorImpl) solarSystemGenerators.get(0).getActiveFeatureGenerators().get(1);
    }

    private void setupMockGL() {
        GL20 mockGL = new MockGL();
        Gdx.gl = mockGL;
        Gdx.gl20 = mockGL;
    }

    private void setupConfigManagers() {
        ItemManager itemManager = setupItemManager();
        HullConfigManager hullConfigManager = setupHullConfigManager(itemManager);

        PlanetConfigs planetConfigManager = new PlanetConfigs(hullConfigManager, gameColors,itemManager);
        context.put(PlanetConfigs.class, planetConfigManager);

        SolarSystemConfigManager solarSystemConfigManager = new SolarSystemConfigManager(hullConfigManager, itemManager);
        context.put(SolarSystemConfigManager.class, solarSystemConfigManager);
    }

    private ItemManager setupItemManager() {
        gameColors = new GameColors();
        effectTypes = new EffectTypes();
        soundManager = new OggSoundManager(context);
        return new ItemManager(soundManager, effectTypes, gameColors);
    }

    private HullConfigManager setupHullConfigManager(ItemManager itemManager) {
        abilityCommonConfigs = new AbilityCommonConfigs(effectTypes, gameColors, soundManager);
        return new HullConfigManager(itemManager, abilityCommonConfigs);
    }

    private void setupSolarSystemGenerator() {
        solarSystemGenerator.initializeRandomDefaultFeatureGenerators(1f);
        solarSystemGenerator.calculateFeaturePositions();
        solarSystemGenerator.buildFeatureGenerators();
        solarSystemGenerator.getBuiltPlanets().get(0);
    }

}
