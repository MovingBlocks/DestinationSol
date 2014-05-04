package com.miloshpetrov.sol2.game;

import com.badlogic.gdx.math.Vector2;
import com.miloshpetrov.sol2.menu.IniReader;

import java.nio.file.Files;
import java.nio.file.Paths;

public class DebugAspects {
  public static String DEV_ROOT_PATH;

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
  public static boolean TEX_DEBUG = false;
  public static String FORCE_PLANET_TYPE = "";
  public static String SPAWN_PLACE = "";

  public static void read() {
    boolean devBuild = Files.exists(Paths.get("devBuild"));
    if (devBuild) DEV_ROOT_PATH = "../trunk/main/"; // supposing that solWin is in the same direcrory where trunk is.

    IniReader r = new IniReader("debugOptions.ini");

    ZOOM_OVERRIDE = r.f("zoomOverride", ZOOM_OVERRIDE);
    OBJECT_BORDERS = r.b("objectBorders", OBJECT_BORDERS);
    DRA_BORDERS = r.b("draBorders", DRA_BORDERS);
    PHYSICS_DEBUG = r.b("physicsDebug", PHYSICS_DEBUG);
    PLANET_BORDERS = r.b("planetBorders", PLANET_BORDERS);
    STATS = r.b("stats", STATS);
    WARNINGS = r.b("warnings", WARNINGS);
    GRID_SZ = r.f("gridSz", GRID_SZ);
    TO_STRING = r.b("toString", TO_STRING);
    NO_OBJS = r.b("noObjs", NO_OBJS);
    DEBUG_SLOWDOWN = r.f("debugSlowdown", DEBUG_SLOWDOWN);
    DIRECT_CAM_CONTROL = r.b("directCamControl", DIRECT_CAM_CONTROL);
    DETAILED_MAP = r.b("detailedMap", DETAILED_MAP);
    MOBILE = r.b("mobile", MOBILE);
    GOD_MODE = r.b("godMode", GOD_MODE);
    NO_SOUND = r.b("noSound", NO_SOUND);
    SOUND_DEBUG = r.b("soundDebug", SOUND_DEBUG);
    SOUND_IN_SPACE = r.b("soundInSpace", SOUND_IN_SPACE);
    TEX_DEBUG = r.b("texDebug", TEX_DEBUG);
    SPAWN_PLACE = r.s("spawnPlace", SPAWN_PLACE);
  }
}
