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
import org.destinationsol.assets.audio.PlayableSound;
import org.destinationsol.game.DmgType;
import org.destinationsol.game.particle.EffectConfig;

public class ProjectileConfig {

    public final TextureAtlas.AtlasRegion tex;
    public final float texSz;
    public final float speed;
    public final float physSize;
    public final boolean stretch;
    public final DmgType dmgType;
    public final PlayableSound collisionSound;
    public final float lightSz;
    public final EffectConfig trailEffect;
    public final EffectConfig bodyEffect;
    public final EffectConfig collisionEffect;
    public final EffectConfig collisionEffectBackground;
    public final boolean zeroAbsSpeed;
    public final Vector2 origin;
    public final float acc;
    public final PlayableSound workSound;
    public final boolean massless;
    public final float density;
    public final float guideRotationSpeed;
    public final float dmg;
    public final float emTime;

    public ProjectileConfig(TextureAtlas.AtlasRegion tex, float texSz, float speed, boolean stretch,
                            float physSize, DmgType dmgType, PlayableSound collisionSound, float lightSz, EffectConfig trailEffect,
                            EffectConfig bodyEffect, EffectConfig collisionEffect, EffectConfig collisionEffectBackground,
                            boolean zeroAbsSpeed, Vector2 origin, float acc, PlayableSound workSound, boolean massless, float density,
                            float guideRotationSpeed, float dmg, float emTime) {
        this.tex = tex;
        this.texSz = texSz;
        this.speed = speed;
        this.stretch = stretch;
        this.physSize = physSize;
        this.dmgType = dmgType;
        this.collisionSound = collisionSound;
        this.lightSz = lightSz;
        this.trailEffect = trailEffect;
        this.bodyEffect = bodyEffect;
        this.collisionEffect = collisionEffect;
        this.collisionEffectBackground = collisionEffectBackground;
        this.zeroAbsSpeed = zeroAbsSpeed;
        this.origin = origin;
        this.acc = acc;
        this.workSound = workSound;
        this.massless = massless;
        this.density = density;
        this.guideRotationSpeed = guideRotationSpeed;
        this.dmg = dmg;
        this.emTime = emTime;
        if (physSize == 0 && massless) {
            throw new AssertionError("only projectiles with physSize > 0 can be massless");
        }
        if (density > 0 && (physSize == 0 || massless)) {
            throw new AssertionError("density on a massless projectile");
        }
    }

}
