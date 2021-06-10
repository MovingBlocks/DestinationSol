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

import java.util.ArrayList;

/**
 * This class defines the contract for SolSystem generators. Any SolSystem in the game will be created from a concrete
 * implementation of this class. Every SolSystem is given access to all the available features. Particular
 * implementation can decide which of those features will be used
 */
public abstract class SolSystemGenerator {
    //This field is protected as that allows subclasses to access it
    protected ArrayList<FeatureGenerator> featureGenerators = new ArrayList<>();

    /**
     * This method allows {@link org.destinationsol.world.WorldBuilder} to give SolSystems access to other
     * world features so they can be initialized and incorporated into the SolSystem
     * @param generators The list of FeatureGenerators available
     */
    public void setFeatureGenerators(ArrayList<FeatureGenerator> generators) {
        for (FeatureGenerator feature : generators) {
            featureGenerators.add(feature);
        }
    }

    /**
     * This method is intended to first set up the SolSystem during world generation and then initialize all the
     * FeatureGenerators of that SolSystem
     */
    public abstract void build();

}
