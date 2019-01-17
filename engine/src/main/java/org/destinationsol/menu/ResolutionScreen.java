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
package org.destinationsol.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import org.destinationsol.GameOptions;
import org.destinationsol.SolApplication;
import org.destinationsol.assets.Assets;
import org.destinationsol.common.SolColor;
import org.destinationsol.game.i18n.Translation;
import org.destinationsol.ui.DisplayDimensions;
import org.destinationsol.ui.FontSize;
import org.destinationsol.ui.SolInputManager;
import org.destinationsol.ui.SolUiBaseScreen;
import org.destinationsol.ui.SolUiControl;
import org.destinationsol.ui.UiDrawer;

public class ResolutionScreen extends SolUiBaseScreen {
    private DisplayDimensions displayDimensions;

    private final TextureAtlas.AtlasRegion backgroundTexture;

    private final SolUiControl closeControl;
    private final SolUiControl resolutionControl;
    private final SolUiControl fullscreenControl;

    ResolutionScreen(MenuLayout menuLayout, GameOptions gameOptions) {
        displayDimensions = SolApplication.displayDimensions;

        resolutionControl = new SolUiControl(menuLayout.buttonRect(-1, 2), true);
        resolutionControl.setDisplayName(Translation.translate("${core:resolutionmenu#resolution}"));
        controls.add(resolutionControl);

        fullscreenControl = new SolUiControl(menuLayout.buttonRect(-1, 3), true);
        fullscreenControl.setDisplayName(Translation.translate("${core:resolutionmenu#fullscreen}"));
        controls.add(fullscreenControl);

        closeControl = new SolUiControl(menuLayout.buttonRect(-1, 4), true, gameOptions.getKeyEscape());
        closeControl.setDisplayName(Translation.translate("${core:resolutionmenu#back}"));
        controls.add(closeControl);

        backgroundTexture = Assets.getAtlasRegion("engine:mainMenuBg", Texture.TextureFilter.Linear);
    }

    @Override
    public void updateCustom(SolApplication solApplication, SolInputManager.InputPointer[] inputPointers, boolean clickedOutside) {
        SolInputManager inputManager = solApplication.getInputManager();
        GameOptions options = solApplication.getOptions();

        if (closeControl.isJustOff()) {
            Gdx.graphics.setDisplayMode(options.x, options.y, options.fullscreen);
            inputManager.setScreen(solApplication, solApplication.getMenuScreens().options);
            return;
        }

        resolutionControl.setDisplayName(options.x + "x" + options.y);
        if (resolutionControl.isJustOff()) {
            options.advanceResolution();
        }

        fullscreenControl.setDisplayName(options.fullscreen ? Translation.translate("${core:resolutionmenu#fullscreen}") : Translation.translate("${core:resolutionmenu#windowed}"));
        if (fullscreenControl.isJustOff()) {
            options.advanceFullscreen();
        }
    }

    @Override
    public void drawBackground(UiDrawer uiDrawer, SolApplication solApplication) {
        uiDrawer.draw(backgroundTexture, displayDimensions.getRatio(), 1, displayDimensions.getRatio() / 2, 0.5f, displayDimensions.getRatio() / 2, 0.5f, 0, SolColor.WHITE);
    }

    @Override
    public void drawText(UiDrawer uiDrawer, SolApplication solApplication) {
        uiDrawer.drawString("Click 'Back' to apply changes", .5f * displayDimensions.getRatio(), .3f, FontSize.MENU, true, SolColor.WHITE);
    }
}
