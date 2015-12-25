/*
 * Copyright 2015 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.destinationsol.game.screens;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import org.destinationsol.common.SolMath;
import org.destinationsol.game.SolGame;
import org.destinationsol.game.SolObject;
import org.destinationsol.game.ship.SolShip;

public class CollisionWarnDrawer extends WarnDrawer {
  private final MyRayBack myWarnCallback = new MyRayBack();
  private SolShip myHero;

  public CollisionWarnDrawer(float r) {
    super(r, "Object Near");
  }

  public boolean shouldWarn(SolGame game) {
    myHero = game.getHero();
    if (myHero == null) return false;
    Vector2 pos = myHero.getPosition();
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
      SolObject o = (SolObject) fixture.getBody().getUserData();
      if (myHero == o) {
        return -1;
      }
      show = true;
      return 0;
    }
  }
}
