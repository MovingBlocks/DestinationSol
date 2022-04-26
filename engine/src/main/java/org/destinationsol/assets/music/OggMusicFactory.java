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

import org.terasology.context.annotation.Service;
import org.terasology.gestalt.assets.AssetFactory;
import org.terasology.gestalt.assets.AssetType;
import org.terasology.gestalt.assets.ResourceUrn;

@Service
public class OggMusicFactory implements AssetFactory<OggMusic, OggMusicData> {

    @Override
    public OggMusic build(ResourceUrn urn, AssetType<OggMusic, OggMusicData> type, OggMusicData data) {
        return new OggMusic(urn, type, data);
    }
}
