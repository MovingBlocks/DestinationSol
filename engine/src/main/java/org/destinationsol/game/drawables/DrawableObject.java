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
package org.destinationsol.game.drawables;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import org.destinationsol.Const;
import org.destinationsol.common.Consumed;
import org.destinationsol.common.SolMath;
import org.destinationsol.game.DmgType;
import org.destinationsol.game.FarObj;
import org.destinationsol.game.RemoveController;
import org.destinationsol.game.SolGame;
import org.destinationsol.game.SolObject;
import org.destinationsol.game.planet.Planet;

import java.util.List;

public class DrawableObject implements SolObject {
    private final Vector2 myPos;
    private final Vector2 mySpd;
    private final RemoveController myRemoveController;
    private final boolean myHideOnPlanet;
    private final Vector2 myMoveDiff;
    private final List<Drawable> myDrawables;
    private final boolean myTemporary;

    private float myMaxFadeTime;
    private float myFadeTime;

    public DrawableObject(List<Drawable> drawables, @Consumed Vector2 pos, @Consumed Vector2 spd, RemoveController removeController, boolean temporary, boolean hideOnPlanet) {
        myDrawables = drawables;
        myPos = pos;
        mySpd = spd;
        myRemoveController = removeController;
        myHideOnPlanet = hideOnPlanet;
        myMoveDiff = new Vector2();
        myTemporary = temporary;

        myMaxFadeTime = -1;
        myFadeTime = -1;
    }

    @Override
    public void update(SolGame game) {
        myMoveDiff.set(mySpd);
        float ts = game.getTimeStep();
        myMoveDiff.scl(ts);
        myPos.add(myMoveDiff);
        if (myHideOnPlanet) {
            Planet np = game.getPlanetMan().getNearestPlanet();
            Vector2 npPos = np.getPos();
            float npgh = np.getGroundHeight();
            DrawableManager drawableManager = game.getDrawableManager();
            for (Drawable drawable : myDrawables) {
                if (!(drawable instanceof RectSprite)) {
                    continue;
                }
                if (!drawableManager.isInCam(drawable)) {
                    continue;
                }
                Vector2 draPos = drawable.getPos();
                float gradSz = .25f * Const.ATM_HEIGHT;
                float distPerc = (draPos.dst(npPos) - npgh - Const.ATM_HEIGHT) / gradSz;
                distPerc = SolMath.clamp(distPerc);
                ((RectSprite) drawable).tint.a = distPerc;
            }
        } else if (myMaxFadeTime > 0) {
            myFadeTime -= ts;
            float tintPerc = myFadeTime / myMaxFadeTime;
            for (Drawable drawable : myDrawables) {
                if (!(drawable instanceof RectSprite)) {
                    continue;
                }
                RectSprite rs = (RectSprite) drawable;
                rs.tint.a = SolMath.clamp(tintPerc * rs.baseAlpha);
            }

        }
    }

    @Override
    public boolean shouldBeRemoved(SolGame game) {
        if (myMaxFadeTime > 0 && myFadeTime <= 0) {
            return true;
        }
        if (myTemporary) {
            boolean rem = true;
            for (Drawable drawable : myDrawables) {
                if (!drawable.okToRemove()) {
                    rem = false;
                    break;
                }
            }
            if (rem) {
                return true;
            }
        }
        return myRemoveController != null && myRemoveController.shouldRemove(myPos);
    }

    @Override
    public void onRemove(SolGame game) {
    }

    @Override
    public void receiveDmg(float dmg, SolGame game, Vector2 pos, DmgType dmgType) {
    }

    @Override
    public boolean receivesGravity() {
        return false;
    }

    @Override
    public void receiveForce(Vector2 force, SolGame game, boolean acc) {
    }

    @Override
    public Vector2 getPosition() {
        return myPos;
    }

    @Override
    public FarObj toFarObj() {
        return myTemporary ? null : new FarDrawable(myDrawables, myPos, mySpd, myRemoveController, myHideOnPlanet);
    }

    @Override
    public List<Drawable> getDrawables() {
        return myDrawables;
    }

    @Override
    public float getAngle() {
        return 0;
    }

    @Override
    public Vector2 getSpd() {
        return null;
    }

    @Override
    public void handleContact(SolObject other, ContactImpulse impulse, boolean isA, float absImpulse,
                              SolGame game, Vector2 collPos) {
    }

    @Override
    public String toDebugString() {
        return null;
    }

    @Override
    public Boolean isMetal() {
        return null;
    }

    @Override
    public boolean hasBody() {
        return false;
    }

    public void fade(float fadeTime) {
        myMaxFadeTime = fadeTime;
        myFadeTime = fadeTime;
    }
}
