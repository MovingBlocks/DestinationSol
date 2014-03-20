package com.miloshpetrov.sol2.game;

import com.badlogic.gdx.math.Vector2;
import com.miloshpetrov.sol2.menu.IniReader;

import java.nio.file.Files;
import java.nio.file.Paths;

public class DebugAspects {
  public static final Vector2 DEBUG_POINT = new Vector2();

  public static float ZOOM_OVERRIDE = 0;
  public static boolean OBJECT_BORDERS = false;
  public static boolean DRA_BORDERS = false;
  public static boolean PHYSICS_DEBUG = false;
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
  public static String DEV_ROOT_PATH;
  public static boolean TEX_DEBUG = false;

  public static void read() {
    boolean devBuild = Files.exists(Paths.get("devBuild"));
    if (devBuild) DEV_ROOT_PATH = "../trunk/main/"; // supposing that solWin is in the same direcrory where trunk is.

    IniReader r = new IniReader("debugOptions.ini");

    ZOOM_OVERRIDE = r.f("zoomOverride", ZOOM_OVERRIDE);
    STATS = r.b("stats", STATS);
    WARNINGS = r.b("warnings", WARNINGS);
    GOD_MODE = r.b("godMode", GOD_MODE);
    NO_SOUND = r.b("noSound", NO_SOUND);

    SOUND_DEBUG = r.b("soundDebug", SOUND_DEBUG);
    SOUND_IN_SPACE = r.b("soundInSpace", SOUND_IN_SPACE);

    TEX_DEBUG = r.b("texDebug", TEX_DEBUG);

    PHYSICS_DEBUG = r.b("physicsDebug", PHYSICS_DEBUG);
  }
}
