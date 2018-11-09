/*
 * Copyright 2018 MovingBlocks
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
import org.destinationsol.assets.audio.OggMusicFileFormat;
import org.destinationsol.assets.audio.OggSound;
import org.destinationsol.assets.audio.OggSoundFileFormat;
import org.destinationsol.assets.emitters.Emitter;
import org.destinationsol.assets.emitters.EmitterFileFormat;
import org.destinationsol.assets.fonts.Font;
import org.destinationsol.assets.fonts.FontFileFormat;
import org.destinationsol.assets.json.Json;
import org.destinationsol.assets.json.JsonFileFormat;
import org.destinationsol.assets.textures.DSTexture;
import org.destinationsol.assets.textures.DSTextureFileFormat;
import org.destinationsol.game.DebugOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.assets.Asset;
import org.terasology.assets.AssetData;
import org.terasology.assets.ResourceUrn;
import org.terasology.assets.format.AssetDataFile;
import org.terasology.assets.format.producer.AssetFileDataProducer;
import org.terasology.assets.module.ModuleAwareAssetTypeManager;
import org.terasology.module.ModuleEnvironment;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class AssetHelper {
    private ModuleAwareAssetTypeManager assetTypeManager;
    private static String[] folders_;
    private static final Logger logger = LoggerFactory.getLogger(AssetHelper.class);

    public AssetHelper(ModuleEnvironment environment) {
        assetTypeManager = new ModuleAwareAssetTypeManager();

        logger.info("Loading sounds...");
        assetTypeManager.createAssetType(OggSound.class, OggSound::new, "sounds");
        ((AssetFileDataProducer)assetTypeManager.getAssetType(OggSound.class).get().getProducers().get(0)).addAssetFormat(new OggSoundFileFormat());

        logger.info("Loading music...");
        assetTypeManager.createAssetType(OggMusic.class, OggMusic::new, "music");
        ((AssetFileDataProducer)assetTypeManager.getAssetType(OggMusic.class).get().getProducers().get(0)).addAssetFormat(new OggMusicFileFormat());

        logger.info("Loading fonts...");
        assetTypeManager.createAssetType(Font.class, Font::new, "fonts");
        ((AssetFileDataProducer)assetTypeManager.getAssetType(Font.class).get().getProducers().get(0)).addAssetFormat(new FontFileFormat());

        logger.info("Loading emitters...");
        assetTypeManager.createAssetType(Emitter.class, Emitter::new, "emitters");
        ((AssetFileDataProducer)assetTypeManager.getAssetType(Emitter.class).get().getProducers().get(0)).addAssetFormat(new EmitterFileFormat());

        logger.info("Loading JSON...");
        assetTypeManager.createAssetType(Json.class, Json::new, "collisionMeshes", "ships", "items", "configs", "grounds", "mazes", "asteroids");
        ((AssetFileDataProducer)assetTypeManager.getAssetType(Json.class).get().getProducers().get(0)).addAssetFormat(new JsonFileFormat());

        logger.info("Loading textures...");
        assetTypeManager.createAssetType(DSTexture.class, DSTexture::new, "textures", "ships", "items", "grounds", "mazes", "asteroids");
        ((AssetFileDataProducer)assetTypeManager.getAssetType(DSTexture.class).get().getProducers().get(0)).addAssetFormat(new DSTextureFileFormat());

        logger.info("Switching to new module environment...");
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
