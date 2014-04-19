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

  private float myAngle;

  public BallProjectileBody(SolGame game, Vector2 pos, float angle, Projectile projectile, float physSize, Vector2 gunSpd,
    float spdLen) {
    myBody = AsteroidBuilder.buildBall(game, pos, angle, physSize / 2, 1);

    mySpd = new Vector2();
    SolMath.fromAl(mySpd, angle, spdLen);
    mySpd.add(gunSpd);
    myBody.setLinearVelocity(mySpd);
    myBody.setUserData(projectile);

    myPos = new Vector2();
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
    float spdAngle = SolMath.angle(mySpd);
    float diff = SolMath.norm(spdAngle - myAngle);
    myBody.setAngularVelocity(diff / game.getTimeStep() * SolMath.degRad);
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
  public void setSpd(Vector2 spd) {
    myBody.setLinearVelocity(spd);
    mySpd.set(spd);
  }
}
