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

import com.badlogic.gdx.math.Vector2;
import org.destinationsol.common.SolMath;
import org.destinationsol.common.SolRandom;
import org.destinationsol.game.ShipConfig;
import org.destinationsol.game.SolNames;
import org.destinationsol.game.planet.DecoConfig;
import org.destinationsol.game.planet.Planet;
import org.destinationsol.game.planet.PlanetConfig;
import org.destinationsol.game.planet.PlanetConfigs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * This class defines the general behavior for Planet generators (such as ground, sky, gravity, etc). Any Planet will be
 * created from a concrete implementation of this class, with behavior specific to that Planet defined there.
 */
public abstract class PlanetGenerator extends FeatureGenerator {
    public static final float PLANET_MAX_DIAMETER = 78f;
    public static final float PLANET_SPEED = .2f;
    public static final float PLANET_GROUND_SPEED = .2f;
    protected static final float DEFAULT_MAX_GROUND_HEIGHT = 25f;
    protected static final float DEFAULT_ATMOSPHERE_HEIGHT = 14f;
    private static final Logger logger = LoggerFactory.getLogger(PlanetGenerator.class);
    protected SolNames solNames = new SolNames();
    private Planet planet;
    private Vector2 solarSystemPosition = new Vector2();
    private String name;
    private boolean isInFirstSolarSystem;
    private boolean isInnerPlanet;
    private PlanetConfig planetConfig;
    private float groundHeight;
    //distance from ground of planet to atmosphere
    private float atmosphereHeight;
    private float angleInSolarSystem;
    private float distanceFromSolarSystemCenter;
    private float initialPlanetAngle;
    //This is the speed the Planet orbits around the SolarSystem at
    private float orbitSolarSystemSpeed;
    //This is the speed the Planet spins around it's axis at
    private float planetRotationSpeed;

    private PlanetConfigs planetConfigManager;

    /**
     * This method sets the Planet variable for the PlanetGenerator. It should be called at the end of the build() method,
     * after the settings of the planet have been set. This Planet be used after the world-gen phase in the runtime code.
     */
    protected void instantiatePlanet() {
        planet = new Planet(getSolarSystemPosition(), getAngleInSolarSystem(), getDistanceFromSolarSystemCenter(),
                getInitialPlanetAngle(), orbitSolarSystemSpeed, planetRotationSpeed, getGroundHeight(),
                false, getPlanetConfig(), name);
    }

    /**
     * This returns a PlanetConfig using logic to determine Planet difficulty.
     * @return the PlanetConfig with the correct difficulty
     */
    protected PlanetConfig getPlanetConfigDefaultSettings() {
        return getPlanetConfigManager().getRandom(!getIsInnerPlanet() && getIsInFirstSolarSystem(),
                getIsInnerPlanet() && !getIsInFirstSolarSystem());
    }

    /**
     * This calculates the proper speed for a planet to orbit around the center of the SolarSystem. It is randomized
     * slightly, so not all planets orbit at the same speed.
     * @return float representing orbit speed
     */
    protected float calculateDefaultPlanetOrbitSpeed() {
        return SolMath.arcToAngle(PLANET_SPEED, getDistanceFromSolarSystemCenter()) * SolMath.toInt(SolRandom.seededTest(.5f));
    }

    /**
     * This calculates the proper speed for a planet to rotate around its axis. It is randomized slightly, so not all
     * planets rotate at the same speed.
     * @return float representing rotation speed
     */
    protected float calculateDefaultPlanetRotationSpeed() {
        return SolMath.arcToAngle(PLANET_GROUND_SPEED, getGroundHeight()) * SolMath.toInt(SolRandom.seededTest(.5f));
    }

    /**
     * This method modifies the density of low orbit ships that generate on the Planet. The density represents how
     * frequently the ships spawn
     * @param densityMultiplier amount by which the current density should be multiplied. Must be at least 0
     */
    protected void modifyLowOrbitShipsDensity(float densityMultiplier) {
        if (densityMultiplier >= 0) {
            for (ShipConfig shipConfig : getPlanetConfig().lowOrbitEnemies) {
                shipConfig.density *= densityMultiplier;
            }
        } else {
            logger.error("Ship density multiplier must be at least 0");
        }
    }

    /**
     * This method modifies the density of high orbit ships that generate on the Planet. The density represents how
     * frequently the ships spawn
     * @param densityMultiplier amount by which the current density should be multiplied. Must be at least 0
     */
    protected void modifyHighOrbitShipsDensity(float densityMultiplier) {
        if (densityMultiplier >= 0) {
            for (ShipConfig shipConfig : getPlanetConfig().highOrbitEnemies) {
                shipConfig.density *= densityMultiplier;
            }
        } else {
            logger.error("Ship density multiplier must be at least 0");
        }
    }

    /**
     * This method modifies the density of ground enemies (turrets) that generate on the Planet. The density represents how
     * frequently the turrets spawn
     * @param densityMultiplier amount by which the current density should be multiplied. Must be at least 0
     */
    protected void modifyGroundEnemiesDensity(float densityMultiplier) {
        if (densityMultiplier >= 0) {
            for (ShipConfig shipConfig : getPlanetConfig().groundEnemies) {
                shipConfig.density *= densityMultiplier;
            }
        } else {
            logger.error("Ship density multiplier must be at least 0");
        }
    }

    /**
     * This method modifies the percentage of the atmosphere at which low orbit ships start spawning.
     * @param atmospherePercentageMultiplier amount by which the current atmosphere percentage should be multiplied
     */
    protected void modifyLowOrbitShipsAtmospherePercentage(float atmospherePercentageMultiplier) {
        getPlanetConfig().lowOrbitalEnemiesBuilder.setAtmospherePercentage(
                getPlanetConfig().lowOrbitalEnemiesBuilder.getAtmospherePercentage() * atmospherePercentageMultiplier);
    }

    /**
     * This method modifies the percentage of the atmosphere at which high orbit ships start spawning.
     * @param atmospherePercentageMultiplier amount by which the current atmosphere percentage should be multiplied
     */
    protected void modifyHighOrbitShipsAtmospherePercentage(float atmospherePercentageMultiplier) {
        getPlanetConfig().highOrbitalEnemiesBuilder.setAtmospherePercentage(
                getPlanetConfig().highOrbitalEnemiesBuilder.getAtmospherePercentage() * atmospherePercentageMultiplier);
    }

    /**
     * This method modifies the distance from which low orbit ships will detect a player
     * @param detectionDistanceMultiplier amount by which the current detection distance should be multiplied
     */
    protected void modifyLowOrbitShipsDetectionDistance(float detectionDistanceMultiplier) {
        getPlanetConfig().lowOrbitalEnemiesBuilder.setDetectionDistance(
                getPlanetConfig().lowOrbitalEnemiesBuilder.getDetectionDistance() * detectionDistanceMultiplier);
    }

    /**
     * This method modifies the distance from which high orbit ships will detect a player
     * @param detectionDistanceMultiplier amount by which the current detection distance should be multiplied
     */
    protected void modifyHighOrbitShipsDetectionDistance(float detectionDistanceMultiplier) {
        getPlanetConfig().highOrbitalEnemiesBuilder.setDetectionDistance(
                getPlanetConfig().highOrbitalEnemiesBuilder.getDetectionDistance() * detectionDistanceMultiplier);
    }

    /**
     * This method modifies the density of clouds that generate above the Planet. The density represents how
     * frequently the clouds spawn
     * @param cloudDensityMultiplier amount by which the current density should be multiplied. Must be at least 0
     */
    protected void modifyCloudDensity(float cloudDensityMultiplier) {
        if (cloudDensityMultiplier >= 0) {
            getPlanetConfig().cloudBuilder.setCloudDensity(
                    getPlanetConfig().cloudBuilder.getCloudDensity() * cloudDensityMultiplier);
        } else {
            logger.error("Cloud density multiplier must be at least 0");
        }
    }

    /**
     * This method modifies the percentage of the atmosphere at which clouds start spawning.
     * @param startingAtmospherePercentage the height at which clouds should start spawning
     */
    protected void setCloudsStartingAtmospherePercentage(float startingAtmospherePercentage) {
        if (startingAtmospherePercentage >= 0 && startingAtmospherePercentage < 1) {
            getPlanetConfig().cloudBuilder.setAtmosphereStartingPercentage(startingAtmospherePercentage);
        } else {
            logger.error("Starting atmosphere percentage for clouds must be at least 0 and less than 1");
        }
    }

    /**
     * This method modifies the percentage of the atmosphere at which clouds stop spawning.
     * @param endingAtmospherePercentage the height at which clouds should stop spawning
     */
    protected void setCloudsEndingAtmospherePercentage(float endingAtmospherePercentage) {
        if (endingAtmospherePercentage >= 0 && endingAtmospherePercentage < 1) {
            getPlanetConfig().cloudBuilder.setAtmosphereEndingPercentage(endingAtmospherePercentage);
        } else {
            logger.error("Ending atmosphere percentage for clouds must be at least 0 and less than 1");
        }
    }

    /**
     * This method sets the smallest width that a cloud should spawn with, in terms of percentage of the
     * default width.
     * @param startingWidthPercentage the lowest percentage width a cloud will spawn with
     */
    protected void setCloudsWidthStartingPercentage(float startingWidthPercentage) {
        if (startingWidthPercentage >= 0) {
            getPlanetConfig().cloudBuilder.setCloudWidthStartingPercentage(startingWidthPercentage);
        } else {
            logger.error("Starting width percentage for clouds must be at least 0");
        }
    }

    /**
     * This method sets the largest width that a cloud should spawn with, in terms of percentage of the
     * default width.
     * @param endingWidthPercentage the largest percentage width a cloud will spawn with
     */
    protected void setCloudsWidthEndingPercentage(float endingWidthPercentage) {
        if (endingWidthPercentage >= 0) {
            getPlanetConfig().cloudBuilder.setCloudWidthEndingPercentage(endingWidthPercentage);
        } else {
            logger.error("Ending width percentage for clouds must be at least 0");
        }
    }

    /**
     * This allows access to the decoration config files of the Planet. Modifying these allows for changing the look
     * of decorations without making an entire new Planet config
     * @return the Planet's decoration config list
     */
    protected List<DecoConfig> getDecorationConfigs() {
        return planetConfig.deco;
    }

    public void setAtmosphereHeight(float atmosphereHeight) {
        this.atmosphereHeight = atmosphereHeight;
    }

    public float getAtmosphereHeight() {
        return atmosphereHeight;
    }

    public void setGroundHeight(float groundHeight) {
        this.groundHeight = groundHeight;
    }

    public float getGroundHeight() {
        return groundHeight;
    }

    void calculateRadius() {
        setRadius(getGroundHeight() + getAtmosphereHeight());
    }

    public void setAngleInSolarSystem(float angleInSolarSystem) {
        this.angleInSolarSystem = angleInSolarSystem;
    }

    public float getAngleInSolarSystem() {
        return angleInSolarSystem;
    }

    public void setDistanceFromSolarSystemCenter(float distanceFromSolarSystemCenter) {
        this.distanceFromSolarSystemCenter = distanceFromSolarSystemCenter;
    }

    public float getDistanceFromSolarSystemCenter() {
        return distanceFromSolarSystemCenter;
    }

    public void setPlanetConfig(PlanetConfig planetConfig) {
        this.planetConfig = planetConfig;
    }

    public PlanetConfig getPlanetConfig() {
        return planetConfig;
    }

    public PlanetConfigs getPlanetConfigManager() {
        return planetConfigManager;
    }

    public void setPlanetConfigManager(PlanetConfigs planetConfigManager) {
        this.planetConfigManager = planetConfigManager;
    }

    public void setIsInFirstSolarSystem(boolean isInfirstSolarSystem) {
        this.isInFirstSolarSystem = isInfirstSolarSystem;
    }

    public boolean getIsInFirstSolarSystem() {
        return isInFirstSolarSystem;
    }

    public void setIsInnerPlanet(boolean isInnerPlanet) {
        this.isInnerPlanet = isInnerPlanet;
    }

    public boolean getIsInnerPlanet() {
        return isInnerPlanet;
    }

    public void setInitialPlanetAngle(float initialPlanetAngle) {
        this.initialPlanetAngle = initialPlanetAngle;
    }

    public float getInitialPlanetAngle() {
        return initialPlanetAngle;
    }

    public Vector2 getSolarSystemPosition() {
        return solarSystemPosition;
    }

    public void setSolarSystemPosition(Vector2 solarSystemPosition) {
        this.solarSystemPosition.set(solarSystemPosition);
    }

    /**
     * Sets the speed the planet will rotate around its axis.
     * @param planetRotationSpeed rotation speed
     */
    public void setPlanetRotationSpeed(float planetRotationSpeed) {
        this.planetRotationSpeed = planetRotationSpeed;
    }

    public float getPlanetRotationSpeed() {
        return planetRotationSpeed;
    }

    /**
     * Sets the speed the planet will orbit around the center of the SolarSystem.
     * @param orbitSolarSystemSpeed orbit speed
     */
    public void setOrbitSolarSystemSpeed(float orbitSolarSystemSpeed) {
        this.orbitSolarSystemSpeed = orbitSolarSystemSpeed;
    }

    public float getOrbitSolarSystemSpeed() {
        return orbitSolarSystemSpeed;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Planet getPlanet() {
        return planet;
    }
}
