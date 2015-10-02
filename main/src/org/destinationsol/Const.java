package org.destinationsol;

public class Const {
  public static final float ATM_HEIGHT = 14f;
  public static final float MAX_SKY_HEIGHT_FROM_GROUND = 1.5f * ATM_HEIGHT;
  public static final float MAX_GROUND_HEIGHT = 25f;
  public static final float SUN_RADIUS = 2f * (MAX_GROUND_HEIGHT + ATM_HEIGHT);
  public static final float MAX_MOVE_SPD = 8f;
  public static final float REAL_TIME_STEP = 1.0f / 60.0f;
  public static final float CHUNK_SIZE = 20f;
  public static final int ITEM_GROUPS_PER_PAGE = 8;
  public final static float PLANET_GAP = 8f;
  public static final String VERSION = "1.3.1";
  public static final float FRICTION = .5f;
  public static final float IMPULSE_TO_COLL_VOL = 2f;

  public static final float CAM_VIEW_DIST_GROUND = 2.8f;
  public static final float AUTO_SHOOT_GROUND = CAM_VIEW_DIST_GROUND * .4f;
  public static final float CAM_VIEW_DIST_SPACE = 4.5f;
  public static final float AUTO_SHOOT_SPACE = CAM_VIEW_DIST_SPACE * .8f;
  public static final float AI_DET_DIST = CAM_VIEW_DIST_SPACE * 1.2f;
  public static final float CAM_VIEW_DIST_JOURNEY = 8.6f;
  public static final float DEFAULT_AI_SPD = 4f;
  public static final float BIG_AI_SPD = 2f;
}
