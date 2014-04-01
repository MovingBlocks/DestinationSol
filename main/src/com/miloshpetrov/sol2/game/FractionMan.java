package com.miloshpetrov.sol2.game;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import com.miloshpetrov.sol2.TexMan;
import com.miloshpetrov.sol2.game.input.Pilot;
import com.miloshpetrov.sol2.game.projectile.Projectile;
import com.miloshpetrov.sol2.game.ship.SolShip;

import java.util.EnumMap;

public class FractionMan {

  private final EnumMap<Fraction,TextureAtlas.AtlasRegion> myIcons;
  private final MyRayBack myRayBack;

  public FractionMan(TexMan texMan) {
    myIcons = new EnumMap<Fraction, TextureAtlas.AtlasRegion>(Fraction.class);
    for (Fraction f : Fraction.values()) {
      String iconName = TexMan.ICONS_DIR + f.getName();
      TextureAtlas.AtlasRegion icon = texMan.getTex(iconName, null);
      myIcons.put(f, icon);
    }
    myRayBack = new MyRayBack();
  }

  public SolShip getNearestEnemy(SolGame game, SolShip ship) {
    Pilot pilot = ship.getPilot();
    float detectionDist = pilot.getDetectionDist();
    if (detectionDist <= 0) return null;
    Fraction f = pilot.getFraction();
    return getNearestEnemy(game, detectionDist, f, ship.getPos());
  }

  public SolShip getNearestEnemy(SolGame game, Projectile proj) {
    return getNearestEnemy(game, Float.MAX_VALUE, proj.getFraction(), proj.getPos());
  }

  public SolShip getNearestEnemy(SolGame game, float detectionDist, Fraction f, Vector2 pos) {
    SolShip res = null;
    float minDst = detectionDist;
    for (SolObj o : game.getObjMan().getObjs()) {
      if (!(o instanceof SolShip)) continue;
      SolShip ship2 = (SolShip) o;
      if (!areEnemies(f, ship2.getPilot().getFraction())) continue;
      float dst = ship2.getPos().dst(pos);
      if (minDst < dst)  continue;
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

  public TextureAtlas.AtlasRegion getIcon(Fraction f) {
    return myIcons.get(f);
  }

  public boolean areEnemies(SolShip s1, SolShip s2) {
    Fraction f1 = s1.getPilot().getFraction();
    Fraction f2 = s2.getPilot().getFraction();
    return areEnemies(f1, f2);
  }

  public boolean areEnemies(Fraction f1, Fraction f2) {
    return f1 != null && f2 != null && f1 != f2;
  }

  private static class MyRayBack implements RayCastCallback {
    public SolShip shipFrom;
    public SolShip shipTo;
    public boolean hasObstacle;

    @Override
    public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal, float fraction) {
      SolObj o = (SolObj) fixture.getBody().getUserData();
      if (o == shipFrom || o == shipTo) {
        return -1;
      }
      hasObstacle = true;
      return 0;
    }
  }
}
