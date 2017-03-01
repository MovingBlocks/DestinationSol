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

import com.badlogic.gdx.audio.Sound;
import org.terasology.assets.Asset;
import org.terasology.assets.AssetType;
import org.terasology.assets.ResourceUrn;

public class OggSound extends Asset<OggSoundData> implements PlayableSound {
    private OggSoundData soundData;

    public OggSound(ResourceUrn urn, AssetType<?, OggSoundData> assetType, OggSoundData data) {
        super(urn, assetType);
        reload(data);
        getDisposalHook().setDisposeAction(this::doDispose);
    }

    @Override
    protected void doReload(OggSoundData data) {
        soundData = data;
    }

    @Override
    public OggSound getOggSound() {
        return this;
    }

    @Override
    public float getBasePitch() {
        return soundData.getBasePitch();
    }

    public void setBasePitch(float basePitch) {
        soundData.setBasePitch(basePitch);
    }

    public float getLoopTime() {
        return soundData.getLoopTime();
    }

    public float getBaseVolume() {
        return soundData.getBaseVolume();
    }

    public Sound getSound() {
        return soundData.getSound();
    }

    public void doDispose() {
        soundData.dispose();
    }

    @Override
    public String toString() {
        return String.format("[%s] baseVolume: %s loopTime: %s", getUrn(), soundData.getBaseVolume(), soundData.getLoopTime());
    }
}
