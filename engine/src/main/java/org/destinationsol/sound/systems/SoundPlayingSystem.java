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
package org.destinationsol.sound.systems;

import com.badlogic.gdx.math.Vector2;
import org.destinationsol.assets.sound.OggSoundManager;
import org.destinationsol.common.In;
import org.destinationsol.entitysystem.EventReceiver;
import org.destinationsol.game.SolGame;
import org.destinationsol.location.components.Position;
import org.destinationsol.sound.events.SoundEvent;
import org.terasology.gestalt.entitysystem.entity.EntityRef;
import org.terasology.gestalt.entitysystem.event.EventResult;
import org.terasology.gestalt.entitysystem.event.ReceiveEvent;

/**
 * This system plays sounds emitting from entities with a {@link Position} component, using the {@link OggSoundManager}.
 */
public class SoundPlayingSystem implements EventReceiver {

    @In
    private SolGame game;

    @In
    private OggSoundManager soundManager;

    /**
     * Plays a given sound emitting from an entity, at that entity's {@link Position}.
     */
    @ReceiveEvent(components = Position.class)
    public EventResult playSound(SoundEvent event, EntityRef entity) {
        Vector2 position = entity.getComponent(Position.class).get().position;
        soundManager.play(game, event.playableSound, null, entity, event.volumeMultplier);
        return EventResult.CONTINUE;
    }
}
