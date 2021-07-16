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

import org.destinationsol.common.SolRandom;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is a concrete implementation of a MazeGenerator and handles its creation. This class defines the
 * behavior specific to the default Mazes in Destination: Sol.
 * TODO: Implement the behavior of default MazeGenerators in this class (As it is implemented in the game currently)
 */
public class MazeGeneratorImpl extends MazeGenerator {
    private static final Logger logger = LoggerFactory.getLogger(MazeGeneratorImpl.class);

    @Override
    public void build() {
        logger.info("Building a maze now!");
        setRadius(SolRandom.seededRandomFloat(.7f, 1) * MAX_MAZE_RADIUS);
    }
}
