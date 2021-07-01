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

import java.util.ArrayList;
import java.util.List;

/**
 * This class defines the general behavior for Planet generators (such as belts, radius etc). Any SolarSystem in the game
 * will be created from a concrete implementation of this class, with its specific implementation defined there.
 * Every SolarSystem is given access to all the available FeatureGenerators (PlanetGenerators, MazeGenerators, etc).
 * Particular implementations can decide which of those FeatureGenerators will be used to populate the SolarSystem.
 */
public abstract class SolarSystemGenerator {
    protected static final float MAX_MAZE_RADIUS = 40f;
    protected static final float MAZE_GAP = 10f;

    //This field is protected to allow subclasses to access it
    protected ArrayList<FeatureGenerator> featureGeneratorTypes = new ArrayList<>();
    ArrayList<FeatureGenerator> activeFeatureGenerators = new ArrayList<>();
    private Vector2 position;
    private float radius;
    private boolean positioned;
    private int planetCount = 5;
    private int possibleBeltCount = 1;
    private int mazeCount = 2;
    private int sunCount = 1;
    private int otherFeaturesCount = 0;
    private ArrayList<Float> planetDistances = new ArrayList<>();

    public SolarSystemGenerator() {
        position = Vector2.Zero;
    }

    /**
     * This method is intended to first set up the SolarSystem during world generation and then initialize all the
     * FeatureGenerators of that SolarSystem
     */
    public abstract void build();

    /**
     * This method calclutes the radius for this SolarSystem. It uses the Features that are included in this system
     * to determine what the radius should be.
     */
    protected float calcSolarSystemRadius() {
        float solarSystemRadius = 0;
        for (FeatureGenerator featureGenerator : activeFeatureGenerators) {
            solarSystemRadius += getTotalFeatureWidth(featureGenerator);
        }
        solarSystemRadius += MAZE_GAP;
        return solarSystemRadius;
    }

    /**
     * This method assigns a position to the sun, equal to the SolarSystem position. It assumes there is only one
     * Sun in the system.
     */
    protected void calculateSunPositionOneSun() {
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
    protected void calculateMazePositions() {
        for (FeatureGenerator generator : activeFeatureGenerators) {
            Vector2 result = new Vector2();
            List<Float> usedAnglesInSolarSystem = new ArrayList<>();
            if (generator.getClass().getSuperclass().equals(MazeGenerator.class)) {
                float angle = SolRandom.seededRandomFloat(180);
                SolMath.fromAl(result, angle, (radius - (MAX_MAZE_RADIUS + MAZE_GAP)));
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
    protected void calculatePlanetPositions() {
        float distanceFromSolarSystemCenter = Const.SUN_RADIUS;
        for (FeatureGenerator featureGenerator : activeFeatureGenerators) {
            Vector2 result = new Vector2();
            if (featureGenerator.getClass().getSuperclass().equals(PlanetGenerator.class)) {
                PlanetGenerator generator = (PlanetGenerator) featureGenerator;

                distanceFromSolarSystemCenter += Const.PLANET_GAP;
                distanceFromSolarSystemCenter += featureGenerator.getRadius();

                generator.setDistanceFromSolarSystemCenter(distanceFromSolarSystemCenter);
                SolMath.fromAl(result, generator.angleInSolarSystem, distanceFromSolarSystemCenter);
                generator.setPosition(result);
                planetDistances.add(distanceFromSolarSystemCenter);

                distanceFromSolarSystemCenter += featureGenerator.getRadius();
                distanceFromSolarSystemCenter += Const.PLANET_GAP;
                System.out.println(generator + " distance from center: " + generator.distanceFromSolarSystemCenter);
            }
        }
    }

    /**
     * This method is used when inserting a Belt between Planets within a SolarSystem. It takes the position of the
     * planet before where the best will be placed, adds half of the belt width, and then places the Belt at that position.
     * Then it moves the Planets which are outside of the Belt to be further away.
     */
    protected void calculateBeltPositions() {
        int beltsPlaced = 0;
        ArrayList<Integer> usedPositions = new ArrayList<>();
        for (FeatureGenerator featureGenerator : activeFeatureGenerators) {
            if (featureGenerator.getClass().getSuperclass().equals(BeltGenerator.class) && beltsPlaced < (getPlanetCount() - 1)) {
                int orbitInsertionPosition = chooseOrbitInsertionPosition(usedPositions);
                float distanceFromSolarSystemCenter = planetDistances.get(orbitInsertionPosition);

                distanceFromSolarSystemCenter += Const.PLANET_GAP;
                distanceFromSolarSystemCenter += featureGenerator.getRadius();
                ((BeltGenerator) featureGenerator).setDistanceFromCenter(distanceFromSolarSystemCenter);
                System.out.println("Distance for belt: " + distanceFromSolarSystemCenter);
                repositionPlanetsFurther(featureGenerator, distanceFromSolarSystemCenter);

                //Belt position is equal to SolarSystem position as belts wrap around the entire SolarSystem
                featureGenerator.setPosition(getPosition());
            }
        }
    }

    /**
     * This will create new instances of SunGenerator implementations from the list of available feature generators.
     * It will only choose FeatureGenerators which are subclasses of {@link SunGenerator}.
     * It will continue to make new instances until the correct number of Suns is reached
     */
    protected void initializeRandomSunGenerators() {
        int sunsLeft = getSunCount();
        while (sunsLeft > 0) {
            int index = SolRandom.seededRandomInt(featureGeneratorTypes.size());
            if (featureGeneratorTypes.get(index).getClass().getSuperclass().equals(SunGenerator.class)) {
                Class<? extends FeatureGenerator> newFeatureGeneratorType = featureGeneratorTypes.get(index).getClass();
                try {
                    FeatureGenerator newFeatureGenerator = newFeatureGeneratorType.newInstance();
                    activeFeatureGenerators.add(newFeatureGenerator);
                    sunsLeft--;
                } catch (Exception e) {
                    e.printStackTrace();
                }
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
            if (featureGeneratorTypes.get(index).getClass().getSuperclass().equals(PlanetGenerator.class)) {
                Class<? extends FeatureGenerator> newFeatureGeneratorType = featureGeneratorTypes.get(index).getClass();
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
        int mazesLeft = getMazeCount();
        while (mazesLeft > 0) {
            int index = SolRandom.seededRandomInt(featureGeneratorTypes.size());
            if (featureGeneratorTypes.get(index).getClass().getSuperclass().equals(MazeGenerator.class)) {
                Class<? extends FeatureGenerator> newFeatureGeneratorType = featureGeneratorTypes.get(index).getClass();
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
     * @param beltChance chance each belt possibility will actually generate a belt
     */
    protected void initializeRandomBeltGenerators(float beltChance) {
        int beltsLeft = getPossibleBeltCount();
        while (beltsLeft > 0) {
            int index = SolRandom.seededRandomInt(featureGeneratorTypes.size());
            if (featureGeneratorTypes.get(index).getClass().getSuperclass().equals(BeltGenerator.class)) {
                Class<? extends FeatureGenerator> newFeatureGeneratorType = featureGeneratorTypes.get(index).getClass();
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
                Class<? extends FeatureGenerator> newFeatureGeneratorType = featureGeneratorTypes.get(index).getClass();
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
     * This method determines the total width of a feature. For most features, that width is equal to 2 times the
     * feature radius plus a gap on each side of the feature. For the sun, the full width is not necessary, as only
     * half of it is in the radius of the SolarSystem. A gap is also not added for the sun
     * @param featureGenerator feature to determine width of
     * @return width of the feature
     */
    private float getTotalFeatureWidth(FeatureGenerator featureGenerator) {
        float featureWidth = 0;
        if (featureGenerator.getClass().getSuperclass().equals(SunGenerator.class)) {
            featureWidth += featureGenerator.getRadius();
        } else {
            featureWidth += Const.PLANET_GAP;
            featureWidth += (featureGenerator.getRadius() * 2);
            //prevents planets from touching in their orbits
            featureWidth += Const.PLANET_GAP;
        }
        return featureWidth;
    }

    /**
     * This method is used to choose a valid position to insert a belt into. It chooses a position that is between two planets
     * (not on the edge of the system and not previously used
     * @param usedPositions list of previously used insertion positions
     * @return position to use
     */
    private int chooseOrbitInsertionPosition(ArrayList<Integer> usedPositions) {
        int orbitPosition = 0;
        boolean positionChosen = false;
        while (!positionChosen) {
            orbitPosition = SolRandom.seededRandomInt(1, planetDistances.size());
            if (!usedPositions.contains(orbitPosition)) {
                usedPositions.add(orbitPosition);
                positionChosen = true;
            }
        }
        return orbitPosition;
    }

    /**
     * This method is used when inserting a Generator between Planets within a SolarSystem. It moves any Planets outside
     * of the orbit of that Generator further away from the SolarSystem center. It moves them the width of the Generator.
     * @param generator the Generator which is being inserted between planets
     * @param distance distance from SolarSystem center that the Generator is being placed.
     */
    private void repositionPlanetsFurther(FeatureGenerator generator, float distance) {
        for (FeatureGenerator featureGenerator : activeFeatureGenerators) {
            //if the feature is a planet and is further than the belt, move the planet away
            if (featureGenerator.getClass().getSuperclass().equals(PlanetGenerator.class)
                    && featureGenerator.getPosition().dst(getPosition()) >= distance) {
                Vector2 positionToSet = new Vector2();
                float distanceFromSun = featureGenerator.getPosition().dst(this.getPosition());
                distanceFromSun += Const.PLANET_GAP;
                distanceFromSun += generator.getRadius();
                distanceFromSun += generator.getRadius();
                distanceFromSun += Const.PLANET_GAP;
                ((PlanetGenerator) featureGenerator).setDistanceFromSolarSystemCenter(distanceFromSun);
                SolMath.fromAl(positionToSet, ((PlanetGenerator) featureGenerator).angleInSolarSystem, distanceFromSun);
                positionToSet.add(this.position);
                generator.setPosition(positionToSet);
            }
        }
    }

    /**
     * This method checks if two angles are within a certain number of degrees of each other or not
     * @param firstAngleToCheck angle to check
     * @param secondAngleToCheck angle to compare it to
     * @param degrees how many degrees apart to ensure angles are between
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
     * @param index index of generator in list
     * @return true if not one of the default types
     */
    private boolean isOtherGeneratorType(int index) {
        return !featureGeneratorTypes.get(index).getClass().getSuperclass().equals(BeltGenerator.class)
                && !featureGeneratorTypes.get(index).getClass().getSuperclass().equals(MazeGenerator.class)
                && !featureGeneratorTypes.get(index).getClass().getSuperclass().equals(PlanetGenerator.class)
                && !featureGeneratorTypes.get(index).getClass().getSuperclass().equals(SunGenerator.class);
    }

    public void setFeatureGeneratorTypes(ArrayList<FeatureGenerator> generators) {
        featureGeneratorTypes.addAll(generators);
    }

    public void setPosition(Vector2 position) {
        Vector2 oldPosition = new Vector2(this.getPosition());
        this.position = position;
        for (FeatureGenerator featureGenerator : activeFeatureGenerators) {
            //Move each FeatureGenerator of the SolarSystemGenerator with the SolarSystemGenerator
            Vector2 featurePosition = new Vector2(featureGenerator.getPosition());
            featureGenerator.setPosition(featurePosition.add(position.sub(oldPosition)));
        }
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

    public void setSunCount(int sunCount) {
        this.sunCount = sunCount;
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
        return otherFeaturesCount;
    }

    public int getSunCount() {
        return sunCount;
    }



}
