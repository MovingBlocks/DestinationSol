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

import java.util.ArrayList;

interface TextureProvider {
    /**
     * Reads a image file and returns it as a usable atlas.
     *
     * @param textureFile The image file for the atlas.
     * @return The atlas.
     */
    TextureAtlas.AtlasRegion getTexture(FileHandle textureFile);

    void dispose();

    Sprite createSprite(String name);

    ArrayList<TextureAtlas.AtlasRegion> getTexs(String name);

    TextureAtlas.AtlasRegion getCopy(TextureAtlas.AtlasRegion tex);
}
