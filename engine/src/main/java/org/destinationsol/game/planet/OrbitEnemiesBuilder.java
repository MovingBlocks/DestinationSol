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
package org.destinationsol.game.planet;

import org.destinationsol.game.ShipConfig;

import java.util.ArrayList;
import java.util.List;

/**
 * This class keeps track of the variables which are taken into account when placing various types of ships within the
 * orbit of a Planet. These values are used by {@link PlanetObjectsBuilder} to create the actual ships that are on
 * a Planet. This class keeps track specifically of variables which affect the entire category of Orbit Enemies. Other
 * variables, such ship density (how many of the ships are places), are dependent on the specific ShipConfig,
 * and so are held within the list of ShipConfigs.
 */
public class OrbitEnemiesBuilder {
    private ArrayList<ShipConfig> orbitEnemies = new ArrayList<>();
    //Offset Percentage represents how far apart enemies should be made, in terms of circumference of the planet
    float offsetPercentage;
    float atmospherePercentage;
    float detectionDist;

    public OrbitEnemiesBuilder(List<ShipConfig> orbitEnemies, float offsetPercentage, float atmospherePercentage, float detectionDistance) {
        this.orbitEnemies.addAll(orbitEnemies);
        this.offsetPercentage = offsetPercentage;
        this.atmospherePercentage = atmospherePercentage;
        this.detectionDist = detectionDistance;
    }

    public ArrayList<ShipConfig> getOrbitEnemies() {
        return orbitEnemies;
    }

    public float getOffsetPercentage() {
        return offsetPercentage;
    }

    public void setOffsetPercentage(float offsetPercentage) {
        this.offsetPercentage = offsetPercentage;
    }

    public float getAtmospherePercentage() {
        return atmospherePercentage;
    }

    public void setAtmospherePercentage(float atmospherePercentage) {
        this.atmospherePercentage = atmospherePercentage;
    }

    public float getDetectionDist() {
        return detectionDist;
    }

    public void setDetectionDist(float detectionDist) {
        this.detectionDist = detectionDist;
    }
}
