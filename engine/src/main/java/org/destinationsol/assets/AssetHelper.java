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

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class AssetHelper {
    private ModuleAwareAssetTypeManager assetTypeManager;
    private static String[] folders_;

    public AssetHelper(ModuleEnvironment environment) {
        assetTypeManager = new ModuleAwareAssetTypeManager();

        assetTypeManager.registerCoreAssetType(OggSound.class, OggSound::new, "sounds");
        assetTypeManager.registerCoreAssetType(OggMusic.class, OggMusic::new, "music");
        assetTypeManager.registerCoreAssetType(Font.class, Font::new, "fonts");
        assetTypeManager.registerCoreAssetType(Emitter.class, Emitter::new, "emitters");
        assetTypeManager.registerCoreAssetType(Json.class, Json::new, "collisionMeshes", "ships", "items", "configs", "grounds", "mazes", "asteroids");
        assetTypeManager.registerCoreAssetType(DSTexture.class, DSTexture::new, "textures", "ships", "items", "grounds", "mazes", "asteroids");

        assetTypeManager.switchEnvironment(environment);
    }

    public <T extends Asset<U>, U extends AssetData> Optional<T> get(ResourceUrn urn, Class<T> type) {
        return assetTypeManager.getAssetManager().getAsset(urn, type);
    }

    public Set<ResourceUrn> list(Class<? extends Asset<?>> type) {
        return assetTypeManager.getAssetManager().getAvailableAssets(type);
    }

    public Set<ResourceUrn> list(Class<? extends Asset<?>> type, String regex) {
        Set<ResourceUrn> finalList = new HashSet<>();

        Set<ResourceUrn> resourceList = assetTypeManager.getAssetManager().getAvailableAssets(type);
        for (ResourceUrn resourceUrn : resourceList) {
            if (resourceUrn.toString().matches(regex)) {
                finalList.add(resourceUrn);
            }
        }

        return finalList;
    }

    public void setFolders(String... folders) {
        folders_ = folders;
    }

    public static String resolveToPath(List<AssetDataFile> assetDataFiles) {
        for (AssetDataFile assetDataFile : assetDataFiles) {
            List<String> folders = assetDataFile.getPath();

            boolean validPath = true;
            if (folders_ != null) {
                for (int i = 0; i < folders_.length; i++) {
                    if (!folders_[i].equals(folders.get(folders.size() - i - 1))) {
                        validPath = false;
                        break;
                    }
                }
            }
            if (!validPath)
                continue;

            StringBuilder path = new StringBuilder();

            if (folders.get(0).equals("engine")) {
                if (DebugOptions.DEV_ROOT_PATH != null) {
                    path.append(DebugOptions.DEV_ROOT_PATH);
                } else {
                    path.append("src/main/resources/");
                }
            } else {
                if (DebugOptions.DEV_ROOT_PATH == null) {
                    path.append("../");
                }
                path.append("modules/").append(folders.get(0)).append("/");
            }

            for (int i = 1; i < folders.size(); i++) {
                path.append(folders.get(i)).append("/");
            }

            path.append(assetDataFile.getFilename());

            return path.toString();
        }

        throw new RuntimeException("Could not resolve path!");
    }
}
