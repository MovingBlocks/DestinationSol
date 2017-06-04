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
import org.destinationsol.TextureManager;
import org.destinationsol.assets.Assets;
import org.destinationsol.assets.json.Json;
import org.destinationsol.game.GameColors;
import org.destinationsol.game.SolGame;
import org.destinationsol.game.dra.DraLevel;
import org.terasology.assets.ResourceUrn;

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

    public SpecialEffects(EffectTypes effectTypes, TextureManager textureManager, GameColors cols) {
        Json json = Assets.getJson(new ResourceUrn("core:specialEffectsConfig"));
        JsonValue rootNode = json.getJsonValue();

        mySmoke = EffectConfig.load(rootNode.get("smoke"), effectTypes, textureManager, cols);
        myFire = EffectConfig.load(rootNode.get("fire"), effectTypes, textureManager, cols);
        myElectricity = EffectConfig.load(rootNode.get("electricity"), effectTypes, textureManager, cols);
        myShipExplSmoke = EffectConfig.load(rootNode.get("shipExplosionSmoke"), effectTypes, textureManager, cols);
        myShipExplFire = EffectConfig.load(rootNode.get("shipExplosionFire"), effectTypes, textureManager, cols);
        myAsteroidDust = EffectConfig.load(rootNode.get("asteroidDust"), effectTypes, textureManager, cols);
        myForceBeacon = EffectConfig.load(rootNode.get("forceBeacon"), effectTypes, textureManager, cols);
        starPortFlow = EffectConfig.load(rootNode.get("starPortFlow"), effectTypes, textureManager, cols);
        transcendentWork = EffectConfig.load(rootNode.get("transcendentWork"), effectTypes, textureManager, cols);

        json.dispose();
    }

    public List<ParticleSrc> buildBodyEffs(float objRad, SolGame game, Vector2 pos, Vector2 spd) {
        ArrayList<ParticleSrc> res = new ArrayList<>();
        float sz = objRad * .9f;
        ParticleSrc smoke = new ParticleSrc(mySmoke, sz, DraLevel.PART_FG_0, new Vector2(), true, game, pos, spd, 0);
        res.add(smoke);
        ParticleSrc fire = new ParticleSrc(myFire, sz, DraLevel.PART_FG_1, new Vector2(), true, game, pos, spd, 0);
        res.add(fire);
        ParticleSrc elec = new ParticleSrc(myElectricity, objRad * 1.2f, DraLevel.PART_FG_0, new Vector2(), true, game, pos, spd, 0);
        res.add(elec);
        return res;
    }

    public void explodeShip(SolGame game, Vector2 pos, float sz) {
        PartMan pm = game.getPartMan();
        ParticleSrc smoke = new ParticleSrc(myShipExplSmoke, 2 * sz, DraLevel.PART_FG_0, new Vector2(), false, game, pos, Vector2.Zero, 0);
        pm.finish(game, smoke, pos);
        ParticleSrc fire = new ParticleSrc(myShipExplFire, .7f * sz, DraLevel.PART_FG_1, new Vector2(), false, game, pos, Vector2.Zero, 0);
        pm.finish(game, fire, pos);
        pm.blinks(pos, game, sz);
    }

    public void asteroidDust(SolGame game, Vector2 pos, Vector2 spd, float size) {
        PartMan pm = game.getPartMan();
        ParticleSrc smoke = new ParticleSrc(myAsteroidDust, size, DraLevel.PART_FG_0, new Vector2(), true, game, pos, spd, 0);
        pm.finish(game, smoke, pos);
    }

    public ParticleSrc buildForceBeacon(float sz, SolGame game, Vector2 relPos, Vector2 basePos, Vector2 spd) {
        return new ParticleSrc(myForceBeacon, sz, DraLevel.PART_FG_0, relPos, false, game, basePos, spd, 0);
    }
}
