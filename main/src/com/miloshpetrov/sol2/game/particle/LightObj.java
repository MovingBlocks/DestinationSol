package com.miloshpetrov.sol2.game.particle;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.miloshpetrov.sol2.game.*;
import com.miloshpetrov.sol2.game.dra.Dra;
import com.miloshpetrov.sol2.game.dra.DraMan;

import java.util.ArrayList;
import java.util.List;

public class LightObj implements SolObj {

  private final LightSrc myLightSrc;
  private final ArrayList<Dra> myDras;
  private final float myRadius;
  private final Vector2 myPos;

  // consumes pos
  public LightObj(SolGame game, float sz, boolean hasHalo, float intensity, Vector2 pos, float fadeTime) {
    myPos = pos;
    myLightSrc = new LightSrc(game, sz, hasHalo, intensity, new Vector2());
    myLightSrc.setFadeTime(fadeTime);
    myLightSrc.setWorking();
    myDras = new ArrayList<Dra>();
    myLightSrc.collectDras(myDras);
    myRadius = DraMan.radiusFromDras(myDras);
  }

  @Override
  public void update(SolGame game) {
    myLightSrc.update(false, 0, game);
  }

  @Override
  public boolean shouldBeRemoved(SolGame game) {
    return myLightSrc.isFinished();
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
  public void receiveForce(Vector2 force, SolGame game, boolean acc) {
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
    return 0;
  }

  @Override
  public Vector2 getSpd() {
    return null;
  }

  @Override
  public void handleContact(SolObj other, ContactImpulse impulse, boolean isA, float absImpulse,
    SolGame game, Vector2 collPos)
  {
  }

  @Override
  public String toDebugString() {
    return null;
  }

  @Override
  public Boolean isMetal() {
    return null;
  }

  @Override
  public boolean hasBody() {
    return false;
  }
}
