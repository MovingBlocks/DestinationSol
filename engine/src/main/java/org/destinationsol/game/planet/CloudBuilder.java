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
package org.destinationsol.game.planet;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;

import java.util.ArrayList;
import java.util.List;

/**
 * This class keeps track of the variables which are taken into account when placing clouds (or other floating decorations
 * onto a Planet. These values are used by {@link PlanetObjectsBuilder} to create the actual objects that decorate
 * a Planet.
 */
public class CloudBuilder {
    private final List<TextureAtlas.AtlasRegion> cloudTextures = new ArrayList<>();

    /** Frequency at which clouds will generate */
    private float cloudDensity;

    /** The lowest point (in terms of percentage of the atmosphere) that clouds will generate at */
    private float atmosphereStartingPercentage;

    /** The highest point (in terms of percentage of the atmosphere) that clouds will generate at */
    private float atmosphereEndingPercentage;

    /** The smallest width (in terms of percentage of the default width) that a cloud will generate with */
    private float cloudWidthStartingPercentage;

    /**The largest width (in terms of percentage of the default width) that a cloud will generate with */
    private float cloudWidthEndingPercentage;

    public CloudBuilder(List<TextureAtlas.AtlasRegion> cloudTextures, float cloudDensity,
                        float atmosphereStartingPercentage, float atmosphereEndingPercentage,
                        float cloudWidthStartingPercentage, float cloudWidthEndingPercentage) {
        this.cloudTextures.addAll(cloudTextures);
        this.cloudDensity = cloudDensity;
        this.atmosphereStartingPercentage = atmosphereStartingPercentage;
        this.atmosphereEndingPercentage = atmosphereEndingPercentage;
        this.cloudWidthStartingPercentage = cloudWidthStartingPercentage;
        this.cloudWidthEndingPercentage = cloudWidthEndingPercentage;
    }

    public float getAtmosphereStartingPercentage() {
        return atmosphereStartingPercentage;
    }

    public void setAtmosphereStartingPercentage(float atmosphereStartingPercentage) {
        this.atmosphereStartingPercentage = atmosphereStartingPercentage;
    }

    public float getAtmosphereEndingPercentage() {
        return atmosphereEndingPercentage;
    }

    public void setAtmosphereEndingPercentage(float atmosphereEndingPercentage) {
        this.atmosphereEndingPercentage = atmosphereEndingPercentage;
    }

    public float getCloudWidthStartingPercentage() {
        return cloudWidthStartingPercentage;
    }

    public void setCloudWidthStartingPercentage(float cloudWidthStartingPercentage) {
        this.cloudWidthStartingPercentage = cloudWidthStartingPercentage;
    }

    public float getCloudWidthEndingPercentage() {
        return cloudWidthEndingPercentage;
    }

    public void setCloudWidthEndingPercentage(float cloudWidthEndingPercentage) {
        this.cloudWidthEndingPercentage = cloudWidthEndingPercentage;
    }

    public float getCloudDensity() {
        return cloudDensity;
    }

    public void setCloudDensity(float cloudDensity) {
        this.cloudDensity = cloudDensity;
    }

    public List<TextureAtlas.AtlasRegion> getCloudTextures() {
        return cloudTextures;
    }
}
