package com.miloshpetrov.sol2.game.projectile;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.miloshpetrov.sol2.common.SolMath;
import com.miloshpetrov.sol2.game.SolGame;
import com.miloshpetrov.sol2.game.asteroid.AsteroidBuilder;

public class BallProjectileBody implements ProjectileBody {
  private final Body myBody;
  private final Vector2 myPos;
  private final Vector2 mySpd;
  private final float myAcc;

  private float myAngle;

  public BallProjectileBody(SolGame game, Vector2 pos, float angle, Projectile projectile,
    Vector2 gunSpd, float spdLen, ProjectileConfig config)
  {
    float density = config.density == -1 ? 1 : config.density;
    myBody = AsteroidBuilder.buildBall(game, pos, angle, config.physSize / 2, density, config.massless);

    mySpd = new Vector2();
    SolMath.fromAl(mySpd, angle, spdLen);
    mySpd.add(gunSpd);
    myBody.setLinearVelocity(mySpd);
    myBody.setUserData(projectile);

    myPos = new Vector2();
    myAcc = config.acc;
    setParamsFromBody();
  }

  private void setParamsFromBody() {
    myPos.set(myBody.getPosition());
    myAngle = myBody.getAngle() * SolMath.radDeg;
    mySpd.set(myBody.getLinearVelocity());
  }

  @Override
  public void update(SolGame game) {
    setParamsFromBody();
    if (myAcc > 0 && SolMath.canAccelerate(myAngle, mySpd)) {
      Vector2 force = SolMath.fromAl(myAngle, myAcc * myBody.getMass());
      myBody.applyForceToCenter(force, true);
      SolMath.free(force);
    }
  }

  @Override
  public Vector2 getPos() {
    return myPos;
  }

  @Override
  public Vector2 getSpd() {
    return mySpd;
  }

  @Override
  public void receiveForce(Vector2 force, SolGame game, boolean acc) {
    if (acc) force.scl(myBody.getMass());
    myBody.applyForceToCenter(force, true);
  }

  @Override
  public void onRemove(SolGame game) {
    myBody.getWorld().destroyBody(myBody);
  }

  @Override
  public float getAngle() {
    return myAngle;
  }

  @Override
  public void changeAngle(float diff) {
    myAngle += diff;
    myBody.setTransform(myPos, myAngle * SolMath.degRad);
  }

  @Override
  public float getDesiredAngle(Vector2 nePos) {
    float spdLen = mySpd.len();
    float toNe = SolMath.angle(myPos, nePos);
    Vector2 desiredSpd = SolMath.fromAl(toNe, spdLen);
    float res = SolMath.angle(mySpd, desiredSpd);
    SolMath.free(desiredSpd);
    return res;
  }
}
