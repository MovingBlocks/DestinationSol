package com.miloshpetrov.sol2.game.projectile;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.miloshpetrov.sol2.common.SolMath;
import com.miloshpetrov.sol2.game.SolGame;
import com.miloshpetrov.sol2.game.SolObj;

public class PointProjectileBody implements ProjectileBody {
  private final Vector2 myPos;
  private final Vector2 mySpd;
  private final MyRayBack myRayBack;

  public PointProjectileBody(float angle, Vector2 muzzlePos, Vector2 gunSpd, float spdLen,
    Projectile projectile)
  {
    myPos = new Vector2(muzzlePos);
    mySpd = new Vector2();
    SolMath.fromAl(mySpd, angle, spdLen);
    mySpd.add(gunSpd);
    myRayBack = new MyRayBack(projectile);
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
  public SolObj getObstacle() {
    return myRayBack.obstacle;
  }

  @Override
  public float getAngle() {
    return SolMath.angle(mySpd);
  }

  @Override
  public void handleContact(SolObj other, ContactImpulse impulse, boolean isA, float absImpulse,
    SolGame game, Vector2 collPos)
  {
  }

  @Override
  public void setSpd(Vector2 spd) {
    mySpd.set(spd);
  }


  private static class MyRayBack implements RayCastCallback {
    public final Projectile myProjectile;
    public SolObj obstacle;
    public final Vector2 collPoint;

    private MyRayBack(Projectile projectile) {
      this.myProjectile = projectile;
      collPoint = new Vector2();
    }

    @Override
    public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal, float fraction) {
      if (fixture.getFilterData().categoryBits == 0) return -1;
      SolObj o = (SolObj) fixture.getBody().getUserData();
      if (!myProjectile.shouldCollide(o)) return -1;
      obstacle = o;
      collPoint.set(point);
      return 0;
    }
  }
}
