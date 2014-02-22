package com.miloshpetrov.sol2.game.projectile;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.miloshpetrov.sol2.common.SolMath;
import com.miloshpetrov.sol2.game.*;
import com.miloshpetrov.sol2.game.dra.Dra;
import com.miloshpetrov.sol2.game.dra.DraMan;
import com.miloshpetrov.sol2.game.particle.LightSrc;
import com.miloshpetrov.sol2.game.particle.ParticleSrc;

import java.util.ArrayList;
import java.util.List;

public class Rocket implements Projectile {
  public static final float INITIAL_SPD_LEN = 0f;
  public static final float MAX_SPD_LEN = 5.5f;
  public static final float ACC_LEN = 6f;

  private final ParticleSrc myFlameSrc;
  private final LightSrc myLightSrc;
  private final float myDmg;
  private final Body myBody;
  private final ArrayList<Dra> myDras;
  private final Vector2 myPos;
  private final float myRadius;
  private final Vector2 mySpd;
  private final Fraction myFraction;

  private float myAngle;
  private boolean myCollided;

  public Rocket(Body body, ArrayList<Dra> dras, ParticleSrc flameSrc, LightSrc lightSrc, Fraction fraction, float dmg) {
    myBody = body;
    myDras = dras;
    myFlameSrc = flameSrc;
    myLightSrc = lightSrc;
    myDmg = dmg;
    myPos = new Vector2();
    mySpd = new Vector2();
    myRadius = DraMan.radiusFromDras(dras);
    myFraction = fraction;
    setParamsFromBody();
  }

  @Override
  public void update(SolGame game) {
    if (myCollided) return;
    setParamsFromBody();
    float spdLenProj = SolMath.project(mySpd, myAngle);
    if (spdLenProj < MAX_SPD_LEN) {
      Vector2 spdDiff = SolMath.fromAl(myAngle, ACC_LEN * game.getTimeStep());
      mySpd.add(spdDiff);
      myBody.setLinearVelocity(mySpd);
      SolMath.free(spdDiff);
    }
    myFlameSrc.setSpd(mySpd);
    myLightSrc.update(true, myAngle, game);
  }

  private void setParamsFromBody() {
    myPos.set(myBody.getPosition());
    myAngle = myBody.getAngle() * SolMath.radDeg;
    mySpd.set(myBody.getLinearVelocity());
  }

  @Override
  public boolean shouldBeRemoved(SolGame game) {
    return myCollided;
  }

  @Override
  public void onRemove(SolGame game) {
    myBody.getWorld().destroyBody(myBody);
    game.getPartMan().finish(game, myFlameSrc, myPos);
  }

  @Override
  public float getRadius() {
    return myRadius;
  }

  @Override
  public void receiveDmg(float dmg, SolGame game, Vector2 pos) {
    explode(game);
  }

  @Override
  public boolean receivesGravity() {
    return true;
  }

  @Override
  public void receiveAcc(Vector2 acc, SolGame game) {
    Vector2 f = SolMath.getVec(acc);
    f.scl(myBody.getMass());
    myBody.applyForceToCenter(f, true);
    SolMath.free(f);
  }

  @Override
  public Vector2 getPos() {
    return myPos;
  }

  @Override
  public FarObj toFarObj() {
    return null;
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
    return mySpd;
  }

  @Override
  public void handleContact(SolObj other, Contact contact, ContactImpulse impulse, boolean isA, float absImpulse,
    SolGame game) {
    if (myCollided) return;
    explode(game);
    if (other != null) {
      other.receiveDmg(myDmg, game, contact.getWorldManifold().getPoints()[0]);
    }
  }

  @Override
  public String toDebugString() {
    return null;
  }

  private void explode(SolGame cmp) {
    myCollided = true;
    cmp.getPartMan().explode(myPos, cmp, true);
  }

  public boolean shouldCollide(Object o) {
    return false;
  }

  public Fraction getFraction() {
    return myFraction;
  }
}
