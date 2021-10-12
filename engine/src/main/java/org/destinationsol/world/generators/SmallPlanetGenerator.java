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

/**
 * This class is a concrete implementation of a PlanetGenerator and handles preparing a Planet to be built.
 * This class creates planets that are similar to the default Planets of Destination: Sol, but smaller,
 * with different clouds, turrets, and more high orbit enemies.
 */
public class SmallPlanetGenerator extends PlanetGenerator {

    @Override
    public void build() {
        //sets the PlanetConfig for either an easy, medium, or hard planet.
        setPlanetConfig(getPlanetConfigDefaultSettings());

        setGroundHeight(getGroundHeightUsingDefault() * .6f);
        setAtmosphereHeight(DEFAULT_ATMOSPHERE_HEIGHT);
        calculateRadius();

        setOrbitSolarSystemSpeed(calculateDefaultPlanetOrbitSpeed());
        setPlanetRotationSpeed(calculateDefaultPlanetRotationSpeed());
        setName(SolRandom.seededRandomElement(solNames.planets.get(getPlanetConfig().moduleName)));

        modifyCloudDensity(1.4f);
        modifyHighOrbitShipsDensity(1.35f);
        setCloudsStartingAtmospherePercentage(0.25f);
        setCloudsEndingAtmospherePercentage(0.8f);
        setCloudsWidthEndingPercentage(3f);
        modifyGroundEnemiesDensity(1.2f);
        instantiatePlanet();
    }
}
