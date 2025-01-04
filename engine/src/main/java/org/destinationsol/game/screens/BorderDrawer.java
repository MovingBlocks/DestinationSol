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
package org.destinationsol.game.screens;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import org.destinationsol.Const;
import org.destinationsol.SolApplication;
import org.destinationsol.assets.Assets;
import org.destinationsol.common.SolColor;
import org.destinationsol.common.SolMath;
import org.destinationsol.game.FactionManager;
import org.destinationsol.game.HardnessCalc;
import org.destinationsol.game.Hero;
import org.destinationsol.game.MapDrawer;
import org.destinationsol.game.SolCam;
import org.destinationsol.game.SolGame;
import org.destinationsol.game.SolObject;
import org.destinationsol.game.StarPort;
import org.destinationsol.game.context.Context;
import org.destinationsol.game.faction.Faction;
import org.destinationsol.game.planet.Planet;
import org.destinationsol.game.planet.PlanetManager;
import org.destinationsol.game.planet.SolarSystem;
import org.destinationsol.game.planet.SunSingleton;
import org.destinationsol.game.ship.FarShip;
import org.destinationsol.game.ship.SolShip;
import org.destinationsol.ui.DisplayDimensions;
import org.destinationsol.ui.UiDrawer;
import org.destinationsol.ui.Waypoint;

import java.util.ArrayList;
import java.util.List;

public class BorderDrawer {
    private DisplayDimensions displayDimensions;

    public static final float PLANET_PROXIMITY_INDICATOR_SIZE = .02f;
    private static final float BORDER_ICON_SZ = .12f;
    private static final float MAX_ICON_DIST = Const.ATM_HEIGHT;
    private static final float MAX_DRAW_DIST = (Const.MAX_GROUND_HEIGHT + Const.ATM_HEIGHT) * 2;
    private final ArrayList<PlanetProximityIndicator> planetProximityIndicators;
    private final Vector2 myTmpVec = new Vector2();

    public BorderDrawer() {
        displayDimensions = SolApplication.displayDimensions;

        TextureAtlas.AtlasRegion texture = Assets.getAtlasRegion("engine:uiPlanetProximityIndicator");
        int hCellCount = (int) (displayDimensions.getRatio() / PLANET_PROXIMITY_INDICATOR_SIZE);
        int vCellCount = (int) (1 / PLANET_PROXIMITY_INDICATOR_SIZE);
        float hStep = displayDimensions.getRatio() / hCellCount;
        float vStep = 1f / vCellCount;
        float x = hStep / 2;
        float y = vStep / 2;
        planetProximityIndicators = new ArrayList<>();
        for (int i = 0; i < vCellCount; i++) {
            PlanetProximityIndicator t = new PlanetProximityIndicator(x, y, displayDimensions.getRatio(), PLANET_PROXIMITY_INDICATOR_SIZE, texture);
            planetProximityIndicators.add(t);
            PlanetProximityIndicator t2 = new PlanetProximityIndicator(displayDimensions.getRatio() - x, y, displayDimensions.getRatio(), PLANET_PROXIMITY_INDICATOR_SIZE, texture);
            planetProximityIndicators.add(t2);
            y += vStep;
        }
        x = 1.5f * PLANET_PROXIMITY_INDICATOR_SIZE;
        y = PLANET_PROXIMITY_INDICATOR_SIZE / 2;
        for (int i = 1; i < hCellCount - 1; i++) {
            PlanetProximityIndicator t = new PlanetProximityIndicator(x, y, displayDimensions.getRatio(), PLANET_PROXIMITY_INDICATOR_SIZE, texture);
            planetProximityIndicators.add(t);
            PlanetProximityIndicator t2 = new PlanetProximityIndicator(x, 1 - y, displayDimensions.getRatio(), PLANET_PROXIMITY_INDICATOR_SIZE, texture);
            planetProximityIndicators.add(t2);
            x += hStep;
        }
    }

    public void draw(UiDrawer drawer, SolApplication application, Context context) {
        SolGame game = application.getGame();
        SolCam cam = context.get(SolCam.class);
        Vector2 camPosition = cam.getPosition();
        Hero hero = game.getHero();
        drawPlanetProximityIndicators(drawer, game, cam, camPosition);
        MapDrawer mapDrawer = game.getMapDrawer();
        FactionManager factionManager = game.getFactionMan();
        float heroDamageCap = hero.isTranscendent() ? Float.MAX_VALUE : HardnessCalc.getShipDmgCap(hero.getShip());

        List<SolObject> objects = game.getObjectManager().getObjects();
        for (SolObject object : objects) {
            if ((object instanceof SolShip)) {
                SolShip ship = (SolShip) object;
                Vector2 shipPosition = ship.getPosition();
                Faction shipFaction = ship.getPilot().getFaction();
                float shipSize = ship.getHull().config.getSize();
                float shipAngle = ship.getAngle();
                maybeDrawIcon(drawer, shipPosition, cam, shipSize, shipAngle, mapDrawer, factionManager, hero, shipFaction, object, heroDamageCap, ship.getHull().config.getIcon());
            }
            if ((object instanceof StarPort)) {
                StarPort starPort = (StarPort) object;
                maybeDrawIcon(drawer, starPort.getPosition(), cam, StarPort.SIZE, starPort.getAngle(), mapDrawer, null, null, null, null, -1, mapDrawer.getStarPortTex());
            }
        }

        List<FarShip> farShips = game.getObjectManager().getFarShips();
        for (FarShip ship : farShips) {
            Vector2 shipPos = ship.getPosition();
            Faction shipFaction = ship.getPilot().getFaction();
            float shipSize = ship.getHullConfig().getSize();
            float shipAngle = ship.getAngle();
            maybeDrawIcon(drawer, shipPos, cam, shipSize, shipAngle, mapDrawer, factionManager, hero, shipFaction, ship, heroDamageCap, ship.getHullConfig().getIcon());
        }
        List<StarPort.FarStarPort> farPorts = game.getObjectManager().getFarPorts();
        for (StarPort.FarStarPort starPort : farPorts) {
            maybeDrawIcon(drawer, starPort.getPosition(), cam, StarPort.SIZE, starPort.getAngle(), mapDrawer, null, null, null, null, -1, mapDrawer.getStarPortTex());
        }
        drawWaypoints(drawer, hero.getWaypoints(), cam, mapDrawer);
    }

    private void drawWaypoints(UiDrawer uiDrawer, ArrayList<Waypoint> waypoints, SolCam cam, MapDrawer mapDrawer) {
        Vector2 camPos = cam.getPosition();
        for (Waypoint waypoint : waypoints) {
            float closeness = 1 - waypoint.position.dst(camPos) / MAX_ICON_DIST;
            float size;
            if (closeness < 0.5f) {
                size = BORDER_ICON_SZ * 0.5f;
            } else {
                size = BORDER_ICON_SZ * closeness;
            }
            Vector2 position = new Vector2();
            SolMath.toRel(waypoint.position, position, cam.getAngle(), camPos);

            float len = position.len();
            float newLen = len - (.25f * .3f);

            position.scl(newLen / len);

            if (cam.isRelVisible(position)) {
                continue;
            }

            float prefX = (displayDimensions.getRatio() - size) / 2;
            float prefY = .5f - size / 2;
            float dimensionsRatio = prefX / prefY;
            boolean prefXAxis = position.y == 0 || dimensionsRatio < SolMath.abs(position.x / position.y);
            float mul = SolMath.abs(prefXAxis ? (prefX / position.x) : (prefY / position.y));
            position.scl(mul);
            position.add(displayDimensions.getRatio() / 2, .5f);
            mapDrawer.drawWaypointIcon(size, position, mapDrawer.getWaypointTexture(), uiDrawer, waypoint.color);
        }
    }

    private void maybeDrawIcon(UiDrawer drawer, Vector2 position, SolCam cam, float objSize,
                               float objAngle, MapDrawer mapDrawer, FactionManager factionManager, Hero hero,
                               Faction objFac, Object shipHack, float heroDmgCap, TextureAtlas.AtlasRegion icon) {
        Vector2 camPos = cam.getPosition();
        float closeness = 1 - position.dst(camPos) / MAX_ICON_DIST;
        if (closeness < 0) {
            return;
        }
        float camAngle = cam.getAngle();
        SolMath.toRel(position, myTmpVec, camAngle, camPos);
        float len = myTmpVec.len();
        float newLen = len - .25f * objSize;
        myTmpVec.scl(newLen / len);

        if (cam.isRelVisible(myTmpVec)) {
            return;
        }

        float size = BORDER_ICON_SZ * closeness;
        float prefX = displayDimensions.getRatio() / 2 - size / 2;
        float prefY = .5f - size / 2;
        float dimensionsRatio = prefX / prefY;
        boolean prefXAxis = myTmpVec.y == 0 || dimensionsRatio < SolMath.abs(myTmpVec.x / myTmpVec.y);
        float mul = SolMath.abs(prefXAxis ? (prefX / myTmpVec.x) : (prefY / myTmpVec.y));
        myTmpVec.scl(mul);
        myTmpVec.add(displayDimensions.getRatio() / 2, .5f);

        mapDrawer.drawObjIcon(size, myTmpVec, objAngle - camAngle, factionManager, hero, objFac, heroDmgCap, shipHack, icon, drawer);
    }

    private void drawPlanetProximityIndicators(UiDrawer drawer, SolGame game, SolCam cam, Vector2 camPosition) {
        PlanetManager planetManager = game.getPlanetManager();
        Planet nearestPlanet = planetManager.getNearestPlanet();
        if (nearestPlanet != null && nearestPlanet.getPosition().dst(camPosition) < nearestPlanet.getFullHeight()) {
            return;
        }
        for (PlanetProximityIndicator planetProximityIndicator : planetProximityIndicators) {
            planetProximityIndicator.reset();
        }

        float camAngle = cam.getAngle();
        ArrayList<Planet> planets = planetManager.getPlanets();
        for (Planet planet : planets) {
            Vector2 objPos = planet.getPosition();
            float objRad = planet.getFullHeight();
            apply0(camPosition, camAngle, objPos, objRad);
        }
        SolarSystem sys = planetManager.getNearestSystem(camPosition);
        apply0(camPosition, camAngle, sys.getPosition(), SunSingleton.SUN_HOT_RAD);
        for (PlanetProximityIndicator planetProximityIndicator : planetProximityIndicators) {
            planetProximityIndicator.draw(drawer);
        }
    }

    private void apply0(Vector2 camPos, float camAngle, Vector2 objPos, float objRad) {
        float dst = objPos.dst(camPos);
        float distPercentage = (dst - objRad) / MAX_DRAW_DIST;
        if (distPercentage < 1) {
            float relAngle = SolMath.angle(camPos, objPos) - camAngle;
            float angularWHalf = SolMath.angularWidthOfSphere(objRad, dst);
            apply(distPercentage, angularWHalf, relAngle);
        }
    }

    private void apply(float distPercentage, float angularWHalf, float relAngle) {
        for (PlanetProximityIndicator planetProximityIndicator : planetProximityIndicators) {
            if (SolMath.angleDiff(planetProximityIndicator.myAngle, relAngle) < angularWHalf) {
                planetProximityIndicator.setDistPerc(distPercentage);
            }
        }
    }

    private static class PlanetProximityIndicator {
        private final float myX;
        private final float myY;
        private final TextureAtlas.AtlasRegion myTexture;
        private final float myMaxSz;
        private final Color myCol;
        private final float myAngle;
        private float myPercentage;

        private PlanetProximityIndicator(float x, float y, float r, float maxSz, TextureAtlas.AtlasRegion tex) {
            myX = x;
            myY = y;
            myTexture = tex;
            myMaxSz = maxSz * .9f;
            Vector2 position = new Vector2(x, y);
            Vector2 centah = new Vector2(r / 2, .5f);
            myAngle = SolMath.angle(centah, position);
            myCol = new Color(SolColor.UI_DARK);
        }

        private void draw(UiDrawer drawer) {
            float sz = myPercentage * myMaxSz;
            myCol.a = myPercentage;
            drawer.draw(myTexture, sz, sz, sz / 2, sz / 2, myX, myY, 0, myCol);
        }

        private void setDistPerc(float distPercentage) {
            float closeness = 1 - distPercentage;
            if (closeness < myPercentage) {
                return;
            }
            myPercentage = closeness;
        }

        private void reset() {
            myPercentage = 0;
        }
    }
}
