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

import org.destinationsol.game.Console;
import org.destinationsol.game.Hero;
import org.destinationsol.game.SolGame;
import org.destinationsol.game.console.ConsoleInputHandler;

/**
 * A command used to change current ship on the fly.
 * Takes one argument - id of the ship you want to load
 */
public class ChangeShipCommandHandler implements ConsoleInputHandler {

    private Hero hero;
    private SolGame game;

    public ChangeShipCommandHandler(Hero hero, SolGame game) {
        this.hero = hero;
        this.game = game;
    }

    @Override
    public void handle(String input, Console console) {
        String[] args = input.split(" ", 2);

        if (args.length != 2) {
            console.warn("Usage: \"changeShip module:shipName\"");
            return;
        }

        hero.changeShip(hero, args[1], game, console);
    }
}
