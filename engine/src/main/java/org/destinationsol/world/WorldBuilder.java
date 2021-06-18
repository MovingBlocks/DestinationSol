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

import org.destinationsol.common.SolRandom;
import org.destinationsol.game.context.Context;
import org.destinationsol.modules.ModuleManager;
import org.destinationsol.world.generators.FeatureGenerator;
import org.destinationsol.world.generators.SolarSystemGenerator;

import java.lang.reflect.Modifier;
import java.util.*;

/**
 * This class is the starting point for world generation. When a new world is created, this class first retrieves
 * all Generator classes and then initiates each SolarSystem's build process. Two types of world generation classes are
 * retrieved: those that subclass SolarSystemGenerator and those at subclass FeatureGenerator.
 */
public class WorldBuilder {

    //These ArrayLists hold instances of any class which extends SolarSystemGenerator or FeatureGenerator, respectively
    private ArrayList<SolarSystemGenerator> solarSystemGenerators = new ArrayList<>();
    private ArrayList<FeatureGenerator> featureGenerators = new ArrayList<>();
    private Context context;
    private final int numberOfSystems;
    private int systemsBuilt; //This field is for testing whether the correct number of SolarSystems are built

    public WorldBuilder(Context context, int numSystems) {
        this.context = context;
        numberOfSystems = numSystems;
        populateSolarSystemGeneratorList();
        populateFeatureGeneratorList();
        systemsBuilt = 0;
    }

    /**
     * This method uses reflection to retrieve all SolarSystemGenerator classes. They are added to the list
     * of SolarSystemGenerators.
     */
    private void populateSolarSystemGeneratorList() {

        for (Class generator : context.get(ModuleManager.class).getEnvironment().getSubtypesOf(SolarSystemGenerator.class)) {
            try {
                SolarSystemGenerator solarSystemGenerator = (SolarSystemGenerator) generator.newInstance();
                solarSystemGenerators.add(solarSystemGenerator);
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * This method uses reflection to retrieve all concrete FeatureGenerator classes. They are added to the list
     * of FeatureGenerators.
     */
    private void populateFeatureGeneratorList() {
        for (Class generator : context.get(ModuleManager.class).getEnvironment().getSubtypesOf(FeatureGenerator.class)) {
            if (!Modifier.isAbstract(generator.getModifiers())) {
                try {
                    FeatureGenerator featureGenerator = (FeatureGenerator) generator.newInstance();
                    featureGenerators.add(featureGenerator);
                } catch (InstantiationException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * This method initializes the SolarSystemGenerators. How many generators are initialized depends on the number
     * of SolarSystems the world is set to have. When there are multiple types of SolarSystemGenerators available, this
     * method chooses randomly from the list of all generators to decide which to create.
     *
     * This method calls setFeatureGenerators() on each SolarSystemGenerator to give each SolarSystem access to other
     * FeatureGenerators (Planets, Mazes, etc.) so they can be initialized and incorporated into the SolarSystem.
     */
    private void initializeRandomSolarSystemGenerators() {
        for (int i = 0; i < numberOfSystems; i++) {
            SolarSystemGenerator solarSystemGenerator = solarSystemGenerators.get(SolRandom.seededRandomInt(solarSystemGenerators.size()));
            solarSystemGenerator.setFeatureGenerators(featureGenerators);
            solarSystemGenerator.build();
            systemsBuilt++;
        }
    }

    public void buildWithRandomSolarSystemGenerators() {
        initializeRandomSolarSystemGenerators();
    }

    public int getSystemsBuilt() {
        return systemsBuilt;
    }

    public ArrayList<SolarSystemGenerator> getSolarSystemGenerators() {
        return solarSystemGenerators;
    }

    public ArrayList<FeatureGenerator> getFeatureGenerators() {
        return featureGenerators;
    }

    public Context getContext() {
        return context;
    }
}
