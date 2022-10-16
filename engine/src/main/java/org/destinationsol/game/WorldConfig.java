/*
 * Copyright 2018 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.destinationsol.game;

import org.destinationsol.game.planet.SystemsBuilder;

import java.util.ArrayList;
import java.util.List;

public class WorldConfig {
    protected long seed;
    protected int numberOfSystems;
    private List<String> solarSystemGenerators;
    private List<String> featureGenerators;

    public WorldConfig() {
        seed = System.currentTimeMillis();
        numberOfSystems = SystemsBuilder.DEFAULT_SYSTEM_COUNT;
        solarSystemGenerators = new ArrayList<>();
        featureGenerators = new ArrayList<>();
    }

    public WorldConfig(long seed, int numberOfSystems,
                       List<String> solarSystemGenerators,
                       List<String> featureGenerators) {
        this.seed = seed;
        this.numberOfSystems = numberOfSystems;
        this.solarSystemGenerators = solarSystemGenerators;
        this.featureGenerators = featureGenerators;
    }

    public long getSeed() {
        return seed;
    }

    public void setSeed(long seed) {
        this.seed = seed;
    }

    public int getNumberOfSystems() {
        return numberOfSystems;
    }

    public void setNumberOfSystems(int numberOfSystems) {
        this.numberOfSystems = numberOfSystems;
    }

    public List<String> getSolarSystemGenerators() {
        return solarSystemGenerators;
    }

    public void setFeatureGenerators(List<String> featureGenerators) {
        this.featureGenerators = featureGenerators;
    }

    public List<String> getFeatureGenerators() {
        return featureGenerators;
    }

    public void setSolarSystemGenerators(List<String> solarSystemGenerators) {
        this.solarSystemGenerators = solarSystemGenerators;
    }
}
