package com.miloshpetrov.sol2.game;

import com.badlogic.gdx.graphics.Color;
import com.miloshpetrov.sol2.common.*;

public abstract class ColorSpan {

  public static RgbSpan rgb(Color start, Color end) {
    return new RgbSpan(start, end);
  }

  public static RgbSpan rgb(float[] start, float[] end) {
    Color startC = new Color();
    ColUtil.fromHSB(start[0], start[1], start[2], start[3], startC);
    Color endC = new Color();
    ColUtil.fromHSB(end[0], end[1], end[2], end[3], endC);
    return rgb(startC, endC);
  }

  public static HsbSpan hsb(Color start, Color end) {
    return hsb(ColUtil.toHSB(start), ColUtil.toHSB(end));
  }

  public static HsbSpan hsb(float[] start, float[] end) {
    return new HsbSpan(start, end);
  }

  public abstract void set(float perc, Color col);

  public static class RgbSpan extends ColorSpan {
    private final Color myStart;
    private final Color myEnd;

    public RgbSpan(Color start, Color end) {
      myStart = new Color(start);
      myEnd = new Color(end);
    }

    @Override
    public void set(float perc, Color col) {
      perc = SolMath.clamp(perc, 0, 1);
      col.r = midVal(myStart.r, myEnd.r, perc);
      col.g = midVal(myStart.g, myEnd.g, perc);
      col.b = midVal(myStart.b, myEnd.b, perc);
      col.a = midVal(myStart.a, myEnd.a, perc);
    }

    private float midVal(float s, float e, float perc) {
      return s + perc * (e - s);
    }
  }

}
