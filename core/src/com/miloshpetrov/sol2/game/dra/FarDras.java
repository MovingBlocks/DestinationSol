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

  public FarDras(List<Dra> dras, Vector2 pos, Vector2 spd, RemoveController removeController,
    boolean hideOnPlanet) {
    myDras = dras;
    myPos = pos;
    mySpd = spd;
    myRemoveController = removeController;
    myRadius = DraMan.radiusFromDras(myDras);
    myHideOnPlanet = hideOnPlanet;
  }

  @Override
  public boolean shouldBeRemoved(SolGame game) {
    return myRemoveController != null && myRemoveController.shouldRemove(myPos);
  }

  @Override
  public SolObject toObj(SolGame game) {
    return new DrasObject(myDras, myPos, mySpd, myRemoveController, false, myHideOnPlanet);
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

  @Override
  public boolean hasBody() {
    return false;
  }

  public List<Dra> getDras() {
    return myDras;
  }
}
