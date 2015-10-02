package org.destinationsol.game.planet;

import com.badlogic.gdx.math.Vector2;
import org.destinationsol.game.HardnessCalc;

public class SystemBelt {
  private final Float myHalfWidth;
  private final float myRadius;
  private final SolSystem myS;
  private final SysConfig myConfig;
  private final float myDps;

  public SystemBelt(Float halfWidth, float radius, SolSystem s, SysConfig config) {
    myHalfWidth = halfWidth;
    myRadius = radius;
    myS = s;
    myConfig = config;
    myDps = HardnessCalc.getBeltDps(config);
  }

  public float getRadius() {
    return myRadius;
  }

  public Float getHalfWidth() {
    return myHalfWidth;
  }

  public boolean contains(Vector2 pos) {
    float toCenter = myS.getPos().dst(pos);
    return myRadius - myHalfWidth < toCenter && toCenter < myRadius + myHalfWidth;
  }

  public SysConfig getConfig() {
    return myConfig;
  }

  public float getDps() {
    return myDps;
  }
}
