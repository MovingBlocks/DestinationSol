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

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.JsonValue;
import org.destinationsol.assets.Assets;
import org.destinationsol.common.SolMath;

import java.util.ArrayList;
import java.util.List;

public class DecoConfig {
    public final float density;
    public final float szMin;
    public final float szMax;
    public final Vector2 orig;
    public final boolean allowFlip;
    public final List<TextureAtlas.AtlasRegion> texs;

    public DecoConfig(float density, float szMin, float szMax, Vector2 orig, boolean allowFlip, List<TextureAtlas.AtlasRegion> texs) {
        this.density = density;
        this.szMin = szMin;
        this.szMax = szMax;
        this.orig = orig;
        this.allowFlip = allowFlip;
        this.texs = texs;
    }

    static List<DecoConfig> load(JsonValue planetConfig) {
        ArrayList<DecoConfig> res = new ArrayList<>();
        for (JsonValue deco : planetConfig.get("decorations")) {
            float density = deco.getFloat("density");
            float szMin = deco.getFloat("szMin");
            float szMax = deco.getFloat("szMax");
            Vector2 orig = SolMath.readV2(deco, "orig");
            boolean allowFlip = deco.getBoolean("allowFlip");
            String texName = deco.name;
            List<TextureAtlas.AtlasRegion> texs = Assets.listTexturesMatching(texName + "_.*");
            DecoConfig c = new DecoConfig(density, szMin, szMax, orig, allowFlip, texs);
            res.add(c);
        }
        return res;
    }
}
