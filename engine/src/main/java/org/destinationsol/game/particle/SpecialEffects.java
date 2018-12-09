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
import com.badlogic.gdx.utils.JsonValue;
import org.destinationsol.assets.Assets;
import org.destinationsol.assets.json.Json;
import org.destinationsol.game.GameColors;
import org.destinationsol.game.SolGame;
import org.destinationsol.game.drawables.DrawableLevel;

import java.util.ArrayList;
import java.util.List;

public class SpecialEffects {

    public final EffectConfig starPortFlow;
    public final EffectConfig transcendentWork;
    private final EffectConfig smoke;
    private final EffectConfig fire;
    private final EffectConfig electricity;
    private final EffectConfig shipExplosionSmoke;
    private final EffectConfig shipExplosionFire;
    private final EffectConfig asteroidDust;
    private final EffectConfig forceBeacon;

    public SpecialEffects(EffectTypes effectTypes, GameColors colours) {
        Json json = Assets.getJson("core:specialEffectsConfig");
        JsonValue rootNode = json.getJsonValue();

        smoke = EffectConfig.load(rootNode.get("smoke"), effectTypes, colours);
        fire = EffectConfig.load(rootNode.get("fire"), effectTypes, colours);
        electricity = EffectConfig.load(rootNode.get("electricity"), effectTypes, colours);
        shipExplosionSmoke = EffectConfig.load(rootNode.get("shipExplosionSmoke"), effectTypes, colours);
        shipExplosionFire = EffectConfig.load(rootNode.get("shipExplosionFire"), effectTypes, colours);
        asteroidDust = EffectConfig.load(rootNode.get("asteroidDust"), effectTypes, colours);
        forceBeacon = EffectConfig.load(rootNode.get("forceBeacon"), effectTypes, colours);
        starPortFlow = EffectConfig.load(rootNode.get("starPortFlow"), effectTypes, colours);
        transcendentWork = EffectConfig.load(rootNode.get("transcendentWork"), effectTypes, colours);

        json.dispose();
    }

    public List<DSParticleEmitter> buildBodyEffs(float objRad, SolGame game, Vector2 position, Vector2 speed) {
        ArrayList<DSParticleEmitter> res = new ArrayList<>();
        float sz = objRad * .9f;
        DSParticleEmitter smoke = new DSParticleEmitter(this.smoke, sz, DrawableLevel.PART_FG_0, new Vector2(), true, game, position, speed, 0);
        res.add(smoke);
        DSParticleEmitter fire = new DSParticleEmitter(this.fire, sz, DrawableLevel.PART_FG_1, new Vector2(), true, game, position, speed, 0);
        res.add(fire);
        DSParticleEmitter electricity = new DSParticleEmitter(this.electricity, objRad * 1.2f, DrawableLevel.PART_FG_0, new Vector2(), true, game, position, speed, 0);
        res.add(electricity);
        return res;
    }

    public void explodeShip(SolGame game, Vector2 position, float size) {
        PartMan pm = game.getPartMan();
        DSParticleEmitter smoke = new DSParticleEmitter(shipExplosionSmoke, 2 * size, DrawableLevel.PART_FG_0, new Vector2(), false, game, position, Vector2.Zero, 0);
        pm.finish(game, smoke, position);
        DSParticleEmitter fire = new DSParticleEmitter(shipExplosionFire, .7f * size, DrawableLevel.PART_FG_1, new Vector2(), false, game, position, Vector2.Zero, 0);
        pm.finish(game, fire, position);
        pm.blinks(position, game, size);
    }

    public void asteroidDust(SolGame game, Vector2 position, Vector2 speed, float size) {
        DSParticleEmitter smoke = new DSParticleEmitter(asteroidDust, size, DrawableLevel.PART_FG_0, new Vector2(), true, game, position, speed, 0);
        game.getPartMan().finish(game, smoke, position);
    }

    public DSParticleEmitter buildForceBeacon(float size, SolGame game, Vector2 relativePosition, Vector2 basePosition, Vector2 speed) {
        return new DSParticleEmitter(forceBeacon, size, DrawableLevel.PART_FG_0, relativePosition, false, game, basePosition, speed, 0);
    }
}
