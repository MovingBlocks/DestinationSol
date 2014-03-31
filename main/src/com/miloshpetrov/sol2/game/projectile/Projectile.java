package com.miloshpetrov.sol2.game.projectile;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.miloshpetrov.sol2.Const;
import com.miloshpetrov.sol2.common.Col;
import com.miloshpetrov.sol2.common.SolMath;
import com.miloshpetrov.sol2.game.*;
import com.miloshpetrov.sol2.game.dra.*;
import com.miloshpetrov.sol2.game.particle.*;
import com.miloshpetrov.sol2.game.ship.SolShip;

import java.util.ArrayList;
import java.util.List;

public class Projectile implements SolObj {

  private final ArrayList<Dra> myDras;
  private final float myDmg;
  private final float myRadius;
  private final ProjectileBody myBody;
  private final Fraction myFraction;
  private final ParticleSrc myBodyEffect;
  private final ParticleSrc myTrailEffect;
  private final LightSrc myLightSrc;
  private final ProjectileConfig myConfig;

  private boolean myShouldRemove;

  public Projectile(SolGame game, float angle, Vector2 muzzlePos, Vector2 gunSpd, Fraction fraction, float dmg,
    ProjectileConfig config)
  {
    myDmg = dmg;
    myDras = new ArrayList<Dra>();
    myConfig = config;

    Dra dra;
    if (myConfig.stretch) {
      dra = new MyDra(this, myConfig.tex, myConfig.texSz);
    } else {
      dra = new RectSprite(myConfig.tex, myConfig.texSz, 0, 0, new Vector2(), DraLevel.PROJECTILES, 0, 0, Col.W);
    }
    myDras.add(dra);
    myRadius = myConfig.spdLen * Const.REAL_TIME_STEP;
    if (myConfig.physSize > 0) {
      myBody = new BallProjectileBody(game, muzzlePos, angle, this, myConfig.physSize, gunSpd, myConfig.spdLen);
    } else {
      myBody = new PointProjectileBody(angle, muzzlePos, gunSpd, myConfig.spdLen, this);
    }
    myFraction = fraction;
    myBodyEffect = buildEffect(game, myConfig.bodyEffect, DraLevel.PART_BG_0, null);
    myTrailEffect = buildEffect(game, myConfig.trailEffect, DraLevel.PART_BG_0, null);
    if (myConfig.lightSz > 0) {
      myLightSrc = new LightSrc(game, myConfig.lightSz, true, 1f, new Vector2());
      myLightSrc.collectDras(myDras);
    } else {
      myLightSrc = null;
    }
  }

  private ParticleSrc buildEffect(SolGame game, EffectConfig ec, DraLevel draLevel, Vector2 pos) {
    if (ec == null) return null;
    ParticleSrc res = new ParticleSrc(ec.effectType, ec.sz, draLevel, new Vector2(), ec.tex);
    if (res.isContinuous()) {
      res.setWorking(true);
      myDras.add(res);
    } else {
      game.getPartMan().finish(game, res, pos);
    }
    return res;
  }

  @Override
  public void update(SolGame game) {
    myBody.update(game);
    SolObj obstacle = myBody.getObstacle();
    if (obstacle != null) {
      finish(game);
      Vector2 pos = myBody.getPos();
      obstacle.receiveDmg(myDmg, game, pos, myConfig.dmgType);
      game.getSoundMan().play(game, myConfig.collisionSound, null, obstacle);
    } else {
      if (myBodyEffect != null) myBodyEffect.setSpd(myBody.getSpd());
      if (myLightSrc != null) myLightSrc.update(true, myBody.getAngle(), game);
    }
  }

  private void finish(SolGame game) {
    myShouldRemove = true;
    Vector2 pos = myBody.getPos();
    buildEffect(game, myConfig.collisionEffect1, DraLevel.PART_FG_0, pos);
    buildEffect(game, myConfig.collisionEffect2, DraLevel.PART_FG_1, pos);
    if (myConfig.collisionEffect2 != null) {
      game.getPartMan().blinks(pos, game, myConfig.collisionEffect2.sz);
    }
  }

  @Override
  public boolean shouldBeRemoved(SolGame game) {
    return myShouldRemove;
  }

  @Override
  public void onRemove(SolGame game) {
    Vector2 pos = myBody.getPos();
    if (myBodyEffect != null) game.getPartMan().finish(game, myBodyEffect, pos);
    if (myTrailEffect != null) game.getPartMan().finish(game, myTrailEffect, pos);
    myBody.onRemove(game);
  }

  @Override
  public float getRadius() {
    return myRadius;
  }

  @Override
  public void receiveDmg(float dmg, SolGame game, Vector2 pos, DmgType dmgType) {
    finish(game);
  }

  @Override
  public boolean receivesGravity() {
    return true;
  }

  @Override
  public void receiveAcc(Vector2 acc, SolGame game) {
    myBody.receiveAcc(acc, game);
  }

  @Override
  public Vector2 getPos() {
    return myBody.getPos();
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
    return myBody.getAngle();
  }

  @Override
  public Vector2 getSpd() {
    return myBody.getSpd();
  }

  @Override
  public void handleContact(SolObj other, ContactImpulse impulse, boolean isA, float absImpulse,
    SolGame game, Vector2 collPos)
  {
    myBody.handleContact(other, impulse, isA, absImpulse, game, collPos);
  }

  @Override
  public String toDebugString() {
    return null;
  }

  @Override
  public Boolean isMetal() {
    return null;
  }

  public Fraction getFraction() {
    return myFraction;
  }

  public boolean shouldCollide(SolObj o) {
    if (o instanceof SolShip) {
      return ((SolShip) o).getPilot().getFraction() != myFraction;
    }
    if (o instanceof Projectile) {
      return ((Projectile) o).myFraction != myFraction;
    }
    return true;
  }


  private static class MyDra implements Dra {
    private final Projectile myProjectile;
    private final TextureAtlas.AtlasRegion myTex;
    private final float myWidth;

    public MyDra(Projectile projectile, TextureAtlas.AtlasRegion tex, float width) {
      myProjectile = projectile;
      myTex = tex;
      myWidth = width;
    }

    @Override
    public Texture getTex0() {
      return myTex.getTexture();
    }

    @Override
    public TextureAtlas.AtlasRegion getTex() {
      return myTex;
    }

    @Override
    public DraLevel getLevel() {
      return DraLevel.PROJECTILES;
    }

    @Override
    public void update(SolGame game, SolObj o) {
    }

    @Override
    public void prepare(SolObj o) {
    }

    @Override
    public Vector2 getPos() {
      return myProjectile.getPos();
    }

    @Override
    public Vector2 getRelPos() {
      return Vector2.Zero;
    }

    @Override
    public float getRadius() {
      return myProjectile.myRadius;
    }

    @Override
    public void draw(Drawer drawer, SolGame game) {
      float h = myWidth;
      Vector2 pos = myProjectile.getPos();
      float w = myProjectile.getSpd().len() * game.getTimeStep();
      if (w < h) w = h;
      drawer.draw(myTex, w, h, w, h / 2, pos.x, pos.y, SolMath.angle(myProjectile.getSpd()), Col.LG);
    }

    @Override
    public boolean isEnabled() {
      return true;
    }

    @Override
    public boolean okToRemove() {
      return false;
    }
  }

}
