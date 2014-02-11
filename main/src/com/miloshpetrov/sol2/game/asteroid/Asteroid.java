package com.miloshpetrov.sol2.game.asteroid;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.miloshpetrov.sol2.common.SolMath;
import com.miloshpetrov.sol2.game.*;
import com.miloshpetrov.sol2.game.dra.Dra;
import com.miloshpetrov.sol2.game.dra.DraMan;
import com.miloshpetrov.sol2.game.planet.Planet;
import com.miloshpetrov.sol2.game.planet.SystemsBuilder;

import java.util.ArrayList;
import java.util.List;


public class Asteroid implements SolObj {

  private final Body myBody;
  private final Vector2 myPos;
  private final ArrayList<Dra> myDras;
  private final int myModelNr;
  private final RemoveController myRemoveController;
  private final float myRadius;
  private float myAngle;

  public Asteroid(int modelNr, Body body, RemoveController removeController, ArrayList<Dra> dras) {
    myModelNr = modelNr;
    myRemoveController = removeController;
    myDras = dras;
    myBody = body;
    myPos = new Vector2();
    myRadius = DraMan.radiusFromDras(myDras);
    setParamsFromBody();
  }

  @Override
  public Vector2 getPos() {
    return myPos;
  }

  @Override
  public FarObj toFarObj() {
    return new FarAsteroid(myModelNr, myPos, myAngle, myRemoveController, myRadius);
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
  public void handleContact(SolObj other, Contact contact, ContactImpulse impulse, boolean isA, float absImpulse,
    SolGame game)
  {
  }

  @Override
  public String toDebugString() {
    return null;
  }

  @Override
  public void update(SolGame game) {
    setParamsFromBody();
    avoidPlanet(game);
  }

  private void setParamsFromBody() {
    myPos.set(myBody.getPosition());
    myAngle = myBody.getAngle() * SolMath.radDeg;
  }

  @Override
  public boolean shouldBeRemoved(SolGame game) {
    return myRemoveController.shouldRemove(myPos);
  }

  private void avoidPlanet(SolGame game) {
    Planet np = game.getPlanetMan().getNearestPlanet();
    Vector2 planetPos = np.getPos();
    if (!(planetPos.dst(myPos) < np.getFullHeight())) return;
    Vector2 vel = SolMath.getVec(myPos);
    vel.sub(planetPos);
    SolMath.fromAl(vel, vel.angle(), SystemsBuilder.PLANET_SPD * 1.5f);
    myBody.setLinearVelocity(vel);
    SolMath.free(vel);
  }

  @Override
  public void onRemove(SolGame game) {
    myBody.getWorld().destroyBody(myBody);
  }

  @Override
  public float getRadius() {
    return myRadius;
  }

  @Override
  public void receiveDmg(float dmg, SolGame game, Vector2 pos) {
  }

  @Override
  public boolean receivesGravity() {
    return false;
  }

  @Override
  public void receiveAcc(Vector2 acc, SolGame game) {
  }
}
