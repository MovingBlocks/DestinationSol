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
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import org.destinationsol.GameOptions;
import org.destinationsol.SolApplication;
import org.destinationsol.assets.Assets;
import org.destinationsol.assets.audio.OggMusicManager;
import org.destinationsol.common.SolColor;
import org.destinationsol.game.DebugOptions;
import org.destinationsol.ui.DisplayDimensions;
import org.destinationsol.ui.SolInputManager;
import org.destinationsol.ui.SolUiBaseScreen;
import org.destinationsol.ui.SolUiControl;
import org.destinationsol.ui.UiDrawer;

public class MainMenuScreen extends SolUiBaseScreen {
    private final boolean isMobile;
    private final GameOptions gameOptions;

    private final TextureAtlas.AtlasRegion logoTexture;
    private final TextureAtlas.AtlasRegion backgroundTexture;
    private DisplayDimensions displayDimensions;

    private final SolUiControl tutorialControl;
    private final SolUiControl optionsControl;
    private final SolUiControl exitControl;
    private final SolUiControl newGameControl;
    private final SolUiControl creditsControl;

    MainMenuScreen(MenuLayout menuLayout, boolean isMobile, GameOptions gameOptions) {
        this.isMobile = isMobile;
        this.gameOptions = gameOptions;

        displayDimensions = SolApplication.displayDimensions;

        int w = 300;
        int h = 75;
        int padding = 10;

        int offsetY = -(padding + h/2);

        tutorialControl = new SolUiControl(w, h, UiDrawer.positions.get("bottom"), 0, offsetY, true, Input.Keys.T);
        tutorialControl.setDisplayName("Tutorial");
        controls.add(tutorialControl);

        offsetY -= padding + h;

        newGameControl = new SolUiControl(w, h, UiDrawer.positions.get("bottom"), 0, offsetY, true, gameOptions.getKeyShoot());
        newGameControl.setDisplayName("Play Game");
        controls.add(newGameControl);

        offsetY -= padding + h;

        // TODO: Temporarily showing on mobile as well. Fix!
        // optionsControl = new SolUiControl(isMobile ? null : menuLayout.buttonRect(-1, 3), true, Input.Keys.O);
        optionsControl = new SolUiControl(w, h, UiDrawer.positions.get("bottom"), 0, offsetY, true, Input.Keys.O);
        optionsControl.setDisplayName("Options");
        controls.add(optionsControl);

        offsetY -= padding + h;

        exitControl = new SolUiControl(w, h, UiDrawer.positions.get("bottom"), 0, offsetY, true, gameOptions.getKeyEscape());
        exitControl.setDisplayName("Exit");
        controls.add(exitControl);

        creditsControl = new SolUiControl(MenuLayout.bottomRightFloatingButton(displayDimensions), true, Input.Keys.C);
        creditsControl.setDisplayName("Credits");
        controls.add(creditsControl);

        backgroundTexture = Assets.getAtlasRegion("engine:mainMenuBg", Texture.TextureFilter.Linear);
        logoTexture = Assets.getAtlasRegion("engine:mainMenuLogo", Texture.TextureFilter.Linear);
    }

    @Override
    public void updateCustom(SolApplication solApplication, SolInputManager.InputPointer[] inputPointers, boolean clickedOutside) {
        tutorialControl.setEnabled(solApplication.getOptions().controlType != GameOptions.ControlType.CONTROLLER);

        if (tutorialControl.isJustOff()) {
            solApplication.play(true, "Imperial Small", true);
            return;
        }

        SolInputManager inputManager = solApplication.getInputManager();
        MenuScreens screens = solApplication.getMenuScreens();

        if (newGameControl.isJustOff()) {
            inputManager.setScreen(solApplication, screens.newGame);
            return;
        }

        if (optionsControl.isJustOff()) {
            inputManager.setScreen(solApplication, screens.options);
            return;
        }

        if (exitControl.isJustOff()) {
            // Save the settings on exit, but not on mobile as settings don't exist there.
            if (!isMobile) {
                solApplication.getOptions().save();
            }
            Gdx.app.exit();
            return;
        }

        if (creditsControl.isJustOff()) {
            inputManager.setScreen(solApplication, screens.credits);
        }
    }

    @Override
    public void onAdd(SolApplication solApplication) {
        solApplication.getMusicManager().playMusic(OggMusicManager.MENU_MUSIC_SET, gameOptions);
    }

    @Override
    public void drawBackground(UiDrawer uiDrawer, SolApplication solApplication) {
        uiDrawer.draw(backgroundTexture, displayDimensions.getRatio(), 1, displayDimensions.getRatio() / 2, 0.5f, displayDimensions.getRatio() / 2, 0.5f, 0, SolColor.WHITE);
    }

    @Override
    public void drawImages(UiDrawer uiDrawer, SolApplication solApplication) {
        final float sy = .35f;
        final float sx = sy * 400 / 218;
        if (!DebugOptions.PRINT_BALANCE) {
            uiDrawer.draw(logoTexture, sx, sy, sx / 2, sy / 2, displayDimensions.getRatio() / 2, 0.1f + sy / 2, 0, SolColor.WHITE);
        }
    }
}
