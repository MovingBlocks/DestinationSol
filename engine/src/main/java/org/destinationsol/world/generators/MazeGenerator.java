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
import org.destinationsol.game.ShipConfig;
import org.destinationsol.game.maze.Maze;
import org.destinationsol.game.maze.MazeConfig;
import org.destinationsol.game.maze.MazeConfigs;
import org.destinationsol.game.maze.MazeTile;

/**
 * This class defines the general behavior for Maze generators (such as having a layout, or a radius). Any Maze will be
 * created from a concrete implementation of this class, with behavior specific to that Maze defined there.
 */
public abstract class MazeGenerator extends FeatureGenerator {
    protected static final float MAX_MAZE_RADIUS = 40f;
    public static final float MAX_MAZE_DIAMETER = 80f;
    public static final float MAZE_BUFFER = 10f;
    private MazeConfigs mazeConfigManager;
    private MazeConfig mazeConfig;
    private Maze maze;

    protected void modifyInnerEnemiesFrequency(float frequencyMultiplier) {
        for (ShipConfig shipConfig : mazeConfig.innerEnemies) {
            shipConfig.density *= frequencyMultiplier;
        }
    }

    protected void modifyOuterEnemiesFrequency(float frequencyMultiplier) {
        for (ShipConfig shipConfig : mazeConfig.outerEnemies) {
            shipConfig.density *= frequencyMultiplier;
        }
    }

    protected void modifyBossFrequency(float frequencyMultiplier) {
        for (ShipConfig shipConfig : mazeConfig.bosses) {
            shipConfig.density *= frequencyMultiplier;
        }
    }

    public void setMazeConfig(MazeConfig mazeConfig) {
        this.mazeConfig = mazeConfig;
    }

    public MazeConfig getMazeConfig() {
        return mazeConfig;
    }

    public MazeConfig getRandomMazeConfig() {
         return SolRandom.seededRandomElement(mazeConfigManager.configs);
    }

    protected void instantiateMaze() {
        maze = new Maze(getMazeConfig(), getPosition(), getRadius());
    }

    public void setMazeConfigManager(MazeConfigs mazeConfigManager) {
        this.mazeConfigManager = mazeConfigManager;
    }

    public Maze getMaze() {
        return maze;
    }

    protected float calculateDefaultMazeSize() {
        return SolRandom.seededRandomFloat(.7f, 1) * MAX_MAZE_RADIUS;
    }
}
