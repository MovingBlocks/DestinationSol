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
package org.destinationsol.game.time;

public interface TimeFactor {
    /**
     * Returns strength of this factor.
     *
     * Value of {@code 0.5f} means time flowing 2 times slower, value of {@code 2f} means time flows 2 times faster.
     *
     * @return Strength of this factor
     */
    float getFactor();

    /**
     * Returns true when factor should no longer apply, or is {@code 1} and will not change.
     *
     * @return True when factor should no longer do anything, false otherwise
     */
    boolean canBeRemoved();

    /**
     * Can be used for whatever updating of factor's properties is necessary.
     *
     * Can be used for example for modifying factor's strength based on how long it has already been active.
     * @see DiminishingTimeFactor#update()
     */
    default void update() {
    }
}
