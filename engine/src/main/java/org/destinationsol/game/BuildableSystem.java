/*
 * Copyright 2019 MovingBlocks
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
package org.destinationsol.game;

import com.badlogic.gdx.math.Vector2;
import org.destinationsol.game.chunk.SpaceEnvConfig;

import java.util.List;

/**
 * Functional interface allows creation of a buildable system to be built on the outskirts of planetary systems.
 * @see org.destinationsol.game.planet.SystemsBuilder for initialisation
 * See org.destinationsol.mazes.Maze for an example use-case.
 */
public interface BuildableSystem extends UpdateAwareSystem {

    /**
     * Initialise the {@code BuildableSystem}
     * @param configurationSystems {@code List<ConfigurationSystem>} All implementations found by gestalt
     * @param position of the {@code BuildableSystem} as determined by {@code SystemsBuilder#build}
     * @param radius of the {@code BuildableSystem} as determined by {@code SystemsBuilder#build}
     */
    void build(List<ConfigurationSystem> configurationSystems, Vector2 position, float radius);

    /**
     * Gets position of this {@code BuildableSystem}
     * @return {@code Vector2} position
     */
    Vector2 getPosition();

    /**
     * Gets radius of this {@code BuildableSystem}
     * @return {@code float} radius
     */
    float getRadius();

    /**
     * Gets the maximum radius allowed for this {@code BuildableSystem} for use in {@code SystemsBuilder#build}
     * @return {@code float} maximum radius
     */
    float getMaximumRadius();

    /**
     * Gets space environment configuration of this {@code BuildableSystem} to determine any background objects to draw
     * @return {@code SpaceEnvConfig} spaceEnvironmentConfiguration
     */
    SpaceEnvConfig getSpaceEnvironmentConfiguration();

    /**
     * Gets the estimated DPS of this {@code BuildableSystem} to determine its potential danger against the {@code Hero}
     * @return {@code float} damagePerSecond
     */
    float getDamagePerSecond();

    /**
     * Gets border distance to use on top of the {@code BuildableSystem} radius to draw map icon
     * @return {@code float} map border
     */
    float getMapBorder();

    /**
     * Gets gestalt location of texture to use for the map icon
     * @return {@code String} map texture location
     */
    String getMapTextureLocation();
}
