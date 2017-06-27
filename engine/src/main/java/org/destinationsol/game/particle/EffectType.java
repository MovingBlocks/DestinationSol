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
package org.destinationsol.game.particle;

import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import org.destinationsol.assets.Assets;

public class EffectType {
    public final boolean continuous;
    public final boolean additive;
    private final ParticleEmitter myEmitter;

    public EffectType(String effectName) {
        myEmitter = new ParticleEmitter(Assets.getEmitter(effectName).getParticleEmitter());
        continuous = myEmitter.isContinuous();
        myEmitter.setContinuous(false);
        additive = myEmitter.isAdditive();
        myEmitter.setAdditive(false);
    }

    public ParticleEmitter newEmitter() {
        return new ParticleEmitter(myEmitter);
    }
}
