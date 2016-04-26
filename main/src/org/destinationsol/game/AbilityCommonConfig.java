/*
 * Copyright 2015 MovingBlocks
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

package org.destinationsol.game;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.JsonValue;
import org.destinationsol.TextureManager;
import org.destinationsol.assets.audio.OggSound;
import org.destinationsol.assets.audio.PlayableSound;
import org.destinationsol.game.particle.EffectConfig;
import org.destinationsol.game.particle.EffectTypes;
import org.destinationsol.game.sound.OggSoundManager;

public class AbilityCommonConfig {
    public final EffectConfig effect;
    public final PlayableSound activatedSound;

    public AbilityCommonConfig(EffectConfig effect, PlayableSound activatedSound) {
        this.effect = effect;
        this.activatedSound = activatedSound;
    }

    public static AbilityCommonConfig load(JsonValue node, EffectTypes types, TextureManager textureManager, GameColors cols,
                                           FileHandle configFile, OggSoundManager soundManager) {
        EffectConfig ec = EffectConfig.load(node.get("effect"), types, textureManager, configFile, cols);
        OggSound activatedSound = soundManager.getSound(node.getString("activatedSound"));
        return new AbilityCommonConfig(ec, activatedSound);
    }
}
