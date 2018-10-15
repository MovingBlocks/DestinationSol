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
package org.destinationsol.game.drawables;

import com.badlogic.gdx.math.Vector2;
import org.destinationsol.Const;
import org.destinationsol.common.Consumed;
import org.destinationsol.common.SolMath;
import org.destinationsol.game.DmgType;
import org.destinationsol.game.FarObject;
import org.destinationsol.game.RemoveController;
import org.destinationsol.game.SolGame;
import org.destinationsol.game.SolObject;
import org.destinationsol.game.planet.Planet;

import java.util.List;

public class DrawableObject implements SolObject {
    private final Vector2 position;
    private final Vector2 velocity;
    private final RemoveController removeController;
    private final boolean hideOnPlanet;
    private final Vector2 moveDifference;
    private final List<Drawable> drawables;
    private final boolean isTemporary;

    private float maxFadeTime;
    private float fadeTime;

    public DrawableObject(List<Drawable> drawables, @Consumed Vector2 position, @Consumed Vector2 velocity, RemoveController removeController, boolean temporary, boolean hideOnPlanet) {
        this.drawables = drawables;
        this.position = position;
        this.velocity = velocity;
        this.removeController = removeController;
        this.hideOnPlanet = hideOnPlanet;
        moveDifference = new Vector2();
        isTemporary = temporary;

        maxFadeTime = -1;
        fadeTime = -1;
    }

    @Override
    public void update(SolGame game) {
        moveDifference.set(velocity);
        float timeStep = game.getTimeStep();
        moveDifference.scl(timeStep);
        position.add(moveDifference);
        if (hideOnPlanet) {
            Planet planet = game.getPlanetManager().getNearestPlanet();
            Vector2 planetPosition = planet.getPosition();
            float planetGroundHeight = planet.getGroundHeight();
            DrawableManager drawableManager = game.getDrawableManager();
            for (Drawable drawable : drawables) {
                if (!(drawable instanceof RectSprite)) {
                    continue;
                }
                if (!drawableManager.isVisible(drawable)) {
                    continue;
                }
                Vector2 drawablePosition = drawable.getPosition();
                float gradSz = .25f * Const.ATM_HEIGHT;
                float distPercentage = (drawablePosition.dst(planetPosition) - planetGroundHeight - Const.ATM_HEIGHT) / gradSz;
                distPercentage = SolMath.clamp(distPercentage);
                ((RectSprite) drawable).tint.a = distPercentage;
            }
        } else if (maxFadeTime > 0) {
            fadeTime -= timeStep;
            float tintPercentage = fadeTime / maxFadeTime;
            for (Drawable drawable : drawables) {
                if (!(drawable instanceof RectSprite)) {
                    continue;
                }
                RectSprite rectSprite = (RectSprite) drawable;
                rectSprite.tint.a = SolMath.clamp(tintPercentage * rectSprite.baseAlpha);
            }

        }
    }

    @Override
    public boolean shouldBeRemoved(SolGame game) {
        if (maxFadeTime > 0 && fadeTime <= 0) {
            return true;
        }
        if (isTemporary) {
            boolean rem = true;
            for (Drawable drawable : drawables) {
                if (!drawable.okToRemove()) {
                    rem = false;
                    break;
                }
            }
            if (rem) {
                return true;
            }
        }
        return removeController != null && removeController.shouldRemove(position);
    }

    @Override
    public void onRemove(SolGame game) {
    }

    @Override
    public void receiveDmg(float dmg, SolGame game, Vector2 position, DmgType dmgType) {
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
        return position;
    }

    @Override
    public FarObject toFarObject() {
        return isTemporary ? null : new FarDrawable(drawables, position, velocity, removeController, hideOnPlanet);
    }

    @Override
    public List<Drawable> getDrawables() {
        return drawables;
    }

    @Override
    public float getAngle() {
        return 0;
    }

    @Override
    public Vector2 getVelocity() {
        return null;
    }

    @Override
    public void handleContact(SolObject other, float absImpulse,
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
        maxFadeTime = fadeTime;
        this.fadeTime = fadeTime;
    }
}
