package com.miloshpetrov.sol2.game.projectile;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.miloshpetrov.sol2.Const;
import com.miloshpetrov.sol2.common.SolMath;
import com.miloshpetrov.sol2.game.SolGame;
import com.miloshpetrov.sol2.game.SolObj;

public class BallProjectileBody implements ProjectileBody {
  private final Body myBody;
  private final Vector2 myPos;
  private final Vector2 mySpd;

  private float myAngle;
  private Object myObstacle;

  public BallProjectileBody(SolGame game, Vector2 pos, float angle, Bullet projectile, float physSize, Vector2 gunSpd,
    float spdLen) {
    myBody = buildBall(game, pos, angle, projectile, physSize, gunSpd, spdLen);
    myPos = new Vector2();
    mySpd = new Vector2();
    setParamsFromBody();
  }

  private void setParamsFromBody() {
    myPos.set(myBody.getPosition());
    myAngle = myBody.getAngle() * SolMath.radDeg;
    mySpd.set(myBody.getLinearVelocity());
  }

  private static Body buildBall(SolGame game, Vector2 pos, float angle, Bullet projectile, float physSize, Vector2 gunSpd,
    float spdLen) {
    BodyDef bd = new BodyDef();
    bd.type = BodyDef.BodyType.DynamicBody;
    bd.angle = angle * SolMath.degRad;
    bd.angularDamping = 0;
    bd.position.set(pos);
    bd.linearDamping = 0;
    SolMath.fromAl(bd.linearVelocity, angle, spdLen);
    bd.linearVelocity.add(gunSpd);
    Body body = game.getObjMan().getWorld().createBody(bd);
    FixtureDef fd = new FixtureDef();
    fd.density = 1;
    fd.friction = Const.FRICTION;
    fd.shape = new CircleShape();
    fd.shape.setRadius(physSize/2);
    body.createFixture(fd);
    fd.shape.dispose();
    body.setUserData(projectile);
    return body;
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
  public void handleContact(SolObj other, Contact contact, ContactImpulse impulse, boolean isA, float absImpulse,
    SolGame game)
  {
    if (myObstacle != null) return;
    if (other != null) {
      myPos.set(contact.getWorldManifold().getPoints()[0]);
      myObstacle = other;
    }
  }
}
