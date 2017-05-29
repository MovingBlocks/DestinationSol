/*
 * Copyright 2015 MovingBlocks
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

package org.destinationsol.files;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import org.destinationsol.game.DebugOptions;

/**
 * Singleton class that can provide file handles to various directories.
 */
public final class FileManager {

    private final static String ASSETS_DIR = "res/";
    private final static String CONFIG_DIR = ASSETS_DIR + "configs/";
    private final static String UNEXPECTED_FILE_LOCATION_TYPE = "Unexpected file location type: %s.";
    private static FileManager instance = null;

    private FileManager() { }

    /**
     * Returns the singleton instance of this class.
     *
     * @return The instance.
     */
    public static FileManager getInstance() {
        if (instance == null) {
            instance = new FileManager();
        }

        return instance;
    }

    /**
     * Returns a file handle to the config directory.
     *
     * @return A file handle to the config directory.
     */
    public FileHandle getConfigDirectory() {
        return getFile(CONFIG_DIR, FileLocation.STATIC_FILES);
    }

    /**
     * Returns a handle to a static file.
     * Dynamic files are files which are written or updated by the application.
     *
     * @param filePath The path to the file, relative to the dynamic file directory.
     * @return A file handle to the file.
     */
    public FileHandle getDynamicFile(String filePath) {
        return getFile(filePath, FileLocation.DYNAMIC_FILES);
    }

    /**
     * Returns a handle to a static file.
     * Static files are files which are not written to by the application.
     *
     * @param filePath The path to the file, relative to the static file directory.
     * @return A file handle to the file.
     */
    public FileHandle getStaticFile(String filePath) {
        return getFile(filePath, FileLocation.STATIC_FILES);
    }

    /**
     * Returns a handle to a static file or dynamic file.
     * Static files are files which are not written to by the application.
     *
     * @param filePath     The path to the file, relative to the static/dynamic file directory.
     * @param fileLocation Whether the file resides in the static or dynamic file directory.
     * @return A file handle to the file.
     */
    public FileHandle getFile(String filePath, FileLocation fileLocation) {
        if (DebugOptions.DEV_ROOT_PATH != null) {
            return Gdx.files.absolute(DebugOptions.DEV_ROOT_PATH + filePath);
        }

        switch (fileLocation) {
            case STATIC_FILES:
                return Gdx.files.internal(filePath);
            case DYNAMIC_FILES:
                return Gdx.files.local(filePath);
            default:
                throw new UnsupportedOperationException(String.format(UNEXPECTED_FILE_LOCATION_TYPE, fileLocation));
        }
    }

    /**
     * Enum for the storage locations of files.
     */
    public enum FileLocation {
        // Static files are files which are not written to by the application.
        STATIC_FILES,

        // Dynamic files are files which are written or updated by the application.
        DYNAMIC_FILES
    }
}
