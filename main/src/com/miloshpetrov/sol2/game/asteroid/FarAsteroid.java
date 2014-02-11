package com.miloshpetrov.sol2.game.asteroid;

import com.badlogic.gdx.math.Vector2;
import com.miloshpetrov.sol2.game.*;

public class FarAsteroid implements FarObj {
  private final int myModelNr;
  private final Vector2 myPos;
  private final float myAngle;
  private final RemoveController myRemoveController;
  private final float myRadius;

  public FarAsteroid(int modelNr, Vector2 pos, float angle, RemoveController removeController, float radius) {
    myModelNr = modelNr;
    myPos = pos;
    myAngle = angle;
    myRemoveController = removeController;
    myRadius = radius;
  }

  @Override
  public boolean shouldBeRemoved(SolGame game) {
    return myRemoveController.shouldRemove(myPos);
  }

  @Override
  public SolObj toObj(SolGame game) {
    return game.getAsteroidBuilder().build(game, myPos, myModelNr, myRemoveController);
  }

  @Override
  public void update(SolGame game) {
  }

  @Override
  public float getRadius() {
    return myRadius;
  }

  @Override
  public Vector2 getPos() {
    return myPos;
  }

  @Override
  public String toDebugString() {
    return null;
  }
}
