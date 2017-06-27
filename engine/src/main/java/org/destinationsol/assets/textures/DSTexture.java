/*
 * Copyright 2017 MovingBlocks
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
package org.destinationsol.assets.textures;

import com.badlogic.gdx.graphics.Texture;
import org.terasology.assets.Asset;
import org.terasology.assets.AssetType;
import org.terasology.assets.ResourceUrn;

public class DSTexture extends Asset<DSTextureData> {
    private DSTextureData dsTextureData;

    public DSTexture(ResourceUrn urn, AssetType<?, DSTextureData> assetType, DSTextureData data) {
        super(urn, assetType);
        reload(data);
    }

    @Override
    protected void doReload(DSTextureData data) {
        this.dsTextureData = data;
    }

    public Texture getTexture() {
        return dsTextureData.getTexture();
    }
}
