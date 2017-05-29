/*
 * Copyright 2016 MovingBlocks
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
import org.terasology.assets.Asset;
import org.terasology.assets.AssetData;
import org.terasology.assets.ResourceUrn;
import org.terasology.assets.module.ModuleAwareAssetTypeManager;
import org.terasology.module.ModuleEnvironment;

import java.util.Optional;
import java.util.Set;

public class AssetHelper {
    private ModuleAwareAssetTypeManager assetTypeManager;

    public AssetHelper(ModuleEnvironment environment) {
        assetTypeManager = new ModuleAwareAssetTypeManager();

        assetTypeManager.registerCoreAssetType(OggSound.class, OggSound::new, "sounds");
        assetTypeManager.registerCoreAssetType(OggMusic.class, OggMusic::new, "music");
        assetTypeManager.registerCoreAssetType(Atlas.class, Atlas::new, "atlas");
        assetTypeManager.registerCoreAssetType(Font.class, Font::new, "fonts");
        assetTypeManager.registerCoreAssetType(Emitter.class, Emitter::new, "emitters");
        assetTypeManager.registerCoreAssetType(Json.class, Json::new, "collisionMeshes", "ships", "items", "configs");
        assetTypeManager.registerCoreAssetType(DSTexture.class, DSTexture::new, "images", "ships", "items");

        assetTypeManager.switchEnvironment(environment);
    }

    public Set<ResourceUrn> list(Class<? extends Asset<?>> type) {
        return assetTypeManager.getAssetManager().getAvailableAssets(type);
    }

    public <T extends Asset<U>, U extends AssetData> Optional<T> get(ResourceUrn urn, Class<T> type) {
        return assetTypeManager.getAssetManager().getAsset(urn, type);
    }

    public OggSound getSound(ResourceUrn urn) {
        Optional<OggSound> oggSoundOptional = get(urn, OggSound.class);

        if (oggSoundOptional.isPresent()) {
            return oggSoundOptional.get();
        }

        // DebugOptions.MISSING_SOUND_ACTION.handle("OggSound " + urn.toString() + " not found!");
        throw new RuntimeException("OggSound " + urn.toString() + " not found!");
    }

    public OggMusic getMusic(ResourceUrn urn) {
        Optional<OggMusic> oggMusicOptional = get(urn, OggMusic.class);

        if (oggMusicOptional.isPresent()) {
            return oggMusicOptional.get();
        }

        throw new RuntimeException("OggSound " + urn.toString() + " not found!");
    }

    public Atlas getAtlas(ResourceUrn urn) {
        Optional<Atlas> atlasOptional = get(urn, Atlas.class);

        if (atlasOptional.isPresent()) {
            return atlasOptional.get();
        }

        throw new RuntimeException("Atlas " + urn.toString() + " not found!");
    }

    public Font getFont(ResourceUrn urn) {
        Optional<Font> fontOptional = get(urn, Font.class);

        if (fontOptional.isPresent()) {
            return fontOptional.get();
        }

        throw new RuntimeException("Font " + urn.toString() + " not found!");
    }

    public Emitter getEmitter(ResourceUrn urn) {
        Optional<Emitter> emitterOptional = get(urn, Emitter.class);

        if (emitterOptional.isPresent()) {
            return emitterOptional.get();
        }

        throw new RuntimeException("Emitter " + urn.toString() + " not found!");
    }

    public Json getJson(ResourceUrn urn) {
        Optional<Json> jsonOptional = get(urn, Json.class);

        if (jsonOptional.isPresent()) {
            return jsonOptional.get();
        }

        throw new RuntimeException("Json " + urn.toString() + " not found!");
    }

    public DSTexture getDSTexture(ResourceUrn urn) {
        Optional<DSTexture> dsTextureOptional = get(urn, DSTexture.class);

        if (dsTextureOptional.isPresent()) {
            return dsTextureOptional.get();
        }

        throw new RuntimeException("DSTexture " + urn.toString() + " not found!");
    }

    public TextureAtlas.AtlasRegion getAtlasRegion(ResourceUrn urn, Texture.TextureFilter textureFilter) {
        Texture texture = getDSTexture(urn).getTexture();
        texture.setFilter(textureFilter, textureFilter);
        TextureAtlas.AtlasRegion atlasRegion = new TextureAtlas.AtlasRegion(texture, 0, 0, texture.getWidth(), texture.getHeight());
        atlasRegion.flip(false, true);
        return atlasRegion;
    }

    public TextureAtlas.AtlasRegion getAtlasRegion(ResourceUrn urn) {
        return getAtlasRegion(urn, Texture.TextureFilter.Nearest);
    }
}
