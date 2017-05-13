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

import org.destinationsol.assets.TextureMap.TextureMap;
import org.destinationsol.assets.audio.OggMusic;
import org.destinationsol.assets.audio.OggSound;
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
        assetTypeManager.registerCoreAssetType(TextureMap.class, TextureMap::new, "imgs");

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

    public Optional<TextureMap> getTextureMap(ResourceUrn urn) {
        return get(urn, TextureMap.class);
    }

    public Set<ResourceUrn> getSoundSet() {
        return list(OggSound.class);
    }

    public Set<ResourceUrn> getMusicSet() {
        return list(OggMusic.class);
    }

    public Set<ResourceUrn> getTextureMapSet() {
        return list(TextureMap.class);
    }
}
