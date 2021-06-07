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
package org.destinationsol.world;


import org.destinationsol.game.context.Context;
import org.destinationsol.modules.ModuleManager;
import org.destinationsol.world.generators.MazeGenerator;
import org.destinationsol.world.generators.PlanetGenerator;
import org.destinationsol.world.generators.SolSystemGenerator;

import java.util.ArrayList;

/**
 * This class is the starting point for world generation
 */
public class WorldBuilder {
    ArrayList<Object> solSystemGenerators = new ArrayList<>();
    ArrayList<Object> planetGenerators = new ArrayList<>();
    ArrayList<Object> mazeGenerators = new ArrayList<>();
    Context context;

    public WorldBuilder(Context context) {
        this.context = context;
        populateGeneratorList();

        initializeSolSystemGenerators();
    }

    /**
     * This method will use reflection to get all generator classes (using the @WorldGenerator annotation)
     */
    private void populateGeneratorList() {

        for (Class generator : context.get(ModuleManager.class).getEnvironment().getSubtypesOf(SolSystemGenerator.class)) {
            if (generator.isAnnotationPresent(WorldGenerator.class)) {
                try {
                    Object solSystemGenerator = generator.newInstance();
                    solSystemGenerators.add(solSystemGenerator);
                } catch (InstantiationException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }

        for (Class generator : context.get(ModuleManager.class).getEnvironment().getSubtypesOf(PlanetGenerator.class)) {
            if (generator.isAnnotationPresent(WorldGenerator.class)) {
                try {
                    Object planetGenerator = generator.newInstance();
                    planetGenerators.add(planetGenerator);
                } catch (InstantiationException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }

        for (Class generator : context.get(ModuleManager.class).getEnvironment().getSubtypesOf(MazeGenerator.class)) {
            if (generator.isAnnotationPresent(WorldGenerator.class)) {
                try {
                    Object mazeGenerator = generator.newInstance();
                    mazeGenerators.add(mazeGenerator);
                } catch (InstantiationException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Eventually this method will be used to pick a random assortment of the available generators. The number of
     * SolSystemGenerators used will depend on how many Sol Systems the world should have
     */
    private void initializeSolSystemGenerators() {
        for (Object gen : solSystemGenerators) {
            SolSystemGenerator generator = (SolSystemGenerator) gen;
            generator.setPlanetGenerators(planetGenerators);
            generator.setMazeGenerators(mazeGenerators);
            generator.build();
        }
    }
}
