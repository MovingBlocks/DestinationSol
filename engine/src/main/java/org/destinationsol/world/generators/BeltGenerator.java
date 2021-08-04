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

import org.destinationsol.game.planet.BeltConfig;
import org.destinationsol.game.planet.BeltConfigManager;
import org.destinationsol.game.planet.SystemBelt;

/**
 * This class defines the general behavior for Belt generators (such as asteroid frequency). Any Belt will be
 * created from a concrete implementation of this class, with behavior specific to that Belt defined there.
 * TODO: Implement behavior common to all Belts as concrete methods in this class
 */
public abstract class BeltGenerator extends FeatureGenerator {
    protected static final float DEFAULT_BELT_HALF_WIDTH = 20f;
    private float distanceFromCenterOfSolarSystem;
    private SystemBelt systemBelt;
    private BeltConfigManager beltConfigManager;
    private BeltConfig beltConfig;
    private boolean isInFirstSolarSystem;

    public float getDistanceFromCenterOfSolarSystem() {
        return distanceFromCenterOfSolarSystem;
    }

    public void setDistanceFromCenterOfSolarSystem(float distance) {
        distanceFromCenterOfSolarSystem = distance;
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

    protected void instantiateSystemBelt() {
        systemBelt = new SystemBelt(getRadius(), getDistanceFromCenterOfSolarSystem(), getPosition(), getBeltConfig());
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

