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
package org.destinationsol.game;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import org.destinationsol.assets.Assets;
import org.destinationsol.assets.sound.OggSound;
import org.destinationsol.assets.sound.OggSoundManager;
import org.destinationsol.assets.sound.PlayableSound;
import org.destinationsol.game.particle.EffectConfig;
import org.destinationsol.game.particle.EffectTypes;
import org.json.JSONObject;

public class AbilityCommonConfig {
    public final TextureAtlas.AtlasRegion icon;
    public final EffectConfig effect;
    public final PlayableSound activatedSound;

    public AbilityCommonConfig(TextureAtlas.AtlasRegion icon, EffectConfig effect, PlayableSound activatedSound) {
        this.icon = icon;
        this.effect = effect;
        this.activatedSound = activatedSound;
    }

    public static AbilityCommonConfig load(JSONObject node, EffectTypes types, GameColors cols, OggSoundManager soundManager) {
        EffectConfig ec = EffectConfig.load(node.has("effect") ? node.getJSONObject("effect") : null, types, cols);
        OggSound activatedSound = soundManager.getSound(node.getString("activatedSound"));
        TextureAtlas.AtlasRegion icon = node.has("icon") ? Assets.getAtlasRegion(node.getString("icon")) : null;
        return new AbilityCommonConfig(icon, ec, activatedSound);
    }
}
