package com.miloshpetrov.sol2.game.input;

import com.badlogic.gdx.math.Vector2;
import com.miloshpetrov.sol2.common.SolMath;
import com.miloshpetrov.sol2.game.SolGame;
import com.miloshpetrov.sol2.game.planet.Planet;
import com.miloshpetrov.sol2.game.ship.HullConfig;
import com.miloshpetrov.sol2.game.ship.SolShip;

/**
 * Flies in the planet orbit
 */
public class OrbiterDestProvider implements MoveDestProvider {
  private final Planet myPlanet;
  private final float myDesiredSpd;
  private final float myHeight;
  private final boolean myCw;
  private final Vector2 myDest;

  public OrbiterDestProvider(Planet planet, float height, boolean cw) {
    myPlanet = planet;
    myHeight = height;
    myCw = cw;
    myDesiredSpd = SolMath.sqrt(myPlanet.getGravConst() / myHeight);
    myDest = new Vector2();
  }

  @Override
  public Vector2 getDest() {
    return myDest;
  }

  @Override
  public boolean shouldAvoidBigObjs() {
    return false;
  }

  @Override
  public float getDesiredSpdLen() {
    return myDesiredSpd;
  }

  @Override
  public boolean shouldStopNearDest() {
    return false;
  }

  @Override
  public void update(SolGame game, Vector2 shipPos, float maxIdleDist, HullConfig hullConfig) {
    Vector2 pPos = myPlanet.getPos();
    float destAngle = SolMath.angle(pPos, shipPos) + 5 * SolMath.toInt(myCw);
    SolMath.fromAl(myDest, destAngle, myHeight);
    myDest.add(pPos);
  }

  @Override
  public Boolean shouldManeuver(boolean canShoot, SolShip nearestEnemy, boolean nearGround) {
    return null;
  }
}
