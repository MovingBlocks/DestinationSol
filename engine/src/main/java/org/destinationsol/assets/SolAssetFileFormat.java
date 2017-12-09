package org.destinationsol.assets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import org.slf4j.Logger;
import org.terasology.assets.AssetData;
import org.terasology.assets.format.AbstractAssetFileFormat;
import org.terasology.assets.format.AssetDataFile;

import java.util.List;

public abstract class SolAssetFileFormat<T extends AssetData> extends AbstractAssetFileFormat<T> {
    // Regex for stripping module/modulename/ from the beginning of a path
    // com.badlogic.gdx.files#read() will put a prefixing / before the file path, so the ending slash has to be removed as well
    private final String stripModuleFromClasspathRegex = "^modules[/\\\\].+?[/\\\\]";

    public SolAssetFileFormat(String fileExtension, String... fileExtensions) {
        super(fileExtension, fileExtensions);
    }

    /**
     * Find a file in the modules/X/subdirectory
     * @return a {@link FileHandle} to the given path
     */
    protected FileHandle findNormalFile(String path) {
        return Gdx.files.internal(path);
    }

    /**
     * Find a file in the classpath. Will strip modules/modulename/ from the beginning so the classloader can find it.
     * @param path
     * @return a {@link FileHandle} to the given path after converting the path to the format this project uses
     */
    protected FileHandle findFileOnClasspath(String path) {
        if(path.startsWith("modules/")) {
            // When looking for the file inside the jar we need to remove the module/modulename/ prefix
            path = path.replaceFirst(stripModuleFromClasspathRegex, "");
        }

        return Gdx.files.classpath(path);
    }

    protected FileHandle findFileAndLog(List<AssetDataFile> inputs, Logger logger) {
        String path = AssetHelper.resolveToPath(inputs);
        FileHandle handle = findNormalFile(path);
        if(!handle.exists()) {
            logger.debug("Could not find file " + path + ", trying to find it on classpath");
            handle = findFileOnClasspath(path);
            if(!handle.exists()) {
                logger.error("Could not find file " + path + " even on classpath!");
            }
        }

        return handle;
    }
}
