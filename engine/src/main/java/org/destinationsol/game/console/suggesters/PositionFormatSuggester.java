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

import org.destinationsol.game.console.CommandParameterSuggester;
import org.destinationsol.game.console.commands.PositionCommandHandler;

import java.util.HashSet;
import java.util.Set;

public class PositionFormatSuggester implements CommandParameterSuggester<PositionCommandHandler.PositionFormat> {
    @Override
    public Set<PositionCommandHandler.PositionFormat> suggest(Object... resolvedParameters) {
        Set<PositionCommandHandler.PositionFormat> suggestions = new HashSet<>();
        suggestions.add(PositionCommandHandler.PositionFormat.TERSE);
        suggestions.add(PositionCommandHandler.PositionFormat.VERBOSE);
        suggestions.add(PositionCommandHandler.PositionFormat.INTERNAL);
        suggestions.add(PositionCommandHandler.PositionFormat.BOLD);
        return suggestions;
    }
}
