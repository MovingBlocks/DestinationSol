/*
 * Copyright 2020 The Terasology Foundation
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
import org.destinationsol.game.SolGame;
import org.destinationsol.game.console.CommandParameterSuggester;
import org.terasology.gestalt.assets.ResourceUrn;
import org.terasology.nui.asset.UIElement;

import java.util.HashSet;
import java.util.Set;

public class NUIScreenSuggester implements CommandParameterSuggester<String> {
    @Override
    public Set<String> suggest(SolGame game, Object... resolvedParameters) {
        Set<String> suggestions = new HashSet<>();
        for (ResourceUrn urn : Assets.getAssetHelper().list(UIElement.class)) {
            suggestions.add(urn.toString());
        }

        return suggestions;
    }
}
