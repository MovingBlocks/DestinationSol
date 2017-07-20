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
    private final TextureAtlas.AtlasRegion myAtmTex;
    private final TextureAtlas.AtlasRegion myPlanetTex;
    private final TextureAtlas.AtlasRegion myPlanetCoreTex;
    private final TextureAtlas.AtlasRegion myStarTex;
    private final TextureAtlas.AtlasRegion myMazeTex;
    private final TextureAtlas.AtlasRegion mySkullTex;
    private final TextureAtlas.AtlasRegion mySkullBigTex;
    private final TextureAtlas.AtlasRegion myStarPortTex;
    private final TextureAtlas.AtlasRegion myBeltTex;
    private final TextureAtlas.AtlasRegion myBeaconAttackTex;
    private final TextureAtlas.AtlasRegion myBeaconMoveTex;
    private final TextureAtlas.AtlasRegion myBeaconFollowTex;
    private final TextureAtlas.AtlasRegion myIconBg;
    private final TextureAtlas.AtlasRegion myWarnAreaBg;
    private final TextureAtlas.AtlasRegion myWhiteTex;
    private final TextureAtlas.AtlasRegion myLineTex;

    private final Color myAreaWarnCol;
    private final Color myAreaWarnBgCol;
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
        myAreaWarnBgCol = new Color(SolColor.UI_WARN);

        myWarnAreaBg = Assets.getAtlasRegion("engine:mapObjects/warnBg");
        myAtmTex = Assets.getAtlasRegion("engine:mapObjects/atm");
        myPlanetTex = Assets.getAtlasRegion("engine:mapObjects/planet");
        myPlanetCoreTex = Assets.getAtlasRegion("engine:mapObjects/planetCore");
        myStarTex = Assets.getAtlasRegion("engine:mapObjects/star");
        myMazeTex = Assets.getAtlasRegion("engine:mapObjects/maze");
        mySkullBigTex = Assets.getAtlasRegion("engine:mapObjects/skullBig");
        myBeltTex = Assets.getAtlasRegion("engine:mapObjects/asteroids");
        myBeaconAttackTex = Assets.getAtlasRegion("engine:mapObjects/beaconAttack");
        myBeaconMoveTex = Assets.getAtlasRegion("engine:mapObjects/beaconMove");
        myBeaconFollowTex = Assets.getAtlasRegion("engine:mapObjects/beaconFollow");
        myWhiteTex = Assets.getAtlasRegion("engine:mapObjects/whiteTex");
        myLineTex = Assets.getAtlasRegion("engine:mapObjects/gridLine");

        myIconBg = Assets.getAtlasRegion("engine:mapObjects/hullBg");
        mySkullTex = Assets.getAtlasRegion("engine:mapObjects/hullSkull");
        myStarPortTex = Assets.getAtlasRegion("engine:mapObjects/hullStarport");
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
        float viewDist = cam.getViewDist(myZoom);
        FactionManager factionManager = game.getFactionMan();
        SolShip hero = game.getHero();
        Planet np = game.getPlanetMan().getNearestPlanet();
        Vector2 camPos = cam.getPos();
        float camAngle = cam.getAngle();
        float heroDmgCap = hero == null ? Float.MAX_VALUE : HardnessCalc.getShipDmgCap(hero);

        drawer.updateMtx(game);
        game.getGridDrawer().draw(drawer, game, GRID_SZ, myLineTex);
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
        ArrayList<Maze> mazes = game.getPlanetMan().getMazes();
        for (Maze maze : mazes) {
            Vector2 mazePos = maze.getPos();
            float outerRad = maze.getRadius();
            float rad = outerRad - MazeBuilder.BORDER;
            if (viewDist < camPos.dst(mazePos) - rad) {
                continue;
            }
            drawer.draw(myMazeTex, 2 * rad, 2 * rad, rad, rad, mazePos.x, mazePos.y, 45, SolColor.WHITE);
            if (HardnessCalc.isDangerous(heroDmgCap, maze.getDps())) {
                drawAreaDanger(drawer, outerRad, mazePos, 1, camAngle);
            }
        }

    }

    private void drawPlanets(GameDrawer drawer, SolGame game, float viewDist, Planet np, Vector2 camPos, float heroDmgCap,
                             float camAngle) {
        ArrayList<SolSystem> systems = game.getPlanetMan().getSystems();
        SolCam cam = game.getCam();
        float circleWidth = cam.getRealLineWidth() * 6;
        float vh = cam.getViewHeight(myZoom);
        for (SolSystem sys : systems) {
            drawer.drawCircle(myLineTex, sys.getPos(), sys.getRadius(), SolColor.UI_MED, circleWidth, vh);
        }
        for (SolSystem sys : systems) {
            float dangerRad = HardnessCalc.isDangerous(heroDmgCap, sys.getDps()) ? sys.getRadius() : 0;
            Vector2 sysPos = sys.getPos();
            float rad = Const.SUN_RADIUS;
            if (camPos.dst(sysPos) - rad < viewDist) {
                drawer.draw(myStarTex, 2 * rad, 2 * rad, rad, rad, sysPos.x, sysPos.y, 0, SolColor.WHITE);
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
                    drawer.draw(myBeltTex, 2 * halfWidth, 2 * halfWidth, halfWidth, halfWidth, beltIconPos.x, beltIconPos.y, angle * 3, SolColor.WHITE);
                }
                float outerRad = beltRad + halfWidth;
                if (dangerRad < outerRad && HardnessCalc.isDangerous(heroDmgCap, belt.getDps())) {
                    dangerRad = outerRad;
                }
            }
            SolMath.free(beltIconPos);
            if (dangerRad < sys.getInnerRad() && HardnessCalc.isDangerous(heroDmgCap, sys.getInnerDps())) {
                dangerRad = sys.getInnerRad();
            }
            if (dangerRad > 0) {
                drawAreaDanger(drawer, dangerRad, sysPos, .5f, camAngle);
            }
        }

        ArrayList<Planet> planets = game.getPlanetMan().getPlanets();
        for (Planet planet : planets) {
            Vector2 planetPos = planet.getPos();
            float fh = planet.getFullHeight();
            float dstToPlanetAtm = camPos.dst(planetPos) - fh;
            if (viewDist < dstToPlanetAtm) {
                continue;
            }
            drawer.draw(myAtmTex, 2 * fh, 2 * fh, fh, fh, planetPos.x, planetPos.y, 0, SolColor.UI_DARK);
            float gh;
            if (dstToPlanetAtm < 0) {
                gh = planet.getMinGroundHeight() + .5f;
                drawer.draw(myPlanetCoreTex, 2 * gh, 2 * gh, gh, gh, planetPos.x, planetPos.y, planet.getAngle(), SolColor.WHITE);
                drawNpGround(drawer, game, viewDist, np, camPos);
            } else {
                gh = planet.getGroundHeight();
                drawer.draw(myPlanetTex, 2 * gh, 2 * gh, gh, gh, planetPos.x, planetPos.y, camAngle, SolColor.WHITE);
            }
            float dangerRad = HardnessCalc.isDangerous(heroDmgCap, planet.getGroundDps()) ? gh + Const.ATM_HEIGHT / 2 : 0;
            //      if (dangerRad < gh && HardnessCalc.isDangerous(heroDmgCap, planet.getGroundDps())) dangerRad = gh;
            if (dangerRad > 0) {
                drawAreaDanger(drawer, dangerRad, planetPos, 1, camAngle);
            }
        }
    }

    private void drawAreaDanger(GameDrawer drawer, float rad, Vector2 pos, float transpMul, float angle) {
        float perc = 2 * myAreaSkullTime / MAX_AREA_SKULL_TIME;
        if (perc > 1) {
            perc = 2 - perc;
        }
        perc = SolMath.clamp((perc - .5f) * 2 + .5f);
        float a = SolMath.clamp(perc * transpMul);
        myAreaWarnBgCol.a = a;
        myAreaWarnCol.a = a;
        drawer.draw(myWarnAreaBg, rad * 2, rad * 2, rad, rad, pos.x, pos.y, 0, myAreaWarnBgCol);
        rad *= INNER_AREA_ICON_PERC;
        drawer.draw(mySkullBigTex, rad * 2, rad * 2, rad, rad, pos.x, pos.y, angle, myAreaWarnCol);
    }

    private void drawIcons(GameDrawer drawer, SolGame game, float iconSz, float viewDist, FactionManager factionManager,
                           SolShip hero, Vector2 camPos, float heroDmgCap) {
        List<SolObject> objs = game.getObjMan().getObjs();
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
                    FarShip ship = game.getTranscendentHero().getShip();
                    drawObjIcon(iconSz, oPos, t.getAngle(), factionManager, hero, ship.getPilot().getFaction(), heroDmgCap, o, ship.getHullConfig().getIcon(), drawer);
                }

            }
        }

        List<FarShip> farShips = game.getObjMan().getFarShips();
        for (FarShip ship : farShips) {
            Vector2 oPos = ship.getPos();
            if (viewDist < camPos.dst(oPos)) {
                continue;
            }
            String hint = ship.getPilot().getMapHint();
            if (hint == null && !DebugOptions.DETAILED_MAP) {
                continue;
            }
            drawObjIcon(iconSz, oPos, ship.getAngle(), factionManager, hero, ship.getPilot().getFaction(), heroDmgCap, ship, ship.getHullConfig().getIcon(), drawer);
        }

        List<StarPort.MyFar> farPorts = game.getObjMan().getFarPorts();
        for (StarPort.MyFar sp : farPorts) {
            drawStarPortIcon(drawer, iconSz, sp.getFrom(), sp.getTo());
        }
        BeaconHandler bh = game.getBeaconHandler();
        BeaconHandler.Action bhAction = bh.getCurrAction();
        if (bhAction != null) {
            Vector2 beaconPos = bh.getPos();
            TextureRegion icon = myBeaconMoveTex;
            if (bhAction == BeaconHandler.Action.ATTACK) {
                icon = myBeaconAttackTex;
            } else if (bhAction == BeaconHandler.Action.FOLLOW) {
                icon = myBeaconFollowTex;
            }
            float beaconSz = iconSz * 1.5f;
            //      drawer.draw(icon, beaconSz, beaconSz, beaconSz/2, beaconSz/2, beaconPos.x, beaconPos.y, 0, SolColor.WHITE); interleaving
        }
    }

    public void drawStarPortIcon(GameDrawer drawer, float iconSz, Planet from, Planet to) {
        float angle = SolMath.angle(from.getPos(), to.getPos());
        Vector2 pos = StarPort.getDesiredPos(from, to, false);
        drawObjIcon(iconSz, pos, angle, null, null, null, -1, null, myStarPortTex, drawer);
        SolMath.free(pos);
    }

    private void drawStarNodes(GameDrawer drawer, SolGame game, float viewDist, Vector2 camPos, float starNodeW) {
        List<SolObject> objs = game.getObjMan().getObjs();
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

        List<StarPort.MyFar> farPorts = game.getObjMan().getFarPorts();
        for (StarPort.MyFar sp : farPorts) {
            Vector2 oPos = sp.getPos();
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
        drawer.drawLine(myWhiteTex, pos1, pos2, SolColor.UI_LIGHT, starNodeW, true);
        SolMath.free(pos1);
        SolMath.free(pos2);
    }

    private void drawNpGround(GameDrawer drawer, SolGame game, float viewDist, Planet np, Vector2 camPos) {
        ObjectManager objectManager = game.getObjMan();
        List<SolObject> objs = objectManager.getObjs();
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
            FarObj o = fod.fo;
            if (!(o instanceof FarTileObject)) {
                continue;
            }
            FarTileObject to = (FarTileObject) o;
            if (to.getPlanet() != np) {
                continue;
            }
            Vector2 oPos = o.getPos();
            if (viewDist < camPos.dst(oPos)) {
                continue;
            }
            float sz = to.getSz();
            drawPlanetTile(to.getTile(), sz, drawer, oPos, to.getAngle());
        }
    }

    public void drawObjIcon(float iconSz, Vector2 pos, float objAngle,
                            FactionManager factionManager, SolShip hero, Faction objFac, float heroDmgCap,
                            Object shipHack, TextureAtlas.AtlasRegion icon, Object drawerHack) {
        boolean enemy = hero != null && factionManager.areEnemies(objFac, hero.getPilot().getFaction());
        float angle = objAngle;
        if (enemy && mySkullTime > 0 && HardnessCalc.isDangerous(heroDmgCap, shipHack)) {
            icon = mySkullTex;
            angle = 0;
        }
        float innerIconSz = iconSz * INNER_ICON_PERC;

        if (drawerHack instanceof UiDrawer) {
            UiDrawer uiDrawer = (UiDrawer) drawerHack;
            uiDrawer.draw(myIconBg, iconSz, iconSz, iconSz / 2, iconSz / 2, pos.x, pos.y, 0, enemy ? SolColor.UI_WARN : SolColor.UI_LIGHT);
            uiDrawer.draw(icon, innerIconSz, innerIconSz, innerIconSz / 2, innerIconSz / 2, pos.x, pos.y, angle, SolColor.WHITE);
        } else {
            GameDrawer gameDrawer = (GameDrawer) drawerHack;
            gameDrawer.draw(myIconBg, iconSz, iconSz, iconSz / 2, iconSz / 2, pos.x, pos.y, 0, enemy ? SolColor.UI_WARN : SolColor.UI_LIGHT);
            gameDrawer.draw(icon, innerIconSz, innerIconSz, innerIconSz / 2, innerIconSz / 2, pos.x, pos.y, angle, SolColor.WHITE);
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
                drawer.draw(myWhiteTex, szh, szh, 0, 0, p.x, p.y, angle - 90, col);
            }
            drawer.draw(myWhiteTex, szh, szh, 0, 0, p.x, p.y, angle, col);
        }
        if (t.to == SurfaceDirection.FWD || t.to == SurfaceDirection.UP) {
            if (t.to == SurfaceDirection.UP) {
                drawer.draw(myWhiteTex, szh, szh, 0, 0, p.x, p.y, angle + 180, col);
            }
            drawer.draw(myWhiteTex, szh, szh, 0, 0, p.x, p.y, angle + 90, col);
        }
    }

    public TextureAtlas.AtlasRegion getStarPortTex() {
        return myStarPortTex;
    }

}
