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

/**
 * A class that stores an OggSound or a set of OggSounds.
 */
public interface PlayableSound {

    /**
     * Returns an OggSound selected from the sound set.
     *
     * @return
     */
    OggSound getOggSound();

    /**
     * Returns the common pitch value for all the stored OggSound.
     *
     * @return
     */
    float getBasePitch();
}
