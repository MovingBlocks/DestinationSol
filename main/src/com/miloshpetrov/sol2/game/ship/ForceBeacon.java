package com.miloshpetrov.sol2.game.ship;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.miloshpetrov.sol2.common.Col;
import com.miloshpetrov.sol2.common.SolMath;
import com.miloshpetrov.sol2.game.*;
import com.miloshpetrov.sol2.game.dra.*;
import com.miloshpetrov.sol2.game.input.Pilot;

import java.util.List;

public class ForceBeacon {

  public static final float MAX_PULL_DIST = .5f;
  private final Vector2 myRelPos;
  private final Vector2 myPrevPos;
  private final RectSprite myTex;

  public ForceBeacon(SolGame game, Vector2 relPos) {
    myRelPos = relPos;
    TextureAtlas.AtlasRegion tex = game.getTexMan().getTex("misc/forceBeacon");
    myTex = new RectSprite(tex, .2f, 0, 0, new Vector2(relPos), DraLevel.PART_FG_0, 0, 60, Col.W);
    myPrevPos = new Vector2();
  }

  public void collectDras(List<Dra> dras) {
    dras.add(myTex);
  }

  public void update(SolGame game, Vector2 basePos, float baseAngle, SolShip ship) {
    Vector2 pos = SolMath.toWorld(myRelPos, baseAngle, basePos);
    Vector2 spd = SolMath.distVec(myPrevPos, pos).scl(1/game.getTimeStep());
    Fraction frac = ship.getPilot().getFraction();
    pullShips(game, ship, pos, spd, frac, MAX_PULL_DIST);
    SolMath.free(spd);
    myPrevPos.set(pos);
    SolMath.free(pos);
  }

  public static SolShip pullShips(SolGame game, SolShip excludedShip, Vector2 ownPos, Vector2 ownSpd, Fraction frac,
    float maxPullDist)
  {
    SolShip res = null;
    float minLen = Float.MAX_VALUE;
    for (SolObj o : game.getObjMan().getObjs()) {
      if (o == excludedShip) continue;
      if (!(o instanceof SolShip)) continue;
      SolShip ship = (SolShip) o;
      Pilot pilot = ship.getPilot();
      if (pilot.isUp() || pilot.isLeft() || pilot.isRight()) continue;
      if (game.getFractionMan().areEnemies(frac, pilot.getFraction())) continue;
      Vector2 toMe = SolMath.distVec(ship.getPos(), ownPos);
      float toMeLen = toMe.len();
      if (toMeLen < maxPullDist) {
        if (toMeLen > 1) toMe.scl(1/toMeLen);
        if (ownSpd != null) toMe.add(ownSpd);
        ship.getHull().getBody().setLinearVelocity(toMe);
        if (toMeLen < minLen) {
          res = ship;
          minLen = toMeLen;
        }
      }
      SolMath.free(toMe);
    }
    return res;
  }
}
