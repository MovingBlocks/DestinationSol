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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.physics.box2d.Box2D;
import org.destinationsol.assets.sound.OggSoundManager;
import org.destinationsol.files.HullConfigManager;
import org.destinationsol.game.AbilityCommonConfigs;
import org.destinationsol.game.GameColors;
import org.destinationsol.game.context.Context;
import org.destinationsol.game.context.internal.ContextImpl;
import org.destinationsol.game.item.ItemManager;
import org.destinationsol.game.maze.MazeConfigManager;
import org.destinationsol.game.particle.EffectTypes;
import org.destinationsol.game.planet.BeltConfigManager;
import org.destinationsol.game.planet.PlanetConfigManager;
import org.destinationsol.game.planet.SolarSystemConfigManager;
import org.destinationsol.modules.ModuleManager;
import org.destinationsol.testingUtilities.MockGL;
import org.destinationsol.testsupport.AssetsHelperInitializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GalaxyBuilderTest implements AssetsHelperInitializer {
    private Context context;
    private ModuleManager moduleManager;
    GalaxyBuilder galaxyBuilder;
    private GameColors gameColors;
    private EffectTypes effectTypes;
    private OggSoundManager soundManager;
    private AbilityCommonConfigs abilityCommonConfigs;


    @BeforeEach
    public void setUp() throws Exception {
        context = new ContextImpl();
        moduleManager = getModuleManager();
        context.put(ModuleManager.class, moduleManager);

        setupMockGL();

        setupConfigManagers();
    }

    private void setupMockGL() {
        GL20 mockGL = new MockGL();
        Gdx.gl = mockGL;
        Gdx.gl20 = mockGL;
        Box2D.init();
    }

    private void setupConfigManagers() {
        ItemManager itemManager = setupItemManager();
        HullConfigManager hullConfigManager = setupHullConfigManager(itemManager);

        PlanetConfigManager planetConfigManager = new PlanetConfigManager(hullConfigManager, gameColors,itemManager);
        planetConfigManager.loadDefaultPlanetConfigs();
        context.put(PlanetConfigManager.class, planetConfigManager);

        MazeConfigManager mazeConfigManager = new MazeConfigManager(hullConfigManager, itemManager);
        mazeConfigManager.loadDefaultMazeConfigs();
        context.put(MazeConfigManager.class, mazeConfigManager);

        BeltConfigManager beltConfigManager = new BeltConfigManager(hullConfigManager, itemManager);
        beltConfigManager.loadDefaultBeltConfigs();
        context.put(BeltConfigManager.class, beltConfigManager);

        SolarSystemConfigManager solarSystemConfigManager = new SolarSystemConfigManager(hullConfigManager, itemManager);
        context.put(SolarSystemConfigManager.class, solarSystemConfigManager);
    }

    private ItemManager setupItemManager() {
        gameColors = new GameColors();
        effectTypes = new EffectTypes();
        soundManager = Mockito.mock(OggSoundManager.class);
        return new ItemManager(soundManager, effectTypes, gameColors);
    }

    private HullConfigManager setupHullConfigManager(ItemManager itemManager) {
        abilityCommonConfigs = new AbilityCommonConfigs(effectTypes, gameColors, soundManager);
        return new HullConfigManager(itemManager, abilityCommonConfigs);
    }

    @Test
    void populatesSolarSystemsList() {
        int testNumberSystems = 2;
//        galaxyBuilder = new GalaxyBuilder(context, testNumberSystems);
//        galaxyBuilder.buildWithRandomSolarSystemGenerators();
//        assertTrue(galaxyBuilder.getSolarSystemGeneratorTypes().size() > 0);

    }

    @Test
    void populatesFeatureGeneratorsList() {
        int testNumberSystems = 2;
//        galaxyBuilder = new GalaxyBuilder(context, testNumberSystems);
//        galaxyBuilder.buildWithRandomSolarSystemGenerators();
//        assertTrue(galaxyBuilder.getFeatureGeneratorTypes().size() > 0);
    }

    @Test
    void createsCorrectNumberOfSolarSystemGenerators() {
        int testNumberSystems = 2;
//        galaxyBuilder = new GalaxyBuilder(context, testNumberSystems);
//        galaxyBuilder.buildWithRandomSolarSystemGenerators();
//        assertEquals(galaxyBuilder.getActiveSolarSystemGenerators().size(), 2);
    }

    @Test
    void createsCorrectNumberOfSolarSystems() {
        int testNumberSystems = 2;
//        galaxyBuilder = new GalaxyBuilder(context, testNumberSystems);
//        galaxyBuilder.buildWithRandomSolarSystemGenerators();
//        assertEquals(galaxyBuilder.getBuiltSolarSystems().size(), 2);
    }

    @Test
    void setsContext() {
        int testNumberSystems = 2;
//        galaxyBuilder = new GalaxyBuilder(context, testNumberSystems);
//        assertNotNull(galaxyBuilder.getContext());
    }

}
