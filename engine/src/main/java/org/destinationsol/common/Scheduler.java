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
package org.destinationsol.common;

import java.util.TreeMap;

public class Scheduler {

    // Class should not be instantiated
    private Scheduler() { }

    @SuppressWarnings("CheckStyle") // TreeMap is needed for its minheap capabilities (required repeated access and modification of first element)
    private static TreeMap<Long, Runnable> map = new TreeMap<>();

    public static void update() {
        if (map.size() > 0) {
            if (map.firstKey() <= System.currentTimeMillis()) {
                map.pollFirstEntry().getValue().run();
                update(); // If there was more callbacks ready since last iter
            }
        }
    }

    /**
     * Registers a callback to be executed after given amount of time.
     *
     * Example:
     * <code>
     * Scheduler.registerScheduledCallback(500, () -> System.out.println("This was printed with 0.5 second delay");
     * </code>
     *
     * @param delay Amount of time in millis, after which to execute the callback
     * @param callback Callback to execute
     */
    public static void registerScheduledCallback(long delay, Runnable callback) {
        map.put(System.currentTimeMillis() + delay, callback);
    }
}
