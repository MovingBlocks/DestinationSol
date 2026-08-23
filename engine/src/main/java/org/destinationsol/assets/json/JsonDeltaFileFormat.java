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
import org.destinationsol.util.JSONMerger;
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
        JSONMerger.merge(jsonValue, deltaJsonValue);
    }
}
