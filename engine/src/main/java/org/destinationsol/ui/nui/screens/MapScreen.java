/*
 * Copyright 2021 The Terasology Foundation
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

package org.destinationsol.ui.nui.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import org.destinationsol.GameOptions;
import org.destinationsol.SolApplication;
import org.destinationsol.game.MapDrawer;
import org.destinationsol.game.SolCam;
import org.destinationsol.game.SolGame;
import org.destinationsol.game.screens.ScreenToWorldMapper;
import org.destinationsol.game.screens.ShipMouseControl;
import org.destinationsol.game.screens.ShipUiControl;
import org.destinationsol.ui.Waypoint;
import org.destinationsol.ui.nui.NUIScreenLayer;
import org.destinationsol.ui.nui.widgets.KeyActivatedButton;
import org.destinationsol.ui.nui.widgets.UIWarnButton;
import org.joml.Vector2d;
import org.joml.Vector2i;
import org.terasology.input.MouseInput;
import org.terasology.nui.BaseInteractionListener;
import org.terasology.nui.Canvas;
import org.terasology.nui.InteractionListener;
import org.terasology.nui.backends.libgdx.GDXInputUtil;
import org.terasology.nui.events.NUIMouseClickEvent;
import org.terasology.nui.events.NUIMouseDragEvent;
import org.terasology.nui.events.NUIMouseWheelEvent;
import org.terasology.nui.widgets.UIButton;

import javax.inject.Inject;
import java.util.ArrayList;

/**
 * The map screen is responsible for rendering the UI controls for the in-game map.
 * You can zoom-in, zoom-out, pan the map and place/remove waypoints.
 * It is not responsible for rendering the map itself, which is done in {@link MapDrawer}.
 * @see MapDrawer
 */
public class MapScreen extends NUIScreenLayer {
    private enum WaypointOperation {
        NONE,
        ADDING,
        REMOVING
    }

    private static final int MIN_WAYPOINT_DIST = 5;
    private static final String NEW_WAYPOINT_TEXT = "Marker+";
    private static final String REMOVE_WAYPOINT_TEXT = "Marker-";
    private static final String CANCEL_TEXT = "Cancel";

    private final SolApplication solApplication;
    private UIWarnButton closeButton;
    private UIWarnButton zoomInButton;
    private UIWarnButton zoomOutButton;
    private KeyActivatedButton addWaypointButton;
    private KeyActivatedButton removeWaypointButton;
    private WaypointOperation waypointOperation;
    private final InteractionListener dragListener = new BaseInteractionListener() {
        @Override
        public boolean onMouseClick(NUIMouseClickEvent event) {
            if (event.getMouseButton() == MouseInput.MOUSE_LEFT) {
                SolGame game = solApplication.getGame();
                MapDrawer mapDrawer = game.getMapDrawer();
                float mapZoom = mapDrawer.getZoom();
                SolCam solCam = solApplication.getGame().getCam();

                float camAngle = solCam.getAngle();
                Vector2 mapCamPos = solCam.getPosition().cpy().add(mapDrawer.getMapDrawPositionAdditive());
                Vector2i mousePosition = event.getMouse().getPosition();
                // Canvas co-ordinates are relative to the virtual canvas size, rather than the physical canvas size.
                // The scale factor is therefore needed to convert these virtual co-ordinates into screen co-ordinates.
                float uiScale = nuiManager.getUiScale();
                // This is not a typo! The original InputManager code actually divides both x and y by the height!
                Vector2 clickPosition = new Vector2(mousePosition.x * uiScale / Gdx.graphics.getHeight(),
                        mousePosition.y * uiScale / Gdx.graphics.getHeight());
                Vector2 worldPosition = screenPositionToWorld(clickPosition, mapCamPos, camAngle, mapZoom);
                ArrayList<Waypoint> waypoints = game.getHero().getWaypoints();

                if (waypointOperation == WaypointOperation.ADDING) {
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
                        addWaypointButton.setEnabled(true);
                        removeWaypointButton.setEnabled(true);
                        WaypointCreationScreen waypointCreationScreen = game.getScreens().waypointCreationScreen;
                        waypointCreationScreen.setWaypointPosition(worldPosition);

                        nuiManager.pushScreen(waypointCreationScreen);
                    }
                    removeWaypointButton.setEnabled(true);
                    addWaypointButton.setText(NEW_WAYPOINT_TEXT);
                    waypointOperation = WaypointOperation.NONE;
                } else if (waypointOperation == WaypointOperation.REMOVING) {
                    for (int w = 0; w < waypoints.size(); w++) {
                        Waypoint waypoint = waypoints.get(w);
                        if (waypoint.position.x > worldPosition.x - MIN_WAYPOINT_DIST && waypoint.position.x < worldPosition.x + MIN_WAYPOINT_DIST &&
                                waypoint.position.y > worldPosition.y - MIN_WAYPOINT_DIST && waypoint.position.y < worldPosition.y + MIN_WAYPOINT_DIST) {
                            game.getHero().removeWaypoint(waypoint);
                            game.getObjectManager().removeObjDelayed(waypoint);
                        }
                    }
                    addWaypointButton.setEnabled(true);
                    removeWaypointButton.setText(REMOVE_WAYPOINT_TEXT);
                    waypointOperation = WaypointOperation.NONE;
                }

                // Permit left-mouse button dragging by returning true.
                return true;
            }

            return false;
        }

        @Override
        public void onMouseDrag(NUIMouseDragEvent event) {
            MapDrawer mapDrawer = solApplication.getGame().getMapDrawer();
            GameOptions gameOptions = solApplication.getOptions();
            SolCam solCam = solApplication.getGame().getCam();

            Vector2d delta = event.getMouse().getDelta();
            Vector2 deltaPosition = new Vector2((float)delta.x, (float)delta.y);

            // Scroll factor negates the drag and adjusts it to map's zoom
            float scrollFactor = -mapDrawer.getZoom() / Gdx.graphics.getHeight() * gameOptions.getMapScrollSpeed();
            float rotateAngle = solCam.getAngle();
            mapDrawer.getMapDrawPositionAdditive().add(deltaPosition.scl(scrollFactor).rotateDeg(rotateAngle));
        }
    };

    @Inject
    public MapScreen(SolApplication solApplication) {
        this.solApplication = solApplication;
    }

    @Override
    public void initialise() {
        closeButton = find("closeButton", UIWarnButton.class);
        closeButton.setKey(GDXInputUtil.GDXToNuiKey(solApplication.getOptions().getKeyMap()));
        closeButton.subscribe(button -> {
            if (nuiManager.hasScreen(solApplication.getGame().getScreens().waypointCreationScreen)) {
                // Don't exit if the waypoint creation screen is open.
                return;
            }

            if (nuiManager.hasScreen(this)) {
                nuiManager.popScreen();
            }
        });

        zoomInButton = find("zoomInButton", UIWarnButton.class);
        zoomInButton.setKey(GDXInputUtil.GDXToNuiKey(solApplication.getOptions().getKeyZoomIn()));
        zoomInButton.subscribe(button -> {
            MapDrawer mapDrawer = solApplication.getGame().getMapDrawer();
            mapDrawer.changeZoom(true);

            float mapZoom = mapDrawer.getZoom();
            zoomInButton.setEnabled(mapZoom != MapDrawer.MIN_ZOOM);
            zoomOutButton.setEnabled(mapZoom != MapDrawer.MAX_ZOOM);
        });

        zoomOutButton = find("zoomOutButton", UIWarnButton.class);
        zoomOutButton.setKey(GDXInputUtil.GDXToNuiKey(solApplication.getOptions().getKeyZoomOut()));
        zoomOutButton.subscribe(button -> {
            MapDrawer mapDrawer = solApplication.getGame().getMapDrawer();
            mapDrawer.changeZoom(false);

            float mapZoom = mapDrawer.getZoom();
            zoomInButton.setEnabled(mapZoom != MapDrawer.MIN_ZOOM);
            zoomOutButton.setEnabled(mapZoom != MapDrawer.MAX_ZOOM);
        });

        addWaypointButton = find("addWaypointButton", KeyActivatedButton.class);
        addWaypointButton.subscribe(button -> {
            if (waypointOperation == WaypointOperation.ADDING) {
                // Cancel add
                waypointOperation = WaypointOperation.NONE;
                removeWaypointButton.setEnabled(true);
                addWaypointButton.setText(NEW_WAYPOINT_TEXT);
            } else {
                waypointOperation = WaypointOperation.ADDING;
                removeWaypointButton.setEnabled(false);
                addWaypointButton.setText(CANCEL_TEXT);
            }
        });
        removeWaypointButton = find("removeWaypointButton", KeyActivatedButton.class);
        removeWaypointButton.subscribe(button -> {
            if (waypointOperation == WaypointOperation.REMOVING) {
                // Cancel remove
                waypointOperation = WaypointOperation.NONE;
                addWaypointButton.setEnabled(true);
                removeWaypointButton.setText(REMOVE_WAYPOINT_TEXT);
            } else {
                waypointOperation = WaypointOperation.REMOVING;
                addWaypointButton.setEnabled(false);
                removeWaypointButton.setText(CANCEL_TEXT);
            }
        });
    }

    @Override
    public void onAdded() {
        solApplication.getGame().getMapDrawer().setToggled(true);
        waypointOperation = WaypointOperation.NONE;
        solApplication.getGame().getMapDrawer().getMapDrawPositionAdditive().set(0, 0);
    }

    @Override
    public void onRemoved() {
        if (!closeButton.getMode().equals(UIButton.DOWN_MODE)) {
            // Act as if we pressed the close button when the screen is removed.
            // This fixes the escape key not advancing the tutorial.
            closeButton.simulatePress();
        }

        SolGame solGame = solApplication.getGame();
        solGame.getMapDrawer().setToggled(false);
        solApplication.getInputManager().setScreen(solApplication, solGame.getScreens().oldMainGameScreen);
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        ShipUiControl shipControl = solApplication.getGame().getScreens().oldMainGameScreen.getShipControl();
        if (shipControl instanceof ShipMouseControl) {
            shipControl.update(solApplication, true);
        }
    }

    @Override
    public void onDraw(Canvas canvas) {
        canvas.addInteractionRegion(dragListener);
        super.onDraw(canvas);
    }

    @Override
    public void onMouseWheelEvent(NUIMouseWheelEvent event) {
        if (event.getWheelTurns() == 0) {
            return;
        }

        MapDrawer mapDrawer = solApplication.getGame().getMapDrawer();

        if (event.getWheelTurns() > 0 && mapDrawer.getZoom() != MapDrawer.MIN_ZOOM) {
            zoomInButton.simulatePress();
        }

        if (event.getWheelTurns() < 0 && mapDrawer.getZoom() != MapDrawer.MAX_ZOOM) {
            zoomOutButton.simulatePress();
        }
    }

    /**
     * Returns true, if the user is currently in the process of placing a waypoint, otherwise false.
     * @return true, if the user is picking a waypoint spot, otherwise false.
     */
    public boolean isPickingWaypointSpot() {
        return waypointOperation == WaypointOperation.ADDING;
    }

    /**
     * Returns true, if the user is currently in the process of removing a waypoint, otherwise false.
     * @return true, if the user is picking a waypoint to remove, otherwise false.
     */
    public boolean isPickingWaypointToRemove() {
        return waypointOperation == WaypointOperation.REMOVING;
    }

    /**
     * Enables or disables the waypoint buttons ("Marker+" and "Marker-")
     * @param enabled if true, enables the buttons, otherwise disables them.
     */
    public void setWaypointButtonsEnabled(boolean enabled) {
        addWaypointButton.setEnabled(enabled);
        removeWaypointButton.setEnabled(enabled);
    }

    /**
     * Returns the button used to zoom-in the map.
     * This is mostly exposed for use in the tutorial.
     * @return the button used to zoom-in the map.
     */
    public UIWarnButton getZoomInButton() {
        return zoomInButton;
    }

    /**
     * Returns the button used to zoom-out the map.
     * This is mostly exposed for use in the tutorial.
     * @return the button used to zoom-out the map.
     */
    public UIWarnButton getZoomOutButton() {
        return zoomOutButton;
    }

    /**
     * Returns the button used to close the map.
     * This is mostly exposed for use in the tutorial.
     * @return the button used to close the map.
     */
    public UIWarnButton getCloseButton() {
        return closeButton;
    }

    private Vector2 screenPositionToWorld(Vector2 clickPosition, Vector2 camPos, float camAngle, float mapZoom) {
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
}
