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
 * Thrown when a suggester fails instantiating via the newInstance command
 */
public class SuggesterInstantiationException extends RuntimeException {
    private static final long serialVersionUID = 3151467068962337565L;

    public SuggesterInstantiationException() {
    }

    public SuggesterInstantiationException(String message) {
        super(message);
    }

    public SuggesterInstantiationException(String message, Throwable cause) {
        super(message, cause);
    }

    public SuggesterInstantiationException(Throwable cause) {
        super(cause);
    }

    public SuggesterInstantiationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
