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
import org.terasology.gestalt.module.Module;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class WorldConfig {
    protected long seed;
    protected int numberOfSystems;
    private List<String> solarSystemGenerators;
    private List<String> featureGenerators;
    private Set<Module> modules;

    public WorldConfig() {
        seed = System.currentTimeMillis();
        numberOfSystems = SystemsBuilder.DEFAULT_SYSTEM_COUNT;
        solarSystemGenerators = new ArrayList<>();
        featureGenerators = new ArrayList<>();
        modules = new HashSet<>();
    }

    public WorldConfig(long seed, int numberOfSystems,
                       List<String> solarSystemGenerators,
                       List<String> featureGenerators,
                       Set<Module> modules) {
        this.seed = seed;
        this.numberOfSystems = numberOfSystems;
        this.solarSystemGenerators = solarSystemGenerators;
        this.featureGenerators = featureGenerators;
        this.modules = modules;
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

    public Set<Module> getModules() {
        return modules;
    }

    public void setModules(Set<Module> modules) {
        this.modules = modules;
    }
}
