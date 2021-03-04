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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import org.destinationsol.GameOptions;
import org.destinationsol.SolApplication;
import org.destinationsol.game.MapDrawer;
import org.destinationsol.game.SolCam;
import org.destinationsol.game.SolGame;
import org.destinationsol.ui.SolInputManager;
import org.destinationsol.ui.SolUiBaseScreen;
import org.destinationsol.ui.SolUiControl;
import org.destinationsol.ui.Waypoint;

import java.util.ArrayList;

public class MapScreen extends SolUiBaseScreen {
    private final SolUiControl zoomOutControl;
    public final SolUiControl closeControl;
    public final SolUiControl zoomInControl;
    private final SolUiControl addWaypointControl;
    private final SolUiControl removeWaypointControl;

    private final String NEW_WAYPOINT_TEXT = "Marker+";
    private final String REMOVE_WAYPOINT_TEXT = "Marker-";
    private final String CANCEL_TEXT = "Cancel";
    private final int MIN_WAYPOINT_DIST = 5;

    private boolean isPickingWaypointSpot = false;
    private boolean isPickingWaypointToRemove = false;

    MapScreen(RightPaneLayout rightPaneLayout, boolean mobile, GameOptions gameOptions) {
        Rectangle closeArea = mobile ? MainGameScreen.btn(0, MainGameScreen.HELPER_ROW_1, true) : rightPaneLayout.buttonRect(1);
        closeControl = new SolUiControl(closeArea, true, gameOptions.getKeyMap(), gameOptions.getKeyClose());
        closeControl.setDisplayName("Close");
        controls.add(closeControl);

        float row0 = 1 - MainGameScreen.CELL_SZ;
        float row1 = row0 - MainGameScreen.CELL_SZ;

        Rectangle zoomInArea = mobile ? MainGameScreen.btn(0, row1, false) : rightPaneLayout.buttonRect(2);
        zoomInControl = new SolUiControl(zoomInArea, true, gameOptions.getKeyZoomIn());
        zoomInControl.setDisplayName("Zoom In");
        controls.add(zoomInControl);

        Rectangle zoomOutArea = mobile ? MainGameScreen.btn(0, row0, false) : rightPaneLayout.buttonRect(3);
        zoomOutControl = new SolUiControl(zoomOutArea, true, gameOptions.getKeyZoomOut());
        zoomOutControl.setDisplayName("Zoom Out");
        controls.add(zoomOutControl);

        Rectangle addWaypointArea = mobile ? MainGameScreen.btn(0, 0, false) : rightPaneLayout.buttonRect(4);
        addWaypointControl = new SolUiControl(addWaypointArea, true, gameOptions.getKeyShoot2());
        addWaypointControl.setDisplayName(NEW_WAYPOINT_TEXT);
        controls.add(addWaypointControl);

        Rectangle removeWaypointArea = mobile ? MainGameScreen.btn(0, MainGameScreen.CELL_SZ, false) : rightPaneLayout.buttonRect(5);
        removeWaypointControl = new SolUiControl(removeWaypointArea, true, gameOptions.getKeyShoot2());
        removeWaypointControl.setDisplayName(REMOVE_WAYPOINT_TEXT);
        controls.add(removeWaypointControl);
    }

    @Override
    public void updateCustom(SolApplication solApplication, SolInputManager.InputPointer[] inputPointers, boolean clickedOutside) {
        SolGame game = solApplication.getGame();
        GameOptions gameOptions = solApplication.getOptions();
        boolean justClosed = closeControl.isJustOff();
        MapDrawer mapDrawer = game.getMapDrawer();
        mapDrawer.setToggled(!justClosed);
        SolInputManager im = solApplication.getInputManager();

        if (justClosed) {
            mapDrawer.getMapDrawPositionAdditive().set(0, 0);
            isPickingWaypointSpot = false;
            addWaypointControl.setDisplayName(NEW_WAYPOINT_TEXT);
            im.setScreen(solApplication, game.getScreens().mainGameScreen);
        }

        boolean zoomIn = zoomInControl.isJustOff();
        if (zoomIn || zoomOutControl.isJustOff()) {
            mapDrawer.changeZoom(zoomIn);
        }

        float mapZoom = mapDrawer.getZoom();
        zoomInControl.setEnabled(mapZoom != MapDrawer.MIN_ZOOM);
        zoomOutControl.setEnabled(mapZoom != MapDrawer.MAX_ZOOM);
        ShipUiControl shipControl = game.getScreens().mainGameScreen.shipControl;
        if (shipControl instanceof ShipMouseControl) {
            shipControl.update(solApplication, true);
        }

        Boolean scrolledUp = im.getScrolledUp();
        if (scrolledUp != null) {
            if (scrolledUp) {
                zoomOutControl.maybeFlashPressed(gameOptions.getKeyZoomOut());
            } else {
                zoomInControl.maybeFlashPressed(gameOptions.getKeyZoomIn());
            }
        }

        if (im.touchDragged) {
            //Scroll factor negates the drag and adjusts it to map's zoom
            float scrollFactor = -mapDrawer.getZoom() / Gdx.graphics.getHeight() * gameOptions.getMapScrollSpeed();
            float rotateAngle = game.getCam().getAngle();
            mapDrawer.getMapDrawPositionAdditive().add(im.getDrag().scl(scrollFactor).rotate(rotateAngle));
        }

        if (isPickingWaypointSpot) {
            if (inputPointers[0].isJustUnPressed() && !addWaypointControl.isJustOff()) {
                SolCam camera = game.getCam();
                Vector2 mapCamPos = camera.getPosition().add(mapDrawer.getMapDrawPositionAdditive());
                Vector2 clickPosition = new Vector2(inputPointers[0].x, inputPointers[0].y);
                Vector2 worldPosition = screenPositionToWorld(clickPosition, mapCamPos, camera.getAngle(), mapZoom);
                ArrayList<Waypoint> waypoints = game.getHero().getWaypoints();

                //make sure waypoints aren't too close to each other
                boolean canCreate = true;
                for (int w = 0; w < waypoints.size(); w++) {
                    Waypoint waypoint = waypoints.get(w);
                    if (worldPosition.x > waypoint.position.x - MIN_WAYPOINT_DIST && worldPosition.x < waypoint.position.x + MIN_WAYPOINT_DIST &&
                            worldPosition.y > waypoint.position.y - MIN_WAYPOINT_DIST && worldPosition.y < waypoint.position.y + MIN_WAYPOINT_DIST) {
                        canCreate = false;
                        break;
                    }
                }

                if (canCreate) {
                    setWaypointButtonsEnabled(false);
                    WaypointCreationScreen waypointCreationScreen = game.getScreens().waypointCreationScreen;
                    waypointCreationScreen.setWaypointPos(worldPosition);

                    solApplication.getInputManager().setScreen(solApplication, game.getScreens().mapScreen);
                    solApplication.getInputManager().addScreen(solApplication, waypointCreationScreen);
                }
                addWaypointControl.setDisplayName(NEW_WAYPOINT_TEXT);
                isPickingWaypointSpot = false;
            }
        }

        if (isPickingWaypointToRemove) {
            
            if (inputPointers[0].isJustUnPressed() && !removeWaypointControl.isJustOff()) {
                Vector2 clickPosition = new Vector2(inputPointers[0].x, inputPointers[0].y);
                SolCam camera = game.getCam();
                Vector2 realPosition = screenPositionToWorld(clickPosition, camera.getPosition(), camera.getAngle(), mapZoom);

                ArrayList<Waypoint> waypoints = game.getHero().getWaypoints();
                for (int w = 0; w < waypoints.size(); w++) {
                    Waypoint waypoint = waypoints.get(w);
                    if (waypoint.position.x > realPosition.x - MIN_WAYPOINT_DIST && waypoint.position.x < realPosition.x + MIN_WAYPOINT_DIST &&
                            waypoint.position.y > realPosition.y - MIN_WAYPOINT_DIST && waypoint.position.y < realPosition.y + MIN_WAYPOINT_DIST) {
                        game.getHero().removeWaypoint(waypoint);
                        game.getObjectManager().removeObjDelayed(waypoint);
                    }
                }
                addWaypointControl.setEnabled(true);
                removeWaypointControl.setDisplayName(REMOVE_WAYPOINT_TEXT);
                isPickingWaypointToRemove = false;
            }
        }

        if (addWaypointControl.isJustOff()) {
            if (isPickingWaypointSpot) {
                isPickingWaypointSpot = false;
                addWaypointControl.setDisplayName(NEW_WAYPOINT_TEXT);
                removeWaypointControl.setEnabled(true);
            } else {
                isPickingWaypointSpot = true;
                addWaypointControl.setDisplayName(CANCEL_TEXT);
                removeWaypointControl.setEnabled(false);
            }
        }

        if (removeWaypointControl.isJustOff()) {
            if (isPickingWaypointToRemove) {
                isPickingWaypointToRemove = false;
                removeWaypointControl.setDisplayName(REMOVE_WAYPOINT_TEXT);
                addWaypointControl.setEnabled(true);
            } else {
                isPickingWaypointToRemove = true;
                removeWaypointControl.setDisplayName(CANCEL_TEXT);
                addWaypointControl.setEnabled(false);
            }
        }
    }

    public Vector2 screenPositionToWorld(Vector2 clickPosition, Vector2 camPos, float camAngle, float mapZoom) {
        float screenWidth = (float) Gdx.graphics.getWidth();
        float screenHeight = (float)  Gdx.graphics.getHeight();
        return ScreenToWorldMapper.screenClickPositionToWorldPosition(
            new Vector2(screenWidth, screenHeight), 
            clickPosition, 
            camPos, 
            camAngle,
            mapZoom
        );
    }

    public void setWaypointButtonsEnabled(boolean value) {
        removeWaypointControl.setEnabled(value);
        addWaypointControl.setEnabled(value);
    }

    public boolean isPickingWaypointSpot() {
        return isPickingWaypointSpot;
    }

    public boolean isPickingWaypointToRemove() {
        return isPickingWaypointToRemove;
    }
}
