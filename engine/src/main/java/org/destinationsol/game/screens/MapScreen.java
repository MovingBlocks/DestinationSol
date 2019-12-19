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

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.sun.xml.internal.fastinfoset.tools.FI_DOM_Or_XML_DOM_SAX_SAXEvent;
import org.destinationsol.GameOptions;
import org.destinationsol.SolApplication;
import org.destinationsol.game.MapDrawer;
import org.destinationsol.game.SolGame;
import org.destinationsol.game.drawables.Drawable;
import org.destinationsol.game.drawables.DrawableLevel;
import org.destinationsol.game.drawables.RectSprite;
import org.destinationsol.ui.SolInputManager;
import org.destinationsol.ui.SolUiBaseScreen;
import org.destinationsol.ui.SolUiControl;
import org.destinationsol.ui.Waypoint;

import java.awt.*;
import java.lang.reflect.Array;
import java.util.ArrayList;

public class MapScreen extends SolUiBaseScreen {
    private final SolUiControl zoomOutControl;
    public final SolUiControl closeControl;
    public final SolUiControl zoomInControl;
    public final SolUiControl addWaypointControl;
    private boolean isCreatingWaypoint = false;

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
        addWaypointControl.setDisplayName("New Waypoint");
        controls.add(addWaypointControl);
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

        if(addWaypointControl.isJustOff() && !isCreatingWaypoint) {
            System.out.println("Waypoint pick-a-spot");
            System.out.println(inputPointers.length);
            isCreatingWaypoint = true;
        } else if (isCreatingWaypoint) {
            if(inputPointers[0].isJustUnPressed()) {
                float ratio = (float)Gdx.graphics.getWidth()/(float)Gdx.graphics.getHeight();
                Vector2 clickPosition = new Vector2(inputPointers[0].x, inputPointers[0].y);
                clickPosition.scl(5);
                clickPosition.scl(mapZoom);

                Vector2 finalPosition = new Vector2(game.getCam().getPosition());
                finalPosition.add(clickPosition);

                finalPosition.x -= (ratio * mapZoom)/(2.f/5);
                finalPosition.y -= (mapZoom)/(2.f/5);

                game.getScreens().waypointCreationScreen.setWaypointPos(finalPosition);

                solApplication.getInputManager().setScreen(solApplication, game.getScreens().mapScreen);
                solApplication.getInputManager().addScreen(solApplication, game.getScreens().waypointCreationScreen);

                isCreatingWaypoint = false;
            }
        }
    }
}
