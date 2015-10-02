package org.destinationsol.game.planet;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import org.destinationsol.common.SolMath;
import org.destinationsol.game.DmgType;
import org.destinationsol.game.FarObj;
import org.destinationsol.game.SolGame;
import org.destinationsol.game.SolObject;
import org.destinationsol.game.dra.Dra;

import java.util.List;

public class PlanetSprites implements SolObject {

  private final Planet myPlanet;
  private float myRelAngleToPlanet;
  private final float myDist;
  private final List<Dra> myDras;
  private final float myToPlanetRotSpd;
  private final Vector2 myPos;
  private float myAngle;

  public PlanetSprites(Planet planet, float relAngleToPlanet, float dist, List<Dra> dras, float toPlanetRotSpd) {
    myPlanet = planet;
    myRelAngleToPlanet = relAngleToPlanet;
    myDist = dist;
    myDras = dras;
    myToPlanetRotSpd = toPlanetRotSpd;
    myPos = new Vector2();
    setDependentParams();
  }

  @Override
  public void update(SolGame game) {
    setDependentParams();
    myRelAngleToPlanet += myToPlanetRotSpd * game.getTimeStep();
  }

  private void setDependentParams() {
    float angleToPlanet = myPlanet.getAngle() + myRelAngleToPlanet;
    SolMath.fromAl(myPos, angleToPlanet, myDist, true);
    myPos.add(myPlanet.getPos());
    myAngle = angleToPlanet + 90;
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
  public Vector2 getPos() {
    return myPos;
  }

  @Override
  public FarObj toFarObj() {
    return new FarPlanetSprites(myPlanet, myRelAngleToPlanet, myDist, myDras, myToPlanetRotSpd);
  }

  @Override
  public List<Dra> getDras() {
    return myDras;
  }

  @Override
  public float getAngle() {
    return myAngle;
  }

  @Override
  public Vector2 getSpd() {
    return null;
  }

  @Override
  public void handleContact(SolObject other, ContactImpulse impulse, boolean isA, float absImpulse,
    SolGame game, Vector2 collPos)
  {
  }

  @Override
  public String toDebugString() {
    return null;
  }

  @Override
  public Boolean isMetal() {
    return false;
  }

  @Override
  public boolean hasBody() {
    return false;
  }

}
