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
package org.destinationsol.game.particle;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import org.destinationsol.common.SolColor;
import org.destinationsol.common.SolMath;
import org.destinationsol.common.SolRandom;
import org.destinationsol.game.SolGame;
import org.destinationsol.game.drawables.Drawable;
import org.destinationsol.game.drawables.DrawableLevel;
import org.destinationsol.game.drawables.DrawableObject;
import org.destinationsol.game.drawables.RectSprite;
import org.destinationsol.game.item.Shield;
import org.destinationsol.game.ship.hulls.Hull;

import java.util.ArrayList;

public class PartMan {
    public static final float EXPL_LIGHT_MAX_SZ = .4f;
    public static final float EXPL_LIGHT_MAX_FADE_TIME = .8f;
    public static final float SZ_TO_BLINK_COUNT = 18f;

    public PartMan() {
    }

    public void finish(SolGame game, DSParticleEmitter emitter, Vector2 basePos) {
        if (emitter.isContinuous()) {
            emitter.setWorking(false);
        }
        ArrayList<Drawable> drawables = new ArrayList<>();
        drawables.addAll(emitter.getDrawables());
        DrawableObject o = new DrawableObject(drawables, new Vector2(basePos), new Vector2(), null, true, false);
        game.getObjectManager().addObjDelayed(o);
    }

    public void blinks(Vector2 position, SolGame game, float sz) {
        int count = (int) (SZ_TO_BLINK_COUNT * sz * sz);
        for (int i = 0; i < count; i++) {
            Vector2 lightPos = new Vector2();
            SolMath.fromAl(lightPos, SolRandom.randomFloat(180), SolRandom.randomFloat(0, sz / 2));
            lightPos.add(position);
            float lightSz = SolRandom.randomFloat(.5f, 1) * EXPL_LIGHT_MAX_SZ;
            float fadeTime = SolRandom.randomFloat(.5f, 1) * EXPL_LIGHT_MAX_FADE_TIME;
            LightObject light = new LightObject(game, lightSz, true, 1, lightPos, fadeTime, game.getCols().fire);
            game.getObjectManager().addObjDelayed(light);
        }
    }

    public void shieldSpark(SolGame game, Vector2 collPos, Hull hull, TextureAtlas.AtlasRegion shieldTexture, float perc) {
        if (perc <= 0) {
            return;
        }
        Vector2 position = hull.getPosition();
        float angle = SolMath.angle(position, collPos);
        float sz = hull.config.getSize() * Shield.SIZE_PERC * 2;
        float alphaSum = perc * 3;
        RectSprite s = null;
        int count = (int) alphaSum + 1;
        for (int i = 0; i < count; i++) {
            s = blip(game, position, angle, sz, .5f, hull.getSpeed(), shieldTexture);
        }
        float lastTint = SolMath.clamp(alphaSum - (int) alphaSum);
        if (s != null) {
            s.tint.a = lastTint;
            s.baseAlpha = lastTint;
        }
    }

    public RectSprite blip(SolGame game, Vector2 position, float angle, float sz, float fadeTime, Vector2 speed,
                           TextureAtlas.AtlasRegion tex) {
        RectSprite s = new RectSprite(tex, sz, 0, 0, new Vector2(), DrawableLevel.PART_FG_0, angle, 0, SolColor.WHITE, true);
        ArrayList<Drawable> drawables = new ArrayList<>();
        drawables.add(s);
        DrawableObject o = new DrawableObject(drawables, new Vector2(position), new Vector2(speed), null, false, false);
        o.fade(fadeTime);
        game.getObjectManager().addObjDelayed(o);
        return s;
    }

    /**
     * This method updates all of the particle emitters on a Hull with the specified trigger
     *
     * @param hull Hull containing the particle emitters
     * @param triggerType trigger type of the particle emitters
     * @param on boolean where true turns the particle emitters on and false turns it off
     */
    public void updateAllHullEmittersOfType(Hull hull, String triggerType, boolean on) {
        for (DSParticleEmitter particleEmitter : hull.getParticleEmitters()) {
            if (triggerType.equals(particleEmitter.getTrigger())) {
                particleEmitter.setWorking(on);
            }
        }
    }
}
