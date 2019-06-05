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

import com.badlogic.gdx.files.FileHandle;
import org.terasology.assets.format.AssetDataFile;

import java.io.BufferedInputStream;
import java.io.InputStream;

/**
 * AssetDataFileHandle is an instance of FileHandle that provides access to an AssetDataFile, rather than an actual file.
 * Only reading from files will work. Writing to files will crash the game.
 * @see com.badlogic.gdx.files.FileHandle
 */
public class AssetDataFileHandle extends FileHandle {
    protected AssetDataFile dataFile;

    public AssetDataFileHandle(AssetDataFile dataFile) {
        this.dataFile = dataFile;
    }

    @Override
    public BufferedInputStream read(int bufferSize) {
        return (BufferedInputStream) read();
    }

    @Override
    public InputStream read() {
        try {
            return dataFile.openStream();
        } catch (Exception ignore) {
            return null;
        }
    }

    @Override
    public String name() {
        return dataFile.getFilename();
    }

    @Override
    public String extension() {
        return dataFile.getFileExtension();
    }

    @Override
    public String nameWithoutExtension() {
        String fileName = name();
        return dataFile.getFilename().substring(0, fileName.lastIndexOf(".") + 1);
    }

    @Override
    public String path() {
        return String.join("/", dataFile.getPath());
    }

    @Override
    public String pathWithoutExtension() {
        String path = path();
        return path.substring(0, path.indexOf(extension()));
    }

    @Override
    public String toString() {
        return path();
    }

    @Override
    public long length() {
        int length = -1;
        try {
            BufferedInputStream stream = dataFile.openStream();
            //HACK: This method may not produce reliable results in other JVMs.
            length = stream.available();
            stream.close();
        } catch (Exception ignore) {

        }

        return length;
    }
}
