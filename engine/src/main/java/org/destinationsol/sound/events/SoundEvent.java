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
package org.destinationsol.sound.events;

import org.destinationsol.assets.sound.PlayableSound;
import org.terasology.gestalt.entitysystem.event.Event;

/**
 * Plays a sound emitting from an entity.
 */
public class SoundEvent implements Event {

    public final PlayableSound playableSound;
    public final int volumeMultplier;

    public SoundEvent(PlayableSound playableSound, int volumeMultplier) {
        this.playableSound = playableSound;
        this.volumeMultplier = volumeMultplier;
    }
}
