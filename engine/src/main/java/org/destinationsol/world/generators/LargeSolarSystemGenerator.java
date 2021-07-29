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
import org.destinationsol.game.planet.SolarSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LargeSolarSystemGenerator extends SolarSystemGenerator {
    private static final Logger logger = LoggerFactory.getLogger(LargeSolarSystemGenerator.class);

    @Override
    public SolarSystem build() {
        getSolarSystemConfigManager().loadDefaultSolarSystemConfigs();
        setSolarSystemConfig(getSolarSystemConfigManager().getRandomSolarSystemConfig(getSolarSystemNumber() < 1));
        setName(SolRandom.seededRandomElement(getDefaultSolarSystemNames()));
        initializeRandomDefaultFeatureGenerators(.8f);
        calculateFeaturePositions();
        buildFeatureGenerators();


        logger.info(this + " radius: " + getRadius());

        //TODO: remove once there is a visual representation of the SolarSystem in the game
        for (FeatureGenerator generator : activeFeatureGenerators) {
            logger.info(generator + ": " + generator.getPosition());
        }

        return createBuiltSolarSystem();
    }

    @Override
    public SolarSystemSize getSolarSystemSize() {
        return SolarSystemSize.LARGE;
    }

    @Override
    public int getCustomFeaturesCount() {
        return 0;
    }
}
