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

import com.badlogic.gdx.math.Vector2;
import org.destinationsol.game.SolGame;
import org.destinationsol.game.console.annotations.Command;
import org.destinationsol.game.console.annotations.CommandParam;
import org.destinationsol.game.console.annotations.Game;
import org.destinationsol.game.console.annotations.RegisterCommands;
import org.destinationsol.game.console.suggesters.PositionFormatSuggester;

/**
 * A command used to output the position of the hero.
 * <p>
 * It takes only (optionally) a single parameter: the format to output the position in.
 *
 * @see PositionCommandHandler.PositionFormat for more details
 */
@RegisterCommands
public class PositionCommandHandler {

    /**
     * The character that the bars will consist of when outputting in the BOLD format.
     *
     * @see PositionFormat#BOLD
     */
    private static final char BOLD_LINE_CHARACTER = '*';
    /**
     * The number of additional characters to add to the bars when outputting in the BOLD format.
     *
     * @see PositionFormat#BOLD
     */
    private static final int BOLD_EXTRA_CHARACTERS = 6;

    /**
     * The default format ouf output if not specified.
     */

    @Command(shortDescription = "Prints the hero position")
    public String position(@Game SolGame game, @CommandParam(value = "format", required = false, suggester = PositionFormatSuggester.class) PositionFormat format) {

        if (format == null) {
            format = PositionFormat.INTERNAL;
        }

        Vector2 heroPosition = game.getHero().getPosition();

        switch (format) {
            case TERSE:
                return "X: " + heroPosition.x + "   Y: " + heroPosition.y;
            case VERBOSE:
                return "The hero's X co-ordinate is: " + heroPosition.x + "\n"
                        + "The hero's Y co-ordinate is: " + heroPosition.y;
            case BOLD:
                return getBoldFormat(heroPosition);
            case INTERNAL:
                return heroPosition.toString();
        }
        return heroPosition.toString();
    }

    private String getBoldFormat(Vector2 heroPosition) {
        String xOutputString = "X: " + heroPosition.x;
        String yOutputString = "Y: " + heroPosition.y;

        StringBuilder boldLine = new StringBuilder();
        int boldLineLength = Math.max(xOutputString.length(), yOutputString.length());
        for (int i = 0; i < boldLineLength + BOLD_EXTRA_CHARACTERS; i++) {
            boldLine.append(BOLD_LINE_CHARACTER);
        }
        String format = boldLine.toString() + "\n"
                + xOutputString + "\n"
                + yOutputString + "\n"
                + boldLine.toString();
        return format;
    }
    /*private void printFormatHelp(String requested) {
        console.warn("Invalid position format: \"" + requested + "\"!");
        console.warn("Currently available formats: ");
        for (PositionFormat format : PositionFormat.values()) {
            console.warn("   " + format.toString());
        }
    } */

    /**
     * The format that the position should be outputted in.
     */
    public enum PositionFormat {

        /**
         * A minimal output, designed to be as concise and to-the-point as possible.
         */
        TERSE,

        /**
         * A user-readable output, designed to be easily interpreted and understood.
         */
        VERBOSE,

        /**
         * An output designed for maximum visibility.
         */
        BOLD,

        /**
         * The default formatting of the position, used internally by the engine.
         *
         * @see Vector2#toString()
         */
        INTERNAL
    }
}
