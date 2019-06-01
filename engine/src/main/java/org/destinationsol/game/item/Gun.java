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
import org.destinationsol.assets.json.Validator;
import org.json.JSONObject;
import org.destinationsol.assets.Assets;
import org.destinationsol.assets.audio.OggSoundManager;
import org.destinationsol.assets.audio.OggSoundSet;
import org.destinationsol.assets.audio.PlayableSound;
import org.destinationsol.assets.json.Json;
import org.destinationsol.common.SolMath;
import org.destinationsol.game.DmgType;
import org.destinationsol.game.HardnessCalc;
import org.destinationsol.game.SolGame;
import org.destinationsol.game.projectile.ProjectileConfig;

import java.util.Arrays;
import java.util.List;

public class Gun implements SolItem {

    public final Config config;
    public int ammo;
    public float reloadAwait;
    private int myEquipped;

    public Gun(Config config, int ammo, float reloadAwait) {
        this.config = config;
        this.ammo = ammo;
        this.reloadAwait = reloadAwait;
    }

    public Gun(Config config, int ammo, float reloadAwait, int equipped) {
        this(config, ammo, reloadAwait);
        this.myEquipped = equipped;
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
    public Gun copy() {
        return new Gun(config, ammo, reloadAwait, myEquipped);
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

    public boolean canShoot() {
        return ammo > 0 || reloadAwait > 0;
    }

    public int isEquipped() {
        return myEquipped;
    }

    public void setEquipped(int equipped) {
        myEquipped = equipped;
    }

    public static class Config {
        public final float minAngleVar;
        public final float maxAngleVar;
        public final float angleVarDamp;
        public final float angleVarPerShot;
        public final float timeBetweenShots;
        public final float reloadTime;
        public final float gunLength;
        public final String displayName;
        public final TextureAtlas.AtlasRegion tex;
        public final boolean lightOnShot;
        public final int price;
        public final String desc;
        public final float dps;
        public final Gun example;
        public final Clip.Config clipConf;
        public final PlayableSound shootSound;
        public final PlayableSound reloadSound;
        public final TextureAtlas.AtlasRegion icon;
        public final boolean fixed;
        public final float meanDps;
        public final SolItemType itemType;
        public final float texLenPercentage;
        public final String code;

        public Config(float minAngleVar, float maxAngleVar, float angleVarDamp, float angleVarPerShot,
                      float timeBetweenShots,
                      float reloadTime, float gunLength, String displayName,
                      boolean lightOnShot, int price,
                      Clip.Config clipConf, PlayableSound shootSound, PlayableSound reloadSound, TextureAtlas.AtlasRegion tex,
                      TextureAtlas.AtlasRegion icon, boolean fixed, SolItemType itemType, float texLenPercentage, String code) {
            this.shootSound = shootSound;
            this.reloadSound = reloadSound;

            this.tex = tex;

            this.maxAngleVar = maxAngleVar;
            this.minAngleVar = minAngleVar;
            this.angleVarDamp = angleVarDamp;
            this.angleVarPerShot = angleVarPerShot;
            this.timeBetweenShots = timeBetweenShots;
            this.reloadTime = reloadTime;
            this.gunLength = gunLength;
            this.displayName = displayName;
            this.lightOnShot = lightOnShot;
            this.price = price;
            this.clipConf = clipConf;
            this.icon = icon;
            this.fixed = fixed;
            this.itemType = itemType;
            this.texLenPercentage = texLenPercentage;
            this.code = code;

            dps = HardnessCalc.getShotDps(this, clipConf.projConfig.dmg);
            meanDps = HardnessCalc.getGunMeanDps(this);
            this.desc = makeDesc();
            example = new Gun(this, 0, 0);
        }

        public static void load(String gunName, ItemManager itemManager, OggSoundManager soundManager, SolItemTypes types) {
            JSONObject rootNode = Validator.getValidatedJSON(gunName, "engine:schemaGun");

            float minAngleVar = (float) rootNode.optDouble("minAngleVar", 0);
            float maxAngleVar = (float) rootNode.getDouble("maxAngleVar");
            float angleVarDamp = (float) rootNode.getDouble("angleVarDamp");
            float angleVarPerShot = (float) rootNode.getDouble("angleVarPerShot");
            float timeBetweenShots = (float) rootNode.getDouble("timeBetweenShots");
            float reloadTime = (float) rootNode.getDouble("reloadTime");
            float gunLength = (float) rootNode.getDouble("gunLength");
            float texLenPercentage = (float) rootNode.optDouble("texLenPerc", 1);
            String displayName = rootNode.getString("displayName");
            boolean lightOnShot = rootNode.optBoolean("lightOnShot", false);
            int price = rootNode.getInt("price");
            String clipName = rootNode.getString("clipName");
            List<String> reloadSoundUrns = Assets.convertToStringList(rootNode.getJSONArray("reloadSounds"));
            OggSoundSet reloadSoundSet = new OggSoundSet(soundManager, reloadSoundUrns, 1.0f);
            List<String> shootSoundUrns = Assets.convertToStringList(rootNode.getJSONArray("shootSounds"));
            float shootPitch = (float) rootNode.optDouble("shootSoundPitch", 1);
            OggSoundSet shootSoundSet = new OggSoundSet(soundManager, shootSoundUrns, shootPitch);
            boolean fixed = rootNode.optBoolean("fixed", false);
            SolItemType itemType = fixed ? types.fixedGun : types.gun;

            Clip.Config clipConf = null;
            if (!clipName.isEmpty()) {
                Clip clip = ((Clip) itemManager.getExample(clipName));
                if (clip == null) {
                    Clip.Config.load(clipName, itemManager, types);
                    clip = ((Clip) itemManager.getExample(clipName));
                }
                clipConf = clip.getConfig();
            }

            TextureAtlas.AtlasRegion tex = Assets.getAtlasRegion(gunName);
            TextureAtlas.AtlasRegion icon = Assets.getAtlasRegion(gunName + "Icon");

            Config gunConfig = new Config(minAngleVar, maxAngleVar, angleVarDamp, angleVarPerShot, timeBetweenShots,
                    reloadTime, gunLength, displayName, lightOnShot, price, clipConf, shootSoundSet,
                    reloadSoundSet, tex, icon, fixed, itemType, texLenPercentage, gunName);
            itemManager.registerItem(gunConfig.example);
        }

        private String makeDesc() {
            StringBuilder sb = new StringBuilder();
            ProjectileConfig pc = clipConf.projConfig;
            sb.append(fixed ? "Heavy gun (no rotation)\n" : "Light gun (auto rotation)\n");
            if (pc.dmg > 0) {
                sb.append("Dmg: ").append(SolMath.nice(dps)).append("/s\n");
                DmgType dmgType = pc.dmgType;
                if (dmgType == DmgType.ENERGY) {
                    sb.append("Weak against armor\n");
                } else if (dmgType == DmgType.BULLET) {
                    sb.append("Weak against shields\n");
                }
            } else if (pc.emTime > 0) {
                sb.append("Disables enemy ships for ").append(SolMath.nice(pc.emTime)).append(" s\n");
            }
            if (pc.density > 0) {
                sb.append("Knocks enemies back\n");
            }
            sb.append("Reload: ").append(SolMath.nice(reloadTime)).append(" s\n");
            if (clipConf.infinite) {
                sb.append("Infinite ammo\n");
            } else {
                sb.append("Uses ").append(clipConf.plural).append("\n");
            }
            return sb.toString();
        }
    }
}
