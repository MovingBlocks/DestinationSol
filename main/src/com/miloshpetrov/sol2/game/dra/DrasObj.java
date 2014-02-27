package com.miloshpetrov.sol2.game.dra;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.miloshpetrov.sol2.Const;
import com.miloshpetrov.sol2.common.Consumed;
import com.miloshpetrov.sol2.common.SolMath;
import com.miloshpetrov.sol2.game.*;
import com.miloshpetrov.sol2.game.planet.Planet;

import java.util.List;

public class DrasObj implements SolObj {
  private final Vector2 myPos;
  private final Vector2 mySpd;
  private final RemoveController myRemoveController;
  private final boolean myHideOnPlanet;
  private final Vector2 myMoveDiff;
  private final List<Dra> myDras;
  private final float myRadius;
  private final boolean myTemporary;

  private float myMaxFadeTime;
  private float myFadeTime;

  public DrasObj(List<Dra> dras, @Consumed Vector2 pos, @Consumed Vector2 spd, RemoveController removeController, boolean temporary, boolean hideOnPlanet) {
    myDras = dras;
    myPos = pos;
    mySpd = spd;
    myRemoveController = removeController;
    myHideOnPlanet = hideOnPlanet;
    myMoveDiff = new Vector2();
    myRadius = DraMan.radiusFromDras(myDras);
    myTemporary = temporary;

    myMaxFadeTime = -1;
    myFadeTime = -1;
  }

  @Override
  public void update(SolGame game) {
    myMoveDiff.set(mySpd);
    float ts = game.getTimeStep();
    myMoveDiff.scl(ts);
    myPos.add(myMoveDiff);
    if (myHideOnPlanet) {
      Planet np = game.getPlanetMan().getNearestPlanet();
      Vector2 npPos = np.getPos();
      float npgh = np.getGroundHeight();
      DraMan draMan = game.getDraMan();
      for (Dra dra : myDras) {
        if (!(dra instanceof RectSprite)) continue;
        if (!draMan.isInCam(dra)) continue;
        Vector2 draPos = dra.getPos();
        float gradSz = .25f * Const.ATM_HEIGHT;
        float distPerc = (draPos.dst(npPos) - npgh - Const.ATM_HEIGHT) / gradSz;
        distPerc = SolMath.clamp(distPerc, 0, 1);
        ((RectSprite) dra).tint.a = distPerc;
      }
    } else if (myMaxFadeTime > 0) {
      myFadeTime -= ts;
      float tintPerc = myFadeTime / myMaxFadeTime;
      for (Dra dra : myDras) {
        if (!(dra instanceof RectSprite)) continue;
        ((RectSprite) dra).tint.a = SolMath.clamp(tintPerc, 0, 1);
      }

    }
  }

  @Override
  public boolean shouldBeRemoved(SolGame game) {
    if (myMaxFadeTime > 0 && myFadeTime <= 0) return true;
    if (myTemporary) {
      boolean rem = true;
      for (Dra dra : myDras) {
        if (!dra.okToRemove()) {
          rem = false;
          break;
        }
      }
      if (rem) return true;
    }
    return myRemoveController != null && myRemoveController.shouldRemove(myPos);
  }

  @Override
  public void onRemove(SolGame game) {
  }

  @Override
  public float getRadius() {
    return myRadius;
  }

  @Override
  public void receiveDmg(float dmg, SolGame game, Vector2 pos, DmgType dmgType) {
  }

  @Override
  public boolean receivesGravity() {
    return false;
  }

  @Override
  public void receiveAcc(Vector2 acc, SolGame game) {
  }

  @Override
  public Vector2 getPos() {
    return myPos;
  }

  @Override
  public FarObj toFarObj() {
    return myTemporary ? null : new FarDras(myDras, myPos, mySpd, myRemoveController, myRadius, myHideOnPlanet);
  }

  @Override
  public List<Dra> getDras() {
    return myDras;
  }

  @Override
  public float getAngle() {
    return 0;
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

  public void fade(float fadeTime) {
    myMaxFadeTime = fadeTime;
    myFadeTime = fadeTime;
  }
}
