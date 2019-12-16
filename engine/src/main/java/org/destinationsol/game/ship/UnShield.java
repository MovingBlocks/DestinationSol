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

package org.destinationsol.game.ship;

import com.badlogic.gdx.math.Vector2;
import org.json.JSONObject;
import org.destinationsol.common.SolMath;
import org.destinationsol.game.AbilityCommonConfig;
import org.destinationsol.game.DmgType;
import org.destinationsol.game.SolGame;
import org.destinationsol.game.SolObject;
import org.destinationsol.game.drawables.DrawableLevel;
import org.destinationsol.game.item.ItemManager;
import org.destinationsol.game.item.Shield;
import org.destinationsol.game.item.SolItem;
import org.destinationsol.game.particle.DSParticleEmitter;

public class UnShield implements ShipAbility {
    private static final int MAX_RADIUS = 6;
    private final UnShieldConfig config;

    UnShield(UnShieldConfig config) {
        this.config = config;
    }

    @Override
    public AbilityConfig getConfig() {
        return config;
    }

    @Override
    public AbilityCommonConfig getCommonConfig() {
        return config.cc;
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
            Shield shield = oShip.getShield();
            if (shield == null) {
                continue;
            }
            float shieldLife = shield.getLife();
            if (shieldLife <= 0) {
                continue;
            }
            if (!game.getFactionMan().areEnemies(oShip, owner)) {
                continue;
            }
            Vector2 oPos = o.getPosition();
            float dst = oPos.dst(ownerPos);
            float perc = KnockBack.getPerc(dst, MAX_RADIUS);
            if (perc <= 0) {
                continue;
            }
            float amount = perc * config.amount;
            if (shieldLife < amount) {
                amount = shieldLife;
            }
            oShip.receiveDmg(amount, game, ownerPos, DmgType.ENERGY);
        }
        DSParticleEmitter src = new DSParticleEmitter(config.cc.effect, MAX_RADIUS, DrawableLevel.PART_BG_0, new Vector2(), true, game, ownerPos, Vector2.Zero, 0);
        game.getPartMan().finish(game, src, ownerPos);
        return true;
    }

    public static class UnShieldConfig implements AbilityConfig {
        public final float rechargeTime;
        public final float amount;
        private final SolItem chargeExample;
        private final AbilityCommonConfig cc;

        public UnShieldConfig(float rechargeTime, SolItem chargeExample, float amount, AbilityCommonConfig cc) {
            this.rechargeTime = rechargeTime;
            this.chargeExample = chargeExample;
            this.amount = amount;
            this.cc = cc;
        }

        public static AbilityConfig load(JSONObject abNode, ItemManager itemManager, AbilityCommonConfig cc) {
            float rechargeTime = (float) abNode.getDouble("rechargeTime");
            float amount = (float) abNode.getDouble("amount");
            SolItem chargeExample = itemManager.getExample("unShieldCharge");
            return new UnShieldConfig(rechargeTime, chargeExample, amount, cc);
        }

        @Override
        public ShipAbility build() {
            return new UnShield(this);
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
            sb.append("Deal ").append(SolMath.nice(amount)).append(" dmg to enemy shields\n");
        }
    }
}
