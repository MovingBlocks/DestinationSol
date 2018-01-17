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
package org.destinationsol.game.particle;

import com.badlogic.gdx.math.Vector2;
import org.destinationsol.common.Immutable;
import org.destinationsol.common.NotNull;

/**
 * Particle Emitter slot of a ship hull, to which a particle emitter can be attached.
 */
@Immutable
public class ParticleEmitterSlot {

    private final @NotNull Vector2 position;
    private final @NotNull String particleName, trigger;

    public ParticleEmitterSlot(@NotNull Vector2 position, @NotNull String particleName, @NotNull String trigger) {
        if (position == null) {
            throw new IllegalArgumentException("position cannot be null");
        }
        if (particleName == null) {
            throw new IllegalArgumentException("particleName cannot be null");
        }
        if (trigger == null) {
            throw new IllegalArgumentException("trigger cannot be null");
        }

        this.position = new Vector2(position);
        this.particleName = particleName;
        this.trigger = trigger;
    }

    /**
     * Returns the position, relative to the ship hull origin that owns the slot.
     *
     * @return The position, relative to the ship hull origin that owns the slot.
     */
    public
    @NotNull
    Vector2 getPosition() {
        return position;
    }

    /**
     * Returns the name of the Particle Emitter
     *
     * @return The name of the Particle Emitter
     */
    @NotNull
    public String getParticleName() {
        return particleName;
    }

    /**
     * Returns the trigger type set on the Particle Emitter
     *
     * @return The trigger type set on the Particle Emitter
     */
    @NotNull
    public String getTrigger() {
        return trigger;
    }
}
