package org.destinationsol.game.planet;

import com.badlogic.gdx.math.Vector2;
import org.destinationsol.Const;
import org.destinationsol.game.FarObj;
import org.destinationsol.game.SolGame;
import org.destinationsol.game.SolObject;

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
  public SolObject toObj(SolGame game) {
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

  @Override
  public boolean hasBody() {
    return false;
  }
}
