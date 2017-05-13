/*
 * Copyright 2015 MovingBlocks
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

package org.destinationsol.game.gun;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import org.destinationsol.TextureManager;
import org.destinationsol.assets.audio.PlayableSound;
import org.destinationsol.common.SolMath;
import org.destinationsol.files.FileManager;
import org.destinationsol.game.DmgType;
import org.destinationsol.game.HardnessCalc;
import org.destinationsol.game.item.ClipConfig;
import org.destinationsol.game.item.ClipItem;
import org.destinationsol.game.item.ItemManager;
import org.destinationsol.game.item.SolItemType;
import org.destinationsol.game.item.SolItemTypes;
import org.destinationsol.game.projectile.ProjectileConfig;
import org.destinationsol.game.sound.OggSoundManager;
import org.destinationsol.game.sound.OggSoundSet;

import java.util.Arrays;
import java.util.List;

public class GunConfig {
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
    public final GunItem example;
    public final ClipConfig clipConf;
    public final PlayableSound shootSound;
    public final PlayableSound reloadSound;
    public final TextureAtlas.AtlasRegion icon;
    public final boolean fixed;
    public final float meanDps;
    public final SolItemType itemType;
    public final float texLenPerc;
    public final String code;

    public GunConfig(float minAngleVar, float maxAngleVar, float angleVarDamp, float angleVarPerShot,
                     float timeBetweenShots,
                     float reloadTime, float gunLength, String displayName,
                     boolean lightOnShot, int price,
                     ClipConfig clipConf, PlayableSound shootSound, PlayableSound reloadSound, TextureAtlas.AtlasRegion tex,
                     TextureAtlas.AtlasRegion icon, boolean fixed, SolItemType itemType, float texLenPerc, String code) {
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
        this.texLenPerc = texLenPerc;
        this.code = code;

        dps = HardnessCalc.getShotDps(this, clipConf.projConfig.dmg);
        meanDps = HardnessCalc.getGunMeanDps(this);
        this.desc = makeDesc();
        example = new GunItem(this, 0, 0);
    }

    public static void load(TextureManager textureManager, ItemManager itemManager, OggSoundManager soundManager, SolItemTypes types) {
        JsonReader r = new JsonReader();
        FileHandle configFile = FileManager.getInstance().getItemsDirectory().child("guns.json");
        JsonValue parsed = r.parse(configFile);
        for (JsonValue sh : parsed) {
            float minAngleVar = sh.getFloat("minAngleVar", 0);
            float maxAngleVar = sh.getFloat("maxAngleVar");
            float angleVarDamp = sh.getFloat("angleVarDamp");
            float angleVarPerShot = sh.getFloat("angleVarPerShot");
            float timeBetweenShots = sh.getFloat("timeBetweenShots");
            float reloadTime = sh.getFloat("reloadTime");
            float gunLength = sh.getFloat("gunLength");
            float texLenPerc = sh.getFloat("texLenPerc", 1);
            String texName = sh.getString("texName");
            String displayName = sh.getString("displayName");
            boolean lightOnShot = sh.getBoolean("lightOnShot", false);
            int price = sh.getInt("price");
            String clipName = sh.getString("clipName");
            ClipConfig clipConf = clipName.isEmpty() ? null : ((ClipItem) itemManager.getExample(clipName)).getConfig();
            List<String> reloadSoundUrns = Arrays.asList(sh.get("reloadSounds").asStringArray());
            OggSoundSet reloadSoundSet = new OggSoundSet(soundManager, reloadSoundUrns, 1.0f);
            List<String> shootSoundUrns = Arrays.asList(sh.get("shootSounds").asStringArray());
            float shootPitch = sh.getFloat("shootSoundPitch", 1);
            OggSoundSet shootSoundSet = new OggSoundSet(soundManager, shootSoundUrns, shootPitch);
            TextureAtlas.AtlasRegion tex = textureManager.getTexture("smallGameObjects/guns/" + texName);
            TextureAtlas.AtlasRegion icon = textureManager.getTexture(TextureManager.ICONS_DIR + texName);
            boolean fixed = sh.getBoolean("fixed", false);
            String code = sh.name;
            SolItemType itemType = fixed ? types.fixedGun : types.gun;
            GunConfig c = new GunConfig(minAngleVar, maxAngleVar, angleVarDamp, angleVarPerShot, timeBetweenShots, reloadTime,
                    gunLength, displayName, lightOnShot, price, clipConf, shootSoundSet, reloadSoundSet, tex, icon, fixed, itemType, texLenPerc, code);
            itemManager.registerItem(c.example);
        }
    }

    private String makeDesc() {
        StringBuilder sb = new StringBuilder();
        ProjectileConfig pc = clipConf.projConfig;
        sb.append(fixed ? "Heavy gun (no rotation)\n" : "Light gun (auto rotation)\n");
        if (pc.dmg > 0) {
            sb.append("Dmg: ").append(SolMath.nice(dps)).append("/s\n");
            DmgType dmgType = pc.dmgType;
            if (dmgType == DmgType.ENERGY) sb.append("Weak against armor\n");
            else if (dmgType == DmgType.BULLET) sb.append("Weak against shields\n");
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
