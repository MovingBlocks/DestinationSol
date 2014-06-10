package com.miloshpetrov.sol2.game.screens;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import com.miloshpetrov.sol2.common.SolMath;
import com.miloshpetrov.sol2.game.SolGame;
import com.miloshpetrov.sol2.game.SolObj;
import com.miloshpetrov.sol2.game.ship.SolShip;

public class CollisionWarnDrawer extends WarnDrawer {
  private final MyRayBack myWarnCallback = new MyRayBack();
  private SolShip myHero;

  public CollisionWarnDrawer(float r) {
    super(r, "Object Near");
  }

  public boolean shouldWarn(SolGame game) {
    myHero = game.getHero();
    if (myHero == null) return false;
    Vector2 pos = myHero.getPos();
    Vector2 spd = myHero.getSpd();
    float acc = myHero.getAcc();
    float spdLen = spd.len();
    float spdAngle = SolMath.angle(spd);
    if (acc <= 0 || spdLen < 2 * acc) return false;
    // t = v/a;
    // s = att/2 = vv/a/2;
    float breakWay = spdLen * spdLen / acc / 2;
    breakWay += 2 * spdLen;
    Vector2 finalPos = SolMath.getVec(0, 0);
    SolMath.fromAl(finalPos, spdAngle, breakWay);
    finalPos.add(pos);
    myWarnCallback.show = false;
    game.getObjMan().getWorld().rayCast(myWarnCallback, pos, finalPos);
    SolMath.free(finalPos);
    return myWarnCallback.show;
  }

  private class MyRayBack implements RayCastCallback {
    private boolean show;
    @Override
    public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal, float fraction) {
      SolObj o = (SolObj) fixture.getBody().getUserData();
      if (myHero == o) {
        return -1;
      }
      show = true;
      return 0;
    }
  }
}
