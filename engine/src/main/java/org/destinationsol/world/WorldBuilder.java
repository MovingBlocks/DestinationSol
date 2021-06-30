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

import com.badlogic.gdx.math.Vector2;
import org.destinationsol.Const;
import org.destinationsol.common.SolMath;
import org.destinationsol.common.SolRandom;
import org.destinationsol.game.context.Context;
import org.destinationsol.modules.ModuleManager;
import org.destinationsol.world.generators.FeatureGenerator;
import org.destinationsol.world.generators.SolarSystemGenerator;

import java.lang.reflect.Array;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is the starting point for world generation. When a new world is created, this class first retrieves
 * all Generator classes and then initiates each SolarSystem's build process. Two types of world generation classes are
 * retrieved: those that subclass SolarSystemGenerator and those that subclass FeatureGenerator.
 */
public class WorldBuilder {
    //These ArrayLists hold an instance of any class which extends SolarSystemGenerator or FeatureGenerator, respectively
    private ArrayList<SolarSystemGenerator> solarSystemGeneratorTypes = new ArrayList<>();
    private ArrayList<FeatureGenerator> featureGeneratorTypes = new ArrayList<>();
    private ArrayList<SolarSystemGenerator> activeSolarSystemGenerators = new ArrayList<>();
    private Context context;
    private final int numberOfSystems;
    //This field is for testing whether the correct number of SolarSystems are built
    private int systemsBuilt;


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
                solarSystemGeneratorTypes.add(solarSystemGenerator);
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
                    featureGeneratorTypes.add(featureGenerator);
                } catch (InstantiationException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void buildWithRandomSolarSystemGenerators() {
        initializeRandomSolarSystemGenerators();
        buildSolarSystems();
        positionSolarSystems();
    }

    /**
     * This method initializes the SolarSystemGenerators. How many generators are initialized depends on the number
     * of SolarSystems the world is set to have. When there are multiple types of SolarSystemGenerators available, this
     * method chooses randomly from the list of all generators to decide which to create.
     *
     * A Class variable is used in this method to allow new instances to be made of each type of available SolarSystemGenerator.
     *
     * This method calls setFeatureGenerators() on each SolarSystemGenerator to give each SolarSystem access to other
     * FeatureGenerators (Planets, Mazes, etc.) so they can be initialized and incorporated into the SolarSystem.
     */
    public ArrayList<SolarSystemGenerator> initializeRandomSolarSystemGenerators() {
        ArrayList<SolarSystemGenerator> generatorArrayList = new ArrayList<>();
        for (int i = 0; i < numberOfSystems; i++) {
            Class<? extends SolarSystemGenerator> solarSystemGenerator =  solarSystemGeneratorTypes.get(SolRandom.seededRandomInt(solarSystemGeneratorTypes.size())).getClass();
            try {
                SolarSystemGenerator s = solarSystemGenerator.newInstance();
                s.setFeatureGeneratorTypes(featureGeneratorTypes);
                generatorArrayList.add(s);
            } catch (Exception e) {
                e.printStackTrace();
            }
            systemsBuilt++;
        }
        return generatorArrayList;
    }

    /**
     * This method iterates through the loop of SolarSystemGenerators that have been initialized and assigns each of
     * them a position in the world.
     */
    public void positionSolarSystems() {
        for (SolarSystemGenerator generator : activeSolarSystemGenerators) {
            Vector2 position = calculateSolarSystemPosition(activeSolarSystemGenerators, generator.getRadius());
            generator.setPosition(position);
            //Printout of generator position for for testing (as these positions don't have a representation in the game yet
            System.out.println(generator + " position: " + generator.getPosition().x + ", " + generator.getPosition().y);
        }
    }

    /**
     * This method initiates the build process of each SolarSystemGenerator instance.
     */
    private void buildSolarSystems() {
        for (SolarSystemGenerator solarSystemGenerator : activeSolarSystemGenerators) {
            solarSystemGenerator.build();
        }
    }

    /**
     * This method runs a loop which tests 20 random angles at increasing radii starting from the center of the world
     * and working outward until an open spot for the System is found. The tested spot is considered 'open' if placing
     * a SolarSystem there will not cause it to overlap with any others.
     * TODO Implement logic to allow system to be positioned within a particular annulus
     */
    private Vector2 calculateSolarSystemPosition(List<SolarSystemGenerator> systems, float bodyRadius) {
        Vector2 result = new Vector2();
        float distance = 0;
        while (true) {
            //test 20 spots at each radius
            for (int i = 0; i < 20; i++) {
                calculateRandomWorldPositionAtDistance(result, distance);
                //check for overlap with each SolarSystem which already has been placed
                if (isPositionAvailable(systems, bodyRadius, result)) {
                    return result;
                }
            }
            distance += Const.SUN_RADIUS;
        }
    }

    private boolean isPositionAvailable(List<SolarSystemGenerator> systems, float bodyRadius, Vector2 result) {
        for (SolarSystemGenerator system : systems) {
            if (system.getPositioned() && system.getPosition().dst(result) < system.getRadius() + bodyRadius) {
                return false;
            }
        }
        return true;
    }

    private void calculateRandomWorldPositionAtDistance(Vector2 result, float distance) {
        float angle = SolRandom.seededRandomFloat(180);
        SolMath.fromAl(result, angle, distance);
    }

    public int getSystemsBuilt() {
        return systemsBuilt;
    }

    public ArrayList<SolarSystemGenerator> getSolarSystemGeneratorTypes() {
        return solarSystemGeneratorTypes;
    }

    public ArrayList<SolarSystemGenerator> getActiveSolarSystemGenerators() {
        return activeSolarSystemGenerators;
    }

    public ArrayList<FeatureGenerator> getFeatureGeneratorTypes() {
        return featureGeneratorTypes;
    }

    public Context getContext() {
        return context;
    }
}
