/*
 * Copyright 2019 MovingBlocks
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
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import org.destinationsol.GameOptions;
import org.destinationsol.SolApplication;
import org.destinationsol.assets.Assets;
import org.destinationsol.common.SolColor;
import org.destinationsol.menu.MenuLayout;
import org.destinationsol.ui.SolInputManager;
import org.destinationsol.ui.SolUiBaseScreen;
import org.destinationsol.ui.SolUiControl;
import org.destinationsol.ui.SolUiSlider;
import org.destinationsol.ui.UiDrawer;
import org.destinationsol.ui.Waypoint;
import org.destinationsol.ui.nui.screens.MapScreen;

public class WaypointCreationScreen extends SolUiBaseScreen {

    private final SolUiControl cancelControl;
    private final SolUiControl doneControl;

    private final MapScreen mapScreen;

    private Vector2 waypointPos;

    private final Rectangle previewRect;
    private final Rectangle background;
    private final SolUiSlider sliderRed;
    private final SolUiSlider sliderGreen;
    private final SolUiSlider sliderBlue;

    private Color outcomeColor;

    public WaypointCreationScreen(MenuLayout menuLayout, GameOptions gameOptions, MapScreen mapScreen) {
        this.mapScreen = mapScreen;
        doneControl = new SolUiControl(menuLayout.buttonRect(-1, 3), true, gameOptions.getKeyShoot());
        doneControl.setDisplayName("Done");
        controls.add(doneControl);
        cancelControl = new SolUiControl(menuLayout.buttonRect(-1, 4), true, gameOptions.getKeyClose());
        cancelControl.setDisplayName("Cancel");
        controls.add(cancelControl);

        previewRect = menuLayout.buttonRect(-1, -1);
        outcomeColor = Color.BLACK.cpy();

        TextureAtlas.AtlasRegion sliderTexture = Assets.getAtlasRegion("engine:ui/slider");
        TextureAtlas.AtlasRegion sliderMarkerTexture = Assets.getAtlasRegion("engine:ui/sliderMarker");

        sliderRed = new SolUiSlider(menuLayout.buttonRect(-1, 0), "Red: ", 1, 2,sliderTexture, sliderMarkerTexture);
        sliderGreen = new SolUiSlider(menuLayout.buttonRect(-1, 1), "Green: ", 1, 2, sliderTexture, sliderMarkerTexture);
        sliderBlue = new SolUiSlider(menuLayout.buttonRect(-1, 2), "Blue:", 1, 2, sliderTexture, sliderMarkerTexture);

        background = menuLayout.background(-1, -1, 6);
    }

    @Override
    public void onAdd(SolApplication solApplication) {
        sliderRed.setValue(1f);
        sliderGreen.setValue(1f);
        sliderBlue.setValue(1f);
    }

    @Override
    public void drawBackground(UiDrawer uiDrawer, SolApplication solApplication) {
        uiDrawer.draw(background, SolColor.UI_BG);
    }

    @Override
    public void drawImages(UiDrawer uiDrawer, SolApplication solApplication) {
        uiDrawer.draw(previewRect, outcomeColor);
        sliderRed.draw(uiDrawer);
        sliderGreen.draw(uiDrawer);
        sliderBlue.draw(uiDrawer);
    }

    @Override
    public void updateCustom(SolApplication solApplication, SolInputManager.InputPointer[] inputPointers, boolean clickedOutside) {
        if (doneControl.isJustOff()) {
            Waypoint waypoint = new Waypoint(waypointPos, outcomeColor, solApplication.getGame().getMapDrawer().getWaypointTexture());
            solApplication.getGame().getHero().addWaypoint(waypoint);
            solApplication.getGame().getObjectManager().addObjDelayed(waypoint);
            solApplication.getInputManager().setScreen(solApplication, null);
            mapScreen.setWaypointButtonsEnabled(true);
        }
        
        if (cancelControl.isJustOff()) {
            solApplication.getInputManager().setScreen(solApplication, null);
            mapScreen.setWaypointButtonsEnabled(true);
        }

        if (inputPointers[0].pressed) {
            Vector2 clickPos = new Vector2(inputPointers[0].x, inputPointers[0].y);
            sliderRed.click(clickPos);
            sliderGreen.click(clickPos);
            sliderBlue.click(clickPos);
        }

        outcomeColor.r = sliderRed.getValue();
        outcomeColor.g = sliderGreen.getValue();
        outcomeColor.b = sliderBlue.getValue();
    }

    public void setWaypointPos(Vector2 position) {
        outcomeColor = new Color();
        outcomeColor.a = 1.0f;
        waypointPos = position;
    }

}
