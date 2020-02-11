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
package org.destinationsol.game.console;

import org.destinationsol.game.SolGame;
import org.destinationsol.game.ship.SolShip;

import java.util.Set;

/**
 * A class used for suggesting command parameter values
 */
@FunctionalInterface
public interface CommandParameterSuggester<T> {
    /**
     * @param resolvedParameters Currently entered values of the types declared in the command method
     * @return A collection of suggested matches.
     */
    Set<T> suggest(SolGame game, Object... resolvedParameters);
}
