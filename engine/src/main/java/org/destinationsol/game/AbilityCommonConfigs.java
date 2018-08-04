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

import com.badlogic.gdx.utils.JsonValue;
import org.destinationsol.assets.Assets;
import org.destinationsol.assets.audio.OggSoundManager;
import org.destinationsol.assets.json.Json;
import org.destinationsol.game.particle.EffectTypes;

public class AbilityCommonConfigs {
    public final AbilityCommonConfig teleport;
    public final AbilityCommonConfig emWave;
    public final AbilityCommonConfig unShield;
    public final AbilityCommonConfig knockBack;
    public final AbilityCommonConfig sloMo;

    public AbilityCommonConfigs(EffectTypes effectTypes, GameColors cols, OggSoundManager soundManager) {
        Json json = Assets.getJson("core:abilitiesConfig");
        JsonValue rootNode = json.getJsonValue();

        teleport = AbilityCommonConfig.load(rootNode.get("teleport"), effectTypes, cols, soundManager);
        emWave = AbilityCommonConfig.load(rootNode.get("emWave"), effectTypes, cols, soundManager);
        unShield = AbilityCommonConfig.load(rootNode.get("unShield"), effectTypes, cols, soundManager);
        knockBack = AbilityCommonConfig.load(rootNode.get("knockBack"), effectTypes, cols, soundManager);
        sloMo = AbilityCommonConfig.load(rootNode.get("sloMo"), effectTypes, cols, soundManager);

        json.dispose();
    }
}
