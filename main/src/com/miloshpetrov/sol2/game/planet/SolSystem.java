package com.miloshpetrov.sol2.game.planet;

import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;

public class SolSystem {

  private final Vector2 myPos;
  private final ArrayList<Planet> myPlanets;
  private final ArrayList<SystemBelt> myBelts;
  private final SysConfig myConfig;
  private final String myName;
  private final float myRadius;
  private float myInnerRad;

  public SolSystem(Vector2 pos, SysConfig config, String name, float sysRadius) {
    myConfig = config;
    myName = name;
    myPos = new Vector2(pos);
    myPlanets = new ArrayList<Planet>();
    myBelts = new ArrayList<SystemBelt>();
    myRadius = sysRadius;
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

  public float getRadius() {
    return myRadius;
  }

  public SysConfig getConfig() {
    return myConfig;
  }

  public String getName() {
    return myName;
  }

  public void addBelt(SystemBelt belt) {
    myBelts.add(belt);
    float newInnerRad = belt.getRadius() - belt.getHalfWidth();
    if (myInnerRad < newInnerRad) myInnerRad = newInnerRad;
  }

  public float getInnerRad() {
    return myInnerRad;
  }
}
