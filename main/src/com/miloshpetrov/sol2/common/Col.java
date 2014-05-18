package com.miloshpetrov.sol2.common;

import com.badlogic.gdx.graphics.Color;

public class Col {
  public static final Color B50 = col(0, .5f);
  public static final Color B75 = col(0, .75f);
  public static final Color B = col(0, 1);
  public static final Color DDG = col(.12f, 1);
  public static final Color DG = col(.25f, 1);
  public static final Color G25 = col(.5f, .25f);
  public static final Color G = col(.5f, 1);
  public static final Color LG = col(.75f, 1);
  public static final Color W02 = col(1, .02f);
  public static final Color W05 = col(1, .05f);
  public static final Color W10 = col(1, .10f);
  public static final Color W15 = col(1, .15f);
  public static final Color W25 = col(1, .25f);
  public static final Color W50 = col(1, .5f);
  public static final Color W = col(1, 1);

  public static final Color UI_INACTIVE = new Color(0, .75f, 1, .1f);
  public static final Color UI_DARK = new Color(0, .75f, 1, .17f);
  public static final Color UI_MED = new Color(0, .75f, 1, .25f);
  public static final Color UI_LIGHT = new Color(0, .75f, 1, .5f);
  public static final Color UI_GROUND = new Color(0, .56f, .75f, 1f);

  public static Color col(float b, float t) {
    return new Color(b, b, b, t);
  }
}
