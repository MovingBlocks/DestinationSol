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

import com.google.common.collect.Ordering;
import org.destinationsol.game.SolGame;
import org.destinationsol.game.console.Console;
import org.destinationsol.game.console.ConsoleColors;
import org.destinationsol.game.console.ConsoleCommand;
import org.destinationsol.game.console.annotations.Command;
import org.destinationsol.game.console.annotations.CommandParam;
import org.destinationsol.game.console.annotations.Game;
import org.destinationsol.game.console.annotations.RegisterCommands;
import org.destinationsol.game.console.suggesters.CommandNameSuggester;

import java.util.List;

@RegisterCommands
public class HelpCommandHandler {
    @Command(shortDescription = "Prints out short descriptions for all available commands, or a longer help text if a command is provided.")
    public String help(@Game SolGame game, @CommandParam(value = "command", required = false, suggester = CommandNameSuggester.class) String commandName) {
        Console console = game.getScreens().consoleScreen.getConsole();
        if (commandName == null) {
            StringBuilder msg = new StringBuilder();
            // Get all commands, with appropriate sorting
            List<ConsoleCommand> commands = Ordering.natural().immutableSortedCopy(console.getCommands());

            for (ConsoleCommand cmd : commands) {
                if (!msg.toString().isEmpty()) {
                    msg.append(Console.NEW_LINE);
                }

                msg.append(cmd.getUsage());
                msg.append(" - ");
                msg.append(cmd.getDescription());
            }

            return msg.toString();
        } else {
            ConsoleCommand cmd = console.getCommand(commandName);
            if (cmd == null) {
                return "No help available for command '" + commandName + "'. Unknown command.";
            } else {
                StringBuilder msg = new StringBuilder();

                msg.append("=====================================================================");
                msg.append(Console.NEW_LINE);
                msg.append(cmd.getUsage());
                msg.append(Console.NEW_LINE);
                msg.append("=====================================================================");
                msg.append(Console.NEW_LINE);
                if (!cmd.getHelpText().isEmpty()) {
                    msg.append(cmd.getHelpText());
                    msg.append(Console.NEW_LINE);
                    msg.append("=====================================================================");
                    msg.append(Console.NEW_LINE);
                } else if (!cmd.getDescription().isEmpty()) {
                    msg.append(cmd.getDescription());
                    msg.append(Console.NEW_LINE);
                    msg.append("=====================================================================");
                    msg.append(Console.NEW_LINE);
                }

                return msg.toString();
            }
        }
    }
}
