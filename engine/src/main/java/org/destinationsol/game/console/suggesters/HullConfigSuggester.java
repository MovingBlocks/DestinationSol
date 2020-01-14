/*
 * Copyright 2019 MovingBlocks
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
package org.destinationsol.game.console.suggesters;

import org.destinationsol.assets.Assets;
import org.destinationsol.assets.json.Json;
import org.destinationsol.common.SolException;
import org.destinationsol.files.HullConfigManager;
import org.destinationsol.game.SolGame;
import org.destinationsol.game.console.CommandParameterSuggester;
import org.destinationsol.game.ship.hulls.HullConfig;
import org.terasology.gestalt.assets.ResourceUrn;

import java.util.HashSet;
import java.util.Set;

public class HullConfigSuggester implements CommandParameterSuggester<HullConfig> {
    @Override
    public Set<HullConfig> suggest(SolGame game, Object... resolvedParameters) {
        Set<HullConfig> suggestions = new HashSet<>();
        HullConfigManager hullConfigManager = game.getHullConfigManager();
        for (ResourceUrn urn : Assets.getAssetHelper().list(Json.class)) {
            HullConfig config;
            try {
                config = hullConfigManager.getConfig(urn.getModuleName() + ":" + urn.getResourceName());
            } catch (SolException e) {
                // ignore because it throws an error when urn is not a shipConfig, thats fine since we want only shipConfigs
                continue;
            }
            if (config.getType() != HullConfig.Type.STATION) {
                suggestions.add(config);
            }
        }
        return suggestions;
    }
}
