package com.miloshpetrov.sol2.game.projectile;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.miloshpetrov.sol2.common.SolMath;
import com.miloshpetrov.sol2.game.*;

public class PointProjectileBody implements ProjectileBody {
  private final Vector2 myPos;
  private final Vector2 mySpd;
  private final MyRayBack myRayBack;

  public PointProjectileBody(float angle, Vector2 muzzlePos, Vector2 gunSpd, Fraction fraction, float spdLen,
    Bullet bullet) {
    myPos = new Vector2(muzzlePos);
    mySpd = new Vector2();
    SolMath.fromAl(mySpd, angle, spdLen);
    mySpd.add(gunSpd);
    myRayBack = new MyRayBack(bullet);
  }

  @Override
  public void update(SolGame game) {
    if (myRayBack.obstacle != null) return;
    Vector2 prevPos = SolMath.getVec(myPos);
    Vector2 diff = SolMath.getVec(mySpd);
    diff.scl(game.getTimeStep());
    myPos.add(diff);
    SolMath.free(diff);
    game.getObjMan().getWorld().rayCast(myRayBack, prevPos, myPos);
    SolMath.free(prevPos);
    if (myRayBack.obstacle != null) myPos.set(myRayBack.collPoint);
  }

  @Override
  public Vector2 getPos() {
    return myPos;
  }

  @Override
  public void receiveAcc(Vector2 acc, SolGame game) {
    Vector2 diff = SolMath.getVec(acc);
    diff.scl(game.getTimeStep());
    mySpd.add(diff);
    SolMath.free(diff);
  }

  @Override
  public Vector2 getSpd() {
    return mySpd;
  }

  @Override
  public void onRemove(SolGame game) {

  }

  @Override
  public Object getObstacle() {
    return myRayBack.obstacle;
  }

  @Override
  public float getAngle() {
    return SolMath.angle(mySpd);
  }

  @Override
  public void handleContact(SolObj other, Contact contact, ContactImpulse impulse, boolean isA, float absImpulse,
    SolGame game)
  {
  }


  private static class MyRayBack implements RayCastCallback {
    public final Bullet bullet;
    public Object obstacle;
    public final Vector2 collPoint;

    private MyRayBack(Bullet bullet) {
      this.bullet = bullet;
      collPoint = new Vector2();
    }

    @Override
    public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal, float fraction) {
      if (fixture.getFilterData().categoryBits == 0) return -1;
      Object o = fixture.getBody().getUserData();
      if (!bullet.shouldCollide(o)) return -1;
      obstacle = o;
      collPoint.set(point);
      return 0;
    }
  }
}
