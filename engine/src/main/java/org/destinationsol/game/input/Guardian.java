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
package org.destinationsol.game.input;

import com.badlogic.gdx.math.Vector2;
import org.destinationsol.Const;
import org.destinationsol.common.SolMath;
import org.destinationsol.game.ObjectManager;
import org.destinationsol.game.SolGame;
import org.destinationsol.game.SolObject;
import org.destinationsol.game.planet.Planet;
import org.destinationsol.game.ship.FarShip;
import org.destinationsol.game.ship.SolShip;
import org.destinationsol.game.ship.hulls.HullConfig;

import java.util.List;

/**
 * Flies near the given ship. When the ship is destroyed, floats
 */
public class Guardian implements MoveDestProvider {
    public static final float DIST = 1.5f;

    private final Pilot myTargetPilot;
    private final Vector2 myDest;
    private final float myRelAngle;

    private SolShip myTarget;
    private FarShip myFarTarget;

    public Guardian(SolGame game, HullConfig hullConfig, Pilot targetPilot, Vector2 targetPos, HullConfig targetHc,
                    float relAngle) {
        myTargetPilot = targetPilot;
        myDest = new Vector2();
        myRelAngle = relAngle;
        setDest(game, targetPos, targetHc.getApproxRadius(), hullConfig);
    }

    @Override
    public Vector2 getDestination() {
        return myDest;
    }

    @Override
    public boolean shouldAvoidBigObjects() {
        return false;
    }

    @Override
    public float getDesiredSpeed() {
        return Const.MAX_MOVE_SPD;
    }

    @Override
    public boolean shouldStopNearDestination() {
        return true;
    }

    @Override
    public void update(SolGame game, Vector2 shipPos, float maxIdleDist, HullConfig hullConfig, SolShip nearestEnemy) {
        updateTarget(game);
        myDest.set(shipPos);
        Vector2 targetPos;
        float targetApproxRad;
        if (myTarget == null) {
            if (myFarTarget == null) {
                return;
            }
            targetPos = myFarTarget.getPosition();
            targetApproxRad = myFarTarget.getHullConfig().getApproxRadius();
        } else {
            targetPos = myTarget.getPosition();
            targetApproxRad = myTarget.getHull().config.getApproxRadius();
        }
        setDest(game, targetPos, targetApproxRad, hullConfig);
    }

    public void updateTarget(SolGame game) {
        ObjectManager om = game.getObjectManager();
        List<SolObject> objs = om.getObjects();
        if (myTarget != null && objs.contains(myTarget)) {
            return;
        }
        myTarget = null;
        List<FarShip> farShips = om.getFarShips();
        if (myFarTarget != null && farShips.contains(myFarTarget)) {
            return;
        }
        myFarTarget = null;

        for (SolObject o : objs) {
            if (!(o instanceof SolShip)) {
                continue;
            }
            SolShip other = (SolShip) o;
            if (other.getPilot() != myTargetPilot) {
                continue;
            }
            myTarget = other;
            return;
        }
        for (FarShip other : farShips) {
            if (other.getPilot() != myTargetPilot) {
                continue;
            }
            myFarTarget = other;
            return;
        }
    }

    private void setDest(SolGame game, Vector2 targetPos, float targetApproxRad, HullConfig hullConfig) {
        Planet np = game.getPlanetManager().getNearestPlanet(targetPos);
        float desiredAngle = myRelAngle;
        if (np.isNearGround(targetPos)) {
            desiredAngle = SolMath.angle(np.getPosition(), targetPos);
        }
        SolMath.fromAl(myDest, desiredAngle, targetApproxRad + DIST + hullConfig.getApproxRadius());
        myDest.add(targetPos);
    }

    @Override
    public Boolean shouldManeuver(boolean canShoot, SolShip nearestEnemy, boolean nearGround) {
        if (!canShoot) {
            return null;
        }
        Vector2 targetPos = null;
        if (myTarget != null) {
            targetPos = myTarget.getPosition();
        } else if (myFarTarget != null) {
            targetPos = myFarTarget.getPosition();
        }
        float maxManeuverDist = 2 * (nearGround ? Const.CAM_VIEW_DIST_GROUND : Const.CAM_VIEW_DIST_SPACE);
        if (targetPos != null && maxManeuverDist < targetPos.dst(nearestEnemy.getPosition())) {
            return null;
        }
        return true;
    }

    @Override
    public Vector2 getDestinationVelocity() {
        return myTarget == null ? Vector2.Zero : myTarget.getVelocity();
    }

    public float getRelAngle() {
        return myRelAngle;
    }
}
