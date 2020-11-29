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

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import org.destinationsol.assets.AssetDataFileHandle;
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
        AssetDataFileHandle fontDataHandle = new AssetDataFileHandle(inputs.get(0));
        BitmapFont.BitmapFontData fontData = new BitmapFont.BitmapFontData(fontDataHandle, true);

        TextureRegion[] fontTextures = new TextureRegion[fontData.imagePaths.length];
        for (int textureNo = 0; textureNo < fontData.imagePaths.length; textureNo++){
            String[] pathSegments = fontData.imagePaths[textureNo].split("/");
            String fontTextureName = pathSegments[pathSegments.length - 1];
            fontTextureName = fontTextureName.substring(0, fontTextureName.lastIndexOf('.'));
            Texture texture = Assets.getDSTexture(urn.getModuleName() + ":" + fontTextureName).getTexture();
            TextureRegion fontTexture = new TextureRegion(texture);
            fontTextures[textureNo] = fontTexture;
        }

        BitmapFont bitmapFont = new BitmapFont(fontData, Array.with(fontTextures), false);
        bitmapFont.setUseIntegerPositions(false);
        return new FontData(bitmapFont);
    }
}
