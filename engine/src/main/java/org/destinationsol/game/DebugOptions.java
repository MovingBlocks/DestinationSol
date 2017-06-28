/*
 * Copyright 2017 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.destinationsol.game;

import com.badlogic.gdx.math.Vector2;
import org.destinationsol.IniReader;
import org.destinationsol.SolFileReader;

public class DebugOptions {
    public static final Vector2 DEBUG_POINT = new Vector2();
    public static final Vector2 DEBUG_POINT2 = new Vector2();
    public static final Vector2 DEBUG_POINT3 = new Vector2();
    public static final boolean ASSERTIONS = false;
    public static final boolean PRINT_BALANCE = false;
    public static String DEV_ROOT_PATH;

    // World initialization
    public static boolean EMULATE_MOBILE = false;
    public static String SPAWN_PLACE = "";
    public static String FORCE_PLANET_TYPE = "";
    public static String FORCE_SYSTEM_TYPE = "";
    public static boolean NO_OBJS = false;

    // Presentation
    public static boolean NO_DRAS = false;
    public static float ZOOM_OVERRIDE = 0;
    public static float GRID_SZ = 0;
    public static float GAME_SPEED_MULTIPLIER = 1f;
    public static boolean DIRECT_CAM_CONTROL = false;
    public static boolean DETAILED_MAP = false;
    public static boolean NO_SOUND = false;
    public static boolean SOUND_IN_SPACE = false;
    public static boolean SHOW_WARNINGS = false;
    public static boolean SHOW_FPS = false;
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

    public static void read(SolFileReader reader) {
        IniReader r = new IniReader("debugOptions.ini", reader);

        EMULATE_MOBILE = r.getBoolean("emulateMobile", EMULATE_MOBILE);
        SPAWN_PLACE = r.getString("spawnPlace", SPAWN_PLACE);
        FORCE_PLANET_TYPE = r.getString("forcePlanetType", FORCE_PLANET_TYPE);
        FORCE_SYSTEM_TYPE = r.getString("forceSystemType", FORCE_SYSTEM_TYPE);
        NO_OBJS = r.getBoolean("noObjs", NO_OBJS);
        NO_DRAS = r.getBoolean("noDras", NO_DRAS);
        ZOOM_OVERRIDE = r.getFloat("zoomOverride", ZOOM_OVERRIDE);
        GRID_SZ = r.getFloat("gridSz", GRID_SZ);
        GAME_SPEED_MULTIPLIER = r.getFloat("gameSpeedMultiplier", GAME_SPEED_MULTIPLIER);
        DIRECT_CAM_CONTROL = r.getBoolean("directCamControl", DIRECT_CAM_CONTROL);
        DETAILED_MAP = r.getBoolean("detailedMap", DETAILED_MAP);
        NO_SOUND = r.getBoolean("noSound", NO_SOUND);
        SOUND_IN_SPACE = r.getBoolean("soundInSpace", SOUND_IN_SPACE);
        SHOW_WARNINGS = r.getBoolean("showWarnings", SHOW_WARNINGS);
        SHOW_FPS = r.getBoolean("showFps", SHOW_FPS);
        DRAW_OBJ_BORDERS = r.getBoolean("drawObjBorders", DRAW_OBJ_BORDERS);
        DRAW_DRA_BORDERS = r.getBoolean("drawDraBorders", DRAW_DRA_BORDERS);
        DRAW_PHYSIC_BORDERS = r.getBoolean("drawPhysicBorders", DRAW_PHYSIC_BORDERS);
        DRAW_PLANET_BORDERS = r.getBoolean("drawPlanetBorders", DRAW_PLANET_BORDERS);
        MISC_INFO = r.getBoolean("miscInfo", MISC_INFO);
        OBJ_INFO = r.getBoolean("objInfo", OBJ_INFO);
        SOUND_INFO = r.getBoolean("soundInfo", SOUND_INFO);
        TEX_INFO = r.getBoolean("texInfo", TEX_INFO);
        MISSING_SOUND_ACTION = MissingResourceAction.forName(r.getString("missingSoundAction", MissingResourceAction.IGNORE.name));
        MISSING_TEXTURE_ACTION = MissingResourceAction.forName(r.getString("missingTextureAction", MissingResourceAction.IGNORE.name));
        MISSING_PHYSICS_ACTION = MissingResourceAction.forName(r.getString("missingPhysicsAction", MissingResourceAction.IGNORE.name));
    }
}
