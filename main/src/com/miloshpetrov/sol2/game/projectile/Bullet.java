package com.miloshpetrov.sol2.game.projectile;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.miloshpetrov.sol2.Const;
import com.miloshpetrov.sol2.common.Col;
import com.miloshpetrov.sol2.common.SolMath;
import com.miloshpetrov.sol2.game.*;
import com.miloshpetrov.sol2.game.dra.Dra;
import com.miloshpetrov.sol2.game.dra.DraLevel;
import com.miloshpetrov.sol2.game.ship.SolShip;

import java.util.ArrayList;
import java.util.List;

public class Bullet implements Projectile {

  private final Vector2 myPos;
  private final Vector2 mySpd;
  private final Vector2 myPrevPos;
  private final ArrayList<Dra> myDras;
  private final MyRayBack myRayBack;
  private final Fraction myFraction;
  private final float myDmg;
  private final TextureAtlas.AtlasRegion myTex;
  private final float myWidth;
  private final float myRadius;
  private final boolean myExplode;

  public Bullet(SolGame game, float angle, Vector2 muzzlePos, Vector2 gunSpd, Fraction fraction, float dmg,
    TextureAtlas.AtlasRegion tex, float width, float spdLen, boolean explode)
  {
    myDmg = dmg;
    myTex = tex;
    myWidth = width;
    myPos = new Vector2(muzzlePos);
    myPrevPos = new Vector2(muzzlePos);
    mySpd = new Vector2();
    SolMath.fromAl(mySpd, angle, spdLen);
    mySpd.add(gunSpd);
    myDras = new ArrayList<Dra>();
    myDras.add(new MyDra(this, game.getTexMan().whiteTex));
    myRayBack = new MyRayBack();
    myFraction = fraction;
    myRadius = spdLen * Const.REAL_TIME_STEP;
    myExplode = explode;
  }

  @Override
  public void update(SolGame game) {
    if (myRayBack.obstacle != null) return;
    myPrevPos.set(myPos);
    Vector2 diff = SolMath.getVec(mySpd);
    diff.scl(game.getTimeStep());
    myPos.add(diff);
    SolMath.free(diff);
    game.getObjMan().getWorld().rayCast(myRayBack, myPrevPos, myPos);
    if (myRayBack.obstacle != null) {
      if (myExplode) {
        game.getPartMan().explode(myRayBack.collPoint, game, false);
      } else {
        game.getPartMan().spark(myRayBack.collPoint, game);
      }
      if (myRayBack.obstacle instanceof SolObj) {
        ((SolObj) myRayBack.obstacle).receiveDmg(myDmg, game, myRayBack.collPoint);
      }
    }
  }

  @Override
  public boolean shouldBeRemoved(SolGame game) {
    return myRayBack.obstacle != null;
  }

  @Override
  public void onRemove(SolGame game) {
  }

  @Override
  public float getRadius() {
    return myRadius;
  }

  @Override
  public void receiveDmg(float dmg, SolGame game, Vector2 pos) {
  }

  @Override
  public boolean receivesGravity() {
    return true;
  }

  @Override
  public void receiveAcc(Vector2 acc, SolGame game) {
    Vector2 diff = SolMath.getVec(acc);
    diff.mul(game.getTimeStep());
    mySpd.add(diff);
    SolMath.free(diff);
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
    return SolMath.angle(mySpd);
  }

  @Override
  public Vector2 getSpd() {
    return mySpd;
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

  private class MyRayBack implements RayCastCallback {
    public Object obstacle;
    public Vector2 collPoint;

    private MyRayBack() {
      collPoint = new Vector2();
    }

    @Override
    public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal, float fraction) {
      if (fixture.getFilterData().categoryBits == 0) return -1;
      Object o = fixture.getBody().getUserData();
      if (o instanceof SolShip && ((SolShip) o).getPilot().getFraction() == myFraction) {
        return -1;
      }
      if (o instanceof Rocket && ((Rocket) o).getFraction() == myFraction) {
        return -1;
      }
      obstacle = o;
      collPoint.set(point);

      return 0;
    }
  }

  private static class MyDra implements Dra {
    private final Bullet myBullet;

    public MyDra(Bullet bullet, TextureAtlas.AtlasRegion tex) {
      myBullet = bullet;
    }

    @Override
    public Texture getTex() {
      return myBullet.myTex.getTexture();
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
      return myBullet.getPos();
    }

    @Override
    public Vector2 getRelPos() {
      return Vector2.Zero;
    }

    @Override
    public float getRadius() {
      return myBullet.myRadius;
    }

    @Override
    public void draw(Drawer drawer, SolGame game) {
      float h = myBullet.myWidth;
      Vector2 pos = myBullet.myPos;
      float w = myBullet.myPrevPos.dst(pos);
      if (w < h) w = h;
      drawer.draw(myBullet.myTex, w, h, w, h / 2, pos.x, pos.y, SolMath.angle(myBullet.myPrevPos, pos), Col.LG);
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
