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

import java.util.ArrayList;

/**
 * This class defines the general behavior for Planet generators (such as belts, radius etc). Any SolarSystem in the game
 * will be created from a concrete implementation of this class, with its specific implementation defined there.
 * Every SolarSystem is given access to all the available FeatureGenerators (PlanetGenerators, MazeGenerators, etc).
 * Particular implementations can decide which of those FeatureGenerators will be used to populate the SolarSystem.
 */
public abstract class SolarSystemGenerator {
    //This field is protected as that allows subclasses to access it
    protected ArrayList<FeatureGenerator> featureGenerators = new ArrayList<>();

    /**
     * This method is intended to first set up the SolarSystem during world generation and then initialize all the
     * FeatureGenerators of that SolarSystem
     */
    public abstract void build();

    public void setFeatureGenerators(ArrayList<FeatureGenerator> generators) {
        featureGenerators.addAll(generators);
    }

}
