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
import org.destinationsol.world.WorldBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

        setupConfigManagers();

        worldBuilder = new WorldBuilder(context, 1);

        ArrayList<SolarSystemGenerator> solarSystemGenerators = worldBuilder.initializeRandomSolarSystemGenerators();
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

    private void setupConfigManagers() {
        ItemManager itemManager = setupItemManager();
        HullConfigManager hullConfigManager = setupHullConfigManager(itemManager);

        PlanetConfigManager planetConfigManager = new PlanetConfigManager(hullConfigManager, gameColors,itemManager);
        planetConfigManager.loadDefaultPlanetConfigs();
        context.put(PlanetConfigManager.class, planetConfigManager);

        SolarSystemConfigManager solarSystemConfigManager = new SolarSystemConfigManager(hullConfigManager, itemManager);
        context.put(SolarSystemConfigManager.class, solarSystemConfigManager);

        MazeConfigManager mazeConfigManager = new MazeConfigManager(hullConfigManager, itemManager);
        mazeConfigManager.loadDefaultMazeConfigs();
        context.put(MazeConfigManager.class, mazeConfigManager);

        BeltConfigManager beltConfigManager = new BeltConfigManager(hullConfigManager, itemManager);
        beltConfigManager.loadDefaultBeltConfigs();
        context.put(BeltConfigManager.class, beltConfigManager);
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
    void planetHasPositiveRotationSpeed() {
        assertTrue(planetGenerator.getPlanetRotationSpeed() > 0);
    }

    @Test
    void planetHasPositiveOrbitSpeed() {
        assertTrue(planetGenerator.getPlanetRotationSpeed() > 0);
    }
}
