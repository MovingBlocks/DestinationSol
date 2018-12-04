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
import org.json.JSONObject;
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

    static List<DecoConfig> load(JSONObject planetConfig) {
        ArrayList<DecoConfig> res = new ArrayList<>();
        JSONObject decorations = planetConfig.getJSONObject("decorations");
        for (String s : decorations.keySet()) {
            if (!(decorations.get(s) instanceof JSONObject))
                continue;
            JSONObject deco = decorations.getJSONObject(s);
            float density = (float) deco.getDouble("density");
            float szMin = (float) deco.getDouble("szMin");
            float szMax = (float) deco.getDouble("szMax");
            Vector2 orig = SolMath.readV2(deco, "orig");
            boolean allowFlip = deco.getBoolean("allowFlip");
            String texName = s;
            List<TextureAtlas.AtlasRegion> texs = Assets.listTexturesMatching(texName + "_.*");
            DecoConfig c = new DecoConfig(density, szMin, szMax, orig, allowFlip, texs);
            res.add(c);
        }
        return res;
    }
}
