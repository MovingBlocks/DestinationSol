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
package org.destinationsol.game.projectile;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.destinationsol.assets.Assets;
import org.destinationsol.assets.audio.OggSound;
import org.destinationsol.assets.json.Json;
import org.destinationsol.common.SolMath;
import org.destinationsol.game.DmgType;
import org.destinationsol.game.GameColors;
import org.destinationsol.game.particle.EffectConfig;
import org.destinationsol.game.particle.EffectTypes;
import org.destinationsol.game.sound.OggSoundManager;
import org.terasology.assets.ResourceUrn;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.JsonValue;

public class ProjectileConfigs {

    private final Map<String, ProjectileConfig> myConfigs;

    public ProjectileConfigs(OggSoundManager soundManager, EffectTypes effectTypes, GameColors cols) {
        myConfigs = new HashMap<>();

        Set<ResourceUrn> projectileConfigurationFiles = Assets.getAssetHelper().list(Json.class, "[a-z]*:projectilesConfig");

        for (ResourceUrn configUrn : projectileConfigurationFiles) {
            Json json = Assets.getJson(configUrn.toString());
            JsonValue rootNode = json.getJsonValue();

            for (JsonValue node : rootNode) {
                String texName = node.getString("tex");
                TextureAtlas.AtlasRegion tex = Assets.getAtlasRegion(configUrn.getModuleName() + ":" + texName + "Projectile");
                float texSz = node.getFloat("texSz");
                float spdLen = node.getFloat("spdLen");
                float physSize = node.getFloat("physSize", 0);
                boolean stretch = node.getBoolean("stretch", false);
                DmgType dmgType = DmgType.forName(node.getString("dmgType"));
                String collisionSoundUrn = node.getString("collisionSound", "");
                OggSound collisionSound = collisionSoundUrn.isEmpty() ? null : soundManager.getSound(collisionSoundUrn);
                float lightSz = node.getFloat("lightSz", 0);
                EffectConfig trailEffect = EffectConfig.load(node.get("trailEffect"), effectTypes, cols);
                EffectConfig bodyEffect = EffectConfig.load(node.get("bodyEffect"), effectTypes, cols);
                EffectConfig collisionEffect = EffectConfig.load(node.get("collisionEffect"), effectTypes, cols);
                EffectConfig collisionEffectBg = EffectConfig.load(node.get("collisionEffectBg"), effectTypes, cols);
                float guideRotSpd = node.getFloat("guideRotSpd", 0);
                boolean zeroAbsSpd = node.getBoolean("zeroAbsSpd", false);
                Vector2 origin = SolMath.readV2(node.getString("texOrig", "0 0"));
                float acc = node.getFloat("acceleration", 0);
                String workSoundUrn = node.getString("workSound", "");
                OggSound workSound = workSoundUrn.isEmpty() ? null : soundManager.getSound(workSoundUrn);
                boolean bodyless = node.getBoolean("massless", false);
                float density = node.getFloat("density", -1);
                float dmg = node.getFloat("dmg");
                float emTime = node.getFloat("emTime", 0);
                ProjectileConfig config = new ProjectileConfig(tex, texSz, spdLen, stretch, physSize, dmgType,
                        collisionSound, lightSz, trailEffect, bodyEffect, collisionEffect, collisionEffectBg,
                        zeroAbsSpd, origin, acc, workSound, bodyless, density, guideRotSpd, dmg, emTime);
                myConfigs.put(node.name, config);
            }

            json.dispose();
        }
    }

    public ProjectileConfig find(String name) {
        return myConfigs.get(name);
    }
}
