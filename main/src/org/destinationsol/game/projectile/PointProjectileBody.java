package org.destinationsol.game.projectile;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import org.destinationsol.Const;
import org.destinationsol.common.SolMath;
import org.destinationsol.game.SolGame;
import org.destinationsol.game.SolObject;
import org.destinationsol.game.ship.SolShip;

public class PointProjectileBody implements ProjectileBody {
  private final Vector2 myPos;
  private final Vector2 mySpd;
  private final MyRayBack myRayBack;
  private final float myAcc;

  public PointProjectileBody(float angle, Vector2 muzzlePos, Vector2 gunSpd, float spdLen,
    Projectile projectile, SolGame game, float acc)
  {
    myPos = new Vector2(muzzlePos);
    mySpd = new Vector2();
    SolMath.fromAl(mySpd, angle, spdLen);
    mySpd.add(gunSpd);
    myRayBack = new MyRayBack(projectile, game);
    myAcc = acc;
  }

  @Override
  public void update(SolGame game) {
    if (myAcc > 0 && SolMath.canAccelerate(myAcc, mySpd)) {
      float spdLen = mySpd.len();
      if (spdLen < Const.MAX_MOVE_SPD) {
        mySpd.scl((spdLen + myAcc) / spdLen);
      }
    }
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
    if (!acc) force.scl(10f);
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
  public void changeAngle(float diff) {
    SolMath.rotate(mySpd, diff);
  }

  @Override
  public float getDesiredAngle(SolShip ne) {
    return SolMath.angle(myPos, ne.getPos());
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
      SolObject o = (SolObject) fixture.getBody().getUserData();
      boolean oIsMassless = o instanceof Projectile && ((Projectile) o).isMassless();
      if (!oIsMassless && myProjectile.shouldCollide(o, fixture, myGame.getFactionMan())) {
        myPos.set(point);
        myProjectile.setObstacle(o, myGame);
        return 0;
      }
      return -1;
    }
  }
}
