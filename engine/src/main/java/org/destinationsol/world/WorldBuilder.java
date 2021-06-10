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
package org.destinationsol.world;

import org.destinationsol.game.context.Context;
import org.destinationsol.modules.ModuleManager;
import org.destinationsol.world.generators.FeatureGenerator;
import org.destinationsol.world.generators.SolSystemGenerator;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Random;

/**
 * This class is the starting point for world generation. When a new world is created, this class first retrieves
 * all Generator classes and then initiates each SolSystem's build process. Two types of world generation classes are
 * retrieved: classes that subclass SolSystemGenerator and those at subclass FeatureGenerator.
 */
public class WorldBuilder {
    ArrayList<SolSystemGenerator> solSystemGenerators = new ArrayList<>();
    ArrayList<FeatureGenerator> featureGenerators = new ArrayList<>();
    Context context;
    private final int numberOfSystems;


    /**
     * Initialize the WorldBuilder class
     * @param context System Context
     * @param numSystems Number of SolSystems the world should have. This can be set by the user
     */
    public WorldBuilder(Context context, int numSystems) {
        this.context = context;
        numberOfSystems = numSystems;
        populateSolSystemGeneratorList();
        populateFeatureGeneratorList();
        initializeSolSystemGenerators();
    }

    /**
     * This method uses reflection to retrieve all SolSystemGenerator classes. They are added to the list
     * of SolSystemGenerators.
     */
    private void populateSolSystemGeneratorList() {

        for (Class generator : context.get(ModuleManager.class).getEnvironment().getSubtypesOf(SolSystemGenerator.class)) {
            try {
                for (int i = 0; i < numberOfSystems; i++) {
                    //for each SolSystem available, we will make the number of instances equal to the total number
                    //of SolSystems we want for our world
                    SolSystemGenerator solSystemGenerator = (SolSystemGenerator) generator.newInstance();
                    solSystemGenerators.add(solSystemGenerator);
                }
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
     * This method initializes the SolSystemGenerators. How many generators are initialized depends on the number
     * of SolSystems the world is set to have. When there are multiple types of SolSystemGenerators available, this
     * method chooses randomly from the list of all generators to decide which to create.
     */
    private void initializeSolSystemGenerators() {
        Random random = new Random();
        for (int i = 0; i < numberOfSystems; i++) {
            int systemIndex = random.nextInt(solSystemGenerators.size());
            SolSystemGenerator solGenerator = solSystemGenerators.get(systemIndex);
            solSystemGenerators.remove(systemIndex);
            solGenerator.setFeatureGenerators(featureGenerators);
            solGenerator.build();
        }
    }
}
