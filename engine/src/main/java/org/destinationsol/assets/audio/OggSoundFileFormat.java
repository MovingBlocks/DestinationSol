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
import org.destinationsol.assets.SolAssetFileFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.assets.ResourceUrn;
import org.terasology.assets.format.AssetDataFile;
import org.terasology.assets.module.annotations.RegisterAssetFileFormat;

import java.io.IOException;
import java.util.List;

@RegisterAssetFileFormat
public class OggSoundFileFormat extends SolAssetFileFormat<OggSoundData> {
    public OggSoundFileFormat() {
        super("ogg");
    }

    private static Logger logger = LoggerFactory.getLogger(OggSoundFileFormat.class);

    @Override
    public OggSoundData load(ResourceUrn urn, List<AssetDataFile> inputs) throws IOException {
        FileHandle handle = findFileAndLog(inputs, logger);
        return new OggSoundData(Gdx.audio.newSound(handle));
    }
}
