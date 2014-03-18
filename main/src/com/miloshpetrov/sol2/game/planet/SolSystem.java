package com.miloshpetrov.sol2.game.planet;

import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;

public class SolSystem {

  private final Vector2 myPos;
  private final ArrayList<Planet> myPlanets;
  private final ArrayList<SystemBelt> myBelts;
  private float myRadius;

  public SolSystem(Vector2 pos) {
    myPos = new Vector2(pos);
    myPlanets = new ArrayList<Planet>();
    myBelts = new ArrayList<SystemBelt>();

  }

  public ArrayList<Planet> getPlanets() {
    return myPlanets;
  }

  public ArrayList<SystemBelt> getBelts() {
    return myBelts;
  }

  public Vector2 getPos() {
    return myPos;
  }

  public void setRadius(float radius) {
    myRadius = radius;
  }

  public float getRadius() {
    return myRadius;
  }
}
