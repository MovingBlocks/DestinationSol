/*
 * Copyright 2017 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.destinationsol.game.particle;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.JsonValue;
import org.destinationsol.assets.Assets;
import org.destinationsol.game.GameColors;

import java.util.ArrayList;
import java.util.List;

public class EffectConfig {
    public final EffectType effectType;
    public final float sz;
    public final TextureAtlas.AtlasRegion tex;
    public final boolean floatsUp;
    public final Color tint;

    public EffectConfig(EffectType effectType, float sz, TextureAtlas.AtlasRegion tex, boolean floatsUp, Color tint) {
        this.effectType = effectType;
        this.sz = sz;
        this.tex = tex;
        this.floatsUp = floatsUp;
        this.tint = tint;
    }

    public static EffectConfig load(JsonValue node, EffectTypes types, GameColors cols) {
        if (node == null) {
            return null;
        }
        String effectFileName = node.getString("effectFile");
        EffectType effectType = types.forName(effectFileName);
        float sz = node.getFloat("size", 0);
        String texName = node.getString("tex");
        boolean floatsUp = node.getBoolean("floatsUp", false);
        Color tint = cols.load(node.getString("tint"));
        TextureAtlas.AtlasRegion tex = Assets.getAtlasRegion(texName + "Particle");
        return new EffectConfig(effectType, sz, tex, floatsUp, tint);
    }

    public static List<EffectConfig> loadList(JsonValue listNode, EffectTypes types, GameColors cols) {
        ArrayList<EffectConfig> res = new ArrayList<>();
        for (JsonValue node : listNode) {
            EffectConfig ec = load(node, types, cols);
            res.add(ec);
        }
        return res;
    }

}
