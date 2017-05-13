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
import org.terasology.assets.AssetData;

public class OggSoundData implements AssetData {
    private Sound sound;

    private float loopTime;
    private float baseVolume;
    private float basePitch;

    public OggSoundData(Sound sound) {
        this.sound = sound;
        this.loopTime = 0.0f;
        this.baseVolume = 1.0f;
        this.basePitch = 1.0f;
    }

    public void setMetadata(float loop, float volume) {
        loopTime = loop;
        baseVolume = volume;
        // basePitch not set in metadata files
    }

    public float getLoopTime() {
        return loopTime;
    }

    public float getBaseVolume() {
        return baseVolume;
    }

    public float getBasePitch() {
        return basePitch;
    }

    public void setBasePitch(float basePitch) {
        this.basePitch = basePitch;
    }

    public Sound getSound() {
        return sound;
    }

    public void dispose() {
        sound.dispose();
    }
}
