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

import org.destinationsol.world.generators.FeatureGenerator;
import org.destinationsol.world.generators.MazeGenerator;
import org.destinationsol.world.generators.PlanetGenerator;

import static org.destinationsol.world.generators.FeatureGenerator.ORBITAL_FEATURE_BUFFER;

/**
 * This class represents one 'ring' around a SolarSystem's center. It is used to keep track of what Features a SolarSystemGenerator
 * should generate. It keeps track of where those Features will be placed, and whether or not a Feature has been chosen
 * for a specific orbital. Each orbital has a width, which is the distance from the inner edge of the orbital to the
 * outer edge.
 */
public class Orbital {
    public static final float PLANET_ORBITAL_WIDTH = ORBITAL_FEATURE_BUFFER + PlanetGenerator.PLANET_MAX_DIAMETER + ORBITAL_FEATURE_BUFFER;
    public static final float MAZE_ORBITAL_WIDTH = MazeGenerator.MAZE_BUFFER + MazeGenerator.MAX_MAZE_DIAMETER + MazeGenerator.MAZE_BUFFER;

    int positionInSolarSystem;
    float width;
    float startingDistanceFromSystemCenter;
    FeatureGenerator featureGenerator;
    String superTypeName;


    public Orbital(int position) {
        positionInSolarSystem = position;
    }

    public void setPositionInSolarSystem(int positionInSolarSystem) {
        this.positionInSolarSystem = positionInSolarSystem;
    }

    public void setStartingDistanceFromSystemCenter(float startingDistanceFromSystemCenter) {
        this.startingDistanceFromSystemCenter = startingDistanceFromSystemCenter;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public void setFeatureGenerator(FeatureGenerator featureGenerator) {
        this.featureGenerator = featureGenerator;
        superTypeName = featureGenerator.getClass().getSuperclass().getTypeName();
    }

    public int getPositionInSolarSystem() {
        return positionInSolarSystem;
    }

    public float getStartingDistanceFromSystemCenter() {
        return startingDistanceFromSystemCenter;
    }

    public float getWidth() {
        return width;
    }

    public FeatureGenerator getFeatureGenerator() {
        return featureGenerator;
    }

    /**
     * Each feature is placed in the center of the orbital ring, which is half of the orbitals width from the start
     * @return distance from the center of the SolarSystem to place the Feature
     */
    public float calculateDistanceFromCenterOfSystemForFeature() {
        return getStartingDistanceFromSystemCenter() + (getWidth() / 2);
    }

    public String getSuperTypeName() {
        return superTypeName;
    }
}
