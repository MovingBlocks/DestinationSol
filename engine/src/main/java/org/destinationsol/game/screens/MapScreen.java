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
    public final SolUiControl addWaypointControl;
    public final SolUiControl removeWaypointControl;

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
        Rectangle addWaypointArea = mobile ? MainGameScreen.btn(0, row0 - MainGameScreen.CELL_SZ, false) : rightPaneLayout.buttonRect(4);
        addWaypointControl = new SolUiControl(addWaypointArea, true, gameOptions.getKeyShoot2());
        addWaypointControl.setDisplayName(NEW_WAYPOINT_TEXT);
        controls.add(addWaypointControl);
        Rectangle removeWaypointArea = mobile ? MainGameScreen.btn(0, row0 - MainGameScreen.CELL_SZ * 2, false) : rightPaneLayout.buttonRect(5);
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

        if (addWaypointControl.isJustOff() && !isPickingWaypointSpot) {
            isPickingWaypointSpot = true;
            addWaypointControl.setDisplayName(CANCEL_TEXT);
            removeWaypointControl.setEnabled(false);
        } else if (addWaypointControl.isJustOff() && isPickingWaypointSpot) {
            isPickingWaypointSpot = false;
            addWaypointControl.setDisplayName(NEW_WAYPOINT_TEXT);
            removeWaypointControl.setEnabled(true);
        } else if (isPickingWaypointSpot) {
            if (inputPointers[0].isJustUnPressed()) {
                Vector2 clickPosition = new Vector2(inputPointers[0].x, inputPointers[0].y);

                Vector2 worldPosition = screenPositionToWorld(clickPosition, game.getCam().getPosition(), mapZoom);

                ArrayList<Waypoint> waypoints = game.getHero().getWaypoints();
                boolean canCreate = true;
                for (int w = 0; w < waypoints.size(); w++) {
                    Waypoint waypoint = waypoints.get(w);
                    if (worldPosition.x > waypoint.position.x-MIN_WAYPOINT_DIST && worldPosition.x < waypoint.position.x+MIN_WAYPOINT_DIST &&
                        worldPosition.y > waypoint.position.y-MIN_WAYPOINT_DIST && worldPosition.y < waypoint.position.y+MIN_WAYPOINT_DIST) {
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

        if (removeWaypointControl.isJustOff() && !isPickingWaypointToRemove) {
            isPickingWaypointToRemove = true;
            removeWaypointControl.setDisplayName(CANCEL_TEXT);
            addWaypointControl.setEnabled(false);
        } else if (removeWaypointControl.isJustOff() && isPickingWaypointToRemove) {
            removeWaypointControl.setDisplayName(REMOVE_WAYPOINT_TEXT);
            addWaypointControl.setEnabled(true);
            isPickingWaypointToRemove = false;
        } else if (isPickingWaypointToRemove) {
            if (inputPointers[0].isJustUnPressed()) {
                Vector2 clickPosition = new Vector2(inputPointers[0].x, inputPointers[0].y);
                Vector2 realPosition = screenPositionToWorld(clickPosition, game.getCam().getPosition(), mapZoom);

                ArrayList<Waypoint> waypoints = game.getHero().getWaypoints();
                for (int w = 0; w < waypoints.size(); w++) {
                    Waypoint waypoint = waypoints.get(w);
                    if (waypoint.position.x > realPosition.x-MIN_WAYPOINT_DIST && waypoint.position.x < realPosition.x+MIN_WAYPOINT_DIST &&
                        waypoint.position.y > realPosition.y-MIN_WAYPOINT_DIST && waypoint.position.y < realPosition.y+MIN_WAYPOINT_DIST) {
                        game.getHero().removeWaypoint(waypoint);
                        game.getObjectManager().removeObjDelayed(waypoint);
                    }
                }
                isPickingWaypointToRemove = false;
                addWaypointControl.setEnabled(true);
            }
        }
    }

    public Vector2 screenPositionToWorld(Vector2 screenPos, Vector2 camPos, float mapZoom) {
        float ratio = (float)Gdx.graphics.getWidth()/(float)Gdx.graphics.getHeight();
        screenPos.scl(5);
        screenPos.scl(mapZoom);

        Vector2 finalPosition = new Vector2(camPos);
        finalPosition.add(screenPos);

        finalPosition.x -= (ratio * mapZoom)/(2.f/5);
        finalPosition.y -= (mapZoom)/(2.f/5);
        return finalPosition;
    }

    public void setWaypointButtonsEnabled(boolean value) {
        removeWaypointControl.setEnabled(value);
        addWaypointControl.setEnabled(value);
    }
}
