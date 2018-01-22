/*
 * Copyright 2017 MovingBlocks
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
package org.destinationsol.assets.json;

/**
 * Exception to be used when there is an error during parsing some JSON file, especially when there are missing items from JSON.
 * <p>
 * See {@link JsonSanitizer} for meant usage
 */
public class SolJsonException extends RuntimeException {
    public SolJsonException(String msg) {
        super(msg);
    }

    /**
     * This Exception is meant to supplement more descriptive error messages.
     * <p>
     * If somebody does not accept that and does not supply the exception message, notify the user in case exception gets called.
     */
    public SolJsonException() {
        super("Someone is using undescriptive exception. Please report this to MovingBlocks.");
    }
}
