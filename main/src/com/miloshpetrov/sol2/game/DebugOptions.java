package com.miloshpetrov.sol2.game;

import com.badlogic.gdx.math.Vector2;
import com.miloshpetrov.sol2.menu.IniReader;

public class DebugOptions {
  public static String DEV_ROOT_PATH;

  public static final Vector2 DEBUG_POINT = new Vector2();
  public static final Vector2 DEBUG_POINT2 = new Vector2();
  public static final Vector2 DEBUG_POINT3 = new Vector2();
  public static final boolean ASSERTIONS = false;
  public static final boolean PRINT_BALANCE = false;

  // world initialization
  public static boolean EMULATE_MOBILE = false;
  public static String SPAWN_PLACE = "";
  public static String FORCE_PLANET_TYPE = "";
  public static String FORCE_SYSTEM_TYPE = "";
  public static boolean NO_OBJS = false;
  public static boolean GOD_MODE = false;

  // presentation
  public static boolean NO_DRAS = false;
  public static float ZOOM_OVERRIDE = 0;
  public static float GRID_SZ = 0;
  public static float GAME_SPEED_MULTIPLIER = 1f;
  public static boolean DIRECT_CAM_CONTROL = false;
  public static boolean DETAILED_MAP = false;
  public static boolean NO_SOUND = false;
  public static boolean SOUND_IN_SPACE = false;
  public static boolean SHOW_WARNINGS = false;
  public static boolean DRAW_OBJ_BORDERS = false;
  public static boolean DRAW_DRA_BORDERS = false;
  public static boolean DRAW_PHYSIC_BORDERS = false;
  public static boolean DRAW_PLANET_BORDERS = false;
  public static boolean MISC_INFO = false;
  public static boolean OBJ_INFO = false;
  public static boolean SOUND_INFO = false;
  public static boolean TEX_INFO = false;
  public static MissingResourceAction MISSING_SOUND_ACTION;
  public static MissingResourceAction MISSING_TEXTURE_ACTION;
  public static MissingResourceAction MISSING_PHYSICS_ACTION;


  public static void read(boolean mobile) {
    IniReader r = new IniReader("debugOptions.ini", mobile);

    EMULATE_MOBILE = r.b("emulateMobile", EMULATE_MOBILE);
    SPAWN_PLACE = r.s("spawnPlace", SPAWN_PLACE);
    FORCE_PLANET_TYPE = r.s("forcePlanetType", FORCE_PLANET_TYPE);
    FORCE_SYSTEM_TYPE = r.s("forceSystemType", FORCE_SYSTEM_TYPE);
    NO_OBJS = r.b("noObjs", NO_OBJS);
    GOD_MODE = r.b("godMode", GOD_MODE);
    NO_DRAS = r.b("noDras", NO_DRAS);
    ZOOM_OVERRIDE = r.f("zoomOverride", ZOOM_OVERRIDE);
    GRID_SZ = r.f("gridSz", GRID_SZ);
    GAME_SPEED_MULTIPLIER = r.f("gameSpeedMultiplier", GAME_SPEED_MULTIPLIER);
    DIRECT_CAM_CONTROL = r.b("directCamControl", DIRECT_CAM_CONTROL);
    DETAILED_MAP = r.b("detailedMap", DETAILED_MAP);
    NO_SOUND = r.b("noSound", NO_SOUND);
    SOUND_IN_SPACE = r.b("soundInSpace", SOUND_IN_SPACE);
    SHOW_WARNINGS = r.b("showWarnings", SHOW_WARNINGS);
    DRAW_OBJ_BORDERS = r.b("drawObjBorders", DRAW_OBJ_BORDERS);
    DRAW_DRA_BORDERS = r.b("drawDraBorders", DRAW_DRA_BORDERS);
    DRAW_PHYSIC_BORDERS = r.b("drawPhysicBorders", DRAW_PHYSIC_BORDERS);
    DRAW_PLANET_BORDERS = r.b("drawPlanetBorders", DRAW_PLANET_BORDERS);
    MISC_INFO = r.b("miscInfo", MISC_INFO);
    OBJ_INFO = r.b("objInfo", OBJ_INFO);
    SOUND_INFO = r.b("soundInfo", SOUND_INFO);
    TEX_INFO = r.b("texInfo", TEX_INFO);
    MISSING_SOUND_ACTION = MissingResourceAction.forName(r.s("missingSoundAction", MissingResourceAction.IGNORE.name));
    MISSING_TEXTURE_ACTION = MissingResourceAction.forName(r.s("missingTextureAction", MissingResourceAction.IGNORE.name));
    MISSING_PHYSICS_ACTION = MissingResourceAction.forName(r.s("missingPhysicsAction", MissingResourceAction.IGNORE.name));
  }

}
