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
package org.destinationsol.assets.textures;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import org.destinationsol.assets.AssetHelper;
import org.terasology.assets.ResourceUrn;
import org.terasology.assets.format.AbstractAssetFileFormat;
import org.terasology.assets.format.AssetDataFile;
import org.terasology.assets.module.annotations.RegisterAssetFileFormat;

import java.nio.file.Paths;
import java.util.List;

@RegisterAssetFileFormat
public class DSTextureFileFormat extends AbstractAssetFileFormat<DSTextureData> {
    public DSTextureFileFormat() {
        super("png");
    }

    @Override
    public DSTextureData load(ResourceUrn urn, List<AssetDataFile> inputs) {
        String path = AssetHelper.resolveToPath(inputs);

        FileHandle handle = new FileHandle(Paths.get(path).toFile());
        Texture texture = new Texture(handle);
        return new DSTextureData(texture);
    }
}
