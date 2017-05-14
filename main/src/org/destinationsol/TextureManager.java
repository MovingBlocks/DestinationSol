/*
 * Copyright 2017 MovingBlocks
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
import org.destinationsol.common.SolMath;
import org.destinationsol.files.FileManager;
import org.terasology.assets.ResourceUrn;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TextureManager {
    public static final String ICONS_DIR = "ui/icons/";
    public static final String HULL_ICONS_DIR = "ui/hullIcons/";

    private final Map<String, TextureAtlas.AtlasRegion> textureMap = new HashMap<>();
    private final Map<TextureAtlas.AtlasRegion, TextureAtlas.AtlasRegion> myFlipped = new HashMap<>();
    private final Map<String, ArrayList<TextureAtlas.AtlasRegion>> myPacks = new HashMap<>();

    private final TextureProvider textureProvider;
    private final TextureProvider textureProviderStaticFiles;

    public TextureManager(AssetHelper assetHelper) {
        textureProviderStaticFiles = new DevTextureProvider();
        textureProvider = new AtlasTextureProvider(new ResourceUrn("Core:sol"), assetHelper);
    }

    public TextureAtlas.AtlasRegion getFlipped(TextureAtlas.AtlasRegion tex) {
        TextureAtlas.AtlasRegion r = myFlipped.get(tex);
        if (r != null) {
            return r;
        }
        r = textureProvider.getCopy(tex);
        r.flip(true, false);
        myFlipped.put(tex, r);
        return r;
    }

    public TextureAtlas.AtlasRegion getTexture(String fullName) {
        TextureAtlas.AtlasRegion result = textureMap.get(fullName);
        if (result != null) {
            return result;
        }

        FileHandle textureFile = new FileHandle(fullName);

        result = textureProvider.getTexture(textureFile);
        textureMap.put(textureFile.path(), result);

        if (result == null) {
            result = textureProviderStaticFiles.getTexture(textureFile);
            textureMap.put(textureFile.path(), result);
        }

        if (result == null) {
            throw new AssertionError("atlas not found: " + textureFile.path());
        }

        return result;
    }

    public ArrayList<TextureAtlas.AtlasRegion> getPack(String name, FileHandle configFile) {
        ArrayList<TextureAtlas.AtlasRegion> r = myPacks.get(name);
        if (r != null) {
            return r;
        }
        r = textureProvider.getTexs(name, configFile);
        if (r.size() == 0) {
            throw new AssertionError("textures not found: " + name);
        }
        myPacks.put(name, r);
        return r;
    }

    public TextureAtlas.AtlasRegion getRndTex(String name, Boolean flipped, FileHandle configFile) {
        if (flipped == null) {
            flipped = SolMath.test(.5f);
        }
        ArrayList<TextureAtlas.AtlasRegion> pack = getPack(name, configFile);
        TextureAtlas.AtlasRegion r = SolMath.elemRnd(pack);
        if (flipped) {
            r = getFlipped(r);
        }
        return r;
    }

    public Sprite createSprite(String name) {
        return textureProvider.createSprite(name);
    }

    public void dispose() {
        textureProvider.dispose();
    }
}
