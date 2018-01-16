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
package org.destinationsol.game.item;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.JsonValue;
import org.destinationsol.assets.Assets;
import org.destinationsol.assets.audio.OggSound;
import org.destinationsol.assets.audio.PlayableSound;
import org.destinationsol.assets.json.Json;
import org.destinationsol.common.SolMath;
import org.destinationsol.game.DmgType;
import org.destinationsol.game.SolGame;
import org.destinationsol.game.SolObject;
import org.destinationsol.game.ship.SolShip;
import org.destinationsol.game.sound.OggSoundManager;

public class Shield implements SolItem {
    public static final float SIZE_PERC = .7f;
    private final Config myConfig;
    private float myLife;
    private float myIdleTime;
    private int myEquipped;

    private Shield(Config config) {
        myConfig = config;
        myLife = myConfig.maxLife;
        myIdleTime = myConfig.idleTime;
    }

    private Shield(Config config, int equipped) {
        this(config);
        myEquipped = equipped;
    }

    public void update(SolGame game, SolObject owner) {
        float ts = game.getTimeStep();
        if (myIdleTime >= myConfig.idleTime) {
            if (myLife < myConfig.maxLife) {
                float regen = myConfig.regenSpd * ts;
                myLife = SolMath.approach(myLife, myConfig.maxLife, regen);
            }
        } else {
            myIdleTime += ts;
            if (myIdleTime >= myConfig.idleTime) {
                game.getSoundManager().play(game, myConfig.regenSound, null, owner);
            }
        }
    }

    @Override
    public String getDisplayName() {
        return myConfig.displayName;
    }

    @Override
    public float getPrice() {
        return myConfig.price;
    }

    @Override
    public String getDesc() {
        return myConfig.desc;
    }

    @Override
    public SolItem copy() {
        return new Shield(myConfig, myEquipped);
    }

    @Override
    public boolean isSame(SolItem item) {
        return false;
    }

    @Override
    public TextureAtlas.AtlasRegion getIcon(SolGame game) {
        return myConfig.icon;
    }

    @Override
    public SolItemType getItemType() {
        return myConfig.itemType;
    }

    @Override
    public String getCode() {
        return myConfig.code;
    }

    public float getLife() {
        return myLife;
    }

    public float getMaxLife() {
        return myConfig.maxLife;
    }

    public boolean canAbsorb(DmgType dmgType) {
        return myLife > 0 && dmgType != DmgType.FIRE && dmgType != DmgType.CRASH;
    }

    public void absorb(SolGame game, float dmg, Vector2 pos, SolShip ship, DmgType dmgType) {
        if (!canAbsorb(dmgType) || dmg <= 0) {
            throw new AssertionError("illegal call to absorb");
        }
        myIdleTime = 0f;
        if (dmgType == DmgType.BULLET) {
            dmg *= myConfig.bulletDmgFactor;
        } else if (dmgType == DmgType.ENERGY) {
            dmg *= myConfig.energyDmgFactor;
        } else if (dmgType == DmgType.EXPLOSION) {
            dmg *= myConfig.explosionDmgFactor;
        }
        myLife -= myLife < dmg ? myLife : dmg;

        game.getPartMan().shieldSpark(game, pos, ship.getHull(), myConfig.tex, dmg / myConfig.maxLife);
        float volMul = SolMath.clamp(4 * dmg / myConfig.maxLife);
        game.getSoundManager().play(game, myConfig.absorbSound, null, ship, volMul);

    }

    public int isEquipped() {
        return myEquipped;
    }

    public void setEquipped(int equipped) {
        myEquipped = equipped;
    }

    public static class Config {
        public final String displayName;
        public final int price;
        public final String desc;
        public final PlayableSound absorbSound;
        public final PlayableSound regenSound;
        public final Shield example;
        public final float maxLife;
        public final float idleTime;
        public final float regenSpd;
        public final float bulletDmgFactor;
        public final float energyDmgFactor;
        public final float explosionDmgFactor;
        public final TextureAtlas.AtlasRegion icon;
        public final SolItemType itemType;
        public final String code;
        public TextureAtlas.AtlasRegion tex;

        private Config(int maxLife, float idleTime, float regenSpd, float bulletDmgFactor, float energyDmgFactor, float explosionDmgFactor, String displayName, int price,
                PlayableSound absorbSound, PlayableSound regenSound,
                TextureAtlas.AtlasRegion icon, TextureAtlas.AtlasRegion tex, SolItemType itemType, String code) {
            this.maxLife = maxLife;
            this.idleTime = idleTime;
            this.regenSpd = regenSpd;
            this.bulletDmgFactor = bulletDmgFactor;
            this.energyDmgFactor = energyDmgFactor;
            this.explosionDmgFactor = explosionDmgFactor;
            this.displayName = displayName;
            this.price = price;
            this.absorbSound = absorbSound;
            this.regenSound = regenSound;
            this.icon = icon;
            this.tex = tex;
            this.itemType = itemType;
            this.code = code;
            example = new Shield(this);
            this.desc = makeDesc();
        }

        public static void load(String shieldName, ItemManager itemManager, OggSoundManager soundManager, SolItemTypes types) {
            Json json = Assets.getJson(shieldName);
            JsonValue rootNode = json.getJsonValue();

            int maxLife = rootNode.getInt("maxLife");
            float idleTime = rootNode.getFloat("idleTime");
            float regenSpd = rootNode.getFloat("regenSpd");
            float bulletDmgFactor = rootNode.getFloat("bulletDmgFactor");
            float energyDmgFactor = rootNode.getFloat("energyDmgFactor");
            float explosionDmgFactor = rootNode.getFloat("explosionDmgFactor");
            String displayName = rootNode.getString("displayName");
            int price = rootNode.getInt("price");
            String absorbUrn = rootNode.getString("absorbSound");
            float absorbPitch = rootNode.getFloat("absorbSoundPitch", 1);
            OggSound absorbSound = soundManager.getSound(absorbUrn, absorbPitch);
            String regenUrn = rootNode.getString("regenSound");
            OggSound regenSound = soundManager.getSound(regenUrn);

            json.dispose();

            TextureAtlas.AtlasRegion tex = Assets.getAtlasRegion(shieldName);
            TextureAtlas.AtlasRegion icon = Assets.getAtlasRegion(shieldName + "Icon");

            Config config = new Config(maxLife, idleTime, regenSpd, bulletDmgFactor, energyDmgFactor, explosionDmgFactor, displayName, price, absorbSound, regenSound, icon, tex,
                    types.shield, shieldName);
            itemManager.registerItem(config.example);
        }

        private String makeDesc() {
            StringBuilder sb = new StringBuilder();
            sb.append("Takes ").append(SolMath.nice(maxLife)).append(" dmg\n");
            sb.append("Needs ").append(SolMath.nice(idleTime)).append("s to start regeneration\n");
            sb.append("Regenerates ").append(SolMath.nice(regenSpd)).append(" shield points per s\n");
            sb.append("Bullet Dmg resist: ").append(100 - (bulletDmgFactor * 100)).append("%\n");
            sb.append("Energy Dmg resist: ").append(100 - (energyDmgFactor * 100)).append("%\n");
            sb.append("Explosion Dmg resist: ").append(100 - (explosionDmgFactor * 100)).append("%\n");
            return sb.toString();
        }
    }
}
