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
        game.getObjMan().addObjDelayed(o);
    }

    public void blinks(Vector2 pos, SolGame game, float sz) {
        int count = (int) (SZ_TO_BLINK_COUNT * sz * sz);
        for (int i = 0; i < count; i++) {
            Vector2 lightPos = new Vector2();
            SolMath.fromAl(lightPos, SolMath.randomFloat(180), SolMath.randomFloat(0, sz / 2));
            lightPos.add(pos);
            float lightSz = SolMath.randomFloat(.5f, 1) * EXPL_LIGHT_MAX_SZ;
            float fadeTime = SolMath.randomFloat(.5f, 1) * EXPL_LIGHT_MAX_FADE_TIME;
            LightObject light = new LightObject(game, lightSz, true, 1, lightPos, fadeTime, game.getCols().fire);
            game.getObjMan().addObjDelayed(light);
        }
    }

    public void shieldSpark(SolGame game, Vector2 collPos, Hull hull, TextureAtlas.AtlasRegion shieldTex, float perc) {
        if (perc <= 0) {
            return;
        }
        Vector2 pos = hull.getPos();
        float angle = SolMath.angle(pos, collPos);
        float sz = hull.config.getSize() * Shield.SIZE_PERC * 2;
        float alphaSum = perc * 3;
        RectSprite s = null;
        int count = (int) alphaSum + 1;
        for (int i = 0; i < count; i++) {
            s = blip(game, pos, angle, sz, .5f, hull.getSpd(), shieldTex);
        }
        float lastTint = SolMath.clamp(alphaSum - (int) alphaSum);
        if (s != null) {
            s.tint.a = lastTint;
            s.baseAlpha = lastTint;
        }
    }

    public RectSprite blip(SolGame game, Vector2 pos, float angle, float sz, float fadeTime, Vector2 spd,
                           TextureAtlas.AtlasRegion tex) {
        RectSprite s = new RectSprite(tex, sz, 0, 0, new Vector2(), DrawableLevel.PART_FG_0, angle, 0, SolColor.WHITE, true);
        ArrayList<Drawable> drawables = new ArrayList<>();
        drawables.add(s);
        DrawableObject o = new DrawableObject(drawables, new Vector2(pos), new Vector2(spd), null, false, false);
        o.fade(fadeTime);
        game.getObjMan().addObjDelayed(o);
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
