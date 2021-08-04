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

import org.destinationsol.game.ShipConfig;
import org.destinationsol.game.planet.BeltConfig;
import org.destinationsol.game.planet.BeltConfigManager;
import org.destinationsol.game.planet.SystemBelt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class defines the general behavior for Belt generators (such as asteroid frequency). Any Belt will be
 * created from a concrete implementation of this class, with behavior specific to that Belt defined there.
 */
public abstract class BeltGenerator extends FeatureGenerator {
    /** This is the distance from the inner ring of the belt to the outer ring */
    protected static final float DEFAULT_BELT_HALF_WIDTH = 20f;
    private static final float BELT_DEFAULT_FREQUENCY = .04f;
    private static final Logger logger = LoggerFactory.getLogger(BeltGenerator.class);

    /** This delineates the distance that the center of the belt's width will be from the center of the SolarSystem. */
    private float distanceFromCenterOfSolarSystem;
    private SystemBelt systemBelt;
    private BeltConfigManager beltConfigManager;
    private BeltConfig beltConfig;
    private boolean isInFirstSolarSystem;
    private float asteroidFrequency = BELT_DEFAULT_FREQUENCY;

    /**
     * This method modifies how often asteroids will spawn within the belt.
     * @param frequencyMultiplier factor by which to multiply asteroid frequency. Must be at least 0
     */
    protected void modifyBeltAsteroidFrequency(float frequencyMultiplier) {
        if (frequencyMultiplier >= 0) {
            asteroidFrequency *= frequencyMultiplier;
        } else {
            logger.error("Asteroid frequency cannot be negative.");
        }

    }

    /**
     * This method modifies how often inner belt enemies will spawn.
     * @param frequencyMultiplier factor by which to multiply enemy frequency. Must be at least 0
     */
    protected void modifyInnerEnemiesFrequency(float frequencyMultiplier) {
        if (frequencyMultiplier >= 0) {
            for (ShipConfig shipConfig : beltConfig.innerTempEnemies) {
                shipConfig.density *= frequencyMultiplier;
            }
        } else {
            logger.error("Ship frequency cannot be negative");
        }
    }

    /**
     * This method modifies how often belt enemies will spawn.
     * @param frequencyMultiplier factor by which to multiply enemy frequency. Must be at least 0
     */
    protected void modifyEnemiesFrequency(float frequencyMultiplier) {
        if (frequencyMultiplier >= 0) {

            for (ShipConfig shipConfig : beltConfig.tempEnemies) {
                shipConfig.density *= frequencyMultiplier;
            }
        } else {
            logger.error("Ship frequency cannot be negative");
        }
    }

    /**
     * This method creates the SystemBelt object that is to be used by the game during play. This method should be called
     * at the end of the build() method in any BeltGenerator implementation.
     */
    protected void instantiateSystemBelt() {
        systemBelt = new SystemBelt(getRadius(), getDistanceFromCenterOfSolarSystem(), getPosition(), getBeltConfig(), asteroidFrequency);
    }


    public float getDistanceFromCenterOfSolarSystem() {
        return distanceFromCenterOfSolarSystem;
    }

    public void setDistanceFromCenterOfSolarSystem(float distance) {
        distanceFromCenterOfSolarSystem = distance;
    }

    public float getAsteroidFrequency() {
        return asteroidFrequency;
    }

    public void setAsteroidFrequency(float asteroidFrequency) {
        this.asteroidFrequency = asteroidFrequency;
    }

    public void setBeltConfigManager(BeltConfigManager beltConfigManager) {
        this.beltConfigManager = beltConfigManager;
    }

    public BeltConfigManager getBeltConfigManager() {
        return beltConfigManager;
    }

    public void setBeltConfig(BeltConfig beltConfig) {
        this.beltConfig = beltConfig;
    }

    public BeltConfig getBeltConfig() {
        return beltConfig;
    }

    public SystemBelt getSystemBelt() {
        return systemBelt;
    }

    public void setInFirstSolarSystem(boolean inFirstSolarSystem) {
        isInFirstSolarSystem = inFirstSolarSystem;
    }

    public boolean getIsInFirstSolarSystem() {
        return isInFirstSolarSystem;
    }
}

