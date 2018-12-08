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
package org.destinationsol.game.asteroid;

import org.destinationsol.assets.Assets;
import org.json.JSONObject;

public class AsteroidConfig {
    public final float minSplitSize;
    public final float minBurnSize;
    public final float sizeToLife;
    public final float speedToAtmDamage;
    public final float maxSplitSpeed;
    public final float dur;

    public AsteroidConfig(float minSplitSize,float minBurnSize,float sizeToLife,float maxSplitSpeed,float dur) {
        this.minSplitSize = minSplitSize;
        this.minBurnSize = minBurnSize;
        this.sizeToLife = sizeToLife;
        this.speedToAtmDamage = sizeToLife * .11f;
        this.maxSplitSpeed = maxSplitSpeed;
        this.dur = dur;
    }

    static AsteroidConfig load(String moduleName) {
        JSONObject asteroidConfigs;
        try{
            asteroidConfigs = Assets.getJson(moduleName + ":asteroidsConfig").getJsonValue();
        }catch (RuntimeException e){
            asteroidConfigs = Assets.getJson("engine:asteroidsConfig").getJsonValue();
        }
        float minSplitSz = asteroidConfigs.getFloat("MIN_SPLIT_SZ");
        float minBurnSz = asteroidConfigs.getFloat("MIN_BURN_SZ");
        float szToLife = asteroidConfigs.getFloat("SZ_TO_LIFE");
        float maxSplitSpd = asteroidConfigs.getFloat("MAX_SPLIT_SPD");
        float dur = asteroidConfigs.getFloat("DUR");
        return new AsteroidConfig(minSplitSz, minBurnSz, szToLife, maxSplitSpd, dur);
    }
}
