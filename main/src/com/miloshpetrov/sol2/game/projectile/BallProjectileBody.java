package com.miloshpetrov.sol2.game.projectile;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.miloshpetrov.sol2.common.SolMath;
import com.miloshpetrov.sol2.game.SolGame;
import com.miloshpetrov.sol2.game.SolObj;
import com.miloshpetrov.sol2.game.asteroid.AsteroidBuilder;

public class BallProjectileBody implements ProjectileBody {
  private final Body myBody;
  private final Vector2 myPos;
  private final Vector2 mySpd;

  private float myAngle;
  private Object myObstacle;

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
    if (myObstacle != null) return;
    setParamsFromBody();
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
  public void receiveAcc(Vector2 acc, SolGame game) {
    Vector2 f = SolMath.getVec(acc);
    f.scl(myBody.getMass());
    myBody.applyForceToCenter(f, true);
    SolMath.free(f);
  }

  @Override
  public void onRemove(SolGame game) {
    myBody.getWorld().destroyBody(myBody);
  }

  @Override
  public Object getObstacle() {
    return myObstacle;
  }

  @Override
  public float getAngle() {
    return myAngle;
  }

  @Override
  public void handleContact(SolObj other, ContactImpulse impulse, boolean isA, float absImpulse,
    SolGame game, Vector2 collPos)
  {
    if (myObstacle != null) return;
    if (other != null) {
      myPos.set(collPos);
      myObstacle = other;
    }
  }
}
