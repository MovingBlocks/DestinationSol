package com.miloshpetrov.sol2.game.planet;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.miloshpetrov.sol2.common.SolMath;
import com.miloshpetrov.sol2.game.*;
import com.miloshpetrov.sol2.game.dra.*;

import java.util.ArrayList;
import java.util.List;

public class TileObject implements SolObject {

  private final Planet myPlanet;
  private final float myToPlanetRelAngle;
  private final float myDist;
  private final List<Dra> myDras;
  private final Body myBody;
  private final Vector2 myPos;

  // for far objs {
  private final float mySize;
  private final Tile myTile;
  // }

  private float myAngle;

  public TileObject(Planet planet, float toPlanetRelAngle, float dist, float size, RectSprite sprite, Body body, Tile tile) {
    myTile = tile;
    myDras = new ArrayList<Dra>();

    myPlanet = planet;
    myToPlanetRelAngle = toPlanetRelAngle;
    myDist = dist;
    mySize = size;
    myBody = body;
    myPos = new Vector2();

    myDras.add(sprite);
    setDependentParams();
  }

  @Override
  public void update(SolGame game) {
    setDependentParams();

    if (myBody != null) {
      float ts = game.getTimeStep();
      Vector2 spd = SolMath.getVec(myPos);
      spd.sub(myBody.getPosition());
      spd.scl(1f / ts);
      myBody.setLinearVelocity(spd);
      SolMath.free(spd);
      float bodyAngle = myBody.getAngle() * SolMath.radDeg;
      float av = SolMath.norm(myAngle - bodyAngle) * SolMath.degRad / ts;
      myBody.setAngularVelocity(av);
    }
  }

  private void setDependentParams() {
    float toPlanetAngle = myPlanet.getAngle() + myToPlanetRelAngle;
    SolMath.fromAl(myPos, toPlanetAngle, myDist, true);
    myPos.add(myPlanet.getPos());
    myAngle = toPlanetAngle + 90;
  }

  @Override
  public boolean shouldBeRemoved(SolGame game) {
    return false;
  }

  @Override
  public void onRemove(SolGame game) {
    if (myBody != null) myBody.getWorld().destroyBody(myBody);
  }

  @Override
  public void receiveDmg(float dmg, SolGame game, Vector2 pos, DmgType dmgType) {
    game.getSpecialSounds().playHit(game, this, pos, dmgType);
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
    return new FarTileObject(myPlanet, myToPlanetRelAngle, myDist, mySize, myTile);
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
    return true;
  }

  public Planet getPlanet() {
    return myPlanet;
  }

  public float getSz() {
    return mySize;
  }

  public Tile getTile() {
    return myTile;
  }
}
