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
 * This class is a concrete implementation of a PlanetGenerator and handles its creation. This class defines the
 * behavior specific to the default Planets of Destination: Sol.
 */
public class PlanetGeneratorImpl extends PlanetGenerator {

    @Override
    public void build() {
        //sets the PlanetConfig for either an easy, medium, or hard planet.
        setPlanetConfig(getPlanetConfigDefaultSettings());

        setGroundHeight(getGroundHeightUsingDefault());
        setAtmosphereHeight(DEFAULT_ATMOSPHERE_HEIGHT);
        calculateRadius();

        setOrbitSolarSystemSpeed(calculateDefaultPlanetOrbitSpeed());
        setPlanetRotationSpeed(calculateDefaultPlanetRotationSpeed());
        setName(SolRandom.seededRandomElement(solNames.planets.get(getPlanetConfig().moduleName)));

        instantiatePlanet();
    }
}
