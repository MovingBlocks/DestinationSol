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
package org.destinationsol.world.generators;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import org.destinationsol.files.HullConfigManager;
import org.destinationsol.game.AbilityCommonConfigs;
import org.destinationsol.game.GameColors;
import org.destinationsol.game.context.Context;
import org.destinationsol.game.context.internal.ContextImpl;
import org.destinationsol.game.item.ItemManager;
import org.destinationsol.game.particle.EffectTypes;
import org.destinationsol.assets.sound.OggSoundManager;
import org.destinationsol.game.planet.SolarSystemConfigManager;
import org.destinationsol.modules.ModuleManager;
import org.destinationsol.testingUtilities.MockGL;
import org.destinationsol.testsupport.AssetsHelperInitializer;
import org.destinationsol.world.WorldBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.destinationsol.world.generators.MazeGenerator.MAZE_BUFFER;
import static org.destinationsol.world.generators.PlanetGenerator.PLANET_MAX_DIAMETER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SolarSystemGeneratorTest implements AssetsHelperInitializer {
    private Context context;
    private ModuleManager moduleManager;
    private WorldBuilder worldBuilder;
    private GameColors gameColors;
    private EffectTypes effectTypes;
    private OggSoundManager soundManager;
    private AbilityCommonConfigs abilityCommonConfigs;
    private SolarSystemGenerator solarSystemGenerator;

    @BeforeEach
    public void setUp() throws Exception {
        context = new ContextImpl();
        moduleManager = getModuleManager();
        moduleManager.init();
        context.put(ModuleManager.class, moduleManager);

        setupMockGL();

        SolarSystemConfigManager solarSystemConfigManager = setupSolarSystemConfigManager();
        context.put(SolarSystemConfigManager.class, solarSystemConfigManager);

        worldBuilder = new WorldBuilder(context, 1);

        ArrayList<SolarSystemGenerator> solarSystemGenerators = worldBuilder.initializeRandomSolarSystemGenerators();
        solarSystemGenerator = solarSystemGenerators.get(0);
        setupSolarSystemGenerator();
    }

    private void setupMockGL() {
        GL20 mockGL = new MockGL();
        Gdx.gl = mockGL;
        Gdx.gl20 = mockGL;
    }

    private SolarSystemConfigManager setupSolarSystemConfigManager() {
        ItemManager itemManager = setupItemManager();
        HullConfigManager hullConfigManager = setupHullConfigManager(itemManager);
        return new SolarSystemConfigManager(hullConfigManager, itemManager);
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
        solarSystemGenerator.buildFeatureGenerators();
        solarSystemGenerator.calculateFeaturePositions();
    }

    @Test
    void hasCorrectPlanetCountForDefaultSolarSystem() {
        if (solarSystemGenerator.getClass().equals(SolarSystemGeneratorImpl.class)) {
            assertEquals(solarSystemGenerator.getPlanetCount(), 5);
        }
    }

    @Test
    void hasCorrectMazeCountForDefaultSolarSystem() {
        if (solarSystemGenerator.getClass().equals(SolarSystemGeneratorImpl.class)) {
            assertEquals(solarSystemGenerator.getMazeCount(), 2);
        }
    }

    @Test
    void hasCorrectPossibleBeltCountForDefaultSolarSystem() {
        if (solarSystemGenerator.getClass().equals(SolarSystemGeneratorImpl.class)) {
            assertEquals(solarSystemGenerator.getPossibleBeltCount(), 1);
        }
    }

    @Test
    void hasCorrectRadius() {
        float radiusFromFeatures = 0;
        radiusFromFeatures += SunGenerator.SUN_RADIUS;
        for (FeatureGenerator featureGenerator : solarSystemGenerator.getActiveFeatureGenerators()) {
            if (!featureGenerator.getClass().getSuperclass().equals(MazeGenerator.class)) {
                radiusFromFeatures += FeatureGenerator.ORBITAL_FEATURE_BUFFER;
                radiusFromFeatures += PLANET_MAX_DIAMETER;
                radiusFromFeatures += FeatureGenerator.ORBITAL_FEATURE_BUFFER;
            }
        }
        //This only needs to be added once, not for each MazeGenerator. This is because each Maze is in the same orbital
        radiusFromFeatures += MAZE_BUFFER + MazeGenerator.MAX_MAZE_DIAMETER + MAZE_BUFFER;
        //This value will tend to be off by 0.0001 even if calculated correctly, so we are testing if they are very close
        assertTrue(solarSystemGenerator.getRadius() - radiusFromFeatures < 1f);

    }

    @Test
    void mazesAreCorrectDistanceFromSolarSystemCenter() {
        float actualMazeDistance = 0;
        float expectedMazeDistance = 0;
        expectedMazeDistance = solarSystemGenerator.getRadius() - MAZE_BUFFER - (MazeGenerator.MAX_MAZE_DIAMETER / 2);

        for (FeatureGenerator featureGenerator : solarSystemGenerator.getActiveFeatureGenerators()) {
            if (featureGenerator.getClass().getSuperclass().equals(MazeGenerator.class)) {
                actualMazeDistance = featureGenerator.getPosition().dst(solarSystemGenerator.getPosition());
                //This value will tend to be off by 0.0001 even if calculated correctly, so we are testing if they are very close
                assertTrue(expectedMazeDistance - actualMazeDistance < 1f);
            }
        }
    }

    @Test
    void beltPositionEqualsSolarSystemPosition() {
        for (FeatureGenerator featureGenerator : solarSystemGenerator.getActiveFeatureGenerators()) {
            if (featureGenerator.getClass().getSuperclass().equals(BeltGenerator.class)) {
                assertEquals(featureGenerator.getPosition(), solarSystemGenerator.getPosition());
            }
        }
    }

    @Test
    void sunPositionEqualsSolarSystemPosition() {
        for (FeatureGenerator featureGenerator : solarSystemGenerator.getActiveFeatureGenerators()) {
            if (featureGenerator.getClass().getSuperclass().equals(SunGenerator.class)) {
                assertEquals(featureGenerator.getPosition(), solarSystemGenerator.getPosition());
            }
        }
    }

    @Test
    void planetsAreInsideSolarSystem() {
        for (FeatureGenerator featureGenerator : solarSystemGenerator.getActiveFeatureGenerators()) {
            if (featureGenerator.getClass().getSuperclass().equals(PlanetGenerator.class)) {
                assertTrue(featureGenerator.getPosition().dst(solarSystemGenerator.getPosition()) < solarSystemGenerator.getRadius());
            }
        }
    }

    @Test
    void beltsAreInsideSolarSystem() {
        for (FeatureGenerator featureGenerator : solarSystemGenerator.getActiveFeatureGenerators()) {
            if (featureGenerator.getClass().getSuperclass().equals(BeltGenerator.class)) {
                assertTrue(featureGenerator.getPosition().dst(solarSystemGenerator.getPosition()) < solarSystemGenerator.getRadius());
            }
        }
    }
}
