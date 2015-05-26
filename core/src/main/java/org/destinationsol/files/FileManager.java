package org.destinationsol.files;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import org.destinationsol.game.DebugOptions;

/**
 * Singleton class that can provide file handles to various directories.
 */
public final class FileManager {

    /**
     * Returns a file handle to the assets directory.
     * @return A file handle to the assets directory.
     */
    public FileHandle getAssetsDirectory() {
        return getFile(ASSETS_DIR, FileLocation.STATIC_FILES);
    }

    /**
     * Returns a file handle to the config directory.
     * @return A file handle to the config directory.
     */
    public FileHandle getConfigDirectory() {
        return getFile(CONFIG_DIR, FileLocation.STATIC_FILES);
    }

    /**
     * Returns a file handle to the hulls directory.
     * @return A file handle to the hulls directory.
     */
    public FileHandle getHullsDirectory() {
        return getFile(HULLS_CONFIG_DIR, FileLocation.STATIC_FILES);
    }

    /**
     * Returns a file handle to the items directory.
     * @return A file handle to the items directory.
     */
    public FileHandle getItemsDirectory() {
        return getFile(ITEMS_CONFIG_DIR, FileLocation.STATIC_FILES);
    }

    /**
     * Returns a file handle to the fonts directory.
     * @return A file handle to the fonts directory.
     */
    public FileHandle getFontsDirectory() {
        return getFile(FONTS_DIR, FileLocation.STATIC_FILES);
    }

    /**
     * Returns a file handle to the sounds directory.
     * @return A file handle to the sounds directory.
     */
    public FileHandle getSoundsDirectory() {
        return getFile(SOUNDS_DIR, FileLocation.STATIC_FILES);
    }

    /**
     * Returns a file handle to the images directory.
     * @return A file handle to the images directory.
     */
    public FileHandle getImagesDirectory() {
        return getFile(IMAGES_DIR, FileLocation.STATIC_FILES);
    }

    /**
     * Returns a handle to a static file.
     * Dynamic files are files which are written or updated by the application.
     * @param filePath The path to the file, relative to the dynamic file directory.
     * @return A file handle to the file.
     */
    public FileHandle getDynamicFile(String filePath) {
        return getFile(filePath, FileLocation.DYNAMIC_FILES);
    }

    /**
     * Returns a handle to a static file.
     * Static files are files which are not written to by the application.
     * @param filePath The path to the file, relative to the static file directory.
     * @return A file handle to the file.
     */
    public FileHandle getStaticFile(String filePath) {
        return getFile(filePath, FileLocation.STATIC_FILES);
    }

    /**
     * Returns a handle to a static file or dynamic file.
     * Static files are files which are not written to by the application.
     * @param filePath The path to the file, relative to the static/dynamic file directory.
     * @param fileLocation Whether the file resides in the static or dynamic file directory.
     * @return A file handle to the file.
     */
    public FileHandle getFile(String filePath, FileLocation fileLocation) {
        if(DebugOptions.DEV_ROOT_PATH != null) {
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
     * Returns the singleton instance of this class.
     * @return The instance.
     */
    public static FileManager getInstance() {
        if(instance == null) {
            instance = new FileManager();
        }

        return instance;
    }

    private FileManager() {

    }

    /**
     * Enum for the storage locations of files.
     */
    public static enum FileLocation {
        // Static files are files which are not written to by the application.
        STATIC_FILES,

        // Dynamic files are files which are written or updated by the application.
        DYNAMIC_FILES
    };

    private final static String ASSETS_DIR = "assets/";
    private final static String FONTS_DIR = ASSETS_DIR + "fonts/";
    private final static String SOUNDS_DIR = ASSETS_DIR + "sounds/";
    private final static String CONFIG_DIR = ASSETS_DIR + "configs/";
    private final static String IMAGES_DIR = ASSETS_DIR + "imgs/";
    private final static String HULLS_CONFIG_DIR = CONFIG_DIR + "hulls/";
    private final static String ITEMS_CONFIG_DIR = CONFIG_DIR + "items/";
    private final static String UNEXPECTED_FILE_LOCATION_TYPE = "Unexpected file location type: %s.";

    private static FileManager instance = null;
}
