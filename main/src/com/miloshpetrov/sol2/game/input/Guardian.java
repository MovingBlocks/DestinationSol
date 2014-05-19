package com.miloshpetrov.sol2.game.input;

import com.badlogic.gdx.math.Vector2;
import com.miloshpetrov.sol2.Const;
import com.miloshpetrov.sol2.common.SolMath;
import com.miloshpetrov.sol2.game.*;
import com.miloshpetrov.sol2.game.planet.Planet;
import com.miloshpetrov.sol2.game.ship.*;

import java.util.List;

/**
 * Flies near the given ship. When the ship is destroyed, floats
 */
public class Guardian implements MoveDestProvider {
  public static final float DIST = 1f;

  private final Pilot myTargetPilot;
  private final Vector2 myDest;
  private final float myAngle;

  private SolShip myTarget;
  private FarShip myFarTarget;

  public Guardian(SolGame game, HullConfig hullConfig, Pilot targetPilot, Vector2 targetPos, HullConfig targetHc) {
    myTargetPilot = targetPilot;
    myDest = new Vector2();
    myAngle = SolMath.rnd(180);
    setDest(game, targetPos, targetHc.approxRadius, hullConfig);
  }

  @Override
  public Vector2 getDest() {
    return myDest;
  }

  @Override
  public boolean shouldAvoidBigObjs() {
    return true;
  }

  @Override
  public float getDesiredSpdLen() {
    return Const.MAX_MOVE_SPD;
  }

  @Override
  public boolean shouldStopNearDest() {
    return true;
  }

  @Override
  public void update(SolGame game, Vector2 shipPos, float maxIdleDist, HullConfig hullConfig, SolShip nearestEnemy) {
    updateTarget(game);
    myDest.set(shipPos);
    Vector2 targetPos;
    float targetApproxRad;
    if (myTarget == null) {
      if (myFarTarget == null) return;
      targetPos = myFarTarget.getPos();
      targetApproxRad = myFarTarget.getHullConfig().approxRadius;
    } else {
      targetPos = myTarget.getPos();
      targetApproxRad = myTarget.getHull().config.approxRadius;
    }
    setDest(game, targetPos, targetApproxRad, hullConfig);
  }

  public void updateTarget(SolGame game) {
    List<SolObj> objs = game.getObjMan().getObjs();
    if (myTarget != null && objs.contains(myTarget)) return;
    myTarget = null;
    List<FarObj> farObjs = game.getObjMan().getFarObjs();
    if (myFarTarget != null && farObjs.contains(myFarTarget)) return;
    myFarTarget = null;

    for (SolObj o : objs) {
      if (!(o instanceof SolShip)) continue;
      SolShip other = (SolShip) o;
      if (other.getPilot() != myTargetPilot) continue;
      myTarget = other;
      return;
    }
    for (FarObj o : farObjs) {
      if (!(o instanceof FarShip)) continue;
      FarShip other = (FarShip) o;
      if (other.getPilot() != myTargetPilot) continue;
      myFarTarget = other;
      return;
    }
  }

  private void setDest(SolGame game, Vector2 targetPos, float targetApproxRad, HullConfig hullConfig) {
    Planet np = game.getPlanetMan().getNearestPlanet(targetPos);
    float desiredAngle = myAngle;
    if (np.isNearGround(targetPos)) {
      desiredAngle = SolMath.angle(np.getPos(), targetPos);
    }
    SolMath.fromAl(myDest, desiredAngle, targetApproxRad + DIST + hullConfig.approxRadius);
    myDest.add(targetPos);
  }

  @Override
  public Boolean shouldManeuver(boolean canShoot, SolShip nearestEnemy, boolean nearGround) {
    Vector2 targetPos = null;
    if (myTarget != null) {
      targetPos = myTarget.getPos();
    } else if (myFarTarget != null) {
      targetPos = myFarTarget.getPos();
    }
    float maxManeuverDist = 2 * (nearGround ? Const.CAM_VIEW_DIST_GROUND : Const.CAM_VIEW_DIST_SPACE);
    if (targetPos != null && maxManeuverDist < targetPos.dst(nearestEnemy.getPos())) return null;
    return true;
  }

  @Override
  public Vector2 getDestSpd() {
    return myTarget == null ? Vector2.Zero : myTarget.getSpd();
  }

  public float getAngle() {
    return myAngle;
  }
}
