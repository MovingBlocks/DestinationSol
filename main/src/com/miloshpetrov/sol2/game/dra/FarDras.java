package com.miloshpetrov.sol2.game.dra;

import com.badlogic.gdx.math.Vector2;
import com.miloshpetrov.sol2.game.*;

import java.util.List;

public class FarDras implements FarObj {
  private final List<Dra> myDras;
  private final Vector2 myPos;
  private final Vector2 mySpd;
  private final RemoveController myRemoveController;
  private final float myRadius;
  private final boolean myHideOnPlanet;

  public FarDras(List<Dra> dras, Vector2 pos, Vector2 spd, RemoveController removeController, float radius, boolean hideOnPlanet) {
    myDras = dras;
    myPos = pos;
    mySpd = spd;
    myRemoveController = removeController;
    myRadius = radius;
    myHideOnPlanet = hideOnPlanet;
  }

  @Override
  public boolean shouldBeRemoved(SolGame game) {
    return myRemoveController != null && myRemoveController.shouldRemove(myPos);
  }

  @Override
  public SolObj toObj(SolGame game) {
    return new DrasObj(myDras, myPos, mySpd, myRemoveController, false, myHideOnPlanet);
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

  public List<Dra> getDras() {
    return myDras;
  }
}
