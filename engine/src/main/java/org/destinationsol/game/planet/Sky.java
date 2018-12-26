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
package org.destinationsol.game.planet;

import com.badlogic.gdx.math.Vector2;
import org.destinationsol.Const;
import org.destinationsol.assets.Assets;
import org.destinationsol.common.SolColor;
import org.destinationsol.common.SolMath;
import org.destinationsol.game.ColorSpan;
import org.destinationsol.game.DmgType;
import org.destinationsol.game.FarObject;
import org.destinationsol.game.SolCam;
import org.destinationsol.game.SolGame;
import org.destinationsol.game.SolObject;
import org.destinationsol.game.drawables.Drawable;
import org.destinationsol.game.drawables.DrawableLevel;
import org.destinationsol.game.drawables.RectSprite;

import java.util.ArrayList;
import java.util.List;

public class Sky implements SolObject {

    private final Planet planet;
    private final RectSprite filling;
    private final RectSprite gradation;
    private final ArrayList<Drawable> drawables;
    private final ColorSpan skySpan;
    private final Vector2 position;

    public Sky(SolGame game, Planet planet) {
        this.planet = planet;
        drawables = new ArrayList<>();

        filling = new RectSprite(Assets.getAtlasRegion("engine:planetStarCommonWhiteTex"), 5, 0, 0, new Vector2(), DrawableLevel.ATM, 0f, 0, SolColor.col(.5f, 0), false);
        drawables.add(filling);
        gradation = new RectSprite(Assets.getAtlasRegion("engine:planetStarCommonGrad"), 5, 0, 0, new Vector2(), DrawableLevel.ATM, 0f, 0, SolColor.col(.5f, 0), false);
        drawables.add(gradation);
        SkyConfig config = planet.getConfig().skyConfig;
        skySpan = ColorSpan.rgb(config.dawn, config.day);
        position = new Vector2();
        updatePos(game);
    }

    private void updatePos(SolGame game) {
        Vector2 camPos = game.getCam().getPosition();
        Vector2 planetPos = planet.getPosition();
        if (planetPos.dst(camPos) < planet.getGroundHeight() + Const.MAX_SKY_HEIGHT_FROM_GROUND) {
            position.set(camPos);
            return;
        }
        position.set(planetPos);
    }

    @Override
    public void update(SolGame game) {
        updatePos(game);

        Vector2 planetPos = planet.getPosition();
        SolCam cam = game.getCam();
        Vector2 camPos = cam.getPosition();
        float distPercentage = 1 - (planetPos.dst(camPos) - planet.getGroundHeight()) / Const.MAX_SKY_HEIGHT_FROM_GROUND;
        if (distPercentage < 0) {
            return;
        }
        if (1 < distPercentage) {
            distPercentage = 1;
        }

        Vector2 sysPos = planet.getSystem().getPosition();
        float angleToCam = SolMath.angle(planetPos, camPos);
        float angleToSun = SolMath.angle(planetPos, sysPos);
        float dayPercentage = 1 - SolMath.angleDiff(angleToCam, angleToSun) / 180;
        float skyIntensity = SolMath.clamp(1 - ((1 - dayPercentage) / .75f));
        float skyColorPercentage = SolMath.clamp((skyIntensity - .5f) * 2f + .5f);
        skySpan.set(skyColorPercentage, gradation.tint);
        skySpan.set(skyColorPercentage, filling.tint);
        float gradPercentage = SolMath.clamp(2 * skyIntensity);
        float fillPercentage = SolMath.clamp(2 * (skyIntensity - .5f));
        gradation.tint.a = gradPercentage * distPercentage;
        filling.tint.a = fillPercentage * SolMath.clamp(1 - (1 - distPercentage) * 2) * .37f;

        float viewDist = cam.getViewDistance();
        float sz = 2 * viewDist;
        gradation.setTextureSize(sz);
        filling.setTextureSize(sz);

        float angleCamToSun = angleToCam - angleToSun;
        float relAngle;
        if (SolMath.abs(SolMath.norm(angleCamToSun)) < 90) {
            relAngle = angleToCam + 180 + angleCamToSun;
        } else {
            relAngle = angleToCam - angleCamToSun;
        }
        gradation.relativeAngle = relAngle - 90;
    }

    @Override
    public boolean shouldBeRemoved(SolGame game) {
        return false;
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
        return new FarSky(planet);
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
}
