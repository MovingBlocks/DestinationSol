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
package org.destinationsol.game.item;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import org.destinationsol.assets.json.Validator;
import org.json.JSONObject;
import org.destinationsol.assets.Assets;
import org.destinationsol.assets.audio.OggSound;
import org.destinationsol.assets.audio.OggSoundManager;
import org.destinationsol.assets.audio.PlayableSound;
import org.destinationsol.assets.json.Json;
import org.destinationsol.common.SolMath;
import org.destinationsol.game.DmgType;
import org.destinationsol.game.SolGame;
import org.destinationsol.game.SolObject;
import org.destinationsol.game.ship.SolShip;

public class Shield implements SolItem {
    public static final float SIZE_PERC = .7f;
    private final Config config;
    private float myLife;
    private float myIdleTime;
    private int myEquipped;

    private Shield(Config config) {
        this.config = config;
        myLife = config.maxLife;
        myIdleTime = config.idleTime;
    }

    private Shield(Config config, int equipped) {
        this(config);
        myEquipped = equipped;
    }

    public void update(SolGame game, SolObject owner) {
        float ts = game.getTimeStep();
        if (myIdleTime >= config.idleTime) {
            if (myLife < config.maxLife) {
                float regen = config.regenSpeed * ts;
                myLife = SolMath.approach(myLife, config.maxLife, regen);
            }
        } else {
            myIdleTime += ts;
            if (myIdleTime >= config.idleTime) {
                game.getSoundManager().play(game, config.regenSound, null, owner);
            }
        }
    }

    @Override
    public String getDisplayName() {
        return config.displayName;
    }

    @Override
    public float getPrice() {
        return config.price;
    }

    @Override
    public String getDescription() {
        return config.desc;
    }

    @Override
    public SolItem copy() {
        return new Shield(config, myEquipped);
    }

    @Override
    public boolean isSame(SolItem item) {
        return false;
    }

    @Override
    public TextureAtlas.AtlasRegion getIcon(SolGame game) {
        return config.icon;
    }

    @Override
    public SolItemType getItemType() {
        return config.itemType;
    }

    @Override
    public String getCode() {
        return config.code;
    }

    public float getLife() {
        return myLife;
    }

    public float getMaxLife() {
        return config.maxLife;
    }

    public boolean canAbsorb(DmgType dmgType) {
        return myLife > 0 && dmgType != DmgType.FIRE && dmgType != DmgType.CRASH;
    }

    public void absorb(SolGame game, float dmg, Vector2 position, SolShip ship, DmgType dmgType) {
        if (!canAbsorb(dmgType) || dmg <= 0) {
            throw new AssertionError("illegal call to absorb");
        }
        myIdleTime = 0f;
        if (dmgType == DmgType.BULLET) {
            dmg *= config.bulletDmgFactor;
        } else if (dmgType == DmgType.ENERGY) {
            dmg *= config.energyDmgFactor;
        } else if (dmgType == DmgType.EXPLOSION) {
            dmg *= config.explosionDmgFactor;
        }
        myLife -= myLife < dmg ? myLife : dmg;

        game.getPartMan().shieldSpark(game, position, ship.getHull(), config.tex, dmg / config.maxLife);
        float volMul = SolMath.clamp(4 * dmg / config.maxLife);
        game.getSoundManager().play(game, config.absorbSound, null, ship, volMul);

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
        public final float regenSpeed;
        public final float bulletDmgFactor;
        public final float energyDmgFactor;
        public final float explosionDmgFactor;
        public final TextureAtlas.AtlasRegion icon;
        public final SolItemType itemType;
        public final String code;
        public TextureAtlas.AtlasRegion tex;

        private Config(int maxLife, float idleTime, float regenSpeed, float bulletDmgFactor, float energyDmgFactor, float explosionDmgFactor, String displayName, int price,
                PlayableSound absorbSound, PlayableSound regenSound,
                TextureAtlas.AtlasRegion icon, TextureAtlas.AtlasRegion tex, SolItemType itemType, String code) {
            this.maxLife = maxLife;
            this.idleTime = idleTime;
            this.regenSpeed = regenSpeed;
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
            JSONObject rootNode = Validator.getValidatedJSON(shieldName, "engine:schemaShield");

            int maxLife = rootNode.getInt("maxLife");
            float idleTime = (float) rootNode.getDouble("idleTime");
            float regenSpeed = (float) rootNode.getDouble("regenSpd");
            float bulletDmgFactor = (float) rootNode.getDouble("bulletDmgFactor");
            float energyDmgFactor = (float) rootNode.getDouble("energyDmgFactor");
            float explosionDmgFactor = (float) rootNode.getDouble("explosionDmgFactor");
            String displayName = rootNode.getString("displayName");
            int price = rootNode.getInt("price");
            String absorbUrn = rootNode.getString("absorbSound");
            float absorbPitch = (float) rootNode.optDouble("absorbSoundPitch", 1);
            OggSound absorbSound = soundManager.getSound(absorbUrn, absorbPitch);
            String regenUrn = rootNode.getString("regenSound");
            OggSound regenSound = soundManager.getSound(regenUrn);

            TextureAtlas.AtlasRegion tex = Assets.getAtlasRegion(shieldName);
            TextureAtlas.AtlasRegion icon = Assets.getAtlasRegion(shieldName + "Icon");

            Config config = new Config(maxLife, idleTime, regenSpeed, bulletDmgFactor, energyDmgFactor, explosionDmgFactor, displayName, price, absorbSound, regenSound, icon, tex,
                    types.shield, shieldName);
            itemManager.registerItem(config.example);
        }

        private String makeDesc() {
            StringBuilder sb = new StringBuilder();
            sb.append("Takes ").append(SolMath.nice(maxLife)).append(" dmg\n");
            sb.append("Needs ").append(SolMath.nice(idleTime)).append("s to start regeneration\n");
            sb.append("Regenerates ").append(SolMath.nice(regenSpeed)).append(" shield points per s\n");
            sb.append("Bullet Dmg resist: ").append(100 - (bulletDmgFactor * 100)).append("%\n");
            sb.append("Energy Dmg resist: ").append(100 - (energyDmgFactor * 100)).append("%\n");
            sb.append("Explosion Dmg resist: ").append(100 - (explosionDmgFactor * 100)).append("%\n");
            return sb.toString();
        }
    }
}
