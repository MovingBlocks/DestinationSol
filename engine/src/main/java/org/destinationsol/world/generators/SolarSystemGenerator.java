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

    private static final float BELT_HALF_WIDTH = 20f;
    private static final float MAX_MAZE_RADIUS = 40f;
    private static final float MAZE_GAP = 10f;

    //This field is protected to allow subclasses to access it
    protected ArrayList<FeatureGenerator> featureGeneratorTypes = new ArrayList<>();
    ArrayList<FeatureGenerator> activeFeatureGenerators = new ArrayList<>();
    private Vector2 position;
    private float radius;
    private boolean positioned;
    private int planetCount = 5;

    public SolarSystemGenerator() {
        positioned = false;
    }

    public SolarSystemGenerator(int planets) {
        planetCount = planets;
        generateSystemRadius(planetCount);
    }

    /**
     * This method is intended to first set up the SolarSystem during world generation and then initialize all the
     * FeatureGenerators of that SolarSystem
     * @return
     */
    public abstract void build();

    /**
     * This method generates a radius value for this SolarSystemGenerator. This value will be calculated based on the width of the
     * planets within the SolarSystem
     * @param planets number of planets to generate
     */
    public void generateSystemRadius(int planets) {
        List<Float> groundHeights = generatePlanetGroundHeights();
        radius = calcSolarSystemRadius(groundHeights);
    }

    /**
     * This method generates the ground heights that will be associated with each planet in the SolarSystem
     * It occasionally generates a height corresponding to a belt instead of a planet
     * @return a List of generated planet heights for this SolarSystem
     */
    private List<Float> generatePlanetGroundHeights() {
        ArrayList<Float> groundHeights = new ArrayList<>();
        boolean beltCreated = false;
        for (int i = 0; i < planetCount; i++) {
            boolean createBelt = !beltCreated && 0 < i && i < .5f * planetCount && SolRandom.seededTest(.6f);
            float groundHeight;
            if (!createBelt) {
                groundHeight = SolRandom.seededRandomFloat(.5f, 1) * Const.MAX_GROUND_HEIGHT;
            } else {
                groundHeight = -BELT_HALF_WIDTH;
                beltCreated = true;
            }
            groundHeights.add(groundHeight);
        }
        return groundHeights;
    }

    /**
     * This method calclutes the radius for this SolarSystem. It uses a list of previously generated Planet heights to
     * determine how wide the SolarSystem will need to be. It also adds some extra radius width for mazes at the edge of
     * the system.
     * @param planetGroundHeights heights associated with each planet
     * @return total Radius of this SolarSystem
     */
    private float calcSolarSystemRadius(List<Float> planetGroundHeights) {
        float solarSystemRadius = 0;
        //add height of the sun
        solarSystemRadius += Const.SUN_RADIUS;
        //add space for Maze zone
        solarSystemRadius += (MAX_MAZE_RADIUS + MAZE_GAP);
        for (Float groundHeight : planetGroundHeights) {
            solarSystemRadius += Const.PLANET_GAP;
            if (groundHeight > 0) {
                //add the total height a planet takes up in the SolarSystem
                solarSystemRadius += Const.ATM_HEIGHT;
                solarSystemRadius += groundHeight;
                solarSystemRadius += groundHeight;
                solarSystemRadius += Const.ATM_HEIGHT;
            } else {
                //add the total height a belt takes up in the SolarSystem (groundHeight will already be negative)
                solarSystemRadius -= groundHeight;
                solarSystemRadius -= groundHeight;
            }
            //prevents planets from touching in their orbits
            solarSystemRadius += Const.PLANET_GAP;
        }
        return solarSystemRadius;
    }

    /**
     * This places mazes within the outer edge of the solar system. It check to make sure they are places at least 15
     * degrees apart so they do not overlap. Currently, it only works properly for two maze SolarSystem (this is the
     * game default)
     * TODO: refactor to allow for more mazes
     */
    void placeMazes() {
        int numMazes = 0;
        boolean firstMaze = true;
        float firstAngle = 0;
        for (FeatureGenerator generator : activeFeatureGenerators) {
            Vector2 result = new Vector2();
            if (generator.getClass().getSuperclass().equals(MazeGenerator.class)) {
                numMazes++;
                float angle = SolRandom.seededRandomFloat(180);
                SolMath.fromAl(result, angle, (radius - ((MAX_MAZE_RADIUS + MAZE_GAP) / 2)));
                result = result.add(position);
                if (firstMaze) {
                    generator.setPosition(result);
                    firstAngle = angle;
                    firstMaze = false;
                } else {
                    boolean placed = false;
                    while (!placed) {
                        float angle2 = SolRandom.seededRandomFloat(180);
                        SolMath.fromAl(result, angle2, (radius - ((MAX_MAZE_RADIUS + MAZE_GAP) / 2)));
                        result = result.add(position);
                        if (angle2 - angle < 15) {
                            continue;
                        } else {
                            placed = true;
                            generator.setPosition(result);
                            return;
                        }
                    }
                }
            }
        }
    }

    /**
     * This will create new instances of PlanetGenerator implementations from the list of available feature generators.
     * It will only choose FeatureGenerators which are subclasses of {@link PlanetGenerator}.
     * It will continue to make new instances until the correct number of planets is reached
     */
    public void initializeRandomPlanetGenerators() {
        int planetsLeft = planetCount;
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
     * It will continue to make new instances until the correct number of mazes is reached
     */
    public void initializeRandomMazeGenerators(int mazes) {
        int mazesLeft = mazes;
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

    public void setFeatureGeneratorTypes(ArrayList<FeatureGenerator> generators) {
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

}
