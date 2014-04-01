package com.miloshpetrov.sol2.game.item;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.miloshpetrov.sol2.common.SolMath;
import com.miloshpetrov.sol2.game.*;
import com.miloshpetrov.sol2.game.dra.Dra;
import com.miloshpetrov.sol2.game.dra.DraMan;
import com.miloshpetrov.sol2.game.particle.LightSrc;

import java.util.List;

public class Loot implements SolObj {

  public static final int MAX_ROT_SPD = 4;
  public static final float MAX_SPD = .6f;
  public static final int MAX_LIFE = 6;
  public static final float DURABILITY = 70f;
  public static final float PULL_SPD = 1f;
  private final SolItem myItem;
  private final List<Dra> myDras;
  private final LightSrc myLightSrc;
  private final float myRadius;
  private final Vector2 myPos;

  private final Body myBody;
  private int myLife;
  private float myAngle;

  public Loot(SolItem item, Body body, int life, List<Dra> dras, LightSrc ls) {
    myBody = body;
    myLife = life;
    myItem = item;
    myDras = dras;
    myLightSrc = ls;
    myRadius = DraMan.radiusFromDras(myDras);
    myPos = new Vector2();
    setParamsFromBody();
  }

  @Override
  public void update(SolGame game) {
    setParamsFromBody();
    myLightSrc.update(true, myAngle, game);
  }

  private void setParamsFromBody() {
    myPos.set(myBody.getPosition());
    myAngle = myBody.getAngle() * SolMath.radDeg;
  }

  @Override
  public boolean shouldBeRemoved(SolGame game) {
    return myLife <= 0;
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
  public void receiveDmg(float dmg, SolGame game, Vector2 pos, DmgType dmgType) {
    myLife -= dmg;
    game.getSpecialSounds().playDmg(game, this, pos, dmgType);
  }

  @Override
  public boolean receivesGravity() {
    return true;
  }

  @Override
  public void receiveAcc(Vector2 acc, SolGame game) {
    myBody.applyForceToCenter(acc.scl(myBody.getMass()), true);
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
    return null;
  }

  @Override
  public void handleContact(SolObj other, ContactImpulse impulse, boolean isA, float absImpulse,
    SolGame game, Vector2 collPos)
  {
    float dmg = absImpulse / myBody.getMass() / DURABILITY;
    receiveDmg((int) dmg, game, collPos, DmgType.CRASH);
  }

  @Override
  public String toDebugString() {
    return null;
  }

  @Override
  public Boolean isMetal() {
    return true;
  }

  @Override
  public boolean hasBody() {
    return true;
  }

  public void maybePulled(Vector2 toPos, float radius) {
    Vector2 v = SolMath.getVec(toPos);
    v.sub(getPos());
    if (v.len() < radius) {
      SolMath.fromAl(v, v.angle(), PULL_SPD);
      myBody.setLinearVelocity(v);
    }
    SolMath.free(v);
  }

  public SolItem getItem() {
    return myLife > 0 ? myItem : null;
  }

  public void setLife(int life) {
    myLife = life;
  }
}
