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
package org.destinationsol.game.particle;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import org.json.JSONObject;
import org.destinationsol.assets.Assets;
import org.destinationsol.game.GameColors;

import java.util.ArrayList;
import java.util.List;

public class EffectConfig {
    public final EffectType emitter;
    public final float size;
    public final TextureAtlas.AtlasRegion tex;
    public final boolean floatsUp;
    public final Color tint;

    public EffectConfig(EffectType emitter, float size, TextureAtlas.AtlasRegion texture, boolean floatsUp, Color tint) {
        this.emitter = emitter;
        this.size = size;
        this.tex = texture;
        this.floatsUp = floatsUp;
        this.tint = tint;
    }

    public static EffectConfig load(JSONObject node, EffectTypes types, GameColors colours) {
        if (node == null) {
            return null;
        }
        String emitter = node.getString("effectFile");
        EffectType effectType = types.forName(emitter);
        float size = (float) node.optDouble("size", 0);
        String textureName = node.getString("tex");
        boolean floatsUp = node.optBoolean("floatsUp", false);
        Color tint = colours.load(node.getString("tint"));
        TextureAtlas.AtlasRegion tex = Assets.getAtlasRegion(textureName + "Particle");
        return new EffectConfig(effectType, size, tex, floatsUp, tint);
    }

    public static List<EffectConfig> loadList(ArrayList<JSONObject> listNode, EffectTypes types, GameColors colours) {
        ArrayList<EffectConfig> configs = new ArrayList<>();
        for (JSONObject node : listNode) {
            configs.add(load(node, types, colours));
        }
        return configs;
    }
}
