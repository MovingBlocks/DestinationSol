package com.miloshpetrov.sol2.game;

import com.badlogic.gdx.math.Vector2;
import com.miloshpetrov.sol2.menu.IniReader;

public class DebugAspects {
  public static final Vector2 DEBUG_POINT = new Vector2();

  public static float ZOOM_OVERRIDE = 0;
  public static boolean OBJECT_BORDERS = false;
  public static boolean DRA_BORDERS = false;
  public static boolean PHYSIC_BODIES = false;
  public static boolean PLANET_BORDERS = false;
  public static boolean STATS = false;
  public static boolean WARNINGS = false;
  public static float GRID_SZ = 0;
  public static boolean TO_STRING = false;
  public static boolean NO_OBJS = false;
  public static float DEBUG_SLOWDOWN = 1f;
  public static boolean DIRECT_CAM_CONTROL = false;
  public static boolean DETAILED_MAP = false;
  public static boolean MOBILE = false;
  public static boolean GOD_MODE = false;
  public static boolean NO_SOUND = false;
  public static boolean SOUND_DEBUG = false;
  public static boolean SOUND_IN_SPACE = false;
  public static String REPO_PATH;

  public static void read() {
    IniReader r = new IniReader("debugOptions.ini");
    ZOOM_OVERRIDE = r.f("zoomOverride", ZOOM_OVERRIDE);
    STATS = r.b("stats", STATS);
    WARNINGS = r.b("warnings", WARNINGS);
    GOD_MODE = r.b("godMode", GOD_MODE);
    NO_SOUND = r.b("noSound", NO_SOUND);
    SOUND_DEBUG = r.b("soundDebug", SOUND_DEBUG);
    SOUND_IN_SPACE = r.b("soundInSpace", SOUND_IN_SPACE);
    REPO_PATH = r.s("repoPath", REPO_PATH);
    // and so on
  }
}
