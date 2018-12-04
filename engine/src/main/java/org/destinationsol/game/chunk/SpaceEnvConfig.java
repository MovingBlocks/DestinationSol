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

package org.destinationsol.game.chunk;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import org.json.JSONObject;
import org.destinationsol.assets.Assets;

import java.util.List;

public class SpaceEnvConfig {
    public final List<TextureAtlas.AtlasRegion> junkTextures;
    public final float junkDensity;
    public final List<TextureAtlas.AtlasRegion> farJunkTextures;
    public final float farJunkDensity;

    public SpaceEnvConfig(JSONObject json) {
        String junkTexDirStr = json.getString("junkTexs");
        junkTextures = Assets.listTexturesMatching(junkTexDirStr + "_.*");
        junkDensity = (float) json.getDouble("junkDensity");
        String farJunkTexDirStr = json.getString("farJunkTexs");
        farJunkTextures = Assets.listTexturesMatching(farJunkTexDirStr + "_.*");
        farJunkDensity = (float) json.getDouble("farJunkDensity");
    }
}
