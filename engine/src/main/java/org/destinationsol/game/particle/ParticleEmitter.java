/*
 * Copyright 2018 MovingBlocks
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
import com.google.common.base.Preconditions;
import org.destinationsol.common.NotNull;
import org.destinationsol.game.SolGame;
import org.destinationsol.game.drawables.Drawable;
import org.destinationsol.game.drawables.DrawableLevel;
import org.destinationsol.game.item.Engine;
import org.destinationsol.game.ship.SolShip;

import java.util.ArrayList;
import java.util.List;

public class ParticleEmitter {

    private final Vector2 position;
    private final String particleName, trigger;
    private final List<Drawable> drawables;
    private final ParticleSrc particleSrc;

    public ParticleEmitter(@NotNull Vector2 position, @NotNull String particleName, @NotNull String trigger) {
        Preconditions.checkNotNull(position, "position cannot be null");
        this.position = new Vector2(position);
        this.particleName = Preconditions.checkNotNull(particleName, "particleName cannot be null");
        this.trigger = Preconditions.checkNotNull(trigger, "trigger cannot be null");

        drawables = null;
        particleSrc = null;
    }

    public ParticleEmitter(SolGame game, Engine engine, ParticleEmitter particleEmitter, SolShip ship) {
        drawables = new ArrayList<>();
        EffectConfig effectConfig = engine.getEffectConfig();
        Vector2 particleSrcPos = particleEmitter.getPosition();
        Vector2 shipPos = ship.getPosition();
        Vector2 shipSpeed = ship.getSpd();
        particleSrc = new ParticleSrc(effectConfig, -1, DrawableLevel.PART_BG_0, particleSrcPos, true, game, shipPos, shipSpeed, 0);
        drawables.add(particleSrc);

        position = null;
        particleName = null;
        trigger = null;
    }

    public List<Drawable> getDrawables() {
        return drawables;
    }

    // This currently does not allow the particle emitter to stop at any time
    // TODO: add the possibility for trigger events to control the emitter
    public void update() {
        particleSrc.setWorking(true);
    }

    public void onRemove(SolGame game, Vector2 basePos) {
        PartMan partMan = game.getPartMan();
        partMan.finish(game, particleSrc, basePos);
    }

    /**
     * Returns the position, relative to the ship hull origin that owns the slot.
     *
     * @return The position, relative to the ship hull origin that owns the slot.
     */
    public Vector2 getPosition() {
        return position;
    }

    /**
     * Returns the name of the Particle Emitter
     *
     * @return The name of the Particle Emitter
     */
    public String getParticleName() {
        return particleName;
    }

    /**
     * Returns the trigger type set on the Particle Emitter
     *
     * @return The trigger type set on the Particle Emitter
     */
    public String getTrigger() {
        return trigger;
    }
}
