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
import org.destinationsol.assets.sound.OggSoundManager;
import org.destinationsol.files.HullConfigManager;
import org.destinationsol.game.AbilityCommonConfigs;
import org.destinationsol.game.GameColors;
import org.destinationsol.game.WorldConfig;
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
import org.terasology.gestalt.di.BeanContext;
import org.terasology.gestalt.di.DefaultBeanContext;
import org.terasology.gestalt.di.ServiceRegistry;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class BeltGeneratorTest implements AssetsHelperInitializer {
    private ServiceRegistry registry;
    private BeanContext context;
    private ModuleManager moduleManager;
    private GalaxyBuilder galaxyBuilder;
    private GameColors gameColors;
    private EffectTypes effectTypes;
    private OggSoundManager soundManager;
    private AbilityCommonConfigs abilityCommonConfigs;
    private SolarSystemGenerator solarSystemGenerator;
    private BeltGenerator beltGenerator;

    @BeforeEach
    public void setUp() throws Exception {
        registry = new ServiceRegistry();
        moduleManager = getModuleManager();
        registry.with(ModuleManager.class).use(() -> moduleManager).lifetime(Lifetime.Singleton);

        setupMockGL();

        setupConfigManagers();

        context = new DefaultBeanContext(registry);
        galaxyBuilder = new GalaxyBuilder(new WorldConfig(), moduleManager, context.getBean(SolarSystemConfigManager.class), context);

        ArrayList<SolarSystemGenerator> solarSystemGenerators = galaxyBuilder.initializeRandomSolarSystemGenerators();
        solarSystemGenerator = solarSystemGenerators.get(0);
        setupSolarSystemGenerator();
        ArrayList<FeatureGenerator> activeFeatureGenerators = solarSystemGenerators.get(0).getActiveFeatureGenerators();
        beltGenerator = (BeltGenerator) activeFeatureGenerators.get(activeFeatureGenerators.size() - 1);
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
        registry.with(PlanetConfigManager.class).use(() -> planetConfigManager).lifetime(Lifetime.Singleton);

        SolarSystemConfigManager solarSystemConfigManager = new SolarSystemConfigManager(hullConfigManager, itemManager);
        registry.with(SolarSystemConfigManager.class).use(() -> solarSystemConfigManager).lifetime(Lifetime.Singleton);

        MazeConfigManager mazeConfigManager = new MazeConfigManager(hullConfigManager, itemManager);
        mazeConfigManager.loadDefaultMazeConfigs();
        registry.with(MazeConfigManager.class).use(() -> mazeConfigManager).lifetime(Lifetime.Singleton);

        BeltConfigManager beltConfigManager = new BeltConfigManager(hullConfigManager, itemManager);
        beltConfigManager.loadDefaultBeltConfigs();
        registry.with(BeltConfigManager.class).use(() -> beltConfigManager).lifetime(Lifetime.Singleton);
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
    void beltPositionIsSolarSystemPosition() {
        assertTrue(beltGenerator.getPosition().equals(solarSystemGenerator.getPosition()));
    }

    @Test
    void beltHasPositiveWidth() {
        assertTrue(beltGenerator.getRadius() > 0);
    }

    @Test
    void beltBeginsWithinSolarSystem() {
        assertTrue(beltGenerator.getDistanceFromCenterOfSolarSystem() < solarSystemGenerator.getRadius());
    }

    @Test
    void beltHasNonNegativeAsteroidFrequency() {
        assertTrue(beltGenerator.getAsteroidFrequency() >= 0);
    }

    @Test
    void beltHasConfigWithEnemies() {
        assertTrue(beltGenerator.getBeltConfig().tempEnemies.size() > 0);
    }
}
