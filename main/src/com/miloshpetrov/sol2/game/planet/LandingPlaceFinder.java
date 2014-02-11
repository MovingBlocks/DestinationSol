package com.miloshpetrov.sol2.game.planet;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import com.miloshpetrov.sol2.common.SolMath;
import com.miloshpetrov.sol2.game.SolGame;

import java.util.ArrayList;

public class LandingPlaceFinder {
  private final Vector2 myVec = new Vector2();
  private float myDeviation;

  private final RayCastCallback myRayBack = new RayCastCallback() {
    @Override
    public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal, float fraction) {
      if (!(fixture.getBody().getUserData() instanceof TileObj)) {
        return -1;
      }
      myVec.set(point);
      myDeviation = SolMath.abs(SolMath.angle(normal) + 90);
      return fraction;
    }
  };

  public Vector2 find(SolGame game, Planet p, ArrayList<Float> takenAngles) {
    Vector2 pPos = p.getPos();

    Vector2 res = new Vector2(pPos);
    float minDeviation = 90;

    for (int i = 0; i < 20; i++) {
      float angle = SolMath.rnd(180);
      boolean angleTaken = false;
      if (takenAngles != null) for (Float ta : takenAngles) {
        if (SolMath.angleDiff(angle, ta) < 1) {
          angleTaken = true;
          break;
        }
      }
      if (angleTaken) continue;
      myDeviation = angle;
      SolMath.fromAl(myVec, angle, p.getFullHeight());
      myVec.add(pPos);
      game.getObjMan().getWorld().rayCast(myRayBack, myVec, pPos);
      if (myDeviation < minDeviation) {
        res.set(myVec);
        minDeviation = myDeviation;
      }
    }

    res.sub(pPos);
    SolMath.rotate(res, -p.getAngle());
    return res;
  }
}
