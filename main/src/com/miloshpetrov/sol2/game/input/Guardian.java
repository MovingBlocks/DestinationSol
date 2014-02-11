package com.miloshpetrov.sol2.game.input;

import com.badlogic.gdx.math.Vector2;
import com.miloshpetrov.sol2.common.SolMath;
import com.miloshpetrov.sol2.game.*;
import com.miloshpetrov.sol2.game.planet.Planet;
import com.miloshpetrov.sol2.game.ship.*;

import java.util.List;

public class Guardian implements MoveDestProvider {
  private final Pilot myTargetPilot;
  private final Vector2 myDest;
  private final float myDesiredSpd;
  private final float myAngle;

  private SolShip myTarget;
  private FarShip myFarTarget;

  public Guardian(SolGame game, float desiredSpd, SolShip target, HullConfig hullConfig) {
    myTargetPilot = target.getPilot();
    myDesiredSpd = desiredSpd;
    myDest = new Vector2();
    myAngle = SolMath.rnd(180);
    setDest(game, target.getPos(), target.getHull().config.size, hullConfig);
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
    return myDesiredSpd;
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
    float targetSize;
    if (myTarget == null) {
      if (myFarTarget == null) return;
      targetPos = myFarTarget.getPos();
      targetSize = myFarTarget.getHullConfig().size;
    } else {
      targetPos = myTarget.getPos();
      targetSize = myTarget.getHull().config.size;
    }
    setDest(game, targetPos, targetSize, hullConfig);
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

  private void setDest(SolGame game, Vector2 targetPos, float targetSize, HullConfig hullConfig) {
    Planet np = game.getPlanetMan().getNearestPlanet(targetPos);
    float desiredAngle = myAngle;
    if (np.isNearGround(targetPos)) {
      desiredAngle = SolMath.angle(np.getPos(), targetPos);
    }
    SolMath.fromAl(myDest, desiredAngle, targetSize/2 + 2 + hullConfig.size/2);
    myDest.add(targetPos);
  }

  @Override
  public Boolean shouldBattle(boolean canShoot) {
    return true;
  }
}
