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
import org.destinationsol.assets.sound.SpecialSounds;
import org.destinationsol.entitysystem.EventReceiver;
import org.destinationsol.force.events.ImpulseEvent;
import org.destinationsol.game.DmgType;
import org.destinationsol.game.SolGame;
import org.destinationsol.health.events.DamageEvent;
import org.destinationsol.location.components.Position;
import org.destinationsol.material.MaterialType;
import org.destinationsol.material.components.Material;
import org.destinationsol.sound.events.SoundEvent;
import org.terasology.gestalt.entitysystem.entity.EntityRef;
import org.terasology.gestalt.entitysystem.event.EventResult;
import org.terasology.gestalt.entitysystem.event.ReceiveEvent;

import javax.inject.Inject;

/**
 * This system plays sounds emitting from entities with a {@link Position} component, using the {@link OggSoundManager}.
 * <p>
 * Hit and collision sounds are selected and played by {@link SpecialSounds}, which is also what the non-entity
 * ({@link org.destinationsol.game.SolObject}) code path uses, so that both paths stay in step.
 */
public class SoundPlayingSystem implements EventReceiver {

    @Inject
    SolGame game;

    @Inject
    OggSoundManager soundManager;

    @Inject
    SpecialSounds specialSounds;

    @Inject
    public SoundPlayingSystem() {

    }

    /**
     * Plays a given sound emitting from an entity, at that entity's {@link Position}.
     */
    @ReceiveEvent(components = Position.class)
    public EventResult playSound(SoundEvent event, EntityRef entity) {
        Vector2 position = entity.getComponent(Position.class).get().position;
        soundManager.play(game, event.playableSound, position, entity, event.volumeMultiplier);
        return EventResult.CONTINUE;
    }

    /**
     * When an entity takes damage, this plays a sound based on the type of damage taken and the type of material that
     * the entity is. No sound will be played if there is no defined sound for the {@link DmgType}/{@link MaterialType}
     * combination, or if either the {@link DmgType} or {@link MaterialType} is unknown.
     */
    @ReceiveEvent(components = {Position.class, Material.class})
    public EventResult playDamageSound(DamageEvent event, EntityRef entity) {
        MaterialType materialType = entity.getComponent(Material.class).get().materialType;
        Vector2 position = entity.getComponent(Position.class).get().position;
        specialSounds.playHit(game, entity, position, event.getDamageType().orElse(null), materialType);
        return EventResult.CONTINUE;
    }

    /**
     * When an entity experiences a collision, this plays a sound based on the type of material that the entity is. No
     * sound will be played if there is no defined collision sound for the {@link MaterialType}, if the
     * {@link MaterialType} is unknown, or if the collision was too gentle to be heard.
     */
    @ReceiveEvent(components = {Position.class, Material.class})
    public EventResult playCollisionSound(ImpulseEvent event, EntityRef entity) {
        MaterialType materialType = entity.getComponent(Material.class).get().materialType;
        specialSounds.playColl(game, event.getMagnitude(), entity, event.getContactPosition(), materialType);
        return EventResult.CONTINUE;
    }
}
