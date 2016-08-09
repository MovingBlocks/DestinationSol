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

import org.destinationsol.assets.audio.OggMusic;
import org.destinationsol.assets.audio.OggMusicData;
import org.destinationsol.assets.audio.OggSound;
import org.destinationsol.assets.audio.OggSoundData;
import org.terasology.assets.Asset;
import org.terasology.assets.AssetData;
import org.terasology.assets.AssetFactory;
import org.terasology.assets.AssetType;
import org.terasology.assets.ResourceUrn;
import org.terasology.assets.module.ModuleAwareAssetTypeManager;
import org.terasology.module.ModuleEnvironment;

import java.util.Optional;
import java.util.Set;

public class AssetHelper {
    private ModuleAwareAssetTypeManager assetTypeManager;

    public AssetHelper(ModuleEnvironment environment) {
        assetTypeManager = new ModuleAwareAssetTypeManager();

        // TODO: Temporary primitive version below, go back to this one when Java 8 / Android sorted out
        //assetTypeManager.registerCoreAssetType(OggSound.class, OggSound::new, "sounds");

        assetTypeManager.registerCoreAssetType(OggSound.class, new AssetFactory<OggSound, OggSoundData>() {
            @Override
            public OggSound build(ResourceUrn urn, AssetType<OggSound, OggSoundData> assetType, OggSoundData data) {
                return new OggSound(urn, assetType, data);
            }
        }, "sounds");

        //assetTypeManager.registerCoreAssetType(OggMusic.class, OggMusic::new, "music");

        assetTypeManager.registerCoreAssetType(OggMusic.class, new AssetFactory<OggMusic, OggMusicData>() {
            @Override
            public OggMusic build(ResourceUrn urn, AssetType<OggMusic, OggMusicData> assetType, OggMusicData data) {
                return new OggMusic(urn, assetType, data);
            }
        }, "sounds");

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

    public Set<ResourceUrn> getSounds() {
        return list(OggSound.class);
    }

    public Set<ResourceUrn> getMusicSet() {
        return list(OggMusic.class);
    }
}
