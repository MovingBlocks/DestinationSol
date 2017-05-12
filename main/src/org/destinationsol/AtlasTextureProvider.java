/*
 * Copyright 2015 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.destinationsol;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import org.destinationsol.assets.AssetHelper;
import org.terasology.assets.ResourceUrn;

import java.util.ArrayList;

class AtlasTextureProvider implements TextureProvider {
    private final TextureAtlas myAtlas;

    AtlasTextureProvider(ResourceUrn resourceUrn, AssetHelper assetHelper) {
        myAtlas = assetHelper.getTextureMap(resourceUrn).get().getTextureAtlas();
    }

    @Override
    public TextureAtlas.AtlasRegion getTexture(FileHandle textureFile) {
        return myAtlas.findRegion(textureFile.path());
    }

    @Override
    public TextureAtlas.AtlasRegion getTex(String fullName, FileHandle configFile) {
        return myAtlas.findRegion(fullName);
    }

    @Override
    public void dispose() {
        myAtlas.dispose();
    }

    @Override
    public Sprite createSprite(String name) {
        return myAtlas.createSprite(name);
    }

    @Override
    public ArrayList<TextureAtlas.AtlasRegion> getTexs(String name, FileHandle configFile) {
        ArrayList<TextureAtlas.AtlasRegion> r = new ArrayList<TextureAtlas.AtlasRegion>();
        for (TextureAtlas.AtlasRegion rr : myAtlas.findRegions(name)) {
            r.add(rr);
        }
        return r;
    }

    @Override
    public TextureAtlas.AtlasRegion getCopy(TextureAtlas.AtlasRegion tex) {
        return new TextureAtlas.AtlasRegion(tex);
    }
}
