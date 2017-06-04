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

import org.destinationsol.assets.atlas.Atlas;
import org.destinationsol.assets.audio.OggMusic;
import org.destinationsol.assets.audio.OggSound;
import org.destinationsol.assets.emitters.Emitter;
import org.destinationsol.assets.fonts.Font;
import org.destinationsol.assets.json.Json;
import org.destinationsol.assets.textures.DSTexture;
import org.destinationsol.game.DebugOptions;
import org.terasology.assets.Asset;
import org.terasology.assets.AssetData;
import org.terasology.assets.ResourceUrn;
import org.terasology.assets.format.AssetDataFile;
import org.terasology.assets.module.ModuleAwareAssetTypeManager;
import org.terasology.module.ModuleEnvironment;

import java.io.File;
import java.util.List;
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
        assetTypeManager.registerCoreAssetType(DSTexture.class, DSTexture::new, "textures", "ships", "items");

        assetTypeManager.switchEnvironment(environment);
    }

    public <T extends Asset<U>, U extends AssetData> Optional<T> get(ResourceUrn urn, Class<T> type) {
        return assetTypeManager.getAssetManager().getAsset(urn, type);
    }

    public Set<ResourceUrn> list(Class<? extends Asset<?>> type) {
        return assetTypeManager.getAssetManager().getAvailableAssets(type);
    }

    public static String resolveToPath(AssetDataFile assetDataFile) {
        String path = "";

        List<String> folders = assetDataFile.getPath();

        if (folders.get(0).equals("engine")) {
            if (DebugOptions.DEV_ROOT_PATH != null) {
                path += DebugOptions.DEV_ROOT_PATH + "res" + File.separator;
            }
        } else {
            path += "modules" + File.separator + folders.get(0) + File.separator;
        }

        for (int i = 1; i < folders.size(); i++) {
            path += folders.get(i) + File.separator;
        }

        path += assetDataFile.getFilename();

        return path;
    }
}
