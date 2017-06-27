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

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import org.destinationsol.GameOptions;
import org.destinationsol.SolApplication;
import org.destinationsol.assets.Assets;
import org.destinationsol.common.SolColor;
import org.destinationsol.ui.SolInputManager;
import org.destinationsol.ui.SolUiControl;
import org.destinationsol.ui.SolUiScreen;
import org.destinationsol.ui.UiDrawer;

import java.util.ArrayList;
import java.util.List;

public class OptionsScreen implements SolUiScreen {
    private final TextureAtlas.AtlasRegion bgTex;

    private final ArrayList<SolUiControl> controls = new ArrayList<>();
    private final SolUiControl backControl;
    private final SolUiControl resolutionControl;
    private final SolUiControl inputTypeControl;
    private final SolUiControl inputMapControl;
    private final SolUiControl soundVolumeControl;
    private final SolUiControl musicVolumeControl;

    OptionsScreen(MenuLayout menuLayout, GameOptions gameOptions) {
        resolutionControl = new SolUiControl(menuLayout.buttonRect(-1, 1), true);
        resolutionControl.setDisplayName("Resolution");
        controls.add(resolutionControl);

        inputTypeControl = new SolUiControl(menuLayout.buttonRect(-1, 2), true, Input.Keys.C);
        inputTypeControl.setDisplayName("Control Type");
        controls.add(inputTypeControl);

        inputMapControl = new SolUiControl(menuLayout.buttonRect(-1, 3), true, Input.Keys.M);
        inputMapControl.setDisplayName("Controls");
        controls.add(inputMapControl);

        backControl = new SolUiControl(menuLayout.buttonRect(-1, 4), true, gameOptions.getKeyEscape());
        backControl.setDisplayName("Back");
        controls.add(backControl);

        soundVolumeControl = new SolUiControl(menuLayout.buttonRect(-1, 0), true);
        soundVolumeControl.setDisplayName("Sound Volume");
        controls.add(soundVolumeControl);

        musicVolumeControl = new SolUiControl(menuLayout.buttonRect(-1, -1), true);
        musicVolumeControl.setDisplayName("Music Volume");
        controls.add(musicVolumeControl);

        bgTex = Assets.getAtlasRegion("engine:mainMenuBg", Texture.TextureFilter.Linear);
    }

    @Override
    public List<SolUiControl> getControls() {
        return controls;
    }

    @Override
    public void updateCustom(SolApplication solApplication, SolInputManager.InputPointer[] inputPointers, boolean clickedOutside) {
        SolInputManager inputManager = solApplication.getInputMan();
        MenuScreens screens = solApplication.getMenuScreens();
        GameOptions options = solApplication.getOptions();
        if (resolutionControl.isJustOff()) {
            inputManager.setScreen(solApplication, screens.resolutionScreen);
        }

        int controlType = solApplication.getOptions().controlType;
        String controlName = "Keyboard";
        if (controlType == GameOptions.CONTROL_MIXED) {
            controlName = "KB + Mouse";
        }
        if (controlType == GameOptions.CONTROL_MOUSE) {
            controlName = "Mouse";
        }
        if (controlType == GameOptions.CONTROL_CONTROLLER) {
            controlName = "Controller";
        }
        inputTypeControl.setDisplayName("Input: " + controlName);
        if (inputTypeControl.isJustOff()) {
            solApplication.getOptions().advanceControlType(false);
        }

        if (backControl.isJustOff()) {
            inputManager.setScreen(solApplication, screens.main);
        }

        if (inputMapControl.isJustOff()) {
            if (controlType == GameOptions.CONTROL_MIXED) {
                screens.inputMapScreen.setOperations(screens.inputMapScreen.inputMapMixedScreen);
            } else if (controlType == GameOptions.CONTROL_KB) {
                screens.inputMapScreen.setOperations(screens.inputMapScreen.inputMapKeyboardScreen);
            } else if (controlType == GameOptions.CONTROL_CONTROLLER) {
                screens.inputMapScreen.setOperations(screens.inputMapScreen.inputMapControllerScreen);
            }
            inputManager.setScreen(solApplication, screens.inputMapScreen);
        }

        soundVolumeControl.setDisplayName("Sound Volume: " + options.getSFXVolumeAsText());
        if (soundVolumeControl.isJustOff()) {
            options.advanceSoundVolMul();
        }

        musicVolumeControl.setDisplayName("Music Volume: " + options.getMusicVolumeAsText());
        if (musicVolumeControl.isJustOff()) {
            options.advanceMusicVolMul();
            solApplication.getMusicManager().resetVolume(options);
        }
    }

    @Override
    public void drawBg(UiDrawer uiDrawer, SolApplication solApplication) {
        uiDrawer.draw(bgTex, uiDrawer.r, 1, uiDrawer.r / 2, 0.5f, uiDrawer.r / 2, 0.5f, 0, SolColor.WHITE);
    }
}
