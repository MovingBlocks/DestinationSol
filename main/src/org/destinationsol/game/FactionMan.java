package org.destinationsol.game;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import org.destinationsol.TextureManager;
import org.destinationsol.game.input.Pilot;
import org.destinationsol.game.projectile.Projectile;
import org.destinationsol.game.ship.SolShip;

import java.util.List;

public class FactionMan {

  private final MyRayBack myRayBack;

  public FactionMan(TextureManager textureManager) {
    myRayBack = new MyRayBack();
  }

  public SolShip getNearestEnemy(SolGame game, SolShip ship) {
    Pilot pilot = ship.getPilot();
    float detectionDist = pilot.getDetectionDist();
    if (detectionDist <= 0) return null;
    detectionDist += ship.getHull().config.getApproxRadius();
    Faction f = pilot.getFaction();
    return getNearestEnemy(game, detectionDist, f, ship.getPos());
  }

  public SolShip getNearestEnemy(SolGame game, Projectile proj) {
    return getNearestEnemy(game, game.getCam().getViewDist(), proj.getFaction(), proj.getPos());
  }

  public SolShip getNearestEnemy(SolGame game, float detectionDist, Faction f, Vector2 pos) {
    SolShip res = null;
    float minDst = detectionDist;
    List<SolObject> objs = game.getObjMan().getObjs();
    for (int i = 0, objsSize = objs.size(); i < objsSize; i++) {
      SolObject o = objs.get(i);
      if (!(o instanceof SolShip)) continue;
      SolShip ship2 = (SolShip) o;
      if (!areEnemies(f, ship2.getPilot().getFaction())) continue;
      float dst = ship2.getPos().dst(pos) - ship2.getHull().config.getApproxRadius();
      if (minDst < dst) continue;
      minDst = dst;
      res = ship2;
    }
    return res;
  }

  private boolean hasObstacles(SolGame game, SolShip shipFrom, SolShip shipTo) {
    myRayBack.shipFrom = shipFrom;
    myRayBack.shipTo = shipTo;
    myRayBack.hasObstacle = false;
    game.getObjMan().getWorld().rayCast(myRayBack, shipFrom.getPos(), shipTo.getPos());
    return myRayBack.hasObstacle;
  }

  public boolean areEnemies(SolShip s1, SolShip s2) {
    Faction f1 = s1.getPilot().getFaction();
    Faction f2 = s2.getPilot().getFaction();
    return areEnemies(f1, f2);
  }

  public boolean areEnemies(Faction f1, Faction f2) {
    return f1 != null && f2 != null && f1 != f2;
  }

  private static class MyRayBack implements RayCastCallback {
    public SolShip shipFrom;
    public SolShip shipTo;
    public boolean hasObstacle;

    @Override
    public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal, float fraction) {
      SolObject o = (SolObject) fixture.getBody().getUserData();
      if (o == shipFrom || o == shipTo) {
        return -1;
      }
      hasObstacle = true;
      return 0;
    }
  }
}
