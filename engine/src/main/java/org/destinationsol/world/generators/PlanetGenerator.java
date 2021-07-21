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
import org.destinationsol.game.SolNames;
import org.destinationsol.game.planet.Planet;
import org.destinationsol.game.planet.PlanetConfig;
import org.destinationsol.game.planet.PlanetConfigs;

/**
 * This class defines the general behavior for Planet generators (such as ground, sky, gravity, etc). Any Planet will be
 * created from a concrete implementation of this class, with behavior specific to that Planet defined there.
 * TODO: Implement behavior common to all Planets as concrete methods in this class
 */
public abstract class PlanetGenerator extends FeatureGenerator {
    public static final float PLANET_MAX_DIAMETER = 78f;
    protected static final float DEFAULT_MAX_GROUND_HEIGHT = 25f;
    protected static final float DEFAULT_ATMOSPHERE_HEIGHT = 14f;
    public static final float PLANET_SPEED = .2f;
    public static final float PLANET_GROUND_SPEED = .2f;
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
     * This method sets the planet variable for the PlanetGenerator. It should be called at the end of the build() method,
     * after the settings of the planet have been set.
     */
    protected void createPlanet() {
        planet = new Planet(getSolarSystemPosition(), getAngleInSolarSystem(), getDistanceFromSolarSystemCenter(),
                getInitialPlanetAngle(), orbitSolarSystemSpeed, planetRotationSpeed, getGroundHeight(),
                false, getPlanetConfig(), name);
    }

    /**
     * The gets a planet config with the difficulty set in the way it is by default. If a planet is not an inner planet
     * (less than half the SolarSystem radius from the center of the system), and not in the starting system, is in
     * the starting system, it will be easy. If the Planet is in the inner part of its system, and it is not the first
     * system, it will be a hard planet. Otherwise, it will be a medium planet.
     * @return the PlanetConfig with the correct difficulty
     */
    protected PlanetConfig getPlanetConfigDefaultSettings() {
        return getPlanetConfigManager().getRandom(!getIsInnerPlanet() && getIsInFirstSolarSystem(),
                getIsInnerPlanet() && !getIsInFirstSolarSystem());
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



    public void setPlanetRotationSpeed(float planetRotationSpeed) {
        this.planetRotationSpeed = planetRotationSpeed;
    }

    public float getPlanetRotationSpeed() {
        return planetRotationSpeed;
    }

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
