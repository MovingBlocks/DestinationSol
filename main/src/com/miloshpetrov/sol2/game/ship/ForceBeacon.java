package com.miloshpetrov.sol2.game.ship;

import com.badlogic.gdx.math.Vector2;
import com.miloshpetrov.sol2.common.SolMath;
import com.miloshpetrov.sol2.game.*;
import com.miloshpetrov.sol2.game.dra.Dra;
import com.miloshpetrov.sol2.game.input.Pilot;
import com.miloshpetrov.sol2.game.particle.ParticleSrc;
import com.miloshpetrov.sol2.game.sound.SolSound;

import java.util.List;

public class ForceBeacon {

  public static final float MAX_PULL_DIST = .5f;
  private final Vector2 myRelPos;
  private final Vector2 myPrevPos;
  private final ParticleSrc myEffect;

  public ForceBeacon(SolGame game, Vector2 relPos, Vector2 basePos, Vector2 baseSpd) {
    myRelPos = relPos;
    myEffect = game.getSpecialEffects().buildForceBeacon(.75f, game, relPos, basePos, baseSpd);
    myEffect.setWorking(true);
    myPrevPos = new Vector2();
  }

  public void collectDras(List<Dra> dras) {
    dras.add(myEffect);
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

  public static SolShip pullShips(SolGame game, SolObj owner, Vector2 ownPos, Vector2 ownSpd, Fraction frac,
    float maxPullDist)
  {
    SolShip res = null;
    float minLen = Float.MAX_VALUE;
    for (SolObj o : game.getObjMan().getObjs()) {
      if (o == owner) continue;
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
    if (res != null) {
      SolSound sound = game.getSpecialSounds().forceBeaconWork;
      game.getSoundMan().play(game, sound, null, owner);
    }
    return res;
  }
}
