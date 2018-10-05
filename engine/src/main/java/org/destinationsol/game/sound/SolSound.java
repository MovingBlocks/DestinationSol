/*
 * Copyright 2017 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.destinationsol.game.sound;

import com.badlogic.gdx.audio.Sound;

import java.util.ArrayList;
import java.util.List;

public class SolSound {
    public final List<Sound> sounds;
    public final String dir;
    public final String definedBy;
    public final float loopTime;
    public final float baseVolume;
    public final boolean emptyDir;
    public float basePitch;

    public SolSound(String dir, String definedBy, float loopTime, float baseVolume, float basePitch,
                    ArrayList<Sound> sounds, boolean emptyDir) {
        this.dir = dir;
        this.definedBy = definedBy;
        this.loopTime = loopTime;
        this.baseVolume = baseVolume;
        this.sounds = sounds;
        this.basePitch = basePitch;
        this.emptyDir = emptyDir;
    }

    public String getDebugString() {
        StringBuilder sb = new StringBuilder();
        if (emptyDir) {
            sb.append("EMPTY ");
        }
        sb.append(dir).append(" (from ").append(definedBy).append(')');
        return sb.toString();
    }
}
