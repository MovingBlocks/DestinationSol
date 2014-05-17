package com.miloshpetrov.sol2.game.screens;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import com.miloshpetrov.sol2.common.Col;
import com.miloshpetrov.sol2.common.SolMath;
import com.miloshpetrov.sol2.game.SolGame;
import com.miloshpetrov.sol2.game.SolObj;
import com.miloshpetrov.sol2.game.ship.SolShip;
import com.miloshpetrov.sol2.ui.FontSize;
import com.miloshpetrov.sol2.ui.UiDrawer;

public class CollisionWarnDrawer {
  private final RayCastCallback myWarnCallback = new MyRayBack();
  private final Rectangle myWarn;
  private boolean myShowWarn;
  private SolShip myHero;

  public CollisionWarnDrawer(float r) {
    myWarn = new Rectangle(.4f * r, 0, .2f * r, .1f);
  }

  public void update(SolGame game) {
    myShowWarn = false;
    myHero = game.getHero();
    if (myHero == null) return;
    Vector2 pos = myHero.getPos();
    Vector2 spd = myHero.getSpd();
    float acc = myHero.getAcc();
    float spdLen = spd.len();
    float spdAngle = spd.angle();
    if (acc <= 0 || spdLen < 2 * acc) return;
    // t = v/a;
    // s = att/2 = vv/a/2;
    float breakWay = spdLen * spdLen / acc / 2;
    breakWay += 2 * spdLen;
    Vector2 finalPos = SolMath.getVec(0, 0);
    SolMath.fromAl(finalPos, spdAngle, breakWay);
    finalPos.add(pos);
    game.getObjMan().getWorld().rayCast(myWarnCallback, pos, finalPos);
    SolMath.free(finalPos);
  }

  public void draw(UiDrawer drawer) {
    if (!myShowWarn) return;
    drawer.draw(myWarn, Col.UI_LIGHT);
    drawer.drawString("Object Near", myWarn.x + myWarn.width/2, myWarn.y + myWarn.height/2, FontSize.MENU, true, Col.W);
  }

  private class MyRayBack implements RayCastCallback {
    @Override
    public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal, float fraction) {
      SolObj o = (SolObj) fixture.getBody().getUserData();
      if (myHero == o) {
        return -1;
      }
      myShowWarn = true;
      return 0;
    }
  }
}
