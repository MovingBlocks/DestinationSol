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
import com.badlogic.gdx.physics.box2d.Box2D;
import com.badlogic.gdx.physics.box2d.World;
import org.destinationsol.assets.sound.OggSoundManager;
import org.destinationsol.files.HullConfigManager;
import org.destinationsol.game.AbilityCommonConfigs;
import org.destinationsol.game.GameColors;
import org.destinationsol.game.WorldConfig;
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
import org.destinationsol.world.GalaxyBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.terasology.context.Lifetime;
import org.terasology.gestalt.di.DefaultBeanContext;
import org.terasology.gestalt.di.ServiceRegistry;

import java.util.ArrayList;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PlanetGeneratorTest implements AssetsHelperInitializer {

//    private ModuleManager moduleManager;
    private GalaxyBuilder galaxyBuilder;
    private GameColors gameColors;
    private EffectTypes effectTypes;
    private OggSoundManager soundManager;
    private AbilityCommonConfigs abilityCommonConfigs;
    private SolarSystemGenerator solarSystemGenerator;
    private PlanetGenerator planetGenerator;

    @BeforeEach
    public void setUp() throws Exception {
        ServiceRegistry registry = new ServiceRegistry();

        setupMockGL();

        ItemManager itemManager = setupItemManager();
        HullConfigManager hullConfigManager = setupHullConfigManager(itemManager);
        registry.with(GameColors.class).use(() -> gameColors).lifetime(Lifetime.Singleton);
        registry.with(ItemManager.class).use(() -> itemManager).lifetime(Lifetime.Singleton);
        registry.with(HullConfigManager.class).use(() -> hullConfigManager).lifetime(Lifetime.Singleton);
        registry.with(PlanetConfigManager.class).lifetime(Lifetime.Singleton);
        registry.with(PlanetConfigManager.class).lifetime(Lifetime.Singleton);
        registry.with(SolarSystemConfigManager.class).lifetime(Lifetime.Singleton);
        registry.with(MazeConfigManager.class).lifetime(Lifetime.Singleton);
        registry.with(BeltConfigManager.class).lifetime(Lifetime.Singleton);
        registry.with(ModuleManager.class).use(this::getModuleManager).lifetime(Lifetime.Singleton);
        registry.with(WorldConfig.class).use(WorldConfig::new).lifetime(Lifetime.Singleton);
        registry.with(GalaxyBuilder.class).lifetime(Lifetime.Singleton);


        DefaultBeanContext context = new DefaultBeanContext(registry);
        context.getBean(MazeConfigManager.class).loadDefaultMazeConfigs();
        context.getBean(PlanetConfigManager.class).loadDefaultPlanetConfigs();
        context.getBean(BeltConfigManager.class).loadDefaultBeltConfigs();
        context.getBean(ModuleManager.class).init();

        galaxyBuilder = context.getBean(GalaxyBuilder.class);

        ArrayList<SolarSystemGenerator> solarSystemGenerators = galaxyBuilder.initializeRandomSolarSystemGenerators();
        solarSystemGenerator = solarSystemGenerators.get(0);
        setupSolarSystemGenerator();
        planetGenerator = (PlanetGenerator) solarSystemGenerators.get(0).getActiveFeatureGenerators().get(1);
    }

    private void setupMockGL() {
        GL20 mockGL = new MockGL();
        Gdx.gl = mockGL;
        Gdx.gl20 = mockGL;
        Box2D.init();
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

    private void setupSolarSystemGenerator() {
        solarSystemGenerator.initializeRandomDefaultFeatureGenerators(1f);
        solarSystemGenerator.calculateFeaturePositions();
        solarSystemGenerator.buildFeatureGenerators();
    }

    @Test
    void planetDiameterIsWithinRange() {
        assertTrue(planetGenerator.getDiameter() < PlanetGenerator.PLANET_MAX_DIAMETER && planetGenerator.getDiameter() > 0);
    }

    @Test
    void planetRadiusIsGroundHeightPlusAtmosphereHeight() {
        assertEquals(planetGenerator.getRadius(), planetGenerator.getGroundHeight() + planetGenerator.getAtmosphereHeight());
    }

    @Test
    void planetHasConfig() {
        assertNotNull(planetGenerator.getPlanetConfig());
    }

    @Test
    void planetHasName() {
        assertTrue(planetGenerator.getName().length() > 0);
    }

    @Test
    void planetHasNonzeroRotationSpeed() {
        assertTrue(planetGenerator.getPlanetRotationSpeed() != 0);
    }

    @Test
    void planetHasNonzeroOrbitSpeed() {
        assertTrue(planetGenerator.getPlanetRotationSpeed() != 0);
    }
}
