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
    public final float MIN_SPLIT_SZ;
    public final float MIN_BURN_SZ;
    public final float SZ_TO_LIFE;
    public final float SPD_TO_ATM_DMG;
    public final float MAX_SPLIT_SPD;
    public final float DUR;

    public AsteroidConfig(float min_split_sz,float min_burn_sz,float sz_to_life,float max_split_spd,float dur) {
        this.MIN_SPLIT_SZ = min_split_sz;
        this.MIN_BURN_SZ = min_burn_sz;
        this.SZ_TO_LIFE = sz_to_life;
        this.SPD_TO_ATM_DMG = sz_to_life * .11f;
        this.MAX_SPLIT_SPD = max_split_spd;
        this.DUR = dur;
    }

    static AsteroidConfig load(String moduleName){
        JSONObject asteroidConfigs;
        try{
            asteroidConfigs = Assets.getJson(moduleName + ":asteroidsConfig").getJsonValue();
        }catch (RuntimeException e){
            asteroidConfigs = Assets.getJson("engine:asteroidsConfig").getJsonValue();
        }
        float MIN_SPLIT_SZ = asteroidConfigs.getFloat("MIN_SPLIT_SZ");
        float MIN_BURN_SZ = asteroidConfigs.getFloat("MIN_BURN_SZ");
        float SZ_TO_LIFE = asteroidConfigs.getFloat("SZ_TO_LIFE");
        float MAX_SPLIT_SPD = asteroidConfigs.getFloat("MAX_SPLIT_SPD");
        float DUR = asteroidConfigs.getFloat("DUR");
        return new AsteroidConfig(MIN_SPLIT_SZ, MIN_BURN_SZ, SZ_TO_LIFE, MAX_SPLIT_SPD, DUR);
    }
}
