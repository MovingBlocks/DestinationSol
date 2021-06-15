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

/**
 * This class is a concrete implementation of a SolSystem and handles creation of elements
 * specific to this type of SolSystem (such as how many Planets to generate, how large to make
 * the SolSystem, etc). It defines the behavior specific to the default Planets of Destination: Sol.
 * This class also has access to the featureGenerators list from {@link SolSystemGenerator}.
 * This allows it to choose which FeatureGenerators to use in populating the SolSystem.
 */
public class SolSystemGeneratorImpl extends SolSystemGenerator {

    @Override
    public void build() {
        for(FeatureGenerator generator : featureGenerators) {
            generator.build();
        }
    }
}
