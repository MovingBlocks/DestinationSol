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
package org.destinationsol.asteroids.systems;

import org.destinationsol.assets.sound.SpecialSounds;
import org.destinationsol.asteroids.components.AsteroidMesh;
import org.destinationsol.common.In;
import org.destinationsol.common.SolMath;
import org.destinationsol.entitysystem.EntitySystemManager;
import org.destinationsol.entitysystem.EventReceiver;
import org.destinationsol.location.components.Position;
import org.destinationsol.removal.events.DeletionEvent;
import org.destinationsol.removal.systems.DestructionSystem;
import org.destinationsol.size.components.Size;
import org.destinationsol.sound.events.SoundEvent;
import org.terasology.gestalt.entitysystem.entity.EntityRef;
import org.terasology.gestalt.entitysystem.event.Before;
import org.terasology.gestalt.entitysystem.event.EventResult;
import org.terasology.gestalt.entitysystem.event.ReceiveEvent;

/**
 * This system plays asteroid-specific sounds.
 */
public class AsteroidSoundSystem implements EventReceiver {

    @In
    private EntitySystemManager entitySystemManager;

    @In
    private SpecialSounds specialSounds;

    /**
     * When an asteroid is destroyed, this plays the asteroid destruction sound.
     */
    @ReceiveEvent(components = {AsteroidMesh.class, Position.class})
    @Before(DestructionSystem.class)
    public EventResult playDeathSound(DeletionEvent event, EntityRef entity) {
        float volumeMultiplier = 1;
        if (entity.hasComponent(Size.class)) {
            float size = entity.getComponent(Size.class).get().size;
            volumeMultiplier = SolMath.clamp(size / .5f);
        }
        entitySystemManager.sendEvent(new SoundEvent(specialSounds.asteroidCrack, volumeMultiplier), entity);
        return EventResult.CONTINUE;
    }
}
