/*
 * Copyright 2018 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.destinationsol.game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import org.destinationsol.Const;
import org.destinationsol.SolApplication;
import org.destinationsol.assets.Assets;
import org.destinationsol.common.Nullable;
import org.destinationsol.common.SolColor;
import org.destinationsol.common.SolMath;
import org.destinationsol.game.context.Context;
import org.destinationsol.game.faction.Faction;
import org.destinationsol.game.maze.Maze;
import org.destinationsol.game.maze.MazeBuilder;
import org.destinationsol.game.planet.FarTileObject;
import org.destinationsol.game.planet.Planet;
import org.destinationsol.game.planet.SolarSystem;
import org.destinationsol.game.planet.SurfaceDirection;
import org.destinationsol.game.planet.SystemBelt;
import org.destinationsol.game.planet.Tile;
import org.destinationsol.game.planet.TileObject;
import org.destinationsol.game.ship.FarShip;
import org.destinationsol.game.ship.SolShip;
import org.destinationsol.ui.DisplayDimensions;
import org.destinationsol.ui.UiDrawer;
import org.destinationsol.ui.Waypoint;
import org.destinationsol.world.generators.SolarSystemGenerator;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

public class MapDrawer implements UpdateAwareSystem{
    public static final float MIN_ZOOM = 8f;
    private static final float MUL_FACTOR = 2f;
    public static final float MAX_ZOOM = 512f;
    private static final float ICON_RAD = .02f;
    private static final float STAR_NODE_SZ = .003f;
    private static final float INNER_ICON_PERC = .6f;
    private static final float INNER_AREA_ICON_PERC = .7f;
    private static final float GRID_SZ = 40f;
    private static final float MIN_ICON_RAD_PX = 16f;
    private static final float MAX_SKULL_TIME = .75f;
    private static final float MAX_AREA_SKULL_TIME = 3;
    private final TextureAtlas.AtlasRegion atmosphereTexture;
    private final TextureAtlas.AtlasRegion planetTexture;
    private final TextureAtlas.AtlasRegion planetCoreTexture;
    private final TextureAtlas.AtlasRegion starTexture;
    private final TextureAtlas.AtlasRegion mazeTexture;
    private final TextureAtlas.AtlasRegion skullTexture;
    private final TextureAtlas.AtlasRegion skullBigTexture;
    private final TextureAtlas.AtlasRegion starPortTexture;
    private final TextureAtlas.AtlasRegion beltTexture;
    private final TextureAtlas.AtlasRegion beaconAttackTexture;
    private final TextureAtlas.AtlasRegion beaconMoveTexture;
    private final TextureAtlas.AtlasRegion beaconFollowTexture;
    private final TextureAtlas.AtlasRegion iconBackground;
    private final TextureAtlas.AtlasRegion warnAreaBackground;
    private final TextureAtlas.AtlasRegion whiteTexture;
    private final TextureAtlas.AtlasRegion lineTexture;
    private final TextureAtlas.AtlasRegion waypointTexture;

    private final Color areaWarningColor;
    private final Color areaWarningBackgroundColor;
    private final float iconRadius;
    private boolean isToggled;
    private float zoom;
    private float skullTime;
    private float areaSkullTime;

    private final Vector2 mapDrawPositionAdditive = new Vector2();

    @Inject
    MapDrawer() {
        DisplayDimensions displayDimensions = SolApplication.displayDimensions;

        zoom = MAX_ZOOM / MUL_FACTOR / MUL_FACTOR;
        float minIconRad = MIN_ICON_RAD_PX / displayDimensions.getHeight();
        iconRadius = ICON_RAD < minIconRad ? minIconRad : ICON_RAD;

        areaWarningColor = new Color(SolColor.WHITE);
        areaWarningBackgroundColor = new Color(SolColor.UI_WARN);

        warnAreaBackground = Assets.getAtlasRegion("engine:mapObjects/warnBg");
        atmosphereTexture = Assets.getAtlasRegion("engine:mapObjects/atm");
        planetTexture = Assets.getAtlasRegion("engine:mapObjects/planet");
        planetCoreTexture = Assets.getAtlasRegion("engine:mapObjects/planetCore");
        starTexture = Assets.getAtlasRegion("engine:mapObjects/star");
        mazeTexture = Assets.getAtlasRegion("engine:mapObjects/maze");
        skullBigTexture = Assets.getAtlasRegion("engine:mapObjects/skullBig");
        beltTexture = Assets.getAtlasRegion("engine:mapObjects/asteroids");
        beaconAttackTexture = Assets.getAtlasRegion("engine:mapObjects/beaconAttack");
        beaconMoveTexture = Assets.getAtlasRegion("engine:mapObjects/beaconMove");
        beaconFollowTexture = Assets.getAtlasRegion("engine:mapObjects/beaconFollow");
        whiteTexture = Assets.getAtlasRegion("engine:mapObjects/whiteTex");
        lineTexture = Assets.getAtlasRegion("engine:mapObjects/gridLine");
        waypointTexture = Assets.getAtlasRegion("engine:mapObjects/waypoint");

        iconBackground = Assets.getAtlasRegion("engine:mapObjects/hullBg");
        skullTexture = Assets.getAtlasRegion("engine:mapObjects/hullSkull");
        starPortTexture = Assets.getAtlasRegion("engine:mapObjects/hullStarport");
    }

    public boolean isToggled() {
        return isToggled;
    }

    public void setToggled(boolean toggled) {
        isToggled = toggled;
    }

    public void draw(GameDrawer drawer, SolGame game, Context context) {
        SolCam camera = context.get(SolCam.class);
        float iconSz = getIconRadius(camera) * 2;
        float starNodeW = camera.getViewHeight(zoom) * STAR_NODE_SZ;
        float viewDist = camera.getViewDistance(zoom);
        FactionManager factionManager = game.getFactionMan();
        Hero hero = game.getHero();
        Planet np = game.getPlanetManager().getNearestPlanet();
        Vector2 cameraPosition = camera.getPosition();
        float camAngle = camera.getAngle();
        float heroDmgCap = hero.isTranscendent() ? Float.MAX_VALUE : HardnessCalc.getShipDmgCap(hero.getShip());

        //Update drawing camera's position in-case the map is panned around
        OrthographicCamera drawCamera = camera.getCamera();
        drawCamera.position.add(mapDrawPositionAdditive.x, mapDrawPositionAdditive.y, 0);
        drawCamera.update();
        drawer.updateMatrix(context);

        Vector2 viewPosition = cameraPosition.cpy().add(mapDrawPositionAdditive);

        game.getGridDrawer().draw(drawer, game, GRID_SZ, lineTexture);
        drawPlanets(drawer, game, viewDist, np, cameraPosition, viewPosition, heroDmgCap, camAngle, context);
        drawMazes(drawer, game, viewDist, np, viewPosition, heroDmgCap, camAngle);
        drawStarNodes(drawer, game, viewDist, viewPosition, starNodeW);
        drawWaypoints(drawer, game, iconSz, viewDist);

        // using ui textures
        drawIcons(drawer, game, iconSz, viewDist, factionManager, hero, viewPosition, heroDmgCap);

        if (game.getScreens().mapScreen.isPickingWaypointSpot()) {
            drawer.drawString("Click a spot for the new waypoint", drawCamera.position.x, drawCamera.position.y + (zoom* 1.5f), 0.125f * zoom, true, Color.RED);
        }

        if (game.getScreens().mapScreen.isPickingWaypointToRemove()) {
            drawer.drawString("Click a waypoint to remove", drawCamera.position.x, drawCamera.position.y + (zoom* 1.5f), 0.125f * zoom, true, Color.RED);
        }

        //Reset the camera's position for use in any other class
        drawCamera.position.set(cameraPosition.x, cameraPosition.y, 0);
        drawCamera.update();
    }

    public float getIconRadius(SolCam cam) {
        return cam.getViewHeight(zoom) * iconRadius;
    }

    private void drawMazes(GameDrawer drawer, SolGame game, float viewDist, Planet np, Vector2 viewPos, float heroDmgCap,
                           float camAngle) {
        ArrayList<Maze> mazes = game.getPlanetManager().getMazes();
        for (Maze maze : mazes) {
            Vector2 mazePos = maze.getPos();
            float outerRad = maze.getRadius();
            float rad = outerRad - MazeBuilder.BORDER;
            if (viewDist < viewPos.dst(mazePos) - rad) {
                continue;
            }
            drawer.draw(mazeTexture, 2 * rad, 2 * rad, rad, rad, mazePos.x, mazePos.y, 45, SolColor.WHITE);
            if (HardnessCalc.isDangerous(heroDmgCap, maze.getDps())) {
                drawAreaDanger(drawer, outerRad, mazePos, 1, camAngle);
            }
        }

    }

    private void drawPlanets(GameDrawer drawer, SolGame game, float viewDist, Planet np, Vector2 camPos, Vector2 viewPos,
                             float heroDmgCap, float camAngle, Context context) {
        ArrayList<SolarSystem> systems = game.getPlanetManager().getSystems();
        SolCam cam = context.get(SolCam.class);
        float circleWidth = cam.getRealLineWidth() * 6;
        float vh = cam.getViewHeight(zoom);
        for (SolarSystem sys : systems) {
            drawer.drawCircle(lineTexture, sys.getPosition(), sys.getRadius(), SolColor.UI_MED, circleWidth, vh);
        }
        for (SolarSystem sys : systems) {
            float dangerRad = HardnessCalc.isDangerous(heroDmgCap, sys.getDps()) ? sys.getRadius() : 0;
            Vector2 sysPos = sys.getPosition();
            float rad = SolarSystemGenerator.SUN_RADIUS;
            if (viewPos.dst(sysPos) - rad < viewDist) {
                drawer.draw(starTexture, 2 * rad, 2 * rad, rad, rad, sysPos.x, sysPos.y, 0, SolColor.WHITE);
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
                    drawer.draw(beltTexture, 2 * halfWidth, 2 * halfWidth, halfWidth, halfWidth, beltIconPos.x, beltIconPos.y, angle * 3, SolColor.WHITE);
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
            float viewDstToPlanetAtm = viewPos.dst(planetPos) - fh;
            if (viewDist < viewDstToPlanetAtm) {
                continue;
            }
            drawer.draw(atmosphereTexture, 2 * fh, 2 * fh, fh, fh, planetPos.x, planetPos.y, 0, SolColor.UI_DARK);
            float groundHeight;
            float dstToPlanetAtm = camPos.dst(planetPos) - fh;
            if (dstToPlanetAtm < 0) {
                groundHeight = planet.getMinGroundHeight() + .5f;
                drawer.draw(planetCoreTexture, 2 * groundHeight, 2 * groundHeight, groundHeight, groundHeight, planetPos.x, planetPos.y, planet.getAngle(), SolColor.WHITE);
                drawNpGround(drawer, game, viewDist, np, camPos);
            } else {
                groundHeight = planet.getGroundHeight();
                drawer.draw(planetTexture, 2 * groundHeight, 2 * groundHeight, groundHeight, groundHeight, planetPos.x, planetPos.y, camAngle, SolColor.WHITE);
            }
            float dangerRad = HardnessCalc.isDangerous(heroDmgCap, planet.getGroundDamagePerSecond()) ? groundHeight + Const.ATM_HEIGHT / 2 : 0;
            //      if (dangerRad < gh && HardnessCalc.isDangerous(heroDmgCap, planet.getGroundDamagePerSecond())) dangerRad = gh;
            if (dangerRad > 0) {
                drawAreaDanger(drawer, dangerRad, planetPos, 1, camAngle);
            }
        }
    }

    private void drawAreaDanger(GameDrawer drawer, float rad, Vector2 position, float transpMul, float angle) {
        float perc = 2 * areaSkullTime / MAX_AREA_SKULL_TIME;
        if (perc > 1) {
            perc = 2 - perc;
        }
        perc = SolMath.clamp((perc - .5f) * 2 + .5f);
        float a = SolMath.clamp(perc * transpMul);
        areaWarningBackgroundColor.a = a;
        areaWarningColor.a = a;
        drawer.draw(warnAreaBackground, rad * 2, rad * 2, rad, rad, position.x, position.y, 0, areaWarningBackgroundColor);
        rad *= INNER_AREA_ICON_PERC;
        drawer.draw(skullBigTexture, rad * 2, rad * 2, rad, rad, position.x, position.y, angle, areaWarningColor);
    }

    private void drawWaypoints(GameDrawer drawer, SolGame game, float iconSize, float viewDist) {
        ArrayList<Waypoint> waypoints = game.getHero().getWaypoints();
        for (Waypoint waypoint : waypoints) {
            if (waypoint.position.dst(game.getHero().getPosition()) > viewDist) {
                continue;
            }
            drawWaypointIcon(iconSize, waypoint.position, waypointTexture, drawer, waypoint.color);
        }
    }

    private void drawIcons(GameDrawer drawer, SolGame game, float iconSz, float viewDist, FactionManager factionManager,
                           Hero hero, Vector2 viewPos, float heroDmgCap) {
        List<SolObject> objs = game.getObjectManager().getObjects();
        for (SolObject o : objs) {
            Vector2 oPos = o.getPosition();
            if (viewDist < viewPos.dst(oPos)) {
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
                drawStarPortIcon(drawer, iconSz, sp.getFromPlanet(), sp.getToPlanet());
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
            if (viewDist < viewPos.dst(oPos)) {
                continue;
            }
            String hint = ship.getPilot().getMapHint();
            if (hint == null && !DebugOptions.DETAILED_MAP) {
                continue;
            }
            drawObjIcon(iconSz, oPos, ship.getAngle(), factionManager, hero, ship.getPilot().getFaction(), heroDmgCap, ship, ship.getHullConfig().getIcon(), drawer);
        }

        List<StarPort.FarStarPort> farPorts = game.getObjectManager().getFarPorts();
        for (StarPort.FarStarPort sp : farPorts) {
            drawStarPortIcon(drawer, iconSz, sp.getFrom(), sp.getTo());
        }
        BeaconHandler bh = game.getBeaconHandler();
        BeaconHandler.Action bhAction = bh.getCurrAction();
        if (bhAction != null) {
            Vector2 beaconPos = bh.getPos();
            TextureRegion icon = beaconMoveTexture;
            if (bhAction == BeaconHandler.Action.ATTACK) {
                icon = beaconAttackTexture;
            } else if (bhAction == BeaconHandler.Action.FOLLOW) {
                icon = beaconFollowTexture;
            }
            float beaconSz = iconSz * 1.5f;
            //      drawer.draw(icon, beaconSz, beaconSz, beaconSz/2, beaconSz/2, beaconPos.x, beaconPos.y, 0, SolColor.WHITE); interleaving
        }
    }

    //TODO Don't pass null hero to drawObjIcon(). Then remove the annotation from drawObjIcon and remove the hero nullcheck
    public void drawStarPortIcon(GameDrawer drawer, float iconSz, Planet from, Planet to) {
        float angle = SolMath.angle(from.getPosition(), to.getPosition());
        Vector2 position = StarPort.getDesiredPosition(from, to, false);
        drawObjIcon(iconSz, position, angle, null, null, null, -1, null, starPortTexture, drawer);
        SolMath.free(position);
    }

    private void drawStarNodes(GameDrawer drawer, SolGame game, float viewDist, Vector2 viewPos, float starNodeW) {
        List<SolObject> objs = game.getObjectManager().getObjects();
        for (SolObject o : objs) {
            if (!(o instanceof StarPort)) {
                continue;
            }
            Vector2 oPos = o.getPosition();
            if (viewDist < viewPos.dst(oPos)) {
                continue;
            }
            StarPort sp = (StarPort) o;
            drawStarNode(drawer, sp.getFromPlanet(), sp.getToPlanet(), starNodeW);
        }

        List<StarPort.FarStarPort> farPorts = game.getObjectManager().getFarPorts();
        for (StarPort.FarStarPort sp : farPorts) {
            Vector2 oPos = sp.getPosition();
            if (viewDist < viewPos.dst(oPos)) {
                continue;
            }
            if (!sp.isSecondary()) {
                drawStarNode(drawer, sp.getFrom(), sp.getTo(), starNodeW);
            }
        }
    }

    private void drawStarNode(GameDrawer drawer, Planet from, Planet to, float starNodeW) {
        Vector2 pos1 = StarPort.getDesiredPosition(from, to, false);
        Vector2 pos2 = StarPort.getDesiredPosition(to, from, false);
        drawer.drawLine(whiteTexture, pos1, pos2, SolColor.UI_LIGHT, starNodeW, true);
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
        if (enemy && skullTime > 0 && HardnessCalc.isDangerous(heroDmgCap, shipHack)) {
            icon = skullTexture;
            angle = 0;
        }
        float innerIconSz = iconSz * INNER_ICON_PERC;

        if (drawerHack instanceof UiDrawer) {
            UiDrawer uiDrawer = (UiDrawer) drawerHack;
            uiDrawer.draw(iconBackground, iconSz, iconSz, iconSz / 2, iconSz / 2, position.x, position.y, 0, enemy ? SolColor.UI_WARN : SolColor.UI_LIGHT);
            uiDrawer.draw(icon, innerIconSz, innerIconSz, innerIconSz / 2, innerIconSz / 2, position.x, position.y, angle, SolColor.WHITE);
        } else {
            GameDrawer gameDrawer = (GameDrawer) drawerHack;
            gameDrawer.draw(iconBackground, iconSz, iconSz, iconSz / 2, iconSz / 2, position.x, position.y, 0, enemy ? SolColor.UI_WARN : SolColor.UI_LIGHT);
            gameDrawer.draw(icon, innerIconSz, innerIconSz, innerIconSz / 2, innerIconSz / 2, position.x, position.y, angle, SolColor.WHITE);
        }
    }

    public void drawWaypointIcon(float iconSz, Vector2 position, TextureAtlas.AtlasRegion icon, Object drawer, Color color) {
        float innerIconSz = iconSz * INNER_ICON_PERC;
        //drawer can be either UiDrawer or GameDrawer because it is also called from BorderDrawer, which uses UiDrawer, not GameDrawer.
        if (drawer instanceof UiDrawer) {
            UiDrawer uiDrawer = (UiDrawer) drawer;
            uiDrawer.draw(iconBackground, iconSz, iconSz, iconSz / 2, iconSz / 2, position.x, position.y, 0, SolColor.UI_LIGHT);
            uiDrawer.draw(icon, innerIconSz, innerIconSz, innerIconSz / 2, innerIconSz / 2, position.x, position.y, 0, color);
        } else {
            GameDrawer gameDrawer = (GameDrawer) drawer;
            gameDrawer.draw(iconBackground, iconSz, iconSz, iconSz / 2, iconSz / 2, position.x, position.y, 0, SolColor.UI_LIGHT);
            gameDrawer.draw(icon, innerIconSz, innerIconSz, innerIconSz / 2, innerIconSz / 2, position.x, position.y, 0, color);
        }
    }

    public void changeZoom(boolean zoomIn) {
        if (zoomIn) {
            zoom /= MUL_FACTOR;
        } else {
            zoom *= MUL_FACTOR;
        }
        zoom = MathUtils.clamp(zoom, MIN_ZOOM, MAX_ZOOM);
    }

    public float getZoom() {
        return zoom;
    }

    @Override
    public void update(SolGame game, float timeStep) {
        skullTime += timeStep;
        if (skullTime > MAX_SKULL_TIME) {
            skullTime = -MAX_SKULL_TIME;
        }
        areaSkullTime += timeStep;
        if (areaSkullTime > MAX_AREA_SKULL_TIME) {
            areaSkullTime = 0;
        }
    }

    private void drawPlanetTile(Tile t, float sz, GameDrawer drawer, Vector2 p, float angle) {
        float szh = .6f * sz;
        Color col = t.from == SurfaceDirection.UP && t.to == SurfaceDirection.UP ? SolColor.WHITE : SolColor.UI_OPAQUE;
        if (t.from == SurfaceDirection.FWD || t.from == SurfaceDirection.UP) {
            if (t.from == SurfaceDirection.UP) {
                drawer.draw(whiteTexture, szh, szh, 0, 0, p.x, p.y, angle - 90, col);
            }
            drawer.draw(whiteTexture, szh, szh, 0, 0, p.x, p.y, angle, col);
        }
        if (t.to == SurfaceDirection.FWD || t.to == SurfaceDirection.UP) {
            if (t.to == SurfaceDirection.UP) {
                drawer.draw(whiteTexture, szh, szh, 0, 0, p.x, p.y, angle + 180, col);
            }
            drawer.draw(whiteTexture, szh, szh, 0, 0, p.x, p.y, angle + 90, col);
        }
    }

    public TextureAtlas.AtlasRegion getStarPortTex() {
        return starPortTexture;
    }

    public TextureAtlas.AtlasRegion getWaypointTexture() {
        return waypointTexture;
    }

    public Vector2 getMapDrawPositionAdditive() {
        return mapDrawPositionAdditive;
    }
}
