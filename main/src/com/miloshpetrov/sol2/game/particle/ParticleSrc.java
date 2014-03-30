package com.miloshpetrov.sol2.game.particle;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.Vector2;
import com.miloshpetrov.sol2.common.SolMath;
import com.miloshpetrov.sol2.game.*;
import com.miloshpetrov.sol2.game.dra.Dra;
import com.miloshpetrov.sol2.game.dra.DraLevel;

public class ParticleSrc implements Dra {
  public static final float MOVING_AREA_THRESH = 1f;
  public static final float MAX_TIME_BETWEEN_POS_CHANGE = .25f;
  private final ParticleEmitter myEmitter;
  private final ParticleEmitter.ScaledNumericValue myOrigSpdAngle;
  private final ParticleEmitter.ScaledNumericValue myOrigRot;
  private final DraLevel myDraLevel;
  private final Vector2 myRelPos;
  private final Vector2 myOrigRelPos;
  private final TextureAtlas.AtlasRegion myTex;
  private final float myAreaSz;

  private Vector2 myPos;
  private boolean myWorking;
  private float myTimeSincePosChange;

  public ParticleSrc(EffectType effectType, float sz, DraLevel draLevel, Vector2 relPos, TextureAtlas.AtlasRegion tex) {
    myEmitter = effectType.newEmitter();
    myDraLevel = draLevel;
    myRelPos = new Vector2(relPos);
    myOrigRelPos = new Vector2(relPos);
    myTex = tex;
    myPos = new Vector2();

    if (myEmitter.getSpawnShape().getShape() == ParticleEmitter.SpawnShape.point &&
      myEmitter.getVelocity().getHighMax() < MOVING_AREA_THRESH)
    {
      myAreaSz = sz;
    } else {
      applySz(sz);
      myAreaSz = 0;
    }
    myEmitter.setSprite(new Sprite(myTex.getTexture()));

    myOrigSpdAngle = new ParticleEmitter.ScaledNumericValue();
    transferAngle(myEmitter.getAngle(), myOrigSpdAngle, 0f);
    myOrigRot = new ParticleEmitter.ScaledNumericValue();
    transferAngle(myEmitter.getRotation(), myOrigRot, 0f);

    if (!myEmitter.isContinuous()) {
      myEmitter.start();
    } else {
      // this is needed because continuous effects are initially started
      myEmitter.allowCompletion();
    }
  }

  private void applySz(float sz) {
    mulVal(myEmitter.getEmission(), sz * sz);
    ParticleEmitter.SpawnShapeValue sh = myEmitter.getSpawnShape();
    if (sh.getShape() == ParticleEmitter.SpawnShape.point) {
      ParticleEmitter.ScaledNumericValue vel = myEmitter.getVelocity();
      vel.setHighMax(vel.getHighMax() * sz);
    } else if (sh.getShape() == ParticleEmitter.SpawnShape.ellipse) {
      mulVal(myEmitter.getSpawnWidth(), sz);
      mulVal(myEmitter.getSpawnHeight(), sz);
    } else {
      throw new AssertionError("unsupported effect spawn shape");
    }
  }

  private void mulVal(ParticleEmitter.ScaledNumericValue val, float mul) {
    val.setHigh(val.getHighMin() * mul, val.getHighMax() * mul);
    val.setLow(val.getLowMin() * mul, val.getLowMax() * mul);
  }

  private static void transferAngle(ParticleEmitter.ScaledNumericValue from, ParticleEmitter.ScaledNumericValue to, float diff) {
    if (!to.isRelative()) to.setHigh(from.getHighMin() + diff, from.getHighMax() + diff);
    to.setLow(from.getLowMin() + diff, from.getLowMax() + diff);
  }

  public void setWorking(boolean working) {
    if (!myEmitter.isContinuous()) throw new AssertionError("only continuous emitters can start working");
    if (myWorking == working) return;
    myWorking = working;
    if (myWorking) myEmitter.start();
    else myEmitter.allowCompletion();
  }

  public boolean isComplete() {
    return myEmitter.isComplete();
  }

  public void update(SolGame game, SolObj o) {
    maybeSwitchRelPos(game);
    Vector2 basePos = o.getPos();
    float baseAngle = o.getAngle();
    SolMath.toWorld(myPos, myRelPos, baseAngle, basePos);
    float ts = game.getTimeStep();
    fixSpeedBug(ts);
    myEmitter.setPosition(myPos.x, myPos.y);
    setAngle(baseAngle);
    myEmitter.update(ts);
  }

  private void maybeSwitchRelPos(SolGame game) {
    if (myAreaSz == 0) return;
    float ts = game.getTimeStep();
    myTimeSincePosChange += ts;
    if (myTimeSincePosChange < MAX_TIME_BETWEEN_POS_CHANGE) return;
    myTimeSincePosChange = 0;
    SolMath.fromAl(myRelPos, SolMath.rnd(180), SolMath.rnd(0, myAreaSz));
    myRelPos.add(myOrigRelPos);
  }

  private void fixSpeedBug(float ts) {
    myPos.x -= myEmitter.getWind().getLowMin() * ts;
    myPos.y -= myEmitter.getGravity().getLowMin() * ts;
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
    return myRelPos;
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
  public Texture getTex0() {
    return myTex.getTexture();
  }

  @Override
  public TextureAtlas.AtlasRegion getTex() {
    return myTex;
  }

  public boolean isContinuous() {
    return myEmitter.isContinuous();
  }

  public boolean isWorking() {
    return myWorking;
  }
}
