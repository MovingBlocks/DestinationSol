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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import org.slf4j.Logger;
import org.terasology.assets.AssetData;
import org.terasology.assets.format.AbstractAssetFileFormat;
import org.terasology.assets.format.AssetDataFile;

import java.util.List;

public abstract class SolAssetFileFormat<T extends AssetData> extends AbstractAssetFileFormat<T> {

    public SolAssetFileFormat(String fileExtension, String... fileExtensions) {
        super(fileExtension, fileExtensions);
    }

    /**
     * Find a file in the modules/X/subdirectory
     * @return a {@link FileHandle} to the given path
     */
    private FileHandle findNormalFile(String path) {
        return Gdx.files.internal(path);
    }

    /**
     * Find a file in the classpath. Will strip modules/modulename/ from the beginning so the classloader can find it.
     * @param path of the file
     * @return a {@link FileHandle} to the given path after converting the path to the format this project uses
     */
    private FileHandle findFileOnClasspath(String path) {
        String filePath = path;
        if (filePath.startsWith("modules/")) {
            // When looking for the file inside the jar we need to remove the module/modulename/ prefix
            // com.badlogic.gdx.files#read() will put a prefixing / before the file path, so the ending slash has to be removed as well
            filePath = filePath.replaceFirst("^modules[/\\\\].+?[/\\\\]", "");
        }
        return Gdx.files.classpath(filePath);
    }

    protected FileHandle findFileAndLog(List<AssetDataFile> inputs, Logger logger) {
        String path = AssetHelper.resolveToPath(inputs);
        FileHandle handle = findNormalFile(path);
        if (!handle.exists()) {
            logger.debug("Could not find file " + path + ", trying to find it on classpath");
            handle = findFileOnClasspath(path);
            if (!handle.exists()) {
                logger.error("Could not find file " + path + " even on classpath!");
            }
        }
        return handle;
    }
}
