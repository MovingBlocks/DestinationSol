package com.miloshpetrov.sol2;

public class Const {
  public static final float ATM_HEIGHT = 17f;
  public static final float MAX_SKY_HEIGHT_FROM_GROUND = 1.5f * ATM_HEIGHT;
  public static final float MAX_GROUND_HEIGHT = 25f;
  public static final float SUN_RADIUS = 1.2f * (MAX_GROUND_HEIGHT + ATM_HEIGHT);
  public static final float MAX_MOVE_SPD = 8f;
  public static final float MAX_ZOOM = 2.5f;
  public static final float MED_ZOOM = 1.5f;
  public static final float REAL_TIME_STEP = 1.0f / 60.0f;
  public static final float CHUNK_SIZE = 20f;
  public static final int ITEMS_PER_PAGE = 8;
  public final static float PLANET_GAP = 8f;
  public static final String VERSION = "0.07";
  public static final float FRICTION = .5f;
}
