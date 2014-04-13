package com.miloshpetrov.sol2.game.input;

import com.badlogic.gdx.math.Vector2;
import com.miloshpetrov.sol2.common.SolMath;
import com.miloshpetrov.sol2.game.*;
import com.miloshpetrov.sol2.game.planet.Planet;
import com.miloshpetrov.sol2.game.ship.*;

import java.util.List;

/**
 * Flies near the given ship. When the ship is destroyed, floats
 */
public class Guardian implements MoveDestProvider {
  private final Pilot myTargetPilot;
  private final Vector2 myDest;
  private final float myAngle;

  private SolShip myTarget;
  private FarShip myFarTarget;

  public Guardian(SolGame game, SolShip target, HullConfig hullConfig) {
    myTargetPilot = target.getPilot();
    myDest = new Vector2();
    myAngle = SolMath.rnd(180);
    setDest(game, target.getPos(), target.getHull().config.approxRadius, hullConfig);
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
    return NoDestProvider.DESIRED_SPD_LEN;
  }

  @Override
  public boolean shouldStopNearDest() {
    return true;
  }

  @Override
  public void update(SolGame game, Vector2 shipPos, float maxIdleDist, HullConfig hullConfig) {
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
    SolMath.fromAl(myDest, desiredAngle, targetApproxRad + 2 + hullConfig.approxRadius);
    myDest.add(targetPos);
  }

  @Override
  public Boolean shouldManeuver(boolean canShoot) {
    return true;
  }

  public float getAngle() {
    return myAngle;
  }
}
