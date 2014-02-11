package com.miloshpetrov.sol2.game.planet;

import com.badlogic.gdx.math.Vector2;
import com.miloshpetrov.sol2.Const;
import com.miloshpetrov.sol2.game.*;

public class FarSky implements FarObj {
  private final Planet myPlanet;

  public FarSky(Planet planet) {
    myPlanet = planet;
  }

  @Override
  public boolean shouldBeRemoved(SolGame game) {
    return false;
  }

  @Override
  public SolObj toObj(SolGame game) {
    return new Sky(game, myPlanet);
  }

  @Override
  public void update(SolGame game) {
  }

  @Override
  public float getRadius() {
    return myPlanet.getGroundHeight() + Const.MAX_SKY_HEIGHT_FROM_GROUND;
  }

  @Override
  public Vector2 getPos() {
    return myPlanet.getPos();
  }

  @Override
  public String toDebugString() {
    return null;
  }
}
