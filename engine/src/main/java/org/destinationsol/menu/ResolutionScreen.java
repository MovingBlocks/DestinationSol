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
package org.destinationsol.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import org.destinationsol.GameOptions;
import org.destinationsol.SolApplication;
import org.destinationsol.assets.Assets;
import org.destinationsol.common.SolColor;
import org.destinationsol.ui.FontSize;
import org.destinationsol.ui.SolInputManager;
import org.destinationsol.ui.SolUiControl;
import org.destinationsol.ui.SolUiScreen;
import org.destinationsol.ui.UiDrawer;

import java.util.ArrayList;
import java.util.List;

public class ResolutionScreen implements SolUiScreen {
    private final TextureAtlas.AtlasRegion bgTex;

    private final ArrayList<SolUiControl> myControls = new ArrayList<>();
    private final SolUiControl closeControl;
    private final SolUiControl resolutionControl;
    private final SolUiControl fullscreenControl;

    ResolutionScreen(MenuLayout menuLayout, GameOptions gameOptions) {
        resolutionControl = new SolUiControl(menuLayout.buttonRect(-1, 2), true);
        resolutionControl.setDisplayName("Resolution");
        myControls.add(resolutionControl);

        fullscreenControl = new SolUiControl(menuLayout.buttonRect(-1, 3), true);
        fullscreenControl.setDisplayName("Fullscreen");
        myControls.add(fullscreenControl);

        closeControl = new SolUiControl(menuLayout.buttonRect(-1, 4), true, gameOptions.getKeyEscape());
        closeControl.setDisplayName("Back");
        myControls.add(closeControl);

        bgTex = Assets.getAtlasRegion("engine:mainMenuBg", Texture.TextureFilter.Linear);
    }

    @Override
    public List<SolUiControl> getControls() {
        return myControls;
    }

    @Override
    public void updateCustom(SolApplication solApplication, SolInputManager.InputPointer[] inputPointers, boolean clickedOutside) {
        SolInputManager inputManager = solApplication.getInputMan();
        GameOptions options = solApplication.getOptions();

        if (closeControl.isJustOff()) {
            Gdx.graphics.setDisplayMode(options.x, options.y, options.fullscreen);
            inputManager.setScreen(solApplication, solApplication.getMenuScreens().options);
            return;
        }

        resolutionControl.setDisplayName(options.x + "x" + options.y);
        if (resolutionControl.isJustOff()) {
            options.advanceReso();
        }

        fullscreenControl.setDisplayName(options.fullscreen ? "Fullscreen" : "Windowed");
        if (fullscreenControl.isJustOff()) {
            options.advanceFullscreen();
        }
    }

    @Override
    public void drawBg(UiDrawer uiDrawer, SolApplication solApplication) {
        uiDrawer.draw(bgTex, uiDrawer.r, 1, uiDrawer.r / 2, 0.5f, uiDrawer.r / 2, 0.5f, 0, SolColor.WHITE);
    }

    @Override
    public void drawText(UiDrawer uiDrawer, SolApplication solApplication) {
        uiDrawer.drawString("Click 'Back' to apply changes", .5f * uiDrawer.r, .3f, FontSize.MENU, true, SolColor.WHITE);
    }
}
