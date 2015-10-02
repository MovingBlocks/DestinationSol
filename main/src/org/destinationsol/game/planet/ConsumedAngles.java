package org.destinationsol.game.planet;

import org.destinationsol.common.SolMath;

import java.util.ArrayList;
import java.util.List;

public class ConsumedAngles {
  private final List<Float> myAngles;
  private final List<Float> myHalfWidths;

  public ConsumedAngles() {
    myAngles = new ArrayList<Float>();
    myHalfWidths = new ArrayList<Float>();
  }

  public boolean isConsumed(float angle, float objAngularHalfWidth) {
    int sz = myAngles.size();
    for (int i = 0; i < sz; i++) {
      Float a = myAngles.get(i);
      Float hw = myHalfWidths.get(i);
      if (SolMath.angleDiff(angle, a) < hw + objAngularHalfWidth) return true;
    }
    return false;
  }

  public void add(float angle, float hw) {
    myAngles.add(angle);
    myHalfWidths.add(hw);
  }
}
