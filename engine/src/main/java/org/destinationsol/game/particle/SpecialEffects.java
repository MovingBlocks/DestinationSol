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
    private final EffectConfig mySmoke;
    private final EffectConfig myFire;
    private final EffectConfig myElectricity;
    private final EffectConfig myShipExplSmoke;
    private final EffectConfig myShipExplFire;
    private final EffectConfig myAsteroidDust;
    private final EffectConfig myForceBeacon;

    public SpecialEffects(EffectTypes effectTypes, GameColors cols) {
        Json json = Assets.getJson("core:specialEffectsConfig");
        JsonValue rootNode = json.getJsonValue();

        mySmoke = EffectConfig.load(rootNode.get("smoke"), effectTypes, cols);
        myFire = EffectConfig.load(rootNode.get("fire"), effectTypes, cols);
        myElectricity = EffectConfig.load(rootNode.get("electricity"), effectTypes, cols);
        myShipExplSmoke = EffectConfig.load(rootNode.get("shipExplosionSmoke"), effectTypes, cols);
        myShipExplFire = EffectConfig.load(rootNode.get("shipExplosionFire"), effectTypes, cols);
        myAsteroidDust = EffectConfig.load(rootNode.get("asteroidDust"), effectTypes, cols);
        myForceBeacon = EffectConfig.load(rootNode.get("forceBeacon"), effectTypes, cols);
        starPortFlow = EffectConfig.load(rootNode.get("starPortFlow"), effectTypes, cols);
        transcendentWork = EffectConfig.load(rootNode.get("transcendentWork"), effectTypes, cols);

        json.dispose();
    }

    public List<DSParticleEmitter> buildBodyEffs(float objRad, SolGame game, Vector2 position, Vector2 speed) {
        ArrayList<DSParticleEmitter> res = new ArrayList<>();
        float sz = objRad * .9f;
        DSParticleEmitter smoke = new DSParticleEmitter(mySmoke, sz, DrawableLevel.PART_FG_0, new Vector2(), true, game, position, speed, 0);
        res.add(smoke);
        DSParticleEmitter fire = new DSParticleEmitter(myFire, sz, DrawableLevel.PART_FG_1, new Vector2(), true, game, position, speed, 0);
        res.add(fire);
        DSParticleEmitter elec = new DSParticleEmitter(myElectricity, objRad * 1.2f, DrawableLevel.PART_FG_0, new Vector2(), true, game, position, speed, 0);
        res.add(elec);
        return res;
    }

    public void explodeShip(SolGame game, Vector2 position, float sz) {
        PartMan pm = game.getPartMan();
        DSParticleEmitter smoke = new DSParticleEmitter(myShipExplSmoke, 2 * sz, DrawableLevel.PART_FG_0, new Vector2(), false, game, position, Vector2.Zero, 0);
        pm.finish(game, smoke, position);
        DSParticleEmitter fire = new DSParticleEmitter(myShipExplFire, .7f * sz, DrawableLevel.PART_FG_1, new Vector2(), false, game, position, Vector2.Zero, 0);
        pm.finish(game, fire, position);
        pm.blinks(position, game, sz);
    }

    public void asteroidDust(SolGame game, Vector2 position, Vector2 speed, float size) {
        PartMan pm = game.getPartMan();
        DSParticleEmitter smoke = new DSParticleEmitter(myAsteroidDust, size, DrawableLevel.PART_FG_0, new Vector2(), true, game, position, speed, 0);
        pm.finish(game, smoke, position);
    }

    public DSParticleEmitter buildForceBeacon(float sz, SolGame game, Vector2 relPos, Vector2 basePos, Vector2 speed) {
        return new DSParticleEmitter(myForceBeacon, sz, DrawableLevel.PART_FG_0, relPos, false, game, basePos, speed, 0);
    }
}
