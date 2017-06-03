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
package org.destinationsol.assets;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import org.destinationsol.assets.atlas.Atlas;
import org.destinationsol.assets.audio.OggMusic;
import org.destinationsol.assets.audio.OggSound;
import org.destinationsol.assets.emitters.Emitter;
import org.destinationsol.assets.fonts.Font;
import org.destinationsol.assets.json.Json;
import org.destinationsol.assets.textures.DSTexture;
import org.terasology.assets.ResourceUrn;
import org.terasology.module.ModuleEnvironment;

import java.util.Optional;

public abstract class Assets {
    private static AssetHelper assetHelper;

    public static void initialize(ModuleEnvironment environment) {
        assetHelper = new AssetHelper(environment);
    }

    public static OggSound getSound(ResourceUrn urn) {
        Optional<OggSound> oggSoundOptional = assetHelper.get(urn, OggSound.class);

        if (oggSoundOptional.isPresent()) {
            return oggSoundOptional.get();
        }

        // DebugOptions.MISSING_SOUND_ACTION.handle("OggSound " + urn.toString() + " not found!");
        throw new RuntimeException("OggSound " + urn.toString() + " not found!");
    }

    public static OggMusic getMusic(ResourceUrn urn) {
        Optional<OggMusic> oggMusicOptional = assetHelper.get(urn, OggMusic.class);

        if (oggMusicOptional.isPresent()) {
            return oggMusicOptional.get();
        }

        throw new RuntimeException("OggSound " + urn.toString() + " not found!");
    }

    public static Atlas getAtlas(ResourceUrn urn) {
        Optional<Atlas> atlasOptional = assetHelper.get(urn, Atlas.class);

        if (atlasOptional.isPresent()) {
            return atlasOptional.get();
        }

        throw new RuntimeException("Atlas " + urn.toString() + " not found!");
    }

    public static Font getFont(ResourceUrn urn) {
        Optional<Font> fontOptional = assetHelper.get(urn, Font.class);

        if (fontOptional.isPresent()) {
            return fontOptional.get();
        }

        throw new RuntimeException("Font " + urn.toString() + " not found!");
    }

    public static Emitter getEmitter(ResourceUrn urn) {
        Optional<Emitter> emitterOptional = assetHelper.get(urn, Emitter.class);

        if (emitterOptional.isPresent()) {
            return emitterOptional.get();
        }

        throw new RuntimeException("Emitter " + urn.toString() + " not found!");
    }

    public static Json getJson(ResourceUrn urn) {
        Optional<Json> jsonOptional = assetHelper.get(urn, Json.class);

        if (jsonOptional.isPresent()) {
            return jsonOptional.get();
        }

        throw new RuntimeException("Json " + urn.toString() + " not found!");
    }

    public static DSTexture getDSTexture(ResourceUrn urn) {
        Optional<DSTexture> dsTextureOptional = assetHelper.get(urn, DSTexture.class);

        if (dsTextureOptional.isPresent()) {
            return dsTextureOptional.get();
        }

        throw new RuntimeException("DSTexture " + urn.toString() + " not found!");
    }

    public static TextureAtlas.AtlasRegion getAtlasRegion(ResourceUrn urn, Texture.TextureFilter textureFilter) {
        Texture texture = getDSTexture(urn).getTexture();
        texture.setFilter(textureFilter, textureFilter);
        TextureAtlas.AtlasRegion atlasRegion = new TextureAtlas.AtlasRegion(texture, 0, 0, texture.getWidth(), texture.getHeight());
        atlasRegion.flip(false, true);
        return atlasRegion;
    }

    public static TextureAtlas.AtlasRegion getAtlasRegion(ResourceUrn urn) {
        return getAtlasRegion(urn, Texture.TextureFilter.Nearest);
    }
}
