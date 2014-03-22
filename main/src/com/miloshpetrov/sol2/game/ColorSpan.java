package com.miloshpetrov.sol2.game;

import com.badlogic.gdx.graphics.Color;
import com.miloshpetrov.sol2.common.SolMath;

public abstract class ColorSpan {

  public static RgbSpan rgb(Color start, Color end) {
    return new RgbSpan(start, end);
  }

  public static RgbSpan rgb(float[] start, float[] end) {
    Color startC = new Color();
    Util.fromHSB(start[0], start[1], start[2], start[3], startC);
    Color endC = new Color();
    Util.fromHSB(end[0], end[1], end[2], end[3], endC);
    return rgb(startC, endC);
  }

  public static HsbSpan hsb(Color start, Color end) {
    return hsb(Util.toHSB(start), Util.toHSB(end));
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

  public static class Util {
    public static void fromHSB(float hue, float saturation, float brightness, float a, Color dest) {
      float r = 0, g = 0, b = 0;
      if (saturation == 0) {
        r = g = b = brightness;
      } else {
        float h = (hue - (float)Math.floor(hue)) * 6.0f;
        float f = h - (float) Math.floor(h);
        float p = brightness * (1.0f - saturation);
        float q = brightness * (1.0f - saturation * f);
        float t = brightness * (1.0f - (saturation * (1.0f - f)));
        switch ((int) h) {
        case 0:
          r = brightness;
          g = t;
          b = p;
          break;
        case 1:
          r = q;
          g = brightness;
          b = p;
          break;
        case 2:
          r = p;
          g = brightness;
          b = t;
          break;
        case 3:
          r = p;
          g = q;
          b = brightness;
          break;
        case 4:
          r = t;
          g = p;
          b = brightness;
          break;
        case 5:
          r = brightness;
          g = p;
          b = q;
          break;
        }
      }
      dest.r = r;
      dest.g = g;
      dest.b = b;
      dest.a = a;
    }

    public static float[] toHSB(Color src) {
      int r = (int)(src.r * 255 + .5f);
      int g = (int)(src.g * 255 + .5f);
      int b = (int)(src.b * 255 + .5f);
      float hue, saturation, brightness;
      int cmax = (r > g) ? r : g;
      if (b > cmax) cmax = b;
      int cmin = (r < g) ? r : g;
      if (b < cmin) cmin = b;

      brightness = ((float) cmax) / 255.0f;
      if (cmax != 0)
        saturation = ((float) (cmax - cmin)) / ((float) cmax);
      else
        saturation = 0;
      if (saturation == 0)
        hue = 0;
      else {
        float redc = ((float) (cmax - r)) / ((float) (cmax - cmin));
        float greenc = ((float) (cmax - g)) / ((float) (cmax - cmin));
        float bluec = ((float) (cmax - b)) / ((float) (cmax - cmin));
        if (r == cmax)
          hue = bluec - greenc;
        else if (g == cmax)
          hue = 2.0f + redc - bluec;
        else
          hue = 4.0f + greenc - redc;
        hue = hue / 6.0f;
        if (hue < 0)
          hue = hue + 1.0f;
      }
      float[] hsba = new float[4];
      hsba[0] = hue;
      hsba[1] = saturation;
      hsba[2] = brightness;
      hsba[3] = src.a;
      return hsba;
    }
  }
}
