package com.miloshpetrov.sol2.game.asteroid;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.miloshpetrov.sol2.Const;
import com.miloshpetrov.sol2.common.SolMath;
import com.miloshpetrov.sol2.game.*;
import com.miloshpetrov.sol2.game.dra.Dra;
import com.miloshpetrov.sol2.game.dra.DraMan;
import com.miloshpetrov.sol2.game.planet.TileObj;

import java.util.ArrayList;
import java.util.List;


public class Asteroid implements SolObj {

  public static final float MIN_SPLIT_SZ = .25f;
  public static final float MIN_BURN_SZ = .3f;

  public static final float SZ_TO_LIFE = 40f;
  public static final float SPD_TO_ATM_DMG = SZ_TO_LIFE * .11f;
  public static final float MAX_SPLIT_SPD = 1f;
  private final Body myBody;
  private final Vector2 myPos;
  private final Vector2 mySpd;
  private final ArrayList<Dra> myDras;
  private final TextureAtlas.AtlasRegion myTex;
  private final RemoveController myRemoveController;
  private final float myRadius;
  private float myAngle;
  private float myLife;
  private float mySize;


  public Asteroid(SolGame game, TextureAtlas.AtlasRegion tex, Body body, float size, RemoveController removeController, ArrayList<Dra> dras) {
    myTex = tex;
    myRemoveController = removeController;
    myDras = dras;
    myBody = body;
    mySize = size;
    myLife = SZ_TO_LIFE * mySize;
    myPos = new Vector2();
    mySpd = new Vector2();
    myRadius = DraMan.radiusFromDras(myDras);
    setParamsFromBody();
  }

  @Override
  public Vector2 getPos() {
    return myPos;
  }

  @Override
  public FarObj toFarObj() {
    float rotSpd = myBody.getAngularVelocity();
    return new FarAsteroid(myTex, myPos, myAngle, myRemoveController, myRadius, mySize, mySpd, rotSpd);
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
    return myBody.getLinearVelocity();
  }

  @Override
  public void handleContact(SolObj other, Contact contact, ContactImpulse impulse, boolean isA, float absImpulse,
    SolGame game) {
    float dmg;
    if (other instanceof TileObj && MIN_BURN_SZ < mySize) {
      dmg = myLife;
    } else {
      dmg = absImpulse / myBody.getMass();
    }
    receiveDmg(dmg, game, null, DmgType.CRASH);
    if (absImpulse >= .1f) {
      game.getSoundMan().play(game, game.getSpecialSounds().rockColl, myPos, this, absImpulse * Const.IMPULSE_TO_COLL_VOL);
    }
  }

  @Override
  public String toDebugString() {
    return null;
  }

  @Override
  public void update(SolGame game) {
    setParamsFromBody();
  }

  private void setParamsFromBody() {
    myPos.set(myBody.getPosition());
    mySpd.set(myBody.getLinearVelocity());
    myAngle = myBody.getAngle() * SolMath.radDeg;
  }

  @Override
  public boolean shouldBeRemoved(SolGame game) {
    return myLife <= 0 || myRemoveController != null && myRemoveController.shouldRemove(myPos);
  }

  @Override
  public void onRemove(SolGame game) {
    myBody.getWorld().destroyBody(myBody);
    maybeSplit(game);
  }

  private void maybeSplit(SolGame game) {
    if (myLife > 0 || MIN_SPLIT_SZ > mySize) return;
    game.getSoundMan().play(game, game.getSpecialSounds().asteroidSplit, myPos, this);
    float sclSum = 0;
    while (sclSum < .7f * mySize * mySize) {
      Vector2 newPos = new Vector2();
      float relAngle = SolMath.rnd(180);
      SolMath.fromAl(newPos, relAngle, SolMath.rnd(0, mySize /2));
      newPos.add(myPos);
      Vector2 spd = new Vector2();
      SolMath.fromAl(spd, relAngle, SolMath.rnd(0, .5f) *MAX_SPLIT_SPD);
      spd.add(mySpd);
      float sz = mySize * SolMath.rnd(.25f,.5f);
      Asteroid a = game.getAsteroidBuilder().buildNew(game, newPos, spd, sz, myRemoveController);
      game.getObjMan().addObjDelayed(a);
      sclSum += a.mySize * a.mySize;
    }
  }

  @Override
  public float getRadius() {
    return myRadius;
  }

  @Override
  public void receiveDmg(float dmg, SolGame game, Vector2 pos, DmgType dmgType) {
    boolean wasAlive = myLife > 0;
    myLife -= dmg;
    if (wasAlive && myLife <= 0) {
    }
  }

  @Override
  public boolean receivesGravity() {
    return true;
  }

  @Override
  public void receiveAcc(Vector2 acc, SolGame game) {
    acc.scl(myBody.getMass());
    myBody.applyForceToCenter(acc, true);
    if (MIN_BURN_SZ < mySize) {
      float dmg = myBody.getLinearVelocity().len() * SPD_TO_ATM_DMG * game.getTimeStep();
      this.receiveDmg(dmg, game, null, DmgType.FIRE); //todo: fire sprite here
    }
  }

  public float getLife() {
    return myLife;
  }
}

