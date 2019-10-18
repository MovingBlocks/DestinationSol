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
package org.destinationsol.game;

import com.badlogic.gdx.graphics.Color;

public class ConsoleLine {

    private String message;
    private Color color;

    private ConsoleLine(String message) {
        this.message = message;
        this.color = Color.WHITE;
    }

    private ConsoleLine(String message, Color color) {
        this.message = message;
        this.color = color;
    }

    String getMessage() {
        return message;
    }

    Color getColor() {
        return color;
    }

    public static ConsoleLine create(String message) {
        return new ConsoleLine(message);
    }

    public static ConsoleLine create(String message, Color color) {
        return new ConsoleLine(message, color);
    }
}
