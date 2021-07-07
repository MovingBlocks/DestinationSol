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
import org.destinationsol.common.SolMath;
import org.destinationsol.common.SolRandom;
import org.destinationsol.game.planet.SolarSystem;
import org.destinationsol.world.Orbital;

import java.util.ArrayList;
import java.util.List;

import static org.destinationsol.world.generators.FeatureGenerator.ORBITAL_FEATURE_BUFFER;
import static org.destinationsol.world.generators.SunGenerator.SUN_RADIUS;

/**
 * This class defines the general behavior for Planet generators (such as belts, radius etc). Any SolarSystem in the game
 * will be created from a concrete implementation of this class, with its specific implementation defined there.
 * Every SolarSystem is given access to all the available FeatureGenerators (PlanetGenerators, MazeGenerators, etc).
 * Particular implementations can decide which of those FeatureGenerators will be used to populate the SolarSystem.
 */
public abstract class SolarSystemGenerator {

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
    private SolarSystemSize solarSystemSize;
    private Vector2 position;
    private float radius;
    private boolean positioned;
    private int planetCount;
    private int possibleBeltCount;
    private int mazeCount;
    private int customFeaturesCount;

    //This is the final runtime SolarSystem object this generator will create
    private SolarSystem solarSystem;

    public SolarSystemGenerator() {
        position = Vector2.Zero;
        solarSystemSize = getSolarSystemSize();
        if (solarSystemSize.equals(SolarSystemSize.SMALL) || solarSystemSize.equals(SolarSystemSize.MEDIUM)) {
            planetCount = solarSystemSize.getNumberOfOrbitals() - getCustomFeaturesCount();
            possibleBeltCount = solarSystemSize.getNumberOfOrbitals() - planetCount - getCustomFeaturesCount() + 1;
            mazeCount = 2;
        } else {
            planetCount = solarSystemSize.getNumberOfOrbitals() - getCustomFeaturesCount();
            possibleBeltCount = solarSystemSize.getNumberOfOrbitals() - planetCount - getCustomFeaturesCount() + 2;
            mazeCount = 3;
        }
        customFeaturesCount = getCustomFeaturesCount();

        for (int i = 0; i < solarSystemSize.getNumberOfOrbitals(); i++) {
            solarSystemOrbitals.add(new Orbital(i));
        }
        sizeSolarSystemOrbitals();
        this.radius = calcSolarSystemRadius();
    }

    private void sizeSolarSystemOrbitals() {
        float distanceFromSolarSystemCenter = SUN_RADIUS;
        for (int i = 0; i < solarSystemOrbitals.size(); i++) {
            solarSystemOrbitals.get(i).setStartingDistanceFromSystemCenter(distanceFromSolarSystemCenter);
            solarSystemOrbitals.get(i).setWidth(ORBITAL_FEATURE_BUFFER + PlanetGenerator.PLANET_MAX_DIAMETER + ORBITAL_FEATURE_BUFFER);
            distanceFromSolarSystemCenter += solarSystemOrbitals.get(i).getWidth();
        }
        //An extra Orbital is added to represent the Mazes
        solarSystemOrbitals.add(new Orbital(solarSystemOrbitals.size()));
        solarSystemOrbitals.get(solarSystemOrbitals.size() - 1).setStartingDistanceFromSystemCenter(distanceFromSolarSystemCenter);
        solarSystemOrbitals.get(solarSystemOrbitals.size() - 1).setWidth(MazeGenerator.MAZE_BUFFER + MazeGenerator.MAX_MAZE_DIAMETER + MazeGenerator.MAZE_BUFFER);
    }

    /**
     * This method is intended to first set up the SolarSystem during world generation and then initialize all the
     * FeatureGenerators of that SolarSystem
     */
    public abstract void build();

    /**
     * This should be implemented so that SolarSystemGenerator implementations can choose what size SolarSystem to generate.
     * The options are Small (3 orbital objects), Medium (5 orbital objects), and Large (7 orbital objects)
     *
     * @return size of the solar system (SolarSystemSize enum type)
     */
    public abstract SolarSystemSize getSolarSystemSize();

    /**
     * Override this method to tell the WorldBuilder how many non-default Features you want your SolarSystem to generate.
     *
     * @return number of custom features
     */
    public abstract int getCustomFeaturesCount();

    /**
     * This method calculates the radius for this SolarSystem. It uses the Features that are included in this system
     * to determine what the radius should be.
     */
    protected float calcSolarSystemRadius() {
        float solarSystemRadius = SUN_RADIUS;
        for (Orbital orbital : solarSystemOrbitals) {
            solarSystemRadius += orbital.getWidth();
            System.out.println("Orbital " + orbital.getPositionInSolarSystem() + ", distance: " + orbital.getStartingDistanceFromSystemCenter() + ", width: " + orbital.getWidth());
        }
        return solarSystemRadius;
    }

    /**
     * This method assigns a position to the sun, equal to the SolarSystem position.
     */
    private void calculateSunPosition() {
        for (FeatureGenerator generator : activeFeatureGenerators) {
            if (generator.getClass().getSuperclass().equals(SunGenerator.class)) {
                generator.setPosition(position);
            }
        }
    }

    /**
     * This places mazes within the outer orbital of the solar system. It checks to make sure they are placed at least 15
     * degrees apart so they do not overlap
     */
    private void calculateMazePositions() {
        for (FeatureGenerator generator : activeFeatureGenerators) {
            Vector2 result = new Vector2();
            List<Float> usedAnglesInSolarSystem = new ArrayList<>();
            if (generator.getClass().getSuperclass().equals(MazeGenerator.class)) {
                //set the outermost orbital to have a MazeGenerator
                solarSystemOrbitals.get(solarSystemOrbitals.size() - 1).setFeatureGenerator(generator);
                float angle = SolRandom.seededRandomFloat(180);
                SolMath.fromAl(result, angle, solarSystemOrbitals.get(solarSystemOrbitals.size() - 1).calculateDistanceFromCenterOfSystemForFeature());
                if (isGoodAngle(usedAnglesInSolarSystem, angle, 15)) {
                    result = result.add(position);
                    generator.setPosition(result);
                    usedAnglesInSolarSystem.add(angle);
                }
            }
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
            Vector2 result = new Vector2();
            if (featureGenerator.getClass().getSuperclass().equals(PlanetGenerator.class)) {
                PlanetGenerator generator = (PlanetGenerator) featureGenerator;
                Orbital orbital = solarSystemOrbitals.get(orbitalPosition);
                orbital.setFeatureGenerator(generator);

                generator.setDistanceFromSolarSystemCenter(orbital.calculateDistanceFromCenterOfSystemForFeature());
                SolMath.fromAl(result, generator.angleInSolarSystem, generator.getDistanceFromSolarSystemCenter());
                result.add(position);
                generator.setPosition(result);

                System.out.println(generator + " distance from center: " + generator.distanceFromSolarSystemCenter);
                orbitalPosition++;
            }
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
            if (featureGenerator.getClass().getSuperclass().equals(BeltGenerator.class) && beltsPlaced < (getPlanetCount() - 1)) {
                int orbitInsertionPosition = chooseOrbitInsertionPosition();
                float distanceFromSolarSystemCenter = solarSystemOrbitals.get(orbitInsertionPosition).calculateDistanceFromCenterOfSystemForFeature();
                ((BeltGenerator) featureGenerator).setDistanceFromCenterOfSolarSystem(distanceFromSolarSystemCenter);

                //remove the planet that was going to be placed in this orbital
                featureGeneratorsToRemove.add(solarSystemOrbitals.get(orbitInsertionPosition).getFeatureGenerator());
                solarSystemOrbitals.get(orbitInsertionPosition).setFeatureGenerator(featureGenerator);
                System.out.println("Distance for belt: " + distanceFromSolarSystemCenter);

                //Belt position is equal to SolarSystem position as belts wrap around the entire SolarSystem
                featureGenerator.setPosition(getPosition());
            }
        }
        activeFeatureGenerators.removeAll(featureGeneratorsToRemove);
    }

    protected void calculateFeaturePositions() {
        calculateSunPosition();
        calculateMazePositions();
        calculatePlanetPositions();
        calculateBeltPositions();
    }

    /**
     * This will create a new instance of a SunGenerator implementation from the list of available feature generators.
     * It will only choose FeatureGenerators which are subclasses of {@link SunGenerator}.
     * It will make one sun generator instance.
     */
    protected void initializeRandomSunGenerator() {
        int index = SolRandom.seededRandomInt(featureGeneratorTypes.size());
        if (featureGeneratorTypes.get(index).getSuperclass().equals(SunGenerator.class)) {
            Class<? extends FeatureGenerator> newFeatureGeneratorType = featureGeneratorTypes.get(index);
            try {
                FeatureGenerator newFeatureGenerator = newFeatureGeneratorType.newInstance();
                activeFeatureGenerators.add(newFeatureGenerator);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
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
            if (featureGeneratorTypes.get(index).getSuperclass().equals(PlanetGenerator.class)) {
                Class<? extends FeatureGenerator> newFeatureGeneratorType = featureGeneratorTypes.get(index);
                try {
                    FeatureGenerator newFeatureGenerator = newFeatureGeneratorType.newInstance();
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
            if (featureGeneratorTypes.get(index).getSuperclass().equals(MazeGenerator.class)) {
                Class<? extends FeatureGenerator> newFeatureGeneratorType = featureGeneratorTypes.get(index);
                try {
                    FeatureGenerator newFeatureGenerator = newFeatureGeneratorType.newInstance();
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
            if (featureGeneratorTypes.get(index).getSuperclass().equals(BeltGenerator.class)) {
                Class<? extends FeatureGenerator> newFeatureGeneratorType = featureGeneratorTypes.get(index);
                try {
                    if (SolRandom.seededTest(beltChance)) {
                        FeatureGenerator newFeatureGenerator = newFeatureGeneratorType.newInstance();
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
        initializeRandomSunGenerator();
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
                Class<? extends FeatureGenerator> newFeatureGeneratorType = featureGeneratorTypes.get(index);
                try {
                    FeatureGenerator newFeatureGenerator = newFeatureGeneratorType.newInstance();
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
            if (!solarSystemOrbitals.get(orbitPosition).getSuperTypeName().equals(BeltGenerator.class.getTypeName())) {
                positionChosen = true;
            }
        }
        return orbitPosition;
    }

    /**
     * This method checks if two angles are within a certain number of degrees of each other or not
     *
     * @param firstAngleToCheck  angle to check
     * @param secondAngleToCheck angle to compare it to
     * @param degrees            how many degrees apart to ensure angles are between
     * @return whether angles are within specified amount of degrees or not
     */
    private boolean isGoodAngle(List<Float> firstAngleToCheck, float secondAngleToCheck, int degrees) {
        for (Float oldAngle : firstAngleToCheck) {
            //This assures that mazes are placed at least 15 degrees apart
            if (oldAngle - secondAngleToCheck < degrees || oldAngle - secondAngleToCheck > (360 - degrees)) {
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
        return !featureGeneratorTypes.get(index).getSuperclass().equals(BeltGenerator.class)
                && !featureGeneratorTypes.get(index).getSuperclass().equals(MazeGenerator.class)
                && !featureGeneratorTypes.get(index).getSuperclass().equals(PlanetGenerator.class)
                && !featureGeneratorTypes.get(index).getSuperclass().equals(SunGenerator.class);
    }

    public void setFeatureGeneratorTypes(ArrayList<Class<? extends FeatureGenerator>> generators) {
        featureGeneratorTypes.addAll(generators);
    }

    public void setPosition(Vector2 position) {
        this.position = position;
        setPositioned(true);
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

    public void setPositioned(boolean p) {
        positioned = p;
    }

    public boolean getPositioned() {
        return positioned;
    }

    public ArrayList<FeatureGenerator> getActiveFeatureGenerators() {
        return activeFeatureGenerators;
    }

    public void setPlanetCount(int planetCount) {
        this.planetCount = planetCount;
    }

    public void setPossibleBeltCount(int possibleBeltCount) {
        this.possibleBeltCount = possibleBeltCount;
    }

    public void setMazeCount(int mazeCount) {
        this.mazeCount = mazeCount;
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

}
