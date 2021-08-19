/*
 * Copyright 2018 MovingBlocks
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

import com.badlogic.gdx.math.Vector2;
import org.destinationsol.game.HardnessCalc;

public class SystemBelt {
    private final Float halfWidth;
    private final float radius;
    private final Vector2 solarSystemPosition = new Vector2();
    private final BeltConfig config;
    private final float damagePerSecond;
    private final float asteroidFrequency;

    public SystemBelt(Float halfWidth, float radius, Vector2 solarSystemPosition, BeltConfig config, float asteroidFrequency) {
        this.halfWidth = halfWidth;
        this.radius = radius;
        this.solarSystemPosition.set(solarSystemPosition);
        this.config = config;
        this.asteroidFrequency = asteroidFrequency;
        damagePerSecond = HardnessCalc.getBeltDps(config);
    }

    public float getRadius() {
        return radius;
    }

    public Float getHalfWidth() {
        return halfWidth;
    }

    public boolean contains(Vector2 position) {
        float toCenter = solarSystemPosition.dst(position);
        return radius - halfWidth < toCenter && toCenter < radius + halfWidth;
    }

    public BeltConfig getConfig() {
        return config;
    }

    public float getDps() {
        return damagePerSecond;
    }

    public float getAsteroidFrequency() {
        return asteroidFrequency;
    }
}
