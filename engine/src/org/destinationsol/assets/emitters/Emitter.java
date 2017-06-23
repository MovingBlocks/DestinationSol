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
package org.destinationsol.assets.emitters;

import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import org.terasology.assets.Asset;
import org.terasology.assets.AssetType;
import org.terasology.assets.ResourceUrn;

public class Emitter extends Asset<EmitterData> {
    private EmitterData emitterData;

    public Emitter(ResourceUrn urn, AssetType<?, EmitterData> assetType, EmitterData data) {
        super(urn, assetType);
        reload(data);
    }

    @Override
    protected void doReload(EmitterData data) {
        this.emitterData = data;
    }

    public ParticleEmitter getParticleEmitter() {
        return emitterData.getParticleEmitter();
    }
}
