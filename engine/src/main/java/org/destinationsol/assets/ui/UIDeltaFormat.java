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

package org.destinationsol.assets.ui;


import com.badlogic.gdx.files.FileHandle;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import org.destinationsol.assets.AssetDataFileHandle;
import org.destinationsol.util.JSONMerger;
import org.json.JSONObject;
import org.terasology.gestalt.assets.format.AbstractAssetAlterationFileFormat;
import org.terasology.gestalt.assets.format.AssetDataFile;
import org.terasology.gestalt.assets.module.annotations.RegisterAssetDeltaFileFormat;
import org.terasology.nui.asset.UIData;

import javax.inject.Inject;
import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.PrivilegedAction;

@RegisterAssetDeltaFileFormat
public class UIDeltaFormat extends AbstractAssetAlterationFileFormat<UIData> {
    private final UIFormat uiFormat;

    @Inject
    public UIDeltaFormat(UIFormat uiFormat) {
        super("ui");
        this.uiFormat = uiFormat;
    }

    @Override
    public void apply(AssetDataFile input, UIData assetData) throws IOException {
        FileHandle handle = new AssetDataFileHandle(input);
        JSONObject deltaJsonValue = new JSONObject(handle.readString());

        JSONObject jsonValue = new JSONObject(new AssetDataFileHandle(assetData.getSource()).readString());
        JSONMerger.merge(jsonValue, deltaJsonValue);

        JsonReader jsonReader = new JsonReader(new StringReader(jsonValue.toString()));
        jsonReader.setLenient(true);
        AccessController.doPrivileged((PrivilegedAction<Object>) () -> {
            try {
                Field widgetField = assetData.getClass().getDeclaredField("rootWidget");
                widgetField.setAccessible(true);
                widgetField.set(assetData, uiFormat.load(new JsonParser().parse(jsonReader)).getRootWidget());
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            } catch (NoSuchFieldException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return null;
        });
    }
}
