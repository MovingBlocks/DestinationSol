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
    PlanetConfigs planetConfigManager;
    PlanetConfig planetConfig;
    float groundHeight;
    //distance from ground of planet to atmosphere
    float atmosphereHeight;
    float angleInSolarSystem;
    float distanceFromSolarSystemCenter;

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

    void setRadius() {
        setRadius(getGroundHeight() + getAtmosphereHeight());
    }

    public void setAngleInSolarSystem(float angleInSolarSystem) {
        this.angleInSolarSystem = angleInSolarSystem;
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
}
