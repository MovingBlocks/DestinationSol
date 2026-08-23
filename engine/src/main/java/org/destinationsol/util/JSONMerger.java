/*
 * Copyright 2026 The Terasology Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.destinationsol.util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public final class JSONMerger {
    private JSONMerger() {
    }

    /**
     * This method merges the JSONObject input with its delta by recursively checking for differing values.
     *
     * If a value does not exist in the delta, then the original input value is preserved. Otherwise, if the value is
     * a primitive type (excluding array), then the delta value will override the input value. For JSONObject values,
     * this method is called recursively to merge the sub-objects together. In the case of arrays, all of the values
     * in the delta array are appended to the input array. Keys that exist only in the delta are added to the input
     * directly.
     *
     * @param input the JSONObject to merge into
     * @param delta the JSONObject to merge with
     */
    public static void merge(JSONObject input, JSONObject delta) {
        for (String key : delta.keySet()) {
            if (!input.has(key)) {
                // Key only exists in the delta, so add it directly
                input.put(key, delta.get(key));
                continue;
            }
            Object subObject = input.get(key);

            if (subObject instanceof JSONObject) {
                Object deltaObject = delta.get(key);
                if (deltaObject instanceof JSONObject) {
                    merge((JSONObject) subObject, (JSONObject) deltaObject);
                } else {
                    throw new JSONException("Error when parsing delta: Type " + deltaObject.getClass().getSimpleName() + " does not equal JSONObject");
                }

                continue;
            }

            if (subObject instanceof JSONArray) {
                Object deltaObject = delta.get(key);
                if (deltaObject instanceof JSONArray) {
                    mergeArray((JSONArray) subObject, (JSONArray) deltaObject);
                } else {
                    throw new JSONException("Error when parsing delta: Type " + deltaObject.getClass().getSimpleName() + " does not equal JSONArray");
                }

                continue;
            }

            // Assume that a primitive type is used (primitive types cannot be merged, only overridden)
            input.put(key, delta.get(key));
        }
    }

    /**
     * Merges the input with its delta by adding all values from the delta to the input.
     */
    private static void mergeArray(JSONArray input, JSONArray delta) {
        for (int index = 0; index < delta.length(); index++) {
            input.put(delta.get(index));
        }
    }
}
