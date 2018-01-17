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
import org.destinationsol.game.SolGame;
import org.destinationsol.game.drawables.Drawable;
import org.destinationsol.game.drawables.DrawableLevel;
import org.destinationsol.game.item.Engine;
import org.destinationsol.game.ship.SolShip;

import java.util.ArrayList;
import java.util.List;

public class ParticleEmitter {

    private final List<Drawable> myDrawables;
    private final ParticleSrc myParticle;

    public ParticleEmitter(SolGame game, Engine engine, ParticleEmitterSlot particleEmitterSlot, SolShip ship) {
        myDrawables = new ArrayList<>();
        EffectConfig effectConfig = engine.getEffectConfig();
        Vector2 particlePos = particleEmitterSlot.getPosition();
        Vector2 shipPos = ship.getPosition();
        Vector2 shipSpd = ship.getSpd();
        myParticle = new ParticleSrc(effectConfig, -1, DrawableLevel.PART_BG_0, particlePos, true, game, shipPos, shipSpd, 0);
        myDrawables.add(myParticle);
    }

    public List<Drawable> getDrawables() {
        return myDrawables;
    }

    // This currently does not allow the particle emitter to stop at any time
    // TODO: add the possibility for trigger events to control the emitter
    public void update() {
        myParticle.setWorking(true);
    }

    public void onRemove(SolGame game, Vector2 basePos) {
        PartMan pm = game.getPartMan();
        pm.finish(game, myParticle, basePos);
    }
}
