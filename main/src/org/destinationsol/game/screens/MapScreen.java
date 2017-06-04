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
package org.destinationsol.game.screens;

import com.badlogic.gdx.math.Rectangle;
import org.destinationsol.GameOptions;
import org.destinationsol.SolApplication;
import org.destinationsol.game.MapDrawer;
import org.destinationsol.game.SolGame;
import org.destinationsol.ui.SolInputManager;
import org.destinationsol.ui.SolUiControl;
import org.destinationsol.ui.SolUiScreen;

import java.util.ArrayList;
import java.util.List;

public class MapScreen implements SolUiScreen {
    private final List<SolUiControl> controls = new ArrayList<>();
    private final SolUiControl zoomOutControl;
    public final SolUiControl closeControl;
    public final SolUiControl zoomInControl;

    MapScreen(RightPaneLayout rightPaneLayout, boolean mobile, float r, GameOptions gameOptions) {
        Rectangle closeArea = mobile ? MainScreen.btn(0, MainScreen.HELPER_ROW_1, true) : rightPaneLayout.buttonRect(1);
        closeControl = new SolUiControl(closeArea, true, gameOptions.getKeyMap(), gameOptions.getKeyClose());
        closeControl.setDisplayName("Close");
        controls.add(closeControl);
        float row0 = 1 - MainScreen.CELL_SZ;
        float row1 = row0 - MainScreen.CELL_SZ;
        Rectangle zoomInArea = mobile ? MainScreen.btn(0, row1, false) : rightPaneLayout.buttonRect(2);
        zoomInControl = new SolUiControl(zoomInArea, true, gameOptions.getKeyZoomIn());
        zoomInControl.setDisplayName("Zoom In");
        controls.add(zoomInControl);
        Rectangle zoomOutArea = mobile ? MainScreen.btn(0, row0, false) : rightPaneLayout.buttonRect(3);
        zoomOutControl = new SolUiControl(zoomOutArea, true, gameOptions.getKeyZoomOut());
        zoomOutControl.setDisplayName("Zoom Out");
        controls.add(zoomOutControl);
    }

    @Override
    public List<SolUiControl> getControls() {
        return controls;
    }

    @Override
    public void updateCustom(SolApplication solApplication, SolInputManager.InputPointer[] inputPointers, boolean clickedOutside) {
        SolGame game = solApplication.getGame();
        GameOptions gameOptions = solApplication.getOptions();
        boolean justClosed = closeControl.isJustOff();
        MapDrawer mapDrawer = game.getMapDrawer();
        mapDrawer.setToggled(!justClosed);
        SolInputManager im = solApplication.getInputMan();
        if (justClosed) {
            im.setScreen(solApplication, game.getScreens().mainScreen);
        }
        boolean zoomIn = zoomInControl.isJustOff();
        if (zoomIn || zoomOutControl.isJustOff()) {
            mapDrawer.changeZoom(zoomIn);
        }
        float mapZoom = mapDrawer.getZoom();
        zoomInControl.setEnabled(mapZoom != MapDrawer.MIN_ZOOM);
        zoomOutControl.setEnabled(mapZoom != MapDrawer.MAX_ZOOM);
        ShipUiControl sc = game.getScreens().mainScreen.shipControl;
        if (sc instanceof ShipMouseControl) {
            sc.update(solApplication, true);
        }
        Boolean scrolledUp = im.getScrolledUp();
        if (scrolledUp != null) {
            if (scrolledUp) {
                zoomOutControl.maybeFlashPressed(gameOptions.getKeyZoomOut());
            } else {
                zoomInControl.maybeFlashPressed(gameOptions.getKeyZoomIn());
            }
        }
    }
}
