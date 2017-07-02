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
package org.destinationsol.game.planet;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import org.destinationsol.Const;
import org.destinationsol.assets.Assets;
import org.destinationsol.common.SolColor;
import org.destinationsol.common.SolMath;
import org.destinationsol.game.ColorSpan;
import org.destinationsol.game.DmgType;
import org.destinationsol.game.FarObj;
import org.destinationsol.game.SolCam;
import org.destinationsol.game.SolGame;
import org.destinationsol.game.SolObject;
import org.destinationsol.game.drawables.Drawable;
import org.destinationsol.game.drawables.DrawableLevel;
import org.destinationsol.game.drawables.RectSprite;

import java.util.ArrayList;
import java.util.List;

public class Sky implements SolObject {

    private final Planet myPlanet;
    private final RectSprite myFill;
    private final RectSprite myGrad;
    private final ArrayList<Drawable> myDrawables;
    private final ColorSpan mySkySpan;
    private final Vector2 myPos;

    public Sky(SolGame game, Planet planet) {
        myPlanet = planet;
        myDrawables = new ArrayList<>();

        myFill = new RectSprite(Assets.getAtlasRegion("engine:planetStarCommonWhiteTex"), 5, 0, 0, new Vector2(), DrawableLevel.ATM, 0f, 0, SolColor.col(.5f, 0), false);
        myDrawables.add(myFill);
        myGrad = new RectSprite(Assets.getAtlasRegion("engine:planetStarCommonGrad"), 5, 0, 0, new Vector2(), DrawableLevel.ATM, 0f, 0, SolColor.col(.5f, 0), false);
        myDrawables.add(myGrad);
        SkyConfig config = planet.getConfig().skyConfig;
        mySkySpan = ColorSpan.rgb(config.dawn, config.day);
        myPos = new Vector2();
        updatePos(game);
    }

    private void updatePos(SolGame game) {
        Vector2 camPos = game.getCam().getPos();
        Vector2 planetPos = myPlanet.getPos();
        if (planetPos.dst(camPos) < myPlanet.getGroundHeight() + Const.MAX_SKY_HEIGHT_FROM_GROUND) {
            myPos.set(camPos);
            return;
        }
        myPos.set(planetPos);
    }

    @Override
    public void update(SolGame game) {
        updatePos(game);

        Vector2 planetPos = myPlanet.getPos();
        SolCam cam = game.getCam();
        Vector2 camPos = cam.getPos();
        float distPerc = 1 - (planetPos.dst(camPos) - myPlanet.getGroundHeight()) / Const.MAX_SKY_HEIGHT_FROM_GROUND;
        if (distPerc < 0) {
            return;
        }
        if (1 < distPerc) {
            distPerc = 1;
        }

        Vector2 sysPos = myPlanet.getSys().getPos();
        float angleToCam = SolMath.angle(planetPos, camPos);
        float angleToSun = SolMath.angle(planetPos, sysPos);
        float dayPerc = 1 - SolMath.angleDiff(angleToCam, angleToSun) / 180;
        float skyIntensity = SolMath.clamp(1 - ((1 - dayPerc) / .75f));
        float skyColorPerc = SolMath.clamp((skyIntensity - .5f) * 2f + .5f);
        mySkySpan.set(skyColorPerc, myGrad.tint);
        mySkySpan.set(skyColorPerc, myFill.tint);
        float gradPerc = SolMath.clamp(2 * skyIntensity);
        float fillPerc = SolMath.clamp(2 * (skyIntensity - .5f));
        myGrad.tint.a = gradPerc * distPerc;
        myFill.tint.a = fillPerc * SolMath.clamp(1 - (1 - distPerc) * 2) * .37f;

        float viewDist = cam.getViewDist();
        float sz = 2 * viewDist;
        myGrad.setTexSz(sz);
        myFill.setTexSz(sz);

        float angleCamToSun = angleToCam - angleToSun;
        float relAngle;
        if (SolMath.abs(SolMath.norm(angleCamToSun)) < 90) {
            relAngle = angleToCam + 180 + angleCamToSun;
        } else {
            relAngle = angleToCam - angleCamToSun;
        }
        myGrad.relAngle = relAngle - 90;
    }

    @Override
    public boolean shouldBeRemoved(SolGame game) {
        return false;
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
        return new FarSky(myPlanet);
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
}
