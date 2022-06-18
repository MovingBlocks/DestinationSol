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

import com.badlogic.gdx.math.Vector2;
import org.destinationsol.Const;
import org.destinationsol.common.SolMath;
import org.destinationsol.common.SolRandom;
import org.destinationsol.game.SolNames;
import org.destinationsol.game.maze.Maze;
import org.destinationsol.game.maze.MazeConfigManager;
import org.destinationsol.game.planet.BeltConfigManager;
import org.destinationsol.game.planet.Planet;
import org.destinationsol.game.planet.PlanetConfigManager;
import org.destinationsol.game.planet.SolarSystem;
import org.destinationsol.game.planet.SolarSystemConfig;
import org.destinationsol.game.planet.SolarSystemConfigManager;
import org.destinationsol.game.planet.SystemBelt;
import org.destinationsol.world.Orbital;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.context.annotation.IndexInherited;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

/**
 * This class defines the general behavior for Planet generators (such as belts, radius etc). Any SolarSystem in the game
 * will be created from a concrete implementation of this class, with its specific implementation defined there.
 * Every SolarSystem is given access to all the available FeatureGenerators (PlanetGenerators, MazeGenerators, etc).
 * Particular implementations can decide which of those FeatureGenerators will be used to populate the SolarSystem.
 */
@IndexInherited
public abstract class SolarSystemGenerator {
    public static final float MAX_ANGLE_IN_SYSTEM = 180;
    public static final float SUN_RADIUS = 2f * (Const.MAX_GROUND_HEIGHT + Const.ATM_HEIGHT);
    private static final Logger logger = LoggerFactory.getLogger(SolarSystemGenerator.class);

    /**
     * This enum represents the three available sizes of SolarSystems. They can have either 3, 5, or 7 orbital objects
     */
    public enum SolarSystemSize {
        SMALL(3), MEDIUM(5), LARGE(7);

        private final int numberOfOrbitals;

        SolarSystemSize(int orbitals) {
            numberOfOrbitals = orbitals;
        }

        public int getNumberOfOrbitals() {
            return numberOfOrbitals;
        }
    }

    //This field is protected to allow subclasses to access it
    protected ArrayList<Class<? extends FeatureGenerator>> featureGeneratorTypes = new ArrayList<>();
    ArrayList<FeatureGenerator> activeFeatureGenerators = new ArrayList<>();
    ArrayList<Orbital> solarSystemOrbitals = new ArrayList<>();
    //This class is included to give access to the game's default names if desired
    protected SolNames solNames = new SolNames();
    private SolarSystemConfigManager solarSystemConfigManager;
    private SolarSystemConfig solarSystemConfig;
    private SolarSystemSize solarSystemSize;
    private Vector2 position = new Vector2();
    private String name;
    private float radius;
    private boolean positioned;
    private int solarSystemNumber;
    private int planetCount;
    private int possibleBeltCount;
    private int mazeCount;
    private int customFeaturesCount;

    @Inject
    protected PlanetConfigManager planetConfigManager;
    @Inject
    protected MazeConfigManager mazeConfigManager;
    @Inject
    protected BeltConfigManager beltConfigManager;

    public SolarSystemGenerator() {
        solarSystemSize = getSolarSystemSize();

        //There cannot be more custom features than orbitals available
        if (customFeaturesCount < solarSystemSize.getNumberOfOrbitals()) {
            customFeaturesCount = getCustomFeaturesCount();
        } else {
            customFeaturesCount = solarSystemSize.getNumberOfOrbitals();
        }

        if (solarSystemSize.equals(SolarSystemSize.SMALL) || solarSystemSize.equals(SolarSystemSize.MEDIUM)) {
            //Custom Features will replace planets, depending on how many are being used
            planetCount = solarSystemSize.getNumberOfOrbitals() - getCustomFeaturesCount();
            possibleBeltCount = 1;
            mazeCount = 2;
        } else {
            planetCount = solarSystemSize.getNumberOfOrbitals() - getCustomFeaturesCount();
            possibleBeltCount = 2;
            mazeCount = 3;
        }

        for (int i = 0; i < solarSystemSize.getNumberOfOrbitals(); i++) {
            solarSystemOrbitals.add(new Orbital(i));
        }
        sizeSolarSystemOrbitals();
        this.radius = calculateSolarSystemRadius();
    }

    /**
     * This method determines the width of each Orbital in the SolarSystem. It also determines how far away each Orbital
     * will be from the center of the SolarSystem
     */
    private void sizeSolarSystemOrbitals() {
        float distanceFromSolarSystemCenter = SUN_RADIUS;
        for (Orbital solarSystemOrbital : solarSystemOrbitals) {
            solarSystemOrbital.setStartingDistanceFromSystemCenter(distanceFromSolarSystemCenter);
            solarSystemOrbital.setWidth(Orbital.PLANET_ORBITAL_WIDTH);
            distanceFromSolarSystemCenter += solarSystemOrbital.getWidth();
        }
        //An extra Orbital is added to represent the Mazes
        solarSystemOrbitals.add(new Orbital(solarSystemOrbitals.size()));
        solarSystemOrbitals.get(solarSystemOrbitals.size() - 1).setStartingDistanceFromSystemCenter(distanceFromSolarSystemCenter);
        solarSystemOrbitals.get(solarSystemOrbitals.size() - 1).setWidth(Orbital.MAZE_ORBITAL_WIDTH);
    }

    /**
     * This method is intended to first set up the SolarSystem during world generation and then initialize all the
     * FeatureGenerators of that SolarSystem
     */
    public abstract SolarSystem build();

    /**
     * This should be implemented so that SolarSystemGenerator implementations can choose what size SolarSystem to generate.
     * The options are Small (3 orbital objects), Medium (5 orbital objects), and Large (7 orbital objects)
     *
     * @return size of the solar system (SolarSystemSize enum type)
     */
    public abstract SolarSystemSize getSolarSystemSize();

    /**
     * Override this method to tell the GalaxyBuilder how many non-default Features you want your SolarSystem to generate.
     *
     * @return number of custom features
     */
    public abstract int getCustomFeaturesCount();

    /**
     * This method calculates the radius for this SolarSystem. It uses the Features that are included in this system
     * to determine what the radius should be.
     */
    protected float calculateSolarSystemRadius() {
        float solarSystemRadius = SUN_RADIUS;
        for (Orbital orbital : solarSystemOrbitals) {
            solarSystemRadius += orbital.getWidth();
            logger.info("Orbital " + orbital.getPositionInSolarSystem() + ", distance: " + orbital.getStartingDistanceFromSystemCenter() + ", width: " + orbital.getWidth());
        }
        return solarSystemRadius;
    }

    /**
     * This places mazes within the outer orbital of the solar system. It checks to make sure they are placed at least 15
     * degrees apart so they do not overlap
     */
    private void calculateMazePositions() {
        for (FeatureGenerator generator : activeFeatureGenerators) {
            Vector2 result = SolMath.getVec();
            List<Float> usedAnglesInSolarSystem = new ArrayList<>();
            if (MazeGenerator.class.isAssignableFrom(generator.getClass())) {
                //set the outermost orbital to have a MazeGenerator
                solarSystemOrbitals.get(solarSystemOrbitals.size() - 1).setFeatureGenerator(generator);
                float angle = SolRandom.seededRandomFloat(MAX_ANGLE_IN_SYSTEM);
                float distanceToPutMaze = (solarSystemOrbitals.get(solarSystemOrbitals.size() - 1).calculateDistanceFromCenterOfSystemForFeature()
                        + getRadius()) / 2;
                SolMath.fromAl(result, angle, distanceToPutMaze);
                if (isGoodAngle(angle, usedAnglesInSolarSystem, 15)) {
                    result = result.add(position);
                    generator.setPosition(result);
                    usedAnglesInSolarSystem.add(angle);
                }
            }
            SolMath.free(result);
        }
    }

    /**
     * This method goes through every active PlanetGenerator and assigns a position to it in the SolarSystem. That
     * position will be based of a random starting angle in the system, which is generated by the PlanetGenerator. It adds
     * a small gap between the orbit of each planet, so they do not touch. The width of each orbit is based on the Planet's
     * radius, which is determined by the PlanetGenerator.
     */
    private void calculatePlanetPositions() {
        int orbitalPosition = 0;
        for (FeatureGenerator featureGenerator : activeFeatureGenerators) {
            Vector2 result = SolMath.getVec();
            if (PlanetGenerator.class.isAssignableFrom(featureGenerator.getClass())) {
                PlanetGenerator generator = (PlanetGenerator) featureGenerator;
                Orbital orbital = solarSystemOrbitals.get(orbitalPosition);
                orbital.setFeatureGenerator(generator);

                generator.setAngleInSolarSystem(SolRandom.seededRandomFloat(MAX_ANGLE_IN_SYSTEM));
                generator.setInitialPlanetAngle(SolRandom.seededRandomFloat(MAX_ANGLE_IN_SYSTEM));
                generator.setDistanceFromSolarSystemCenter(orbital.calculateDistanceFromCenterOfSystemForFeature());

                SolMath.fromAl(result, generator.getAngleInSolarSystem(), generator.getDistanceFromSolarSystemCenter());
                result.add(position);
                generator.setPosition(result);
                generator.setSolarSystemPosition(this.getPosition());
                generator.setIsInnerPlanet(generator.getDistanceFromSolarSystemCenter() < this.radius / 2);
                generator.setIsInFirstSolarSystem(getSolarSystemNumber() == 0);

                logger.info(generator + " distance from center: " + generator.getDistanceFromSolarSystemCenter());
                orbitalPosition++;
            }
            SolMath.free(result);
        }
    }

    /**
     * This method is used when inserting a Belt between Planets within a SolarSystem. It takes the position of the
     * planet before where the best will be placed, adds half of the belt width, and then places the Belt at that position.
     * Then it moves the Planets which are outside of the Belt to be further away.
     */
    private void calculateBeltPositions() {
        int beltsPlaced = 0;
        //this list is necessary to keep track of which planets are replaced. They cannot be removed while iterating through the list of generators
        ArrayList<FeatureGenerator> featureGeneratorsToRemove = new ArrayList<>();
        for (FeatureGenerator featureGenerator : activeFeatureGenerators) {
            if (BeltGenerator.class.isAssignableFrom(featureGenerator.getClass()) && beltsPlaced < (getPlanetCount() - 1)) {
                int orbitInsertionPosition = chooseOrbitInsertionPosition();
                float distanceFromSolarSystemCenter = solarSystemOrbitals.get(orbitInsertionPosition).calculateDistanceFromCenterOfSystemForFeature();
                ((BeltGenerator) featureGenerator).setDistanceFromCenterOfSolarSystem(distanceFromSolarSystemCenter);

                /* Since a belt will be placed at a random orbital, but it isn't guaranteed that a belt will be placed,
                first each orbital (except the maze orbitals) have Planets inserted into them, and then when a belt
                is placed, a planet is removed and a maze set in its place */
                featureGeneratorsToRemove.add(solarSystemOrbitals.get(orbitInsertionPosition).getFeatureGenerator());
                solarSystemOrbitals.get(orbitInsertionPosition).setFeatureGenerator(featureGenerator);
                logger.info("Distance for belt: " + distanceFromSolarSystemCenter);

                //Belt position is equal to SolarSystem position as belts wrap around the entire SolarSystem
                featureGenerator.setPosition(getPosition());
            }
        }
        activeFeatureGenerators.removeAll(featureGeneratorsToRemove);
    }

    /**
     * Call this method to position all of the default objects in the game.
     */
    protected void calculateFeaturePositions() {
        calculateMazePositions();
        calculatePlanetPositions();
        calculateBeltPositions();
    }

    /**
     * This will create new instances of PlanetGenerator implementations from the list of available feature generators.
     * It will only choose FeatureGenerators which are subclasses of {@link PlanetGenerator}.
     * It will continue to make new instances until the correct number of Planets is reached
     */
    protected void initializeRandomPlanetGenerators() {
        int planetsLeft = getPlanetCount();
        while (planetsLeft > 0) {
            int index = SolRandom.seededRandomInt(featureGeneratorTypes.size());
            if ((PlanetGenerator.class.isAssignableFrom(featureGeneratorTypes.get(index)))) {
                try {
                    PlanetGenerator newFeatureGenerator = (PlanetGenerator) featureGeneratorTypes.get(index).newInstance();
                    newFeatureGenerator.setPlanetConfigManager(planetConfigManager);
                    activeFeatureGenerators.add(newFeatureGenerator);
                    planetsLeft--;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * This will create new instances of MazeGenerator implementations from the list of available feature generators.
     * It will only choose FeatureGenerators which are subclasses of {@link MazeGenerator}.
     * It will continue to make new instances until the correct number of Mazes is reached
     */
    protected void initializeRandomMazeGenerators() {
        //we will initialize up to 12 mazes to ensure they fit around the SolarSystem
        int mazesLeft = Math.min(getMazeCount(), 12);
        while (mazesLeft > 0) {
            int index = SolRandom.seededRandomInt(featureGeneratorTypes.size());
            if (MazeGenerator.class.isAssignableFrom(featureGeneratorTypes.get(index))) {
                try {
                    MazeGenerator newFeatureGenerator = (MazeGenerator) featureGeneratorTypes.get(index).newInstance();
                    newFeatureGenerator.setMazeConfigManager(mazeConfigManager);
                    activeFeatureGenerators.add(newFeatureGenerator);
                    mazesLeft--;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * This method will initialize a BeltGenerator of a random available type. It can be that the SolarSystem does
     * not necessarily generate a belt for every possible belt in the beltCount (as is the game's default implementation).
     * The chance of each count in the beltCount actually becoming a belt is set by the beltChance parameter
     *
     * @param beltChance chance each belt possibility will actually generate a belt
     */
    protected void initializeRandomBeltGenerators(float beltChance) {
        int beltsLeft = getPossibleBeltCount();
        while (beltsLeft > 0) {
            int index = SolRandom.seededRandomInt(featureGeneratorTypes.size());
            if ((BeltGenerator.class.isAssignableFrom(featureGeneratorTypes.get(index)))) {
                try {
                    if (SolRandom.seededTest(beltChance)) {
                        BeltGenerator newFeatureGenerator = (BeltGenerator) featureGeneratorTypes.get(index).newInstance();
                        newFeatureGenerator.setBeltConfigManager(beltConfigManager);
                        newFeatureGenerator.setInFirstSolarSystem(getSolarSystemNumber() == 0);
                        activeFeatureGenerators.add(newFeatureGenerator);
                    }
                    beltsLeft--;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    protected void initializeRandomDefaultFeatureGenerators(float beltChance) {
        initializeRandomPlanetGenerators();
        initializeRandomMazeGenerators();
        initializeRandomBeltGenerators(beltChance);
    }

    /**
     * This will create new instances of FeatureGenerator implementations from the list of available feature generators.
     * It will only choose FeatureGenerators which are not subclasses of MazeGenerator, PlanetGenerator, and BeltGenerator.
     * It will continue to make new instances until the correct number of features is reached
     */
    protected void initializeRandomOtherFeatureGenerators() {
        int featuresLeft = getOtherFeaturesCount();
        while (featuresLeft > 0) {
            int index = SolRandom.seededRandomInt(featureGeneratorTypes.size());
            if (isOtherGeneratorType(index)) {
                try {
                    FeatureGenerator newFeatureGenerator = featureGeneratorTypes.get(index).newInstance();
                    activeFeatureGenerators.add(newFeatureGenerator);
                    featuresLeft--;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void buildFeatureGenerators() {
        for (FeatureGenerator featureGenerator : activeFeatureGenerators) {
            featureGenerator.build();
        }
    }

    /**
     * This method returns the Planet objects instantiated by the PlanetGenerators of this SolarSystem. It is most useful
     * after the build() method is called on each PlanetGenerator
     * @return List of Planets
     */
    ArrayList<Planet> getInstantiatedPlanets() {
        ArrayList<Planet> planets = new ArrayList<>();
        for (FeatureGenerator featureGenerator : activeFeatureGenerators) {
            if (PlanetGenerator.class.isAssignableFrom(featureGenerator.getClass())) {
                planets.add(((PlanetGenerator) featureGenerator).getPlanet());
            }
        }
        return planets;
    }

    /**
     * This method returns the Maze objects instantiated by the MazeGenerators of this SolarSystem. It is most useful
     * after the build() method is called on each MazeGenerator
     * @return List of Mazes
     */
    ArrayList<Maze> getInstantiatedMazes() {
        ArrayList<Maze> mazes = new ArrayList<>();
        for (FeatureGenerator featureGenerator : activeFeatureGenerators) {
            if (MazeGenerator.class.isAssignableFrom(featureGenerator.getClass())) {
                mazes.add(((MazeGenerator) featureGenerator).getMaze());
            }
        }
        return mazes;
    }

    /**
     * This method returns the SystemBelt objects instantiated by the BeltGenerators of this SolarSystem. It is most useful
     * after the build() method is called on each BeltGenerator
     * @return List of Belts
     */
    ArrayList<SystemBelt> getInstantiatedBelts() {
        ArrayList<SystemBelt> belts = new ArrayList<>();
        for (FeatureGenerator featureGenerator : activeFeatureGenerators) {
            if (BeltGenerator.class.isAssignableFrom(featureGenerator.getClass())) {
                belts.add(((BeltGenerator) featureGenerator).getSystemBelt());
            }
        }
        return belts;
    }

    /**
     * This method is used to choose a valid position to insert a belt into. It chooses a position that is between two planets
     * (not on the edge of the system and not previously used for a belt)
     *
     * @return position to use
     */
    private int chooseOrbitInsertionPosition() {
        int orbitPosition = 0;
        boolean positionChosen = false;
        while (!positionChosen) {
            orbitPosition = SolRandom.seededRandomInt(1, solarSystemOrbitals.size() - 1);
            if (!BeltGenerator.class.isAssignableFrom(solarSystemOrbitals.get(orbitPosition).getFeatureGenerator().getClass())) {
                positionChosen = true;
            }
        }
        return orbitPosition;
    }

    /**
     * This method checks if an angle is not within a certain amount of degrees to any other angle in the passed in list
     *
     * @param anglesToCheckAgainst angles to compare to
     * @param angleToCheck angle to check
     * @param degrees how many degrees apart to ensure angles are between
     * @return whether angles are within specified amount of degrees or not
     */
    private boolean isGoodAngle(float angleToCheck, List<Float> anglesToCheckAgainst, int degrees) {
        for (Float oldAngle : anglesToCheckAgainst) {
            //This assures that the angles are at least the specified degrees apart
            if (oldAngle - angleToCheck < degrees || oldAngle - angleToCheck > (360 - degrees)) {
                return false;
            }
        }
        return true;
    }

    /**
     * This tests if a particular Generator from the list of Generator types is not one of the default Generators
     *
     * @param index index of generator in list
     * @return true if not one of the default types
     */
    private boolean isOtherGeneratorType(int index) {
        return !BeltGenerator.class.isAssignableFrom(featureGeneratorTypes.get(index))
                && !MazeGenerator.class.isAssignableFrom(featureGeneratorTypes.get(index))
                && !PlanetGenerator.class.isAssignableFrom(featureGeneratorTypes.get(index));
    }

    public void setFeatureGeneratorTypes(List<Class<? extends FeatureGenerator>> generators) {
        featureGeneratorTypes.addAll(generators);
    }

    public void setPosition(Vector2 position) {
        this.position = SolMath.getVec(position);
        SolMath.free(position);
        setPositioned(true);
    }

    public void setSolarSystemConfigManager(SolarSystemConfigManager solarSystemConfigManager) {
        this.solarSystemConfigManager = solarSystemConfigManager;
    }

    public SolarSystemConfigManager getSolarSystemConfigManager() {
        return solarSystemConfigManager;
    }

    public void setSolarSystemConfig(SolarSystemConfig solarSystemConfig) {
        this.solarSystemConfig = solarSystemConfig;
    }

    public SolarSystemConfig getSolarSystemConfig() {
        return solarSystemConfig;
    }

    /**
     * This method creates a SolarSystem object from the details set by the Generator.
     * @return The SolarSystem object
     */
    public SolarSystem createInstantiatedSolarSystem() {
        SolarSystem solarSystem = new SolarSystem(getPosition(), getSolarSystemConfig(), getName(), getRadius());
        solarSystem.getPlanets().addAll(getInstantiatedPlanets());
        solarSystem.getMazes().addAll(getInstantiatedMazes());
        solarSystem.getBelts().addAll(getInstantiatedBelts());
        return solarSystem;
    }

    /**
     * Sets the SolarSystemConfig to a Config using default difficulty settings
     */
    public void setSolarSystemConfigUsingDefault() {
        setSolarSystemConfig(getSolarSystemConfigManager().getRandomSolarSystemConfig(getSolarSystemNumber() > 0));
    }

    public Vector2 getPosition() {
        return position;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    public float getRadius() {
        return radius;
    }

    public void setPositioned(boolean positioned) {
        this.positioned = positioned;
    }

    public void setSolarSystemNumber(int solarSystemNumber) {
        this.solarSystemNumber = solarSystemNumber;
    }

    public boolean getPositioned() {
        return positioned;
    }

    public ArrayList<FeatureGenerator> getActiveFeatureGenerators() {
        return activeFeatureGenerators;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPlanetCount() {
        return planetCount;
    }

    public int getPossibleBeltCount() {
        return possibleBeltCount;
    }

    public int getMazeCount() {
        return mazeCount;
    }

    public int getOtherFeaturesCount() {
        return customFeaturesCount;
    }

    public int getSolarSystemNumber() {
        return solarSystemNumber;
    }

    public String getName() {
        return name;
    }

    public ArrayList<String> getDefaultSolarSystemNames() {
        return solNames.systems;
    }

}
