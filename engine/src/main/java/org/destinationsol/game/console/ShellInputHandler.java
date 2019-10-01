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

import org.destinationsol.common.SolException;
import org.destinationsol.game.Console;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Default user input handler used by {@link Console}.
 *
 * Can register additional accepted commands and handle their params.
 */
public class ShellInputHandler implements ConsoleInputHandler {

    private final Map<String, ConsoleInputHandler> commands;
    private static Logger logger = LoggerFactory.getLogger(ShellInputHandler.class);

    public ShellInputHandler() {
        commands = new HashMap<>();
        registerCommand("echo", (input, console) -> {
            if (input.contains(" ")) {
                console.println(input.split(" ", 2)[1]);
            }
        });
    }

    /**
     * Registers a command with this input handler.
     *
     * @param cmdName Name, that is first word, of the command
     * @param callback This callback will be called with the full entered command
     * @throws SolException If the command is already registered
     */
    public void registerCommand(String cmdName, ConsoleInputHandler callback) {
        if (commandExists(cmdName)) {
            throw new SolException("Trying to register command that already exists (" + cmdName + ")");
        }
        commands.put(cmdName, callback);
    }

    /**
     * Registers a command with this input handler. If the command was already registered then it is replaced
     *
     * @param cmdName Name, that is first word, of the command
     * @param callback This callback will be called with the full entered command
     */
    public void registerOrReplaceCommand(String cmdName, ConsoleInputHandler callback) {
        if (commandExists(cmdName)) {
            commands.remove(cmdName);
            logger.debug("Command " + cmdName + " already exists, replacing it with given parameter");
        }
        commands.put(cmdName, callback);
    }

    /**
     * Checks if a command has already been registered.
     *
     * @param cmdName Name, that is first word, of the command
     */
    public boolean commandExists(String cmdName) {
        return commands.keySet().contains(cmdName);
    }

    /**
     * Gets the input handler for a registered command
     *
     * @param cmdName Name, that is first word, of the command
     */
    public ConsoleInputHandler getRegisteredCommand(String cmdName) {
        if (!commandExists(cmdName)) {
            throw new SolException("Trying to access command with non-existent name (" + cmdName + ")");
        }
        return commands.get(cmdName);
    }

    @Override
    public void handle(String input, Console console) {
        for (String cmdName : commands.keySet()) {
            if (input.startsWith(cmdName + " ") || input.equals(cmdName)) {
                commands.get(cmdName).handle(input, console);
                break;
            }
        }
    }
}
