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
import org.destinationsol.common.SolMath;
import org.destinationsol.common.SolRandom;
import org.destinationsol.game.context.Context;
import org.destinationsol.game.planet.SolarSystem;
import org.destinationsol.game.planet.SolarSystemConfigManager;
import org.destinationsol.modules.ModuleManager;
import org.destinationsol.world.generators.FeatureGenerator;
import org.destinationsol.world.generators.SolarSystemGenerator;
import org.destinationsol.world.generators.SunGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is the starting point for world generation. When a new world is created, this class first retrieves
 * all Generator classes and then initiates each SolarSystem's build process. Two types of world generation classes are
 * retrieved: those that subclass SolarSystemGenerator and those that subclass FeatureGenerator.
 */
public class WorldBuilder {
    public static final float MAX_ANGLE_WITHIN_WORLD = 180;
    private static final Logger logger = LoggerFactory.getLogger(WorldBuilder.class);
    private static float maxWorldRadius;
    //These ArrayLists hold class types of any class which extends SolarSystemGenerator or FeatureGenerator, respectively
    private ArrayList<Class<? extends SolarSystemGenerator>> solarSystemGeneratorTypes = new ArrayList<>();
    private ArrayList<Class<? extends FeatureGenerator>> featureGeneratorTypes = new ArrayList<>();
    private ArrayList<SolarSystemGenerator> activeSolarSystemGenerators = new ArrayList<>();
    //This ArrayList will hold the final SolarSystem objects that each SolarSystemGenerator will return
    private ArrayList<SolarSystem> builtSolarSystems = new ArrayList<>();
    private Context context;
    private ModuleManager moduleManager;
    private SolarSystemConfigManager solarSystemConfigManager;
    private final int numberOfSystems;

    public WorldBuilder(Context context, int numSystems) {
        this.context = context;
        this.moduleManager = context.get(ModuleManager.class);
        this.solarSystemConfigManager = context.get(SolarSystemConfigManager.class);
        solarSystemConfigManager.loadDefaultSolarSystemConfigs();
        numberOfSystems = numSystems;

        /* The largest SolarSystems have a radius of 12 Sun Radii, so we will check 12 times the number of Systems out
         * from the center of the world when placing a system. However, in order to ensure a place will be found, the
         * number is multiplied by 100
         */
        maxWorldRadius = (12 * numberOfSystems * 100);

        populateSolarSystemGeneratorList();
        populateFeatureGeneratorList();
    }

    /**
     * This method uses reflection to retrieve all SolarSystemGenerator classes. They are added to the list
     * of SolarSystemGenerators.
     */
    private void populateSolarSystemGeneratorList() {
        //It is necessary to use an iterator as getSubtypesOf() returns an Iterable
        moduleManager.getEnvironment().getSubtypesOf(SolarSystemGenerator.class).iterator().forEachRemaining(solarSystemGeneratorTypes::add);
    }

    /**
     * This method uses reflection to retrieve all concrete FeatureGenerator classes. They are added to the list
     * of FeatureGenerators.
     */
    private void populateFeatureGeneratorList() {

        for (Class<? extends FeatureGenerator> generator : moduleManager.getEnvironment().getSubtypesOf(FeatureGenerator.class)) {
            if (!Modifier.isAbstract(generator.getModifiers())) {
                featureGeneratorTypes.add(generator);
            }
        }
    }

    /**
     * This method builds the world using random types of SolarSystemGenerators. This is the default method of generating
     * worlds in Destination: Sol. Each SolarSystem produced we be a random type of SolarSystem from among the generators
     * available.
     */
    public void buildWithRandomSolarSystemGenerators() {
        activeSolarSystemGenerators.addAll(initializeRandomSolarSystemGenerators());
        positionSolarSystems();
        buildSolarSystems();
    }

    /**
     * This method initializes the SolarSystemGenerators. How many generators are initialized depends on the number
     * of SolarSystems the world is set to have. When there are multiple types of SolarSystemGenerators available, this
     * method chooses randomly from the list of all generators to decide which to create.
     * <p>
     * A Class variable is used in this method to allow new instances to be made of each type of available SolarSystemGenerator.
     * <p>
     * This method calls setFeatureGenerators() on each SolarSystemGenerator to give each SolarSystem access to other
     * FeatureGenerators (Planets, Mazes, etc.) so they can be initialized and incorporated into the SolarSystem.
     */
    public ArrayList<SolarSystemGenerator> initializeRandomSolarSystemGenerators() {
        ArrayList<SolarSystemGenerator> generatorArrayList = new ArrayList<>();
        for (int i = 0; i < numberOfSystems; i++) {
            Class<? extends SolarSystemGenerator> solarSystemGenerator = solarSystemGeneratorTypes.get(SolRandom.seededRandomInt(solarSystemGeneratorTypes.size()));
            try {
                SolarSystemGenerator generator = solarSystemGenerator.newInstance();
                generator.setContext(context);
                generator.setFeatureGeneratorTypes(featureGeneratorTypes);
                generator.setSolarSystemConfigManager(solarSystemConfigManager);
                generator.setSolarSystemNumber(i);
                generatorArrayList.add(generator);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return generatorArrayList;
    }

    /**
     * This method iterates through the loop of SolarSystemGenerators that have been initialized and assigns each of
     * them a position in the world.
     */
    private void positionSolarSystems() {
        for (SolarSystemGenerator generator : activeSolarSystemGenerators) {
            try {
                calculateSolarSystemPosition(activeSolarSystemGenerators, generator, generator.getRadius());
            } catch (RuntimeException e) {
                e.printStackTrace();
            }

            //Printout of generator position for testing (as these positions don't have a representation in the game yet)
            logger.info(generator + " position: " + generator.getPosition().x + ", " + generator.getPosition().y);
        }
    }

    /**
     * This method initiates the build process of each SolarSystemGenerator instance.
     */
    private void buildSolarSystems() {
        for (SolarSystemGenerator solarSystemGenerator : activeSolarSystemGenerators) {
            builtSolarSystems.add(solarSystemGenerator.build());
        }
    }

    /**
     * This method runs a loop which tests 20 random angles at increasing radii starting from the center of the world
     * and working outward until an open spot for the System is found. The tested spot is considered 'open' if placing
     * a SolarSystem there will not cause it to overlap with any others.
     * TODO Implement logic to allow system to be positioned within a particular annulus
     */
    private void calculateSolarSystemPosition(List<SolarSystemGenerator> systems, SolarSystemGenerator solarSystemGenerator, float bodyRadius) throws RuntimeException {
        Vector2 result = SolMath.getVec();
        float distance = 0;
        float counter = 0;
        //This loop should find a position for the SolarSystem well before the counter reaches MAX_WORLD_RADIUS, which
        //changes proportional to the nubmer of systems being generated.

        while (counter < maxWorldRadius) {
            //test 20 spots at each radius
            for (int i = 0; i < 20; i++) {
                calculateRandomWorldPositionAtDistance(result, distance);
                //check for overlap with each SolarSystem which already has been placed
                if (isPositionAvailable(systems, bodyRadius, result)) {
                    solarSystemGenerator.getPosition().add(result);
                    solarSystemGenerator.setPositioned(true);
                    SolMath.free(result);
                    return;
                }
            }
            distance += SunGenerator.SUN_RADIUS;
            counter++;
        }
        SolMath.free(result);
        throw new RuntimeException("Could not find position for SolarSystem");
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
        float angle = SolRandom.seededRandomFloat(MAX_ANGLE_WITHIN_WORLD);
        SolMath.fromAl(result, angle, distance);
    }

    public ArrayList<Class<? extends SolarSystemGenerator>> getSolarSystemGeneratorTypes() {
        return solarSystemGeneratorTypes;
    }

    public ArrayList<SolarSystemGenerator> getActiveSolarSystemGenerators() {
        return activeSolarSystemGenerators;
    }

    public ArrayList<Class<? extends FeatureGenerator>> getFeatureGeneratorTypes() {
        return featureGeneratorTypes;
    }

    public Context getContext() {
        return context;
    }

    public ArrayList<SolarSystem> getBuiltSolarSystems() {
        return builtSolarSystems;
    }
}
