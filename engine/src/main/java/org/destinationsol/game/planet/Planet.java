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
import org.destinationsol.Const;
import org.destinationsol.common.Bound;
import org.destinationsol.common.SolMath;
import org.destinationsol.game.HardnessCalc;
import org.destinationsol.game.SolGame;

import java.util.ArrayList;
import java.util.List;

public class Planet {
    private final SolSystem mySys;
    private final Vector2 myPos;
    private final float myDist;
    private final float myToSysRotSpd;
    private final float myRotSpd;
    private final float myGroundHeight;
    private final PlanetConfig myConfig;
    private final String myName;
    private final float myGravConst;
    private final List<Vector2> myLps;
    private final float myGroundDps;
    private final float myAtmDps;
    private boolean myObjsCreated;

    private float myAngleToSys;
    private float myAngle;
    private float myMinGroundHeight;
    private Vector2 mySpd;

    public Planet(SolSystem sys, float angleToSys, float dist, float angle, float toSysRotSpd, float rotSpd,
                  float groundHeight, boolean objsCreated, PlanetConfig config, String name) {
        mySys = sys;
        myAngleToSys = angleToSys;
        myDist = dist;
        myAngle = angle;
        myToSysRotSpd = toSysRotSpd;
        myRotSpd = rotSpd;
        myGroundHeight = groundHeight;
        myConfig = config;
        myName = name;
        myMinGroundHeight = myGroundHeight;
        myObjsCreated = objsCreated;
        myPos = new Vector2();
        mySpd = new Vector2();
        float grav = SolMath.rnd(config.minGrav, config.maxGrav);
        myGravConst = grav * myGroundHeight * myGroundHeight;
        myGroundDps = HardnessCalc.getGroundDps(myConfig, grav);
        myAtmDps = HardnessCalc.getAtmDps(myConfig);
        myLps = new ArrayList<>();
        setSecondaryParams();
    }

    public void update(SolGame game) {
        float ts = game.getTimeStep();
        myAngleToSys += myToSysRotSpd * ts;
        myAngle += myRotSpd * ts;

        setSecondaryParams();
        Vector2 camPos = game.getCam().getPos();
        if (!myObjsCreated && camPos.dst(myPos) < getGroundHeight() + Const.MAX_SKY_HEIGHT_FROM_GROUND) {
            myMinGroundHeight = new PlanetObjectsBuilder().createPlanetObjs(game, this);
            fillLangingPlaces(game);
            myObjsCreated = true;
        }
    }

    private void setSecondaryParams() {
        SolMath.fromAl(myPos, myAngleToSys, myDist, true);
        myPos.add(mySys.getPos());
        float spdLen = SolMath.angleToArc(myToSysRotSpd, myDist);
        float spdAngle = myAngleToSys + 90;
        SolMath.fromAl(mySpd, spdAngle, spdLen);
    }

    private void fillLangingPlaces(SolGame game) {
        for (int i = 0; i < 10; i++) {
            Vector2 lp = game.getPlanetMan().findFlatPlace(game, this, null, 0);
            myLps.add(lp);
        }
    }

    public float getAngle() {
        return myAngle;
    }

    public Vector2 getPos() {
        return myPos;
    }

    public float getFullHeight() {
        return myGroundHeight + Const.ATM_HEIGHT;
    }

    public float getGroundHeight() {
        return myGroundHeight;
    }

    public SolSystem getSys() {
        return mySys;
    }

    @Bound
    public Vector2 getAdjustedEffectSpd(Vector2 pos, Vector2 spd) {
        Vector2 r = SolMath.getVec(spd);
        if (myConfig.skyConfig == null) {
            return r;
        }
        Vector2 up = SolMath.distVec(myPos, pos);
        float dst = up.len();
        if (dst == 0 || getFullHeight() < dst) {
            SolMath.free(up);
            return r;
        }
        float smokeConst = 1.2f * myGravConst;
        if (dst < myGroundHeight) {
            up.scl(smokeConst / dst / myGroundHeight / myGroundHeight);
            r.set(up);
            SolMath.free(up);
            return r;
        }
        float spdPerc = (dst - myGroundHeight) / Const.ATM_HEIGHT;
        r.scl(spdPerc);
        up.scl(smokeConst / dst / dst / dst);
        r.add(up);
        SolMath.free(up);
        return r;
    }

    public float getGravConst() {
        return myGravConst;
    }

    public float getDist() {
        return myDist;
    }

    public float getAngleToSys() {
        return myAngleToSys;
    }

    public float getRotSpd() {
        return myRotSpd;
    }

    public boolean isObjsCreated() {
        return myObjsCreated;
    }

    public List<Vector2> getLandingPlaces() {
        return myLps;
    }

    public float getMinGroundHeight() {
        return myMinGroundHeight;
    }

    public boolean isNearGround(Vector2 pos) {
        return myPos.dst(pos) - myGroundHeight < .25f * Const.ATM_HEIGHT;
    }

    public PlanetConfig getConfig() {
        return myConfig;
    }

    public float getToSysRotSpd() {
        return myToSysRotSpd;
    }

    public String getName() {
        return myName;
    }

    public void calcSpdAtPos(Vector2 spd, Vector2 pos) {
        Vector2 toPos = SolMath.distVec(myPos, pos);
        float fromPlanetAngle = SolMath.angle(toPos);
        float hSpdLen = SolMath.angleToArc(myRotSpd, toPos.len());
        SolMath.free(toPos);
        SolMath.fromAl(spd, fromPlanetAngle + 90, hSpdLen);
        spd.add(mySpd);
    }

    public float getAtmDps() {
        return myAtmDps;
    }

    public float getGroundDps() {
        return myGroundDps;
    }
}
