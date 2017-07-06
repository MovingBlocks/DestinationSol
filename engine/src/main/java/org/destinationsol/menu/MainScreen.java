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
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import org.destinationsol.GameOptions;
import org.destinationsol.SolApplication;
import org.destinationsol.assets.Assets;
import org.destinationsol.common.SolColor;
import org.destinationsol.game.DebugOptions;
import org.destinationsol.ui.SolInputManager;
import org.destinationsol.ui.SolUiControl;
import org.destinationsol.ui.SolUiScreen;
import org.destinationsol.ui.UiDrawer;

import java.util.ArrayList;
import java.util.List;

public class MainScreen implements SolUiScreen {
    private final boolean isMobile;
    private final GameOptions gameOptions;

    private final TextureAtlas.AtlasRegion logoTex;
    public final TextureAtlas.AtlasRegion bgTex;

    private final ArrayList<SolUiControl> controls = new ArrayList<>();
    private final SolUiControl tutorialControl;
    private final SolUiControl optionsControl;
    private final SolUiControl exitControl;
    private final SolUiControl newGameControl;
    private final SolUiControl creditsControl;

    MainScreen(MenuLayout menuLayout, boolean isMobile, float resolutionRatio, GameOptions gameOptions) {
        this.isMobile = isMobile;
        this.gameOptions = gameOptions;

        tutorialControl = new SolUiControl(menuLayout.buttonRect(-1, 1), true, Input.Keys.T);
        tutorialControl.setDisplayName("Tutorial");
        controls.add(tutorialControl);

        newGameControl = new SolUiControl(menuLayout.buttonRect(-1, 2), true, gameOptions.getKeyShoot());
        newGameControl.setDisplayName("Play Game");
        controls.add(newGameControl);

        optionsControl = new SolUiControl(isMobile ? null : menuLayout.buttonRect(-1, 3), true, Input.Keys.O);
        optionsControl.setDisplayName("Options");
        controls.add(optionsControl);

        exitControl = new SolUiControl(menuLayout.buttonRect(-1, 4), true, gameOptions.getKeyEscape());
        exitControl.setDisplayName("Exit");
        controls.add(exitControl);

        creditsControl = new SolUiControl(MenuLayout.bottomRightFloatingButton(resolutionRatio), true, Input.Keys.C);
        creditsControl.setDisplayName("Credits");
        controls.add(creditsControl);

        bgTex = Assets.getAtlasRegion("engine:mainMenuBg", Texture.TextureFilter.Linear);
        logoTex = Assets.getAtlasRegion("engine:mainMenuLogo", Texture.TextureFilter.Linear);
    }

    public List<SolUiControl> getControls() {
        return controls;
    }

    @Override
    public void updateCustom(SolApplication solApplication, SolInputManager.InputPointer[] inputPointers, boolean clickedOutside) {
        if (solApplication.getOptions().controlType == GameOptions.CONTROL_CONTROLLER) {
            tutorialControl.setEnabled(false);
        } else {
            tutorialControl.setEnabled(true);
        }

        if (tutorialControl.isJustOff()) {
            solApplication.loadNewGame(true, "Imperial Small");
            return;
        }

        SolInputManager inputManager = solApplication.getInputMan();
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
        solApplication.getMusicManager().playMenuMusic(gameOptions);
    }

    @Override
    public void drawBg(UiDrawer uiDrawer, SolApplication solApplication) {
        uiDrawer.draw(bgTex, uiDrawer.r, 1, uiDrawer.r / 2, 0.5f, uiDrawer.r / 2, 0.5f, 0, SolColor.WHITE);
    }

    @Override
    public void drawImgs(UiDrawer uiDrawer, SolApplication solApplication) {
        final float sy = .35f;
        final float sx = sy * 400 / 218;
        if (!DebugOptions.PRINT_BALANCE) {
            uiDrawer.draw(logoTex, sx, sy, sx / 2, sy / 2, uiDrawer.r / 2, 0.1f + sy / 2, 0, SolColor.WHITE);
        }
    }
}
