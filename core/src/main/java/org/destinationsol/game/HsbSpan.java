package org.destinationsol.game;

import com.badlogic.gdx.graphics.Color;
import org.destinationsol.common.SolColorUtil;
import org.destinationsol.common.SolMath;

public class HsbSpan extends ColorSpan {
  private final float[] myHsbaStart;
  private final float[] myHsbaEnd;

  HsbSpan(float[] start, float[] end) {
    myHsbaStart = start;
    myHsbaEnd = end;
  }

  @Override
  public void set(float perc, Color col) {
    perc = SolMath.clamp(perc, 0, 1);
    float hue = midVal(0, perc);
    float sat = midVal(1, perc);
    float br = midVal(2, perc);
    float a = midVal(3, perc);
    SolColorUtil.fromHSB(hue, sat, br, a, col);
  }

  private float midVal(int idx, float perc) {
    float s = myHsbaStart[idx];
    float e = myHsbaEnd[idx];
    return s + perc * (e - s);
  }

}
