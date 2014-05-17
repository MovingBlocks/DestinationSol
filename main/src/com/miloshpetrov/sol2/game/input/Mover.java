package com.miloshpetrov.sol2.game.input;

import com.badlogic.gdx.math.Vector2;
import com.miloshpetrov.sol2.Const;
import com.miloshpetrov.sol2.common.SolMath;
import com.miloshpetrov.sol2.game.SolGame;
import com.miloshpetrov.sol2.game.planet.Planet;
import com.miloshpetrov.sol2.game.ship.SolShip;

public class Mover {
  public static final float MAX_SPD_DEVIATION = .5f;
  public static final float MAX_SPD_DEVIATION_NEAR_DEST = .1f;
  public static final float MIN_MOVE_AAD = 5f;
  public static final float MIN_ANGLE_TO_ACC = 25f;
  public static final float MIN_PLANET_MOVE_AAD = 15f;
  private final BigObjAvoider myBigObjAvoider;
  private final SmallObjAvoider mySmallObjAvoider;
  private boolean myUp;
  private boolean myLeft;
  private boolean myRight;
  private Vector2 myDesiredSpd;


  public Mover() {
    myBigObjAvoider = new BigObjAvoider();
    mySmallObjAvoider = new SmallObjAvoider();
    myDesiredSpd = new Vector2();
  }

  public void update(SolGame game, SolShip ship, Vector2 dest, MoveDestProvider destProvider, Planet np, float maxIdleDist, boolean hasEngine) {
    myUp = false;
    myLeft = false;
    myRight = false;

    if (!hasEngine || dest == null) return;

    Vector2 shipPos = ship.getPos();

    float toDestLen = shipPos.dst(dest);
    boolean stopNearDest = destProvider.shouldStopNearDest();
    float maxDeviation;

    if (stopNearDest && toDestLen < maxIdleDist) {
      updateDesiredSpdNearDest(dest, ship, np);
      maxDeviation = MAX_SPD_DEVIATION_NEAR_DEST;
    } else {
      updateDesiredSpd(game, ship, dest, toDestLen, stopNearDest, destProvider, np);
      maxDeviation = MAX_SPD_DEVIATION;
    }

    Vector2 shipSpd = ship.getSpd();
    float spdDeviation = shipSpd.dst(myDesiredSpd);
    if (spdDeviation < maxDeviation) return;

    float shipAngle = ship.getAngle();
    float rotSpd = ship.getRotSpd();
    float rotAcc = ship.getRotAcc();

    float desiredAngle = SolMath.angle(shipSpd, myDesiredSpd);
    float angleDiff = SolMath.angleDiff(desiredAngle, shipAngle);
    myUp = angleDiff < MIN_ANGLE_TO_ACC;
    Boolean ntt = needsToTurn(shipAngle, desiredAngle, rotSpd, rotAcc, MIN_MOVE_AAD);
    if (ntt != null) {
      if (ntt) myRight = true; else myLeft = true;
    }
  }

  private void updateDesiredSpd(SolGame game, SolShip ship, Vector2 dest, float toDestLen, boolean stopNearDest, MoveDestProvider destProvider, Planet np) {
    boolean avoidBigObjs = destProvider.shouldAvoidBigObjs();
    float desiredSpdLen = destProvider.getDesiredSpdLen();
    float toDestAngle = getToDestAngle(game, ship, dest, avoidBigObjs, np);
    if (stopNearDest) {
      float tangentSpd = SolMath.project(ship.getSpd(), toDestAngle);
      float breakWay = tangentSpd * tangentSpd / ship.getAcc() / 2;
      float turnWay = tangentSpd * ship.calcTimeToTurn(toDestAngle + 180);
      boolean needsToBreak = toDestLen < turnWay + breakWay;
      if (needsToBreak) {
        myDesiredSpd.set(0, 0);
        return;
      }
    }
    SolMath.fromAl(myDesiredSpd, toDestAngle, desiredSpdLen);
  }

  public void rotateOnIdle(SolShip ship, Planet np, Vector2 dest, boolean stopNearDest, float maxIdleDist) {
    if (isActive() || dest == null) return;
    Vector2 shipPos = ship.getPos();
    float shipAngle = ship.getAngle();
    float toDestLen = shipPos.dst(dest);
    float desiredAngle;
    float allowedAngleDiff;
    boolean nearFinalDest = stopNearDest && toDestLen < maxIdleDist;
    float dstToPlanet = np.getPos().dst(shipPos);
    if (nearFinalDest) {
      if (np.getFullHeight() < dstToPlanet) return; // stopping in space, don't care of angle
      // stopping on planet
      desiredAngle = SolMath.angle(np.getPos(), shipPos);
      allowedAngleDiff = MIN_PLANET_MOVE_AAD;
    } else {
      // flying somewhere
      if (dstToPlanet < np.getFullHeight() + Const.ATM_HEIGHT) return; // near planet, don't care of angle
      desiredAngle = SolMath.angle(ship.getSpd());
      allowedAngleDiff = MIN_MOVE_AAD;
    }

    Boolean ntt = needsToTurn(shipAngle, desiredAngle, ship.getRotSpd(), ship.getRotAcc(), allowedAngleDiff);
    if (ntt != null) {
      if (ntt) myRight = true; else myLeft = true;
    }
  }

  private void updateDesiredSpdNearDest(Vector2 dest, SolShip ship, Planet np) {
    myDesiredSpd.set(0, 0);
    if (np != null && np.getPos().dst(ship.getPos()) < np.getFullHeight()) {
      updateDesiredSpdOnPlanet(np, dest, ship);
    }
  }

  private void updateDesiredSpdOnPlanet(Planet p, Vector2 dest, SolShip ship) {
    Vector2 toDest = SolMath.distVec(p.getPos(), dest);
    float fromPlanetAngle = SolMath.angle(toDest);
    float hSpdLen = SolMath.angleToArc(p.getRotSpd(), toDest.len());
    SolMath.free(toDest);
    float vSpdLen = SolMath.project(ship.getSpd(), fromPlanetAngle);
    myDesiredSpd.set(vSpdLen, hSpdLen);
    SolMath.rotate(myDesiredSpd, fromPlanetAngle);
  }

  private float getToDestAngle(SolGame game, SolShip ship, Vector2 dest, boolean avoidBigObjs, Planet np) {
    Vector2 shipPos = ship.getPos();
    float toDestAngle = SolMath.angle(shipPos, dest);
    if (avoidBigObjs) {
      toDestAngle = myBigObjAvoider.avoid(game, shipPos, dest, toDestAngle);
    }
    toDestAngle = mySmallObjAvoider.avoid(game, ship, toDestAngle, np);
    return toDestAngle;
  }

  public static Boolean needsToTurn(float angle, float destAngle, float rotSpd, float rotAcc, float allowedAngleDiff) {
    if (SolMath.angleDiff(destAngle, angle) < allowedAngleDiff) return null;

    float breakWay = rotSpd * rotSpd / rotAcc / 2;
    float angleAfterBreak = angle + breakWay * SolMath.toInt(rotSpd > 0);
    float relAngle = SolMath.norm(angle - destAngle);
    float relAngleAfterBreak = SolMath.norm(angleAfterBreak - destAngle);
    if (relAngle > 0 == relAngleAfterBreak > 0) return relAngle < 0;
    return relAngle > 0;
  }

  public boolean isUp() {
    return myUp;
  }

  public boolean isLeft() {
    return myLeft;
  }

  public boolean isRight() {
    return myRight;
  }


  public boolean isActive() {
    return myUp || myLeft || myRight;
  }

  public BigObjAvoider getBigObjAvoider() {
    return myBigObjAvoider;
  }
}
