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
package org.destinationsol.game.particle;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import org.destinationsol.assets.Assets;
import org.destinationsol.common.SolColorUtil;
import org.destinationsol.common.SolMath;
import org.destinationsol.common.SolRandom;
import org.destinationsol.game.SolGame;
import org.destinationsol.game.drawables.Drawable;
import org.destinationsol.game.drawables.DrawableLevel;
import org.destinationsol.game.drawables.RectSprite;
import org.destinationsol.game.drawables.SpriteManager;

import java.util.List;

public class LightSource {
    public static final float DEFAULT_FADE_TIME = .1f;
    public static final float A_RATIO = .5f;
    public static final float SZ_RATIO = .8f;

    private static final String CIRCLE_SPRITE_NAME = "core:lightCircleParticle";
    private static final String HALO_SPRITE_NAME = "core:lightHaloParticle";
    private final RectSprite circle;
    private final RectSprite halo;
    private final float size;
    private final float intensity;
    private float workPercentage;
    private float fadeTime;

    /**
     * doesn't consume relativePosition
     */
    public LightSource(float size, boolean hasHalo, float intensity, Vector2 relativePosition, Color colour) {
        this.size = size;
        Vector2 relPos1 = new Vector2(relativePosition);
        circle = SpriteManager.createSprite(CIRCLE_SPRITE_NAME, DrawableLevel.PART_BG_0, relPos1, colour, true);
        if (hasHalo) {
            Color haloCol = new Color(colour);
            SolColorUtil.changeBrightness(haloCol, .8f);
            halo = SpriteManager.createSprite(HALO_SPRITE_NAME, DrawableLevel.PART_BG_0, relativePosition, haloCol, true);
        } else {
            halo = null;
        }
        this.intensity = intensity;
        fadeTime = DEFAULT_FADE_TIME;
    }

    public void update(boolean working, float baseAngle, SolGame game) {
        if (working) {
            workPercentage = 1f;
        } else {
            workPercentage = SolMath.approach(workPercentage, 0, game.getTimeStep() / fadeTime);
        }
        float baseA = SolRandom.randomFloat(.5f, 1) * workPercentage * intensity;
        circle.tint.a = baseA * A_RATIO;
        float sz = (1 + SolRandom.randomFloat(.2f * intensity)) * size;
        circle.setTextureSize(SZ_RATIO * sz);
        if (halo != null) {
            halo.tint.a = baseA;
            halo.relativeAngle = game.getCam().getAngle() - baseAngle;
            halo.setTextureSize(sz);
        }
    }

    public boolean isFinished() {
        return workPercentage <= 0;
    }

    public void collectDrawables(List<Drawable> drawables) {
        drawables.add(circle);
        if (halo != null) {
            drawables.add(halo);
        }
    }

    public void setFadeTime(float fadeTime) {
        this.fadeTime = fadeTime;
    }

    public void setWorking() {
        workPercentage = 1;
    }

    public void setRelativePosition(Vector2 relativePosition) {
        circle.relativePosition.set(relativePosition);
    }
}
