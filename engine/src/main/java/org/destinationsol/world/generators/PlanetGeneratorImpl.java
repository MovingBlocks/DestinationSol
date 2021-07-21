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

import org.destinationsol.common.SolMath;
import org.destinationsol.common.SolRandom;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is a concrete implementation of a PlanetGenerator and handles its creation. This class defines the
 * behavior specific to the default Planets of Destination: Sol.
 * TODO: Implement the default behavior of a PlanetGenerator in this class (As it is implemented in the game currently)
 */
public class PlanetGeneratorImpl extends PlanetGenerator {
    private static final Logger logger = LoggerFactory.getLogger(PlanetGeneratorImpl.class);

    @Override
    public void build() {
        //sets the PlanetConfig for either an easy, medium, or hard planet.
        setPlanetConfig(getPlanetConfigDefaultSettings());

        setGroundHeight(SolRandom.seededRandomFloat(.5f, 1) * DEFAULT_MAX_GROUND_HEIGHT);
        setAtmosphereHeight(DEFAULT_ATMOSPHERE_HEIGHT);
        calculateRadius();

        setOrbitSolarSystemSpeed(SolMath.arcToAngle(PLANET_SPEED, getDistanceFromSolarSystemCenter()) * SolMath.toInt(SolRandom.seededTest(.5f)));
        setPlanetRotationSpeed(SolMath.arcToAngle(PLANET_GROUND_SPEED, getGroundHeight()) * SolMath.toInt(SolRandom.seededTest(.5f)));
        setName(SolRandom.seededRandomElement(solNames.planets.get(getPlanetConfig().moduleName)));
        logger.info("Building a planet now. Planet name: " + getName() + ". Planet position: " + getPosition() + ". Planet Type: " + getPlanetConfig().configName);

        createPlanet();
    }
}
