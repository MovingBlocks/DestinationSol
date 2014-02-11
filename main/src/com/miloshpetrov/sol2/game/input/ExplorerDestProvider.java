package com.miloshpetrov.sol2.game.input;

import com.badlogic.gdx.math.Vector2;
import com.miloshpetrov.sol2.Const;
import com.miloshpetrov.sol2.common.SolMath;
import com.miloshpetrov.sol2.game.SolGame;
import com.miloshpetrov.sol2.game.planet.Planet;
import com.miloshpetrov.sol2.game.ship.HullConfig;

import java.util.ArrayList;
import java.util.List;

public class ExplorerDestProvider implements MoveDestProvider {
  public static final int MAX_AWAIT_ON_PLANET = 30;
  private final Vector2 myDest;
  private final boolean myAggressive;
  private final float myDesiredSpdLen;
  private Vector2 myRelDest;
  private Planet myPlanet;
  private float myAwaitOnPlanet;
  private boolean myDestIsLanding;
  private final float myHoverPerc;

  public ExplorerDestProvider(SolGame game, Vector2 pos, boolean aggressive, float desiredSpdLen, HullConfig config, float hoverPerc) {
    myDest = new Vector2();
    myHoverPerc = hoverPerc;
    float minDst = Float.MAX_VALUE;
    for (Planet p : game.getPlanetMan().getPlanets()) {
      float dst = p.getPos().dst(pos);
      if (dst < minDst) {
        minDst = dst;
        myPlanet = p;
      }
    }
    calcRelDest(config);
    myAwaitOnPlanet = MAX_AWAIT_ON_PLANET;
    myAggressive = aggressive;
    myDesiredSpdLen = desiredSpdLen;
  }

  private void calcRelDest(HullConfig hullConfig) {
    List<Vector2> lps = myPlanet.getLandingPlaces();
    if (lps.size() > 0) {
      myRelDest = new Vector2(SolMath.elemRnd(lps));
      float len = myRelDest.len();
      myRelDest.scl((len + myHoverPerc * hullConfig.size)/len);
      myDestIsLanding = true;
    } else {
      myRelDest = new Vector2();
      SolMath.fromAl(myRelDest, SolMath.rnd(180), myPlanet.getGroundHeight() + .3f * Const.ATM_HEIGHT);
      myDestIsLanding = false;
    }
  }

  @Override
  public Vector2 getDest() {
    return myDest;
  }

  @Override
  public boolean shouldStopNearDest() {
    return true;
  }

  @Override
  public void update(SolGame game, Vector2 shipPos, float maxIdleDist, HullConfig hullConfig) {
    if (myDest.dst(shipPos) < maxIdleDist) {
      if (myAwaitOnPlanet > 0) {
        myAwaitOnPlanet -= game.getTimeStep();
      } else {
        ArrayList<Planet> ps = game.getPlanetMan().getPlanets();
        myPlanet = SolMath.elemRnd(ps);
        calcRelDest(hullConfig);
        myAwaitOnPlanet = MAX_AWAIT_ON_PLANET;
      }
    }

    if (!myDestIsLanding && !myPlanet.getLandingPlaces().isEmpty()) {
      calcRelDest(hullConfig);
    }

    SolMath.toWorld(myDest, myRelDest, myPlanet.getAngle(), myPlanet.getPos());
  }

  @Override
  public Boolean shouldBattle(boolean canShoot) {
    if (!myAggressive || !canShoot) return null;
    return true;
  }

  @Override
  public boolean shouldAvoidBigObjs() {
    return true;
  }

  @Override
  public float getDesiredSpdLen() {
    return myDesiredSpdLen;
  }
}
