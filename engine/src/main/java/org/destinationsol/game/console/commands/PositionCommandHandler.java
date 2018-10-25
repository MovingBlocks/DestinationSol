/*
 * Copyright 2018 MovingBlocks
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
import org.destinationsol.game.Console;
import org.destinationsol.game.Hero;
import org.destinationsol.game.console.ConsoleInputHandler;

import java.util.Locale;

/**
 * A command used to output the position of the hero.
 *
 * It takes only (optionally) a single parameter: the format to output the position in.
 * @see PositionCommandHandler.PositionFormat for more details
 */
public class PositionCommandHandler implements ConsoleInputHandler {
    /**
     * The format that the position should be outputted in.
     */
    public enum PositionFormat
    {
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
         * @see Vector2.toString()
         */
        INTERNAL
    }

    /**
     * The hero to track the position of.
     * @see Hero for more information.
     */
    public Hero hero;
    /**
     * The character that the bars will consist of when outputting in the BOLD format
     * @see PositionFormat.BOLD
     */
    private final char boldLineCharacter = '*';
    /**
     * The number of additional characters to add to the bars when outputting in the BOLD format
     * @see PositionFormat.BOLD
     */
    private final int boldExtraCharacters = 6;
    private final PositionFormat defaultFormat = PositionFormat.TERSE;
    
    public PositionCommandHandler(Hero player) {
        hero = player;
    }

    @Override
    public void handle(String input, Console console) {
        Vector2 heroPosition = hero.getPosition();
        String[] args = input.split(" ", 2);

        PositionFormat outputFormat = defaultFormat;
        if (args.length == 2) {
            try {
                outputFormat = PositionFormat.valueOf(args[1].toUpperCase(Locale.ENGLISH));
            }
            catch (IllegalArgumentException e) {
                console.println("Invalid position format: \"" + args[1] + "\"!");
                console.println("Currently available formats: ");
                for (PositionFormat format : PositionFormat.values()) {
                    console.println("   " + format.toString());
                }
                return;
            }
        }

        switch (outputFormat) {
            case TERSE:
                console.println("X: " + heroPosition.x + "   Y: " + heroPosition.y);
                break;
            case VERBOSE:
                console.println("The hero's X co-ordinate is: " + heroPosition.x);
                console.println("The hero's Y co-ordinate is: " + heroPosition.y);
                break;
            case BOLD:
                String xOutputString = "X: " + heroPosition.x;
                String yOutputString = "Y: " + heroPosition.y;

                String boldLine = "";
                int boldLineLength = Math.max(xOutputString.length(), yOutputString.length());
                for (int i = 0; i < boldLineLength + boldExtraCharacters; i++) {
                    boldLine += boldLineCharacter;
                }

                console.println(boldLine);
                console.println(xOutputString);
                console.println(yOutputString);
                console.println(boldLine);
                break;
            case INTERNAL:
                console.println(heroPosition.toString());
                break;
        }
    }
}
