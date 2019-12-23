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

import org.destinationsol.common.SolException;
import org.destinationsol.game.Console;
import org.destinationsol.game.console.ConsoleInputHandler;
import org.destinationsol.game.console.ShellInputHandler;

/**
 * A command used to print all commands or get detailed information on a specific one.
 * Can take one argument - the command you want information on
 */
public class HelpCommandHandler implements ConsoleInputHandler {

    private ShellInputHandler defaultInputHandler;

    public HelpCommandHandler(ShellInputHandler defaultInputHandler) {
        this.defaultInputHandler = defaultInputHandler;
    }

    @Override
    public void handle(String input, Console console) {
        String[] args = input.split(" ");

        if (args.length == 1) {
            console.info("--- Showing all commands ---");

            for (String command : defaultInputHandler.getRegisteredCommands().keySet()) {
                if (!command.equals("ShellInputHandler")) {
                    console.info(command);
                }
            }

            return;
        }

        if (args.length == 2) {
            try {
                ConsoleInputHandler handler = defaultInputHandler.getRegisteredCommand(args[1]);
                handler.printHelp(console);
            } catch (SolException solException) {
                console.warn("Unknown command: " + args[1]);
            }

            return;
        }

        printHelp(console);
    }

    @Override
    public void printHelp(Console console) {
        console.info("Prints a list of all commands or detailed information on a specific one.");
        console.info("Usage: help [command]");
    }

}
