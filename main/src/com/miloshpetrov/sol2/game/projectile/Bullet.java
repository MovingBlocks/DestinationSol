package com.miloshpetrov.sol2.game.projectile;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.miloshpetrov.sol2.Const;
import com.miloshpetrov.sol2.common.Col;
import com.miloshpetrov.sol2.common.SolMath;
import com.miloshpetrov.sol2.game.*;
import com.miloshpetrov.sol2.game.dra.*;
import com.miloshpetrov.sol2.game.ship.SolShip;

import java.util.ArrayList;
import java.util.List;

public class Bullet implements Projectile {

  private final ArrayList<Dra> myDras;
  private final float myDmg;
  private final float myRadius;
  private final boolean myExplode;
  private final ProjectileBody myBody;
  private final Fraction myFraction;

  public Bullet(SolGame game, float angle, Vector2 muzzlePos, Vector2 gunSpd, Fraction fraction, float dmg,
    TextureAtlas.AtlasRegion tex, float sz, float spdLen, boolean explode, boolean stretch)
  {
    myDmg = dmg;
    myDras = new ArrayList<Dra>();
    Dra dra;
    if (stretch) {
      dra = new MyDra(this, tex, sz);
    } else {
      dra = new RectSprite(tex, sz, 0, 0, new Vector2(), DraLevel.PROJECTILES, 0, 0, Col.W);
    }
    myDras.add(dra);
    myRadius = spdLen * Const.REAL_TIME_STEP;
    myExplode = explode;
    myBody = new PointProjectileBody(angle, muzzlePos, gunSpd, fraction, spdLen, this);
    myFraction = fraction;
  }

  @Override
  public void update(SolGame game) {
    myBody.update(game);
    Object obstacle = myBody.getObstacle();
    if (obstacle != null) {
      explode(game);
      if (obstacle instanceof SolObj) {
        ((SolObj) obstacle).receiveDmg(myDmg, game, myBody.getPos());
      }
    }
  }

  private void explode(SolGame game) {
    Vector2 pos = myBody.getPos();
    if (myExplode) {
      game.getPartMan().explode(pos, game, false);
    } else {
      game.getPartMan().spark(pos, game);
    }
  }

  @Override
  public boolean shouldBeRemoved(SolGame game) {
    return myBody.getObstacle() != null;
  }

  @Override
  public void onRemove(SolGame game) {
    myBody.onRemove(game);
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
  public void handleContact(SolObj other, Contact contact, ContactImpulse impulse, boolean isA, float absImpulse,
    SolGame game)
  {
    myBody.handleContact(other, contact, impulse, isA, absImpulse, game);
  }

  @Override
  public String toDebugString() {
    return null;
  }

  public Fraction getFraction() {
    return myFraction;
  }

  public boolean shouldCollide(Object o) {
    if (o instanceof SolShip) {
      return ((SolShip) o).getPilot().getFraction() != myFraction;
    }
    if (o instanceof Bullet) {
      return ((Bullet) o).myFraction != myFraction;
    }
    return true;
  }


  private static class MyDra implements Dra {
    private final Bullet myBullet;
    private final TextureAtlas.AtlasRegion myTex;
    private final float myWidth;

    public MyDra(Bullet bullet, TextureAtlas.AtlasRegion tex, float width) {
      myBullet = bullet;
      myTex = tex;
      myWidth = width;
    }

    @Override
    public Texture getTex() {
      return myTex.getTexture();
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
      float h = myWidth;
      Vector2 pos = myBullet.getPos();
      float w = myBullet.getSpd().len() * game.getTimeStep();
      if (w < h) w = h;
      drawer.draw(myTex, w, h, w, h / 2, pos.x, pos.y, SolMath.angle(myBullet.getSpd()), Col.LG);
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
