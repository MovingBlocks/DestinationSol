/*
 * Copyright 2020 The Terasology Foundation
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
package org.destinationsol.assets.music;

import com.badlogic.gdx.Gdx;
import org.terasology.gestalt.assets.ResourceUrn;
import org.terasology.gestalt.assets.format.AbstractAssetFileFormat;
import org.terasology.gestalt.assets.format.AssetDataFile;

import java.io.IOException;
import java.util.List;

public class AndroidOggMusicFileFormat extends AbstractAssetFileFormat<OggMusicData> {
    public AndroidOggMusicFileFormat() {
        super("ogg");
    }

    @Override
    public OggMusicData load(ResourceUrn urn, List<AssetDataFile> inputs) throws IOException {
        // HACK: LibGDX will only accept an AndroidFileHandle (it casts to one internally). The class has a private
        //       constructor, so we cannot use the same workaround as with AssetDataFileHandle.
        //       Android also doesn't seem to support playing media directly form InputStreams directly either.
        //       The only way I've found to get around this is to bypass gestalt using the information it provides
        //       on the source path for the asset.
        //       Android will be forced to use directory modules exclusively because of this.
        AssetDataFile asset = inputs.get(0);

        StringBuilder pathStringBuilder = new StringBuilder();
        if (urn.getModuleName().compareTo("engine") != 0) {
            pathStringBuilder.append("modules/");
        }
        pathStringBuilder.append(urn.getModuleName());
        pathStringBuilder.append("/");

        for (int pathNo = 0; pathNo < asset.getPath().size(); pathNo++) {
            pathStringBuilder.append(asset.getPath().get(pathNo));
            pathStringBuilder.append('/');
        }
        pathStringBuilder.append(asset.getFilename());

        return new OggMusicData(Gdx.audio.newMusic(Gdx.files.internal(pathStringBuilder.toString())));
    }
}
