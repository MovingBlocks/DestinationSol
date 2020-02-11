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
package org.destinationsol.game.console.exceptions;

/**
 *
 */
public class CommandParameterParseException extends Exception {
    private static final long serialVersionUID = 4519046979318192019L;
    private final String parameter;

    public CommandParameterParseException(String message, Throwable cause, String parameter) {
        super(message, cause);
        this.parameter = parameter;
    }

    public CommandParameterParseException(String message, String parameter) {
        super(message);
        this.parameter = parameter;
    }

    public CommandParameterParseException(String parameter) {
        this.parameter = parameter;
    }

    public String getParameter() {
        return parameter;
    }
}
