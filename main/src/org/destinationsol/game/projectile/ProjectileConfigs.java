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

package org.destinationsol.game.projectile;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import org.destinationsol.TextureManager;
import org.destinationsol.assets.AssetHelper;
import org.destinationsol.assets.audio.OggSound;
import org.destinationsol.assets.json.Json;
import org.destinationsol.common.SolMath;
import org.destinationsol.files.FileManager;
import org.destinationsol.game.DmgType;
import org.destinationsol.game.GameColors;
import org.destinationsol.game.particle.EffectConfig;
import org.destinationsol.game.particle.EffectTypes;
import org.destinationsol.game.sound.OggSoundManager;
import org.terasology.assets.ResourceUrn;

import java.util.HashMap;
import java.util.Map;

public class ProjectileConfigs {

    private final Map<String, ProjectileConfig> myConfigs;

    public ProjectileConfigs(TextureManager textureManager, OggSoundManager soundManager, EffectTypes effectTypes, GameColors cols, AssetHelper assetHelper) {
        myConfigs = new HashMap<>();

        Json json = assetHelper.getJson(new ResourceUrn("Core:projectilesConfig"));
        JsonValue rootNode = json.getJsonValue();

        for (JsonValue sh : rootNode) {
            String texName = "smallGameObjects/projectiles/" + sh.getString("texName");
            TextureAtlas.AtlasRegion tex = textureManager.getTexture(texName);
            float texSz = sh.getFloat("texSz");
            float spdLen = sh.getFloat("spdLen");
            float physSize = sh.getFloat("physSize", 0);
            boolean stretch = sh.getBoolean("stretch", false);
            DmgType dmgType = DmgType.forName(sh.getString("dmgType"));
            String collisionSoundUrn = sh.getString("collisionSound", "");
            OggSound collisionSound = collisionSoundUrn.isEmpty() ? null : soundManager.getSound(collisionSoundUrn);
            float lightSz = sh.getFloat("lightSz", 0);
            EffectConfig trailEffect = EffectConfig.load(sh.get("trailEffect"), effectTypes, textureManager, cols, assetHelper);
            EffectConfig bodyEffect = EffectConfig.load(sh.get("bodyEffect"), effectTypes, textureManager, cols, assetHelper);
            EffectConfig collisionEffect = EffectConfig.load(sh.get("collisionEffect"), effectTypes, textureManager, cols, assetHelper);
            EffectConfig collisionEffectBg = EffectConfig.load(sh.get("collisionEffectBg"), effectTypes, textureManager, cols, assetHelper);
            float guideRotSpd = sh.getFloat("guideRotSpd", 0);
            boolean zeroAbsSpd = sh.getBoolean("zeroAbsSpd", false);
            Vector2 origin = SolMath.readV2(sh.getString("texOrig", "0 0"));
            float acc = sh.getFloat("acceleration", 0);
            String workSoundUrn = sh.getString("workSound", "");
            OggSound workSound = workSoundUrn.isEmpty() ? null : soundManager.getSound(workSoundUrn);
            boolean bodyless = sh.getBoolean("massless", false);
            float density = sh.getFloat("density", -1);
            float dmg = sh.getFloat("dmg");
            float emTime = sh.getFloat("emTime", 0);
            ProjectileConfig c = new ProjectileConfig(tex, texSz, spdLen, stretch, physSize, dmgType, collisionSound,
                    lightSz, trailEffect, bodyEffect, collisionEffect, collisionEffectBg, zeroAbsSpd, origin, acc, workSound, bodyless, density, guideRotSpd, dmg, emTime);
            myConfigs.put(sh.name, c);
        }

        json.dispose();
    }

    public ProjectileConfig find(String name) {
        return myConfigs.get(name);
    }
}
