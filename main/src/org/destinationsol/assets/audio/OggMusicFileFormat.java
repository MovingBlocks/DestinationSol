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
package org.destinationsol.assets.audio;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import org.destinationsol.game.DebugOptions;
import org.terasology.assets.ResourceUrn;
import org.terasology.assets.format.AbstractAssetFileFormat;
import org.terasology.assets.format.AssetDataFile;
import org.terasology.assets.module.annotations.RegisterAssetFileFormat;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

@RegisterAssetFileFormat
public class OggMusicFileFormat extends AbstractAssetFileFormat<OggMusicData> {
    public OggMusicFileFormat() {
        super("ogg");
    }

    @Override
    public OggMusicData load(ResourceUrn urn, List<AssetDataFile> inputs) throws IOException {
        String pathString = "";
        if (DebugOptions.DEV_ROOT_PATH != null) {
            pathString = DebugOptions.DEV_ROOT_PATH;
        }
        pathString += "res" + File.separator;

        List<String> path = inputs.get(0).getPath();

        for (int i = 1; i < path.size(); i++) {
            pathString += path.get(i) + File.separator;
        }
        pathString += inputs.get(0).getFilename();

        FileHandle handle = new FileHandle(Paths.get(pathString).toFile());
        return new OggMusicData(Gdx.audio.newMusic(handle));
    }
}
