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
package org.destinationsol.world.generators;

import org.destinationsol.world.WorldGenerator;

import java.util.ArrayList;

@WorldGenerator
public class SolSystemGeneratorImpl extends SolSystemGenerator {
    ArrayList<PlanetGenerator> planetGenerators = new ArrayList<>();
    ArrayList<MazeGenerator> mazeGenerators = new ArrayList<>();

    public SolarSystemGeneratorImpl() {
        /*This is necessary to be in the constructor as the number of planets in the System must be determined before
        build() is called, so the system can be placed */
        super(5);
    }

    @Override
    public void build() {
<<<<<<< 63567f5baf8a93b904ba490a530548d8aa8f016c
        for(FeatureGenerator generator : featureGenerators) {
            generator.build();
=======
        initializeRandomPlanetGenerators();
        initializeRandomMazeGenerators(2);

        placeMazes();

        //Just temporary to see where everything is placed
        for (FeatureGenerator generator : activeFeatureGenerators) {
            System.out.println(generator + ": " + generator.getPosition());
>>>>>>> Initial SolarSystemGenerator designing
        }

    }

    @Override
    void initializeMazeGenerators() {
        for (MazeGenerator generator : mazeGenerators) {
            generator.build();
        }
    }

    @Override
    public void setPlanetGenerators(ArrayList<Object> generators) {
        for (Object o : generators) {
            planetGenerators.add((PlanetGenerator) o);
        }
    }

    @Override
    public void setMazeGenerators(ArrayList<Object> generators) {
        for (Object o : generators) {
            mazeGenerators.add((MazeGenerator) o);
        }
    }

    @Override
    public void build() {
        initializePlanetGenerators();
        initializeMazeGenerators();
    }
}
