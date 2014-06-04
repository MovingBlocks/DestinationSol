package com.miloshpetrov.sol2.game.item;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.miloshpetrov.sol2.common.SolMath;
import com.miloshpetrov.sol2.game.*;
import com.miloshpetrov.sol2.game.dra.Dra;
import com.miloshpetrov.sol2.game.particle.LightSrc;
import com.miloshpetrov.sol2.game.ship.SolShip;

import java.util.List;

public class Loot implements SolObj {

  public static final int MAX_ROT_SPD = 4;
  public static final float MAX_SPD = .6f;
  public static final int MAX_LIFE = 6;
  public static final float DURABILITY = 70f;
  public static final float PULL_DESIRED_SPD = 1f;
  public static final float PULL_FORCE = .3f;
  public static final float MAX_OWNER_AWAIT = 4f;
  public static final float SZ = .12f;
  private final SolItem myItem;
  private final List<Dra> myDras;
  private final LightSrc myLightSrc;
  private final Vector2 myPos;
  private final Body myBody;
  private final float myMass;

  private SolShip myOwner;
  private float myOwnerAwait;
  private int myLife;
  private float myAngle;

  public Loot(SolItem item, Body body, int life, List<Dra> dras, LightSrc ls, SolShip owner) {
    myBody = body;
    myLife = life;
    myItem = item;
    myDras = dras;
    myLightSrc = ls;
    myOwner = owner;
    myOwnerAwait = MAX_OWNER_AWAIT;
    myPos = new Vector2();
    myMass = myBody.getMass();
    setParamsFromBody();
  }

  @Override
  public void update(SolGame game) {
    setParamsFromBody();
    myLightSrc.update(true, myAngle, game);
    if (myOwnerAwait > 0) {
      myOwnerAwait -= game.getTimeStep();
      if (myOwnerAwait <= 0) myOwner = null;
    }
    SolShip puller = null;
    float minDist = Float.MAX_VALUE;
    List<SolObj> objs = game.getObjMan().getObjs();
    for (int i = 0, objsSize = objs.size(); i < objsSize; i++) {
      SolObj o = objs.get(i);
      if (!(o instanceof SolShip)) continue;
      SolShip ship = (SolShip) o;
      if (!ship.getPilot().collectsItems()) continue;
      if (!ship.getItemContainer().canAdd(myItem)) continue;
      float dst = ship.getPos().dst(myPos);
      if (minDist < dst) continue;
      puller = ship;
      minDist = dst;
    }
    if (puller != null) {
      maybePulled(puller, puller.getPos(), puller.getPullDist());
    }
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
  public void receiveDmg(float dmg, SolGame game, Vector2 pos, DmgType dmgType) {
    myLife -= dmg;
    game.getSpecialSounds().playHit(game, this, pos, dmgType);
  }

  @Override
  public boolean receivesGravity() {
    return true;
  }

  @Override
  public void receiveForce(Vector2 force, SolGame game, boolean acc) {
    if (acc) force.scl(myMass);
    myBody.applyForceToCenter(force, true);
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
    float dmg = absImpulse / myMass / DURABILITY;
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

  public void maybePulled(SolShip ship, Vector2 pullerPos, float radius) {
    if (ship == myOwner) return;
    Vector2 toPuller = SolMath.getVec(pullerPos);
    toPuller.sub(getPos());
    float pullerDist = toPuller.len();
    if (0 < pullerDist && pullerDist < radius) {
      toPuller.scl(PULL_DESIRED_SPD /pullerDist);
      Vector2 spd = myBody.getLinearVelocity();
      Vector2 spdDiff = SolMath.distVec(spd, toPuller);
      float spdDiffLen = spdDiff.len();
      if (spdDiffLen > 0) {
        spdDiff.scl(PULL_FORCE / spdDiffLen);
        myBody.applyForceToCenter(spdDiff, true);
      }
      SolMath.free(spdDiff);
    }
    SolMath.free(toPuller);
  }

  public SolItem getItem() {
    return myLife > 0 ? myItem : null;
  }

  public void setLife(int life) {
    myLife = life;
  }

  public SolShip getOwner() {
    return myOwner;
  }

  public void pickedUp(SolGame game, SolShip ship) {
    myLife = 0;
    Vector2 spd = new Vector2(ship.getPos());
    spd.sub(myPos);
    float fadeTime = .25f;
    spd.scl(1 / fadeTime);
    spd.add(ship.getSpd());
    game.getPartMan().blip(game, myPos, myAngle, SZ, fadeTime, spd, myItem.getIcon(game));
    game.getSoundMan().play(game, myItem.getItemType().pickUpSound, null, this);
  }
}
