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

import org.destinationsol.assets.json.Validator;
import org.json.JSONObject;
import org.destinationsol.assets.Assets;
import org.destinationsol.assets.audio.OggSoundManager;
import org.destinationsol.assets.json.Json;
import org.destinationsol.game.particle.EffectTypes;
import org.terasology.assets.ResourceUrn;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class AbilityCommonConfigs {
    public final Map<String, AbilityCommonConfig> abilityConfigs;

    public AbilityCommonConfigs(EffectTypes effectTypes, GameColors cols, OggSoundManager soundManager) {
        abilityConfigs = new HashMap<>();

        for (ResourceUrn resource : Assets.getAssetHelper().list(Json.class, "[a-zA-Z0-9]*:abilitiesConfig")) {
            JSONObject rootNode = Validator.getValidatedJSON(resource.toString(), "engine:schemaAbilitiesConfig");

            for (String abilityName : rootNode.keySet()) {
                String normalisedName = abilityName.toLowerCase(Locale.ENGLISH);
                if (!abilityConfigs.containsKey(normalisedName)) {
                    abilityConfigs.put(normalisedName, AbilityCommonConfig.load(rootNode.getJSONObject(abilityName), effectTypes, cols, soundManager));
                }
            }
        }
    }
}
