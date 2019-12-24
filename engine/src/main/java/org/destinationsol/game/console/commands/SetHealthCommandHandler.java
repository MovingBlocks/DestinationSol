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
import org.destinationsol.game.console.ConsoleInputHandler;

public class SetHealthCommandHandler implements ConsoleInputHandler {

    private Hero hero;

    public SetHealthCommandHandler(Hero hero) {
        this.hero = hero;
    }


    @Override
    public void handle(String input, Console console) {
        String[] args = input.split(" ", 2);
        float newHealthValue;

        if (args.length != 2) {
            printHelp(console);
            return;
        }

        try {
            newHealthValue = Float.parseFloat(args[1]);
        } catch (NumberFormatException e) {
            console.warn("Invalid value");
            printHelp(console);
            return;
        }

        if (newHealthValue > hero.getHull().config.getMaxLife()) {
            console.warn("Cannot set health above maximum");
            return;
        }
        if (newHealthValue < 0f) {
            console.warn("Cannot set health to less than zero");
            return;
        }
        if (hero.isDead()) {
            console.warn("Cannot set health when hero is dead");
            return;
        }

        hero.getShip().setLife(newHealthValue);
    }

    private void printHelp(Console console) {
        console.warn("Usage: \"setHealth value\" eg. \"setHealth 5\"");
    }
}
