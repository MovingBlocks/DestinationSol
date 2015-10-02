package org.destinationsol.game.planet;

import com.badlogic.gdx.math.Vector2;
import org.destinationsol.game.HardnessCalc;

import java.util.ArrayList;

public class SolSystem {

  private final Vector2 myPos;
  private final ArrayList<Planet> myPlanets;
  private final ArrayList<SystemBelt> myBelts;
  private final SysConfig myConfig;
  private final String myName;
  private final float myRadius;
  private final float myDps;
  private final float myInnerDps;
  private float myInnerRad;

  public SolSystem(Vector2 pos, SysConfig config, String name, float sysRadius) {
    myConfig = config;
    myName = name;
    myPos = new Vector2(pos);
    myPlanets = new ArrayList<Planet>();
    myBelts = new ArrayList<SystemBelt>();
    myRadius = sysRadius;
    myDps = HardnessCalc.getSysDps(config, false);
    myInnerRad = myRadius/2;
    myInnerDps = HardnessCalc.getSysDps(config, true);
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
    float newInnerRad = belt.getRadius() - belt.getHalfWidth();
    if (myBelts.size() == 0 || myInnerRad < newInnerRad) myInnerRad = newInnerRad;
    myBelts.add(belt);
  }

  public float getInnerRad() {
    return myInnerRad;
  }

  public float getDps() {
    return myDps;
  }

  public float getInnerDps() {
    return myInnerDps;
  }
}
