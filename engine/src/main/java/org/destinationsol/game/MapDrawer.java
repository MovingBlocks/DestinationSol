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

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import org.destinationsol.Const;
import org.destinationsol.assets.Assets;
import org.destinationsol.common.Nullable;
import org.destinationsol.common.SolColor;
import org.destinationsol.common.SolMath;
import org.destinationsol.game.maze.Maze;
import org.destinationsol.game.maze.MazeBuilder;
import org.destinationsol.game.planet.FarTileObject;
import org.destinationsol.game.planet.Planet;
import org.destinationsol.game.planet.SolSystem;
import org.destinationsol.game.planet.SurfaceDirection;
import org.destinationsol.game.planet.SystemBelt;
import org.destinationsol.game.planet.Tile;
import org.destinationsol.game.planet.TileObject;
import org.destinationsol.game.ship.FarShip;
import org.destinationsol.game.ship.SolShip;
import org.destinationsol.ui.UiDrawer;

import java.util.ArrayList;
import java.util.List;

public class MapDrawer {
    public static final float MIN_ZOOM = 8f;
    public static final float MUL_FACTOR = 2f;
    public static final float MAX_ZOOM = 512f;
    public static final float ICON_RAD = .02f;
    public static final float STAR_NODE_SZ = .003f;
    public static final float INNER_ICON_PERC = .6f;
    public static final float INNER_AREA_ICON_PERC = .7f;
    public static final float GRID_SZ = 40f;
    public static final float MIN_ICON_RAD_PX = 16f;
    private static final float MAX_SKULL_TIME = .75f;
    private static final float MAX_AREA_SKULL_TIME = 3;
    private final TextureAtlas.AtlasRegion myAtmTexture;
    private final TextureAtlas.AtlasRegion myPlanetTexture;
    private final TextureAtlas.AtlasRegion myPlanetCoreTexture;
    private final TextureAtlas.AtlasRegion myStarTexture;
    private final TextureAtlas.AtlasRegion myMazeTexture;
    private final TextureAtlas.AtlasRegion mySkullTexture;
    private final TextureAtlas.AtlasRegion mySkullBigTexture;
    private final TextureAtlas.AtlasRegion myStarPortTexture;
    private final TextureAtlas.AtlasRegion myBeltTexture;
    private final TextureAtlas.AtlasRegion myBeaconAttackTexture;
    private final TextureAtlas.AtlasRegion myBeaconMoveTexture;
    private final TextureAtlas.AtlasRegion myBeaconFollowTexture;
    private final TextureAtlas.AtlasRegion myIconBackground;
    private final TextureAtlas.AtlasRegion myWarnAreaBackground;
    private final TextureAtlas.AtlasRegion myWhiteTexture;
    private final TextureAtlas.AtlasRegion myLineTexture;

    private final Color myAreaWarnCol;
    private final Color myAreaWarnBackgroundCol;
    private final float myIconRad;
    private boolean myToggled;
    private float myZoom;
    private float mySkullTime;
    private float myAreaSkullTime;

    public MapDrawer(float screenHeight) {
        myZoom = MAX_ZOOM / MUL_FACTOR / MUL_FACTOR;
        float minIconRad = MIN_ICON_RAD_PX / screenHeight;
        myIconRad = ICON_RAD < minIconRad ? minIconRad : ICON_RAD;

        myAreaWarnCol = new Color(SolColor.WHITE);
        myAreaWarnBackgroundCol = new Color(SolColor.UI_WARN);

        myWarnAreaBackground = Assets.getAtlasRegion("engine:mapObjects/warnBg");
        myAtmTexture = Assets.getAtlasRegion("engine:mapObjects/atm");
        myPlanetTexture = Assets.getAtlasRegion("engine:mapObjects/planet");
        myPlanetCoreTexture = Assets.getAtlasRegion("engine:mapObjects/planetCore");
        myStarTexture = Assets.getAtlasRegion("engine:mapObjects/star");
        myMazeTexture = Assets.getAtlasRegion("engine:mapObjects/maze");
        mySkullBigTexture = Assets.getAtlasRegion("engine:mapObjects/skullBig");
        myBeltTexture = Assets.getAtlasRegion("engine:mapObjects/asteroids");
        myBeaconAttackTexture = Assets.getAtlasRegion("engine:mapObjects/beaconAttack");
        myBeaconMoveTexture = Assets.getAtlasRegion("engine:mapObjects/beaconMove");
        myBeaconFollowTexture = Assets.getAtlasRegion("engine:mapObjects/beaconFollow");
        myWhiteTexture = Assets.getAtlasRegion("engine:mapObjects/whiteTex");
        myLineTexture = Assets.getAtlasRegion("engine:mapObjects/gridLine");

        myIconBackground = Assets.getAtlasRegion("engine:mapObjects/hullBg");
        mySkullTexture = Assets.getAtlasRegion("engine:mapObjects/hullSkull");
        myStarPortTexture = Assets.getAtlasRegion("engine:mapObjects/hullStarport");
    }

    public boolean isToggled() {
        return myToggled;
    }

    public void setToggled(boolean toggled) {
        myToggled = toggled;
    }

    public void draw(GameDrawer drawer, SolGame game) {
        SolCam cam = game.getCam();
        float iconSz = getIconRadius(cam) * 2;
        float starNodeW = cam.getViewHeight(myZoom) * STAR_NODE_SZ;
        float viewDist = cam.getViewDistance(myZoom);
        FactionManager factionManager = game.getFactionMan();
        Hero hero = game.getHero();
        Planet np = game.getPlanetManager().getNearestPlanet();
        Vector2 camPos = cam.getPosition();
        float camAngle = cam.getAngle();
        float heroDmgCap = hero.isTranscendent() ? Float.MAX_VALUE : HardnessCalc.getShipDmgCap(hero.getShip());

        drawer.updateMatrix(game);
        game.getGridDrawer().draw(drawer, game, GRID_SZ, myLineTexture);
        drawPlanets(drawer, game, viewDist, np, camPos, heroDmgCap, camAngle);
        drawMazes(drawer, game, viewDist, np, camPos, heroDmgCap, camAngle);
        drawStarNodes(drawer, game, viewDist, camPos, starNodeW);

        // using ui textures
        drawIcons(drawer, game, iconSz, viewDist, factionManager, hero, camPos, heroDmgCap);
    }

    public float getIconRadius(SolCam cam) {
        return cam.getViewHeight(myZoom) * myIconRad;
    }

    private void drawMazes(GameDrawer drawer, SolGame game, float viewDist, Planet np, Vector2 camPos, float heroDmgCap,
                           float camAngle) {
        ArrayList<Maze> mazes = game.getPlanetManager().getMazes();
        for (Maze maze : mazes) {
            Vector2 mazePos = maze.getPos();
            float outerRad = maze.getRadius();
            float rad = outerRad - MazeBuilder.BORDER;
            if (viewDist < camPos.dst(mazePos) - rad) {
                continue;
            }
            drawer.draw(myMazeTexture, 2 * rad, 2 * rad, rad, rad, mazePos.x, mazePos.y, 45, SolColor.WHITE);
            if (HardnessCalc.isDangerous(heroDmgCap, maze.getDps())) {
                drawAreaDanger(drawer, outerRad, mazePos, 1, camAngle);
            }
        }

    }

    private void drawPlanets(GameDrawer drawer, SolGame game, float viewDist, Planet np, Vector2 camPos, float heroDmgCap,
                             float camAngle) {
        ArrayList<SolSystem> systems = game.getPlanetManager().getSystems();
        SolCam cam = game.getCam();
        float circleWidth = cam.getRealLineWidth() * 6;
        float vh = cam.getViewHeight(myZoom);
        for (SolSystem sys : systems) {
            drawer.drawCircle(myLineTexture, sys.getPosition(), sys.getRadius(), SolColor.UI_MED, circleWidth, vh);
        }
        for (SolSystem sys : systems) {
            float dangerRad = HardnessCalc.isDangerous(heroDmgCap, sys.getDps()) ? sys.getRadius() : 0;
            Vector2 sysPos = sys.getPosition();
            float rad = Const.SUN_RADIUS;
            if (camPos.dst(sysPos) - rad < viewDist) {
                drawer.draw(myStarTexture, 2 * rad, 2 * rad, rad, rad, sysPos.x, sysPos.y, 0, SolColor.WHITE);
            }

            Vector2 beltIconPos = SolMath.getVec();
            ArrayList<SystemBelt> belts = sys.getBelts();
            for (SystemBelt belt : belts) {
                float beltRad = belt.getRadius();
                float halfWidth = belt.getHalfWidth();
                int beltIconCount = (int) (.12f * beltRad);
                for (int i = 0; i < beltIconCount; i++) {
                    float angle = 360f * i / beltIconCount;
                    SolMath.fromAl(beltIconPos, angle, beltRad);
                    beltIconPos.add(sysPos);
                    drawer.draw(myBeltTexture, 2 * halfWidth, 2 * halfWidth, halfWidth, halfWidth, beltIconPos.x, beltIconPos.y, angle * 3, SolColor.WHITE);
                }
                float outerRad = beltRad + halfWidth;
                if (dangerRad < outerRad && HardnessCalc.isDangerous(heroDmgCap, belt.getDps())) {
                    dangerRad = outerRad;
                }
            }
            SolMath.free(beltIconPos);
            if (dangerRad < sys.getInnerRadius() && HardnessCalc.isDangerous(heroDmgCap, sys.getInnerDps())) {
                dangerRad = sys.getInnerRadius();
            }
            if (dangerRad > 0) {
                drawAreaDanger(drawer, dangerRad, sysPos, .5f, camAngle);
            }
        }

        ArrayList<Planet> planets = game.getPlanetManager().getPlanets();
        for (Planet planet : planets) {
            Vector2 planetPos = planet.getPosition();
            float fh = planet.getFullHeight();
            float dstToPlanetAtm = camPos.dst(planetPos) - fh;
            if (viewDist < dstToPlanetAtm) {
                continue;
            }
            drawer.draw(myAtmTexture, 2 * fh, 2 * fh, fh, fh, planetPos.x, planetPos.y, 0, SolColor.UI_DARK);
            float groundHeight;
            if (dstToPlanetAtm < 0) {
                groundHeight = planet.getMinGroundHeight() + .5f;
                drawer.draw(myPlanetCoreTexture, 2 * groundHeight, 2 * groundHeight, groundHeight, groundHeight, planetPos.x, planetPos.y, planet.getAngle(), SolColor.WHITE);
                drawNpGround(drawer, game, viewDist, np, camPos);
            } else {
                groundHeight = planet.getGroundHeight();
                drawer.draw(myPlanetTexture, 2 * groundHeight, 2 * groundHeight, groundHeight, groundHeight, planetPos.x, planetPos.y, camAngle, SolColor.WHITE);
            }
            float dangerRad = HardnessCalc.isDangerous(heroDmgCap, planet.getGroundDps()) ? groundHeight + Const.ATM_HEIGHT / 2 : 0;
            //      if (dangerRad < gh && HardnessCalc.isDangerous(heroDmgCap, planet.getGroundDps())) dangerRad = gh;
            if (dangerRad > 0) {
                drawAreaDanger(drawer, dangerRad, planetPos, 1, camAngle);
            }
        }
    }

    private void drawAreaDanger(GameDrawer drawer, float rad, Vector2 position, float transpMul, float angle) {
        float perc = 2 * myAreaSkullTime / MAX_AREA_SKULL_TIME;
        if (perc > 1) {
            perc = 2 - perc;
        }
        perc = SolMath.clamp((perc - .5f) * 2 + .5f);
        float a = SolMath.clamp(perc * transpMul);
        myAreaWarnBackgroundCol.a = a;
        myAreaWarnCol.a = a;
        drawer.draw(myWarnAreaBackground, rad * 2, rad * 2, rad, rad, position.x, position.y, 0, myAreaWarnBackgroundCol);
        rad *= INNER_AREA_ICON_PERC;
        drawer.draw(mySkullBigTexture, rad * 2, rad * 2, rad, rad, position.x, position.y, angle, myAreaWarnCol);
    }

    private void drawIcons(GameDrawer drawer, SolGame game, float iconSz, float viewDist, FactionManager factionManager,
                           Hero hero, Vector2 camPos, float heroDmgCap) {
        List<SolObject> objs = game.getObjectManager().getObjects();
        for (SolObject o : objs) {
            Vector2 oPos = o.getPosition();
            if (viewDist < camPos.dst(oPos)) {
                continue;
            }
            if ((o instanceof SolShip)) {
                SolShip ship = (SolShip) o;
                String hint = ship.getPilot().getMapHint();
                if (hint == null && !DebugOptions.DETAILED_MAP) {
                    continue;
                }
                drawObjIcon(iconSz, oPos, ship.getAngle(), factionManager, hero, ship.getPilot().getFaction(), heroDmgCap, o, ship.getHull().config.getIcon(), drawer);
            }
            if ((o instanceof StarPort)) {
                StarPort sp = (StarPort) o;
                drawStarPortIcon(drawer, iconSz, sp.getFrom(), sp.getTo());
            }
            // Fix for when the player is in hyper. Hero is null and replaced in ObjMan with a StarPort.Transcendent
            if ((o instanceof StarPort.Transcendent)) {
                StarPort.Transcendent t = (StarPort.Transcendent) o;
                if (t.getShip().getPilot().isPlayer()) {
                    FarShip ship = game.getHero().getTranscendentHero().getShip();
                    drawObjIcon(iconSz, oPos, t.getAngle(), factionManager, hero, ship.getPilot().getFaction(), heroDmgCap, o, ship.getHullConfig().getIcon(), drawer);
                }

            }
        }

        List<FarShip> farShips = game.getObjectManager().getFarShips();
        for (FarShip ship : farShips) {
            Vector2 oPos = ship.getPosition();
            if (viewDist < camPos.dst(oPos)) {
                continue;
            }
            String hint = ship.getPilot().getMapHint();
            if (hint == null && !DebugOptions.DETAILED_MAP) {
                continue;
            }
            drawObjIcon(iconSz, oPos, ship.getAngle(), factionManager, hero, ship.getPilot().getFaction(), heroDmgCap, ship, ship.getHullConfig().getIcon(), drawer);
        }

        List<StarPort.MyFar> farPorts = game.getObjectManager().getFarPorts();
        for (StarPort.MyFar sp : farPorts) {
            drawStarPortIcon(drawer, iconSz, sp.getFrom(), sp.getTo());
        }
        BeaconHandler bh = game.getBeaconHandler();
        BeaconHandler.Action bhAction = bh.getCurrAction();
        if (bhAction != null) {
            Vector2 beaconPos = bh.getPos();
            TextureRegion icon = myBeaconMoveTexture;
            if (bhAction == BeaconHandler.Action.ATTACK) {
                icon = myBeaconAttackTexture;
            } else if (bhAction == BeaconHandler.Action.FOLLOW) {
                icon = myBeaconFollowTexture;
            }
            float beaconSz = iconSz * 1.5f;
            //      drawer.draw(icon, beaconSz, beaconSz, beaconSz/2, beaconSz/2, beaconPos.x, beaconPos.y, 0, SolColor.WHITE); interleaving
        }
    }

    //TODO Don't pass null hero to drawObjIcon(). Then remove the annotation from drawObjIcon and remove the hero nullcheck
    public void drawStarPortIcon(GameDrawer drawer, float iconSz, Planet from, Planet to) {
        float angle = SolMath.angle(from.getPosition(), to.getPosition());
        Vector2 position = StarPort.getDesiredPos(from, to, false);
        drawObjIcon(iconSz, position, angle, null, null, null, -1, null, myStarPortTexture, drawer);
        SolMath.free(position);
    }

    private void drawStarNodes(GameDrawer drawer, SolGame game, float viewDist, Vector2 camPos, float starNodeW) {
        List<SolObject> objs = game.getObjectManager().getObjects();
        for (SolObject o : objs) {
            if (!(o instanceof StarPort)) {
                continue;
            }
            Vector2 oPos = o.getPosition();
            if (viewDist < camPos.dst(oPos)) {
                continue;
            }
            StarPort sp = (StarPort) o;
            drawStarNode(drawer, sp.getFrom(), sp.getTo(), starNodeW);
        }

        List<StarPort.MyFar> farPorts = game.getObjectManager().getFarPorts();
        for (StarPort.MyFar sp : farPorts) {
            Vector2 oPos = sp.getPosition();
            if (viewDist < camPos.dst(oPos)) {
                continue;
            }
            if (!sp.isSecondary()) {
                drawStarNode(drawer, sp.getFrom(), sp.getTo(), starNodeW);
            }
        }
    }

    private void drawStarNode(GameDrawer drawer, Planet from, Planet to, float starNodeW) {
        Vector2 pos1 = StarPort.getDesiredPos(from, to, false);
        Vector2 pos2 = StarPort.getDesiredPos(to, from, false);
        drawer.drawLine(myWhiteTexture, pos1, pos2, SolColor.UI_LIGHT, starNodeW, true);
        SolMath.free(pos1);
        SolMath.free(pos2);
    }

    private void drawNpGround(GameDrawer drawer, SolGame game, float viewDist, Planet np, Vector2 camPos) {
        ObjectManager objectManager = game.getObjectManager();
        List<SolObject> objs = objectManager.getObjects();
        for (SolObject o : objs) {
            if (!(o instanceof TileObject)) {
                continue;
            }
            TileObject to = (TileObject) o;
            if (to.getPlanet() != np) {
                continue;
            }
            Vector2 oPos = o.getPosition();
            if (viewDist < camPos.dst(oPos)) {
                continue;
            }
            float sz = to.getSz();
            drawPlanetTile(to.getTile(), sz, drawer, oPos, to.getAngle());
        }

        List<FarObjData> farObjs = objectManager.getFarObjs();
        for (FarObjData fod : farObjs) {
            FarObject o = fod.fo;
            if (!(o instanceof FarTileObject)) {
                continue;
            }
            FarTileObject to = (FarTileObject) o;
            if (to.getPlanet() != np) {
                continue;
            }
            Vector2 oPos = o.getPosition();
            if (viewDist < camPos.dst(oPos)) {
                continue;
            }
            float sz = to.getSz();
            drawPlanetTile(to.getTile(), sz, drawer, oPos, to.getAngle());
        }
    }

    public void drawObjIcon(float iconSz, Vector2 position, float objAngle,
                            FactionManager factionManager, @Nullable Hero hero, Faction objFac, float heroDmgCap,
                            Object shipHack, TextureAtlas.AtlasRegion icon, Object drawerHack) {
        boolean enemy = hero != null && hero.isNonTranscendent() && factionManager.areEnemies(objFac, hero.getPilot().getFaction());
        float angle = objAngle;
        if (enemy && mySkullTime > 0 && HardnessCalc.isDangerous(heroDmgCap, shipHack)) {
            icon = mySkullTexture;
            angle = 0;
        }
        float innerIconSz = iconSz * INNER_ICON_PERC;

        if (drawerHack instanceof UiDrawer) {
            UiDrawer uiDrawer = (UiDrawer) drawerHack;
            uiDrawer.draw(myIconBackground, iconSz, iconSz, iconSz / 2, iconSz / 2, position.x, position.y, 0, enemy ? SolColor.UI_WARN : SolColor.UI_LIGHT);
            uiDrawer.draw(icon, innerIconSz, innerIconSz, innerIconSz / 2, innerIconSz / 2, position.x, position.y, angle, SolColor.WHITE);
        } else {
            GameDrawer gameDrawer = (GameDrawer) drawerHack;
            gameDrawer.draw(myIconBackground, iconSz, iconSz, iconSz / 2, iconSz / 2, position.x, position.y, 0, enemy ? SolColor.UI_WARN : SolColor.UI_LIGHT);
            gameDrawer.draw(icon, innerIconSz, innerIconSz, innerIconSz / 2, innerIconSz / 2, position.x, position.y, angle, SolColor.WHITE);
        }
    }

    public void changeZoom(boolean zoomIn) {
        if (zoomIn) {
            myZoom /= MUL_FACTOR;
        } else {
            myZoom *= MUL_FACTOR;
        }
        myZoom = SolMath.clamp(myZoom, MIN_ZOOM, MAX_ZOOM);
    }

    public float getZoom() {
        return myZoom;
    }

    public void update(SolGame game) {
        mySkullTime += game.getTimeStep();
        if (mySkullTime > MAX_SKULL_TIME) {
            mySkullTime = -MAX_SKULL_TIME;
        }
        myAreaSkullTime += game.getTimeStep();
        if (myAreaSkullTime > MAX_AREA_SKULL_TIME) {
            myAreaSkullTime = 0;
        }
    }

    private void drawPlanetTile(Tile t, float sz, GameDrawer drawer, Vector2 p, float angle) {
        float szh = .6f * sz;
        Color col = t.from == SurfaceDirection.UP && t.to == SurfaceDirection.UP ? SolColor.WHITE : SolColor.UI_OPAQUE;
        if (t.from == SurfaceDirection.FWD || t.from == SurfaceDirection.UP) {
            if (t.from == SurfaceDirection.UP) {
                drawer.draw(myWhiteTexture, szh, szh, 0, 0, p.x, p.y, angle - 90, col);
            }
            drawer.draw(myWhiteTexture, szh, szh, 0, 0, p.x, p.y, angle, col);
        }
        if (t.to == SurfaceDirection.FWD || t.to == SurfaceDirection.UP) {
            if (t.to == SurfaceDirection.UP) {
                drawer.draw(myWhiteTexture, szh, szh, 0, 0, p.x, p.y, angle + 180, col);
            }
            drawer.draw(myWhiteTexture, szh, szh, 0, 0, p.x, p.y, angle + 90, col);
        }
    }

    public TextureAtlas.AtlasRegion getStarPortTex() {
        return myStarPortTexture;
    }

}
