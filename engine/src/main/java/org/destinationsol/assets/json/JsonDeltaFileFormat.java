/*
 * Copyright 2020 MovingBlocks
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

import com.badlogic.gdx.files.FileHandle;
import org.destinationsol.assets.AssetDataFileHandle;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.terasology.gestalt.assets.format.AbstractAssetAlterationFileFormat;
import org.terasology.gestalt.assets.format.AssetDataFile;
import org.terasology.gestalt.assets.module.annotations.RegisterAssetDeltaFileFormat;

import javax.inject.Inject;
import java.io.IOException;

@RegisterAssetDeltaFileFormat
public class JsonDeltaFileFormat extends AbstractAssetAlterationFileFormat<JsonData> {
    @Inject
    public JsonDeltaFileFormat() {
        super("json");
    }

    /**
     * Applies an alteration to the given assetData
     *
     * @param input     The input corresponding to this asset
     * @param assetData An assetData to update
     * @throws IOException If there are any errors loading the alteration
     */
    @Override
    public void apply(AssetDataFile input, JsonData assetData) throws IOException {
        FileHandle handle = new AssetDataFileHandle(input);
        JSONObject deltaJsonValue = new JSONObject(handle.readString());

        JSONObject jsonValue = assetData.getJsonValue();
        mergeObjects(jsonValue, deltaJsonValue);
    }

    /**
     * This method merges the JSONObject input with its delta by recursively checking for differing values.
     *
     * If a value does not exist in the delta, then the original input value is preserved. Otherwise, if the value is
     * a primitive type (excluding array), then the delta value will override the input value. For JSONObject values,
     * this method is called recursively to merge the sub-objects together. In the case of arrays, all of the values
     * in the delta array are appended to the input array.
     *
     * @param input the JSONObject to merge into
     * @param delta the JSONObject to merge with
     */
    private void mergeObjects(JSONObject input, JSONObject delta) {
        for (String key : input.keySet()) {
            Object subObject = input.get(key);
            if (!delta.has(key)) {
                // Value is not modified
                continue;
            }

            if (subObject instanceof JSONObject) {
                Object deltaObject = delta.get(key);
                if (deltaObject instanceof JSONObject) {
                    mergeObjects((JSONObject) subObject, (JSONObject) deltaObject);
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
    private void mergeArray(JSONArray input, JSONArray delta) {
        for (int index = 0; index < delta.length(); index++) {
            input.put(delta.get(index));
        }
    }
}
