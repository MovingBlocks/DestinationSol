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

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import org.destinationsol.common.SolMath;
import org.destinationsol.game.GameDrawer;
import org.destinationsol.game.SolGame;
import org.destinationsol.game.SolObject;
import org.destinationsol.game.drawables.Drawable;
import org.destinationsol.game.drawables.DrawableLevel;
import org.destinationsol.game.planet.Planet;

public class ParticleSrc implements Drawable {
    public static final float JUMP_SPD_TRESH = .9f;
    public static final float MAX_TIME_BETWEEN_POS_CHANGE = .25f;
    public static final float MAX_BB_RECALC_AWAIT = .5f;
    private static final float JUMP_SZ_THRESH = .7f;
    private final ParticleEmitter myEmitter;
    private final ParticleEmitter.ScaledNumericValue myOrigSpdAngle;
    private final ParticleEmitter.ScaledNumericValue myOrigRot;
    private final DrawableLevel myDrawableLevel;
    private final Vector2 myRelPos;
    private final Vector2 myOrigRelPos;
    private final float myAreaSz;
    private final EffectConfig myConfig;
    private final float myRelAngle;
    private final boolean myInheritsSpd;
    private final BoundingBox myBb;
    private Vector2 myPos;
    private boolean myWorking;
    private float myTimeSincePosChange;
    private boolean myFloatedUp;
    private float myBbRecalcAwait;

    public ParticleSrc(EffectConfig config, float sz, DrawableLevel drawableLevel, Vector2 relPos, boolean inheritsSpd,
                       SolGame game, Vector2 basePos, Vector2 baseSpd, float relAngle) {
        myConfig = config;
        myEmitter = myConfig.effectType.newEmitter();
        myDrawableLevel = drawableLevel;
        myRelPos = new Vector2(relPos);
        myOrigRelPos = new Vector2(relPos);
        myPos = new Vector2();
        myRelAngle = relAngle;

        if (sz <= 0) {
            sz = config.sz;
        }

        boolean hasArea = myEmitter.getSpawnShape().getShape() != ParticleEmitter.SpawnShape.point;
        boolean movesFast = JUMP_SPD_TRESH < myEmitter.getVelocity().getHighMax();
        boolean bigScale = JUMP_SZ_THRESH < myEmitter.getScale().getHighMax();

        if (hasArea) {
            mulVal(myEmitter.getEmission(), sz * sz);
            mulVal(myEmitter.getSpawnWidth(), sz);
            mulVal(myEmitter.getSpawnHeight(), sz);
            myAreaSz = 0;
        } else if (movesFast) {
            mulVal(myEmitter.getEmission(), sz * sz);
            ParticleEmitter.ScaledNumericValue vel = myEmitter.getVelocity();
            vel.setHigh(vel.getHighMin() * sz, vel.getHighMax() * sz);
            myAreaSz = 0;
        } else if (bigScale) {
            ParticleEmitter.ScaledNumericValue scale = myEmitter.getScale();
            scale.setHigh(scale.getHighMin() * sz, scale.getHighMax() * sz);
            myAreaSz = 0;
        } else {
            myAreaSz = sz;
        }
        myEmitter.setSprite(new Sprite(myConfig.tex));
        float[] tint = myEmitter.getTint().getColors();
        tint[0] = config.tint.r;
        tint[1] = config.tint.g;
        tint[2] = config.tint.b;

        myOrigSpdAngle = new ParticleEmitter.ScaledNumericValue();
        transferAngle(myEmitter.getAngle(), myOrigSpdAngle, 0f);
        myOrigRot = new ParticleEmitter.ScaledNumericValue();
        transferAngle(myEmitter.getRotation(), myOrigRot, 0f);

        myInheritsSpd = inheritsSpd;
        updateSpd(game, baseSpd, basePos);

        if (myConfig.effectType.continuous) {
            // making it continuous after setting initial speed
            myEmitter.setContinuous(true);
            // this is needed because making effect continuous starts it
            myEmitter.allowCompletion();
            // ... and still initial speed is not applied. : (
        } else {
            myEmitter.start();
        }
        myBb = myEmitter.getBoundingBox();
    }

    private static void transferAngle(ParticleEmitter.ScaledNumericValue from, ParticleEmitter.ScaledNumericValue to, float diff) {
        if (!to.isRelative()) {
            to.setHigh(from.getHighMin() + diff, from.getHighMax() + diff);
        }
        to.setLow(from.getLowMin() + diff, from.getLowMax() + diff);
    }

    private void setVal(ParticleEmitter.ScaledNumericValue val, float v) {
        val.setHigh(v, v);
        val.setLow(v, v);
    }

    private void mulVal(ParticleEmitter.ScaledNumericValue val, float mul) {
        val.setHigh(val.getHighMin() * mul, val.getHighMax() * mul);
        val.setLow(val.getLowMin() * mul, val.getLowMax() * mul);
    }

    public boolean isComplete() {
        return myEmitter.isComplete();
    }

    public void update(SolGame game, SolObject o) {
        maybeSwitchRelPos(game);
        Vector2 basePos = o.getPosition();
        float baseAngle = o.getAngle();
        SolMath.toWorld(myPos, myRelPos, baseAngle, basePos, false);
        float ts = game.getTimeStep();
        fixSpeedBug(ts);
        myEmitter.setPosition(myPos.x, myPos.y);
        setAngle(baseAngle);
        updateSpd(game, o.getSpd(), o.getPosition());
        myEmitter.update(ts);

        if (myBbRecalcAwait > 0) {
            myBbRecalcAwait -= game.getTimeStep();
        } else {
            myBbRecalcAwait = MAX_BB_RECALC_AWAIT;
            myEmitter.getBoundingBox();
        }
    }

    private void updateSpd(SolGame game, Vector2 baseSpd, Vector2 basePos) {
        if (isContinuous()) {
            if (!isWorking()) {
                return;
            }
        } else {
            if (myFloatedUp) {
                return;
            }
            myFloatedUp = true;
        }
        if (!myInheritsSpd) {
            baseSpd = Vector2.Zero;
        }
        if (!myConfig.floatsUp) {
            setSpd(baseSpd);
            return;
        }
        Planet np = game.getPlanetMan().getNearestPlanet();
        Vector2 spd = np.getAdjustedEffectSpd(basePos, baseSpd);
        setSpd(spd);
        SolMath.free(spd);
    }

    private void maybeSwitchRelPos(SolGame game) {
        if (myAreaSz == 0) {
            return;
        }
        float ts = game.getTimeStep();
        myTimeSincePosChange += ts;
        if (!myWorking || myTimeSincePosChange < MAX_TIME_BETWEEN_POS_CHANGE) {
            return;
        }
        myTimeSincePosChange = 0;
        SolMath.fromAl(myRelPos, SolMath.rnd(180), SolMath.rnd(0, myAreaSz));
        myRelPos.add(myOrigRelPos);
    }

    private void fixSpeedBug(float ts) {
        myPos.x -= myEmitter.getWind().getLowMin() * ts;
        myPos.y -= myEmitter.getGravity().getLowMin() * ts;
    }

    private void setAngle(float baseAngle) {
        float angle = baseAngle + myRelAngle;
        transferAngle(myOrigSpdAngle, myEmitter.getAngle(), angle);
        boolean includeSpriteAngle = true;
        if (includeSpriteAngle) {
            transferAngle(myOrigRot, myEmitter.getRotation(), angle);
        }
    }

    private void setSpd(Vector2 spd) {
        ParticleEmitter.ScaledNumericValue w = myEmitter.getWind();
        w.setActive(true);
        w.setHigh(spd.x);
        w.setLow(spd.x);
        ParticleEmitter.ScaledNumericValue g = myEmitter.getGravity();
        g.setActive(true);
        g.setHigh(spd.y);
        g.setLow(spd.y);
    }

    @Override
    public void prepare(SolObject o) {
    }

    @Override
    public Vector2 getPos() {
        return myPos;
    }

    @Override
    public Vector2 getRelPos() {
        return myRelPos;
    }

    @Override
    public float getRadius() {
        Vector3 c = new Vector3();
        c = myBb.getCenter(c);
        float toCenter = myPos.dst(c.x, c.y);
        float radius = myBb.getDimensions(c).len() / 2;
        return radius > 0 ? toCenter + radius : 0;
    }

    @Override
    public void draw(GameDrawer drawer, SolGame game) {
        drawer.draw(myEmitter, myConfig.tex, myConfig.effectType.additive);
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public boolean okToRemove() {
        return isComplete();
    }

    @Override
    public DrawableLevel getLevel() {
        return myDrawableLevel;
    }

    @Override
    public Texture getTex0() {
        return myConfig.tex.getTexture();
    }

    @Override
    public TextureAtlas.AtlasRegion getTex() {
        return myConfig.tex;
    }

    public boolean isContinuous() {
        return myConfig.effectType.continuous;
    }

    public boolean isWorking() {
        return myWorking;
    }

    public void setWorking(boolean working) {
        if (!isContinuous()) {
            throw new AssertionError("only continuous emitters can start working");
        }
        if (myWorking == working) {
            return;
        }
        myWorking = working;
        if (myWorking) {
            myEmitter.start();
        } else {
            myEmitter.allowCompletion();
        }
    }
}
