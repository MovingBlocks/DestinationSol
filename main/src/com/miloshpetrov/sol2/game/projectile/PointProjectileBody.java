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
    Projectile projectile, SolGame game)
  {
    myPos = new Vector2(muzzlePos);
    mySpd = new Vector2();
    SolMath.fromAl(mySpd, angle, spdLen);
    mySpd.add(gunSpd);
    myRayBack = new MyRayBack(projectile, game);
  }

  @Override
  public void update(SolGame game) {
    Vector2 prevPos = SolMath.getVec(myPos);
    Vector2 diff = SolMath.getVec(mySpd);
    diff.scl(game.getTimeStep());
    myPos.add(diff);
    SolMath.free(diff);
    game.getObjMan().getWorld().rayCast(myRayBack, prevPos, myPos);
    SolMath.free(prevPos);
  }

  @Override
  public Vector2 getPos() {
    return myPos;
  }

  @Override
  public void receiveForce(Vector2 force, SolGame game, boolean acc) {
    force.scl(game.getTimeStep());
    if (acc) force.scl(.1f);
    mySpd.add(force);
  }

  @Override
  public Vector2 getSpd() {
    return mySpd;
  }

  @Override
  public void onRemove(SolGame game) {

  }

  @Override
  public float getAngle() {
    return SolMath.angle(mySpd);
  }

  @Override
  public void setSpd(Vector2 spd) {
    mySpd.set(spd);
  }


  private class MyRayBack implements RayCastCallback {

    private final Projectile myProjectile;
    private final SolGame myGame;

    private MyRayBack(Projectile projectile, SolGame game) {
      myProjectile = projectile;
      myGame = game;
    }

    @Override
    public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal, float fraction) {
      SolObj o = (SolObj) fixture.getBody().getUserData();
      if (myProjectile.maybeCollide(o, fixture, myGame.getFractionMan())) {
        myPos.set(point);
        return 0;
      }
      return -1;
    }
  }
}
