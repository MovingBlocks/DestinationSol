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
package org.destinationsol.assets.fonts;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import org.destinationsol.assets.Assets;
import org.terasology.gestalt.assets.ResourceUrn;
import org.terasology.gestalt.assets.format.AbstractAssetFileFormat;
import org.terasology.gestalt.assets.format.AssetDataFile;
import org.terasology.gestalt.assets.module.annotations.RegisterAssetFileFormat;

import java.io.IOException;
import java.util.List;

@RegisterAssetFileFormat
public class FontFileFormat extends AbstractAssetFileFormat<FontData> {
    public FontFileFormat() {
        super("font");
    }

    @Override
    public FontData load(ResourceUrn urn, List<AssetDataFile> inputs) throws IOException {
        String path = Assets.getAssetHelper().resolveToPath(inputs);

        //NOTE: The BitmapFont class relies on direct filesystem access, so jar modules will not be able to define new fonts.
        FileHandle handle = Gdx.files.getFileHandle(path, Files.FileType.Internal);
        BitmapFont bitmapFont = new BitmapFont(handle, true);
        bitmapFont.setUseIntegerPositions(false);
        return new FontData(bitmapFont);
    }
}
