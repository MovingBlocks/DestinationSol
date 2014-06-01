package com.miloshpetrov.sol2.game.planet;

import com.badlogic.gdx.math.Vector2;
import com.miloshpetrov.sol2.common.SolMath;
import com.miloshpetrov.sol2.game.*;
import com.miloshpetrov.sol2.game.dra.Dra;
import com.miloshpetrov.sol2.game.dra.DraMan;

import java.util.List;

public class FarPlanetSprites implements FarObj {
  private final Planet myPlanet;
  private float myRelAngleToPlanet;
  private final float myDist;
  private final List<Dra> myDras;
  private final float myRadius;
  private final float myToPlanetRotSpd;
  private Vector2 myPos;

  public FarPlanetSprites(Planet planet, float relAngleToPlanet, float dist, List<Dra> dras,
    float toPlanetRotSpd) {
    myPlanet = planet;
    myRelAngleToPlanet = relAngleToPlanet;
    myDist = dist;
    myDras = dras;
    myRadius = DraMan.radiusFromDras(myDras);
    myToPlanetRotSpd = toPlanetRotSpd;
    myPos = new Vector2();
  }

  @Override
  public boolean shouldBeRemoved(SolGame game) {
    return false;
  }

  @Override
  public SolObj toObj(SolGame game) {
    return new PlanetSprites(myPlanet, myRelAngleToPlanet, myDist, myDras, myToPlanetRotSpd);
  }

  @Override
  public void update(SolGame game) {
    myRelAngleToPlanet += myToPlanetRotSpd * game.getTimeStep();
    if (game.getPlanetMan().getNearestPlanet() == myPlanet) {
      SolMath.fromAl(myPos, myPlanet.getAngle() + myRelAngleToPlanet, myDist);
      myPos.add(myPlanet.getPos());
    }
  }

  @Override
  public float getRadius() {
    return myRadius;
  }

  @Override
  public Vector2 getPos() {
    return myPos;
  }

  @Override
  public String toDebugString() {
    return null;
  }

  @Override
  public boolean hasBody() {
    return false;
  }
}
