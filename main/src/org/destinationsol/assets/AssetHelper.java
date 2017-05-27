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
        assetTypeManager.registerCoreAssetType(Json.class, Json::new, "collisionMeshes", "ships", "items");
        assetTypeManager.registerCoreAssetType(DSTexture.class, DSTexture::new, "ships", "items");

        assetTypeManager.switchEnvironment(environment);
    }

    public Set<ResourceUrn> list(Class<? extends Asset<?>> type) {
        return assetTypeManager.getAssetManager().getAvailableAssets(type);
    }

    public <T extends Asset<U>, U extends AssetData> Optional<T> get(ResourceUrn urn, Class<T> type) {
        return assetTypeManager.getAssetManager().getAsset(urn, type);
    }

    public Optional<OggSound> getSound(ResourceUrn urn) {
        return get(urn, OggSound.class);
    }

    public Optional<OggMusic> getMusic(ResourceUrn urn) {
        return get(urn, OggMusic.class);
    }

    public Optional<Atlas> getAtlas(ResourceUrn urn) {
        return get(urn, Atlas.class);
    }

    public Optional<Font> getFont(ResourceUrn urn) {
        return get(urn, Font.class);
    }

    public Optional<Emitter> getEmitter(ResourceUrn urn) {
        return get(urn, Emitter.class);
    }

    public Optional<Json> getJson(ResourceUrn urn) {
        return get(urn, Json.class);
    }

    public Optional<DSTexture> getDSTexture(ResourceUrn urn) {
        return get(urn, DSTexture.class);
    }

    public TextureAtlas.AtlasRegion getAtlasRegion(ResourceUrn urn) {
        Optional<DSTexture> dsTextureOptional = getDSTexture(urn);
        if (dsTextureOptional.isPresent()) {
            Texture texture = dsTextureOptional.get().getTexture();
            TextureAtlas.AtlasRegion atlasRegion = new TextureAtlas.AtlasRegion(texture, 0, 0, texture.getWidth(), texture.getHeight());
            atlasRegion.flip(false, true);
            return atlasRegion;
        } else {
            return null;
        }
    }
}
