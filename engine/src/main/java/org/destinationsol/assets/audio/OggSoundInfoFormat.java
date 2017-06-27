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

import com.google.common.base.Charsets;
import org.destinationsol.IniReader;
import org.terasology.assets.format.AbstractAssetAlterationFileFormat;
import org.terasology.assets.format.AssetDataFile;
import org.terasology.assets.module.annotations.RegisterAssetSupplementalFileFormat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@RegisterAssetSupplementalFileFormat
public class OggSoundInfoFormat extends AbstractAssetAlterationFileFormat<OggSoundData> {
    public OggSoundInfoFormat() {
        super("soundinfo");
    }

    @Override
    public void apply(AssetDataFile input, OggSoundData assetData) throws IOException {
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(input.openStream(), Charsets.UTF_8))) {
            IniReader iniReader = new IniReader(bufferedReader);
            assetData.setMetadata(iniReader.getFloat("loopTime", 0.0f), iniReader.getFloat("volume", 1.0f));
        }
    }
}
