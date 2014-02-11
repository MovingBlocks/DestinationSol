package com.miloshpetrov.sol2.game.particle;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.badlogic.gdx.math.Vector2;
import com.miloshpetrov.sol2.common.SolMath;
import com.miloshpetrov.sol2.game.*;
import com.miloshpetrov.sol2.game.dra.Dra;
import com.miloshpetrov.sol2.game.dra.DraLevel;

public class ParticleSrc implements Dra {

  private final ParticleEmitter myEmitter;
  private final ParticleEmitter.ScaledNumericValue myOrigSpdAngle;
  private final ParticleEmitter.ScaledNumericValue myOrigRot;
  private final DraLevel myDraLevel;
  public final Vector2 relPos;
  private Vector2 myPos;
  private boolean myWorking;

  /**
   * consumes relPos
   */
  public ParticleSrc(ParticleEmitter e, boolean continuous, DraLevel draLevel, Vector2 relPos) {
    this.relPos = relPos;
    myEmitter = new ParticleEmitter(e);
    myOrigSpdAngle = new ParticleEmitter.ScaledNumericValue();
    transferAngle(myEmitter.getAngle(), myOrigSpdAngle, 0f);
    myOrigRot = new ParticleEmitter.ScaledNumericValue();
    transferAngle(myEmitter.getRotation(), myOrigRot, 0f);

    if (continuous) {
      myEmitter.setContinuous(true);
      myEmitter.allowCompletion();
    }
    myDraLevel = draLevel;
    myPos = new Vector2();
  }

  private void transferAngle(ParticleEmitter.ScaledNumericValue from, ParticleEmitter.ScaledNumericValue to, float diff) {
    if (!to.isRelative()) to.setHigh(from.getHighMin() + diff, from.getHighMax() + diff);
    to.setLow(from.getLowMin() + diff, from.getLowMax() + diff);
  }

  public void setWorking(boolean working) {
    if (myWorking == working) return;
    myWorking = working;
    if (myWorking) myEmitter.start();
    else myEmitter.allowCompletion();
  }

  public boolean isComplete() {
    return myEmitter.isComplete();
  }

  public void update(SolGame game, SolObj o) {
    Vector2 basePos = o.getPos();
    float baseAngle = o.getAngle();
    SolMath.toWorld(myPos, relPos, baseAngle, basePos);
    float ts = game.getTimeStep();
    myPos.x -= myEmitter.getWind().getLowMin() * ts;
    myPos.y -= myEmitter.getGravity().getLowMin() * ts;
    myEmitter.setPosition(myPos.x, myPos.y);
    setAngle(baseAngle);
    myEmitter.update(ts);
  }

  private void setAngle(float angle) {
    transferAngle(myOrigSpdAngle, myEmitter.getAngle(), angle);
    boolean includeSpriteAngle = true;
    if (includeSpriteAngle) {
      transferAngle(myOrigRot, myEmitter.getRotation(), angle);
    }
  }

  public void setSpd(Vector2 spd) {
    if (spd == null) return;
    ParticleEmitter.ScaledNumericValue w = myEmitter.getWind();
    w.setActive(true);
    w.setHigh(spd.x);
    w.setLow(spd.x);
    ParticleEmitter.ScaledNumericValue g = myEmitter.getGravity();
    g.setActive(true);
    g.setHigh(spd.y);
    g.setLow(spd.y);
  }

  @Override
  public void prepare(SolObj o) {
  }

  @Override
  public Vector2 getPos() {
    return myPos;
  }

  @Override
  public Vector2 getRelPos() {
    return relPos;
  }

  @Override
  public float getRadius() {
    return 1;
  }

  @Override
  public void draw(Drawer drawer, SolGame game) {
    drawer.draw(myEmitter);
  }

  @Override
  public boolean isEnabled() {
    return true;
  }

  @Override
  public boolean okToRemove() {
    return isComplete();
  }

  @Override
  public DraLevel getLevel() {
    return myDraLevel;
  }

  @Override
  public Texture getTex() {
    return myEmitter.getSprite().getTexture();
  }

  public boolean isContinuous() {
    return myEmitter.isContinuous();
  }
}
