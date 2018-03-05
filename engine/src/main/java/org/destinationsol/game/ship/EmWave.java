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

package org.destinationsol.game.ship;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.JsonValue;
import org.destinationsol.game.AbilityCommonConfig;
import org.destinationsol.game.SolGame;
import org.destinationsol.game.SolObject;
import org.destinationsol.game.drawables.DrawableLevel;
import org.destinationsol.game.item.ItemManager;
import org.destinationsol.game.item.SolItem;
import org.destinationsol.game.particle.DSParticleEmitter;

public class EmWave implements ShipAbility {
    public static final int MAX_RADIUS = 4;
    private final Config myConfig;

    public EmWave(Config config) {
        myConfig = config;
    }

    @Override
    public AbilityConfig getConfig() {
        return myConfig;
    }

    @Override
    public AbilityCommonConfig getCommonConfig() {
        return myConfig.cc;
    }

    @Override
    public float getRadius() {
        return MAX_RADIUS;
    }

    @Override
    public boolean update(SolGame game, SolShip owner, boolean tryToUse) {
        if (!tryToUse) {
            return false;
        }
        Vector2 ownerPos = owner.getPosition();
        for (SolObject o : game.getObjectManager().getObjects()) {
            if (!(o instanceof SolShip) || o == owner) {
                continue;
            }
            SolShip oShip = (SolShip) o;
            if (!game.getFactionMan().areEnemies(oShip, owner)) {
                continue;
            }
            Vector2 oPos = o.getPosition();
            float dst = oPos.dst(ownerPos);
            float perc = KnockBack.getPerc(dst, MAX_RADIUS);
            if (perc <= 0) {
                continue;
            }
            float duration = perc * myConfig.duration;
            oShip.disableControls(duration, game);
        }
        DSParticleEmitter src = new DSParticleEmitter(myConfig.cc.effect, MAX_RADIUS, DrawableLevel.PART_BG_0, new Vector2(), true, game, ownerPos, Vector2.Zero, 0);
        game.getPartMan().finish(game, src, ownerPos);
        return true;
    }

    public static class Config implements AbilityConfig {
        public final float rechargeTime;
        public final float duration;
        private final SolItem chargeExample;
        private final AbilityCommonConfig cc;

        public Config(float rechargeTime, SolItem chargeExample, float duration, AbilityCommonConfig cc) {
            this.rechargeTime = rechargeTime;
            this.chargeExample = chargeExample;
            this.duration = duration;
            this.cc = cc;
        }

        public static AbilityConfig load(JsonValue abNode, ItemManager itemManager, AbilityCommonConfig cc) {
            float rechargeTime = abNode.getFloat("rechargeTime");
            float duration = abNode.getFloat("duration");
            SolItem chargeExample = itemManager.getExample("emWaveCharge");
            return new Config(rechargeTime, chargeExample, duration, cc);
        }

        @Override
        public ShipAbility build() {
            return new EmWave(this);
        }

        @Override
        public SolItem getChargeExample() {
            return chargeExample;
        }

        @Override
        public float getRechargeTime() {
            return rechargeTime;
        }

        @Override
        public void appendDesc(StringBuilder sb) {
            sb.append("?\n");
        }
    }
}
