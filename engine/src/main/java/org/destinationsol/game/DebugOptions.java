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

public final class DebugOptions {
    public static MissingResourceAction missingPhysicsAction;

    public static final boolean PRINT_BALANCE = false;
    public static String developmentRootPath;
    static final boolean ASSERTIONS = false;
    static final Vector2 DEBUG_POINT = new Vector2();
    static final Vector2 DEBUG_POINT2 = new Vector2();
    static final Vector2 DEBUG_POINT3 = new Vector2();

    // World initialization
    public static boolean emulateMobile;
    public static String forcePlanetType = "";
    public static String forceSystemType = "";
    public static boolean noObjects;
    static String spawnPlace = "";

    // Presentation
    public static boolean noDrawables;
    public static boolean soundInSpace;
    public static boolean showWarnings;
    public static boolean showFps;
    public static boolean drawDrawableBorders;
    public static boolean drawPlanetBorders;
    public static boolean soundInfo;
    public static boolean textureInfo;
    static float zoomOverride;
    static float gridSize;
    static float gameSpeedMultiplier = 1f;
    static boolean detailedMap;
    static boolean drawObjectBorders;
    static boolean drawPhysicBorders;
    static boolean objectInfo;
    private static boolean noSound;
    private static boolean miscInfo;

    private DebugOptions() { }

    public static void read(SolFileReader reader) {
        IniReader r = new IniReader("debugOptions.ini", reader);

        emulateMobile = r.getBoolean("emulateMobile", emulateMobile);
        spawnPlace = r.getString("spawnPlace", spawnPlace);
        forcePlanetType = r.getString("forcePlanetType", forcePlanetType);
        forceSystemType = r.getString("forceSystemType", forceSystemType);
        noObjects = r.getBoolean("noObjs", noObjects);
        noDrawables = r.getBoolean("noDras", noDrawables);
        zoomOverride = r.getFloat("zoomOverride", zoomOverride);
        gridSize = r.getFloat("gridSz", gridSize);
        gameSpeedMultiplier = r.getFloat("gameSpeedMultiplier", gameSpeedMultiplier);
        detailedMap = r.getBoolean("detailedMap", detailedMap);
        noSound = r.getBoolean("noSound", noSound);
        soundInSpace = r.getBoolean("soundInSpace", soundInSpace);
        showWarnings = r.getBoolean("showWarnings", showWarnings);
        showFps = r.getBoolean("showFps", showFps);
        drawObjectBorders = r.getBoolean("drawObjBorders", drawObjectBorders);
        drawDrawableBorders = r.getBoolean("drawDraBorders", drawDrawableBorders);
        drawPhysicBorders = r.getBoolean("drawPhysicBorders", drawPhysicBorders);
        drawPlanetBorders = r.getBoolean("drawPlanetBorders", drawPlanetBorders);
        miscInfo = r.getBoolean("miscInfo", miscInfo);
        objectInfo = r.getBoolean("objInfo", objectInfo);
        soundInfo = r.getBoolean("soundInfo", soundInfo);
        textureInfo = r.getBoolean("texInfo", textureInfo);
        missingPhysicsAction = MissingResourceAction.forName(r.getString("missingPhysicsAction", MissingResourceAction.IGNORE.name));
    }
}
