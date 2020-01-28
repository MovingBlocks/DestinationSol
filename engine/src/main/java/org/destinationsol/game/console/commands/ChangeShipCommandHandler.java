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
package org.destinationsol.game.console.commands;

import org.destinationsol.game.Hero;
import org.destinationsol.game.SolGame;
import org.destinationsol.game.console.annotations.Command;
import org.destinationsol.game.console.annotations.CommandParam;
import org.destinationsol.game.console.annotations.Game;
import org.destinationsol.game.console.annotations.RegisterCommands;
import org.destinationsol.game.console.exceptions.CommandExecutionException;
import org.destinationsol.game.console.suggesters.HullConfigSuggester;
import org.destinationsol.game.ship.hulls.HullConfig;

/**
 * A command used to change current ship on the fly.
 * Takes one argument - id of the ship you want to load
 */
@RegisterCommands
public class ChangeShipCommandHandler {

    @Command(shortDescription = "changes hero ship")
    public String changeShip(@Game SolGame game, @CommandParam(value = "newShip", suggester = HullConfigSuggester.class) HullConfig newHullConfig) throws CommandExecutionException {

        if (newHullConfig == null) {
            throw new CommandExecutionException("Could not find such ship");
        }

        Hero hero = game.getHero();

        return hero.changeShip(hero, newHullConfig, game);
    }

}
