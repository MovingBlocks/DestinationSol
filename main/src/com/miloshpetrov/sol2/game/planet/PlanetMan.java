package com.miloshpetrov.sol2.game.planet;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.miloshpetrov.sol2.TexMan;
import com.miloshpetrov.sol2.common.Col;
import com.miloshpetrov.sol2.common.SolMath;
import com.miloshpetrov.sol2.game.*;
import com.miloshpetrov.sol2.game.ship.HullConfigs;
import com.miloshpetrov.sol2.save.SaveData;

import java.util.ArrayList;

public class PlanetMan {
  private final ArrayList<SolSystem> mySystems;
  private final ArrayList<Planet> myPlanets;
  private final LandingPlaceFinder myLandingPlaceFinder;
  private final PlanetConfigs myPlanetConfigs;
  private Planet myNearestPlanet;

  public PlanetMan(TexMan texMan, HullConfigs hullConfigs) {
    myPlanetConfigs = new PlanetConfigs(texMan, hullConfigs);

    mySystems = new ArrayList<SolSystem>();
    myPlanets = new ArrayList<Planet>();
    myLandingPlaceFinder = new LandingPlaceFinder();
  }

  public void fill(SaveData sd) {
    if (sd != null) {
      mySystems.addAll(sd.systems);
      myPlanets.addAll(sd.planets);
    } else {
      new SystemsBuilder().build(mySystems, myPlanets, myPlanetConfigs);
    }
  }

  public void update(SolGame game) {
    Vector2 camPos = game.getCam().getPos();
    for (Planet p : myPlanets) {
      p.update(game);
    }

    myNearestPlanet = getNearestPlanet(camPos);
    applyGrav(game);
  }

  public Planet getNearestPlanet(Vector2 pos) {
    float minDst = Float.MAX_VALUE;
    Planet res = null;
    for (Planet p : myPlanets) {
      float dst = pos.dst(p.getPos());
      if (dst < minDst) {
        minDst = dst;
        res = p;
      }
    }
    return res;
  }

  private void applyGrav(SolGame game) {
    if (myNearestPlanet == null) return;
    Planet np = myNearestPlanet;

    for (SolObj obj : game.getObjMan().getObjs()) {
      if (!obj.receivesGravity()) continue;
      Vector2 grav = SolMath.getVec(np.getPos());
      grav.sub(obj.getPos());
      float len = grav.len();
      float groundHeight = np.getGroundHeight();
      if (len <= np.getFullHeight()) {
        grav.nor();
        if (len < groundHeight) {
          len = groundHeight;
        }
        float g = np.getGravConst() / len / len;
        grav.mul(g);
        obj.receiveAcc(grav, game);
      }
      SolMath.free(grav);
    }

  }

  public Planet getNearestPlanet() {
    return myNearestPlanet;
  }

  public void drawDebug(Drawer drawer, SolGame game) {
    if (DebugAspects.PLANETS) {
      float lineWidth = game.getCam().getRealLineWidth();
      for (Planet p : myPlanets) {
        Vector2 pos = p.getPos();
        float angle = p.getAngle();
        float fh = p.getFullHeight();
        Color col = p == myNearestPlanet ? Col.W : Col.G;
        drawer.drawCircle(pos, p.getGroundHeight(), col, lineWidth);
        drawer.drawCircle(pos, fh, col, lineWidth);
        drawer.drawLine(pos.x, pos.y, angle, fh, col, lineWidth);
      }

    }
  }

  public ArrayList<Planet> getPlanets() {
    return myPlanets;
  }

  public ArrayList<SolSystem> getSystems() {
    return mySystems;
  }

  public Vector2 findLandingPlace(SolGame game, Planet p, ArrayList<Float> takenAngles) {
    return myLandingPlaceFinder.find(game, p, takenAngles);
  }
}
