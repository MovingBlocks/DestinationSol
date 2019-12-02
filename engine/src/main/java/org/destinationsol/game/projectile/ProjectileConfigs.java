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
package org.destinationsol.game.projectile;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import org.destinationsol.assets.json.Validator;
import org.json.JSONObject;
import org.destinationsol.assets.Assets;
import org.destinationsol.assets.audio.OggSound;
import org.destinationsol.assets.audio.OggSoundManager;
import org.destinationsol.assets.json.Json;
import org.destinationsol.common.SolMath;
import org.destinationsol.game.DmgType;
import org.destinationsol.game.GameColors;
import org.destinationsol.game.particle.EffectConfig;
import org.destinationsol.game.particle.EffectTypes;
import org.terasology.assets.ResourceUrn;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ProjectileConfigs {

    private final Map<String, ProjectileConfig> configs;

    public ProjectileConfigs(OggSoundManager soundManager, EffectTypes effectTypes, GameColors cols) {
        configs = new HashMap<>();

        Set<ResourceUrn> projectileConfigurationFiles = Assets.getAssetHelper().list(Json.class, "[a-zA-Z]*:projectilesConfig");

        for (ResourceUrn configUrn : projectileConfigurationFiles) {
            JSONObject rootNode = Validator.getValidatedJSON(configUrn.toString(), "engine:schemaProjectileConfig");

            for (String s : rootNode.keySet()) {
                if (!(rootNode.get(s) instanceof JSONObject))
                    continue;
                JSONObject node = rootNode.getJSONObject(s);
                String name = s;
                String texName = node.getString("tex");
                TextureAtlas.AtlasRegion tex = Assets.getAtlasRegion(texName + "Projectile");
                float texSz = (float) node.getDouble("texSz");
                float speed = (float) node.getDouble("speed");
                float physSize = (float) node.optDouble("physSize", 0);
                boolean stretch = node.optBoolean("stretch", false);
                DmgType dmgType = DmgType.forName(node.getString("dmgType"));
                String collisionSoundUrn = node.optString("collisionSound", "");
                OggSound collisionSound = collisionSoundUrn.isEmpty() ? null : soundManager.getSound(collisionSoundUrn);
                float lightSz = (float) node.optDouble("lightSz", 0);
                EffectConfig trailEffect = EffectConfig.load(node.has("trailEffect") ? node.getJSONObject("trailEffect") : null, effectTypes, cols);
                EffectConfig bodyEffect = EffectConfig.load(node.has("bodyEffect") ? node.getJSONObject("bodyEffect") : null, effectTypes, cols);
                EffectConfig collisionEffect = EffectConfig.load(node.has("collisionEffect") ? node.getJSONObject("collisionEffect") : null, effectTypes, cols);
                EffectConfig collisionEffectBackground = EffectConfig.load(node.has("collisionEffectBg") ? node.getJSONObject("collisionEffectBg") : null, effectTypes, cols);
                float guideRotationSpeed = (float) node.optDouble("guideRotationSpeed", 0);
                boolean zeroAbsSpeed = node.optBoolean("zeroAbsSpd", false);
                Vector2 origin = SolMath.readV2(node.optString("texOrig", "0 0"));
                float acc = (float) node.optDouble("acceleration", 0);
                String workSoundUrn = node.optString("workSound", "");
                OggSound workSound = workSoundUrn.isEmpty() ? null : soundManager.getSound(workSoundUrn);
                boolean bodyless = node.optBoolean("massless", false);
                float density = (float) node.optDouble("density", -1);
                float dmg = (float) node.getDouble("dmg");
                float emTime = (float) node.optDouble("emTime", 0);
                ProjectileConfig config = new ProjectileConfig(tex, texSz, speed, stretch, physSize, dmgType,
                        collisionSound, lightSz, trailEffect, bodyEffect, collisionEffect, collisionEffectBackground,
                        zeroAbsSpeed, origin, acc, workSound, bodyless, density, guideRotationSpeed, dmg, emTime);
                configs.put(name, config);
            }
        }
    }

    public ProjectileConfig find(String name) {
        return configs.get(name);
    }
}
