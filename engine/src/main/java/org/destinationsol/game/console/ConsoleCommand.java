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

import com.google.common.collect.ImmutableList;
import org.destinationsol.game.console.exceptions.CommandExecutionException;
import org.destinationsol.game.console.exceptions.CommandSuggestionException;

import java.util.Comparator;
import java.util.List;
import java.util.Set;

public interface ConsoleCommand extends Comparable<ConsoleCommand> {
    Comparator<ConsoleCommand> COMPARATOR = new Comparator<ConsoleCommand>() {
        @Override
        public int compare(ConsoleCommand o1, ConsoleCommand o2) {
            int nameComparison = o1.getName().compareTo(o2.getName());

            if (nameComparison != 0) {
                return nameComparison;
            }

            if (!o1.endsWithVarargs() && o2.endsWithVarargs()) {
                return -1;
            } else if (o1.endsWithVarargs() && !o2.endsWithVarargs()) {
                return 1;
            }

            return o2.getRequiredParameterCount() - o1.getRequiredParameterCount();
        }

    };

    /**
     * The name must not be null or empty.
     *
     * @return The name of this command
     */
    String getName();

    /**
     * @return The parameter definitions of this command, never null.
     */
    ImmutableList<CommandParameter> getCommandParameters();

    /**
     * @return A short summary of what this Command does. Is never null, but may be empty.
     */
    String getDescription();

    /**
     * @return A detailed description of how to use this command. Is never null, but may be empty.
     */
    String getHelpText();

    /**
     * @return The required amount of parameters for this method to function properly
     */
    int getRequiredParameterCount();

    /**
     * @return Whether the command ends with a varargs array and the parameter amount can exceed
     * the result of {@link #getRequiredParameterCount()}
     */
    boolean endsWithVarargs();

    /**
     * The usage must not be null or empty.
     *
     * @return The usage hint of this command
     */
    String getUsage();

    /**
     * @return The object containing the command logic
     */
    Object getSource();

    /**
     * Executes the command
     *
     * @param parameters Parameters in an Object[] array as defined in {@link AbstractCommand#getCommandParameters()}.
     * @return A reply to the sender. TODO
     */
    String execute(List<String> parameters) throws CommandExecutionException;

    /**
     * Suggests valid parameters.
     *
     * @param parameters Currently provided parameters in an Object[] array.
     * @return A set of suggestions. Never null.
     */
    //TODO maybe return an array of serializable objects?
    Set<String> suggest(String currentValue, List<String> parameters) throws CommandSuggestionException;

    /**
     * @param command A command to compare this command to
     * @return The result of {@link #COMPARATOR}'s {@code compare(this, command)} command.
     */
    @Override
    int compareTo(ConsoleCommand command);
}
