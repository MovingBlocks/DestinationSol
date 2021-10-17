/*
 * Copyright 2020 MovingBlocks
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

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import org.destinationsol.GameOptions;
import org.destinationsol.SolApplication;
import org.destinationsol.assets.Assets;
import org.destinationsol.common.SolColor;
import org.destinationsol.ui.DisplayDimensions;
import org.destinationsol.ui.SolInputManager;
import org.destinationsol.ui.SolUiBaseScreen;
import org.destinationsol.ui.SolUiControl;
import org.destinationsol.ui.UiDrawer;

public class OptionsScreen extends SolUiBaseScreen {
    private DisplayDimensions displayDimensions;

    private final TextureAtlas.AtlasRegion backgroundTexture;

    private final SolUiControl backControl;
    private final SolUiControl resolutionControl;
    private final SolUiControl inputTypeControl;
    private final SolUiControl inputMapControl;
    private final SolUiControl soundVolumeControl;
    private final SolUiControl musicVolumeControl;
    private final SolUiControl mapScrollSpeedControl;

    OptionsScreen(boolean mobile, MenuLayout menuLayout, GameOptions gameOptions) {
        displayDimensions = SolApplication.displayDimensions;
        int rowNo = mobile ? -1 : -3;
        musicVolumeControl = new SolUiControl(menuLayout.buttonRect(-1, rowNo++), true);
        musicVolumeControl.setDisplayName("Music Volume");
        controls.add(musicVolumeControl);

        soundVolumeControl = new SolUiControl(menuLayout.buttonRect(-1, rowNo++), true);
        soundVolumeControl.setDisplayName("Sound Volume");
        controls.add(soundVolumeControl);

        // Mobile platforms always run in fullscreen but the NUI UI scale can be changed
        resolutionControl = new SolUiControl(menuLayout.buttonRect(-1, rowNo++), true);
        resolutionControl.setDisplayName("Resolution");
        controls.add(resolutionControl);

        mapScrollSpeedControl = new SolUiControl(menuLayout.buttonRect(-1, rowNo++), true);
        mapScrollSpeedControl.setDisplayName("Map Pan Speed");
        controls.add(mapScrollSpeedControl);

        // Mobile platforms always use the same input method: touchscreen controls
        // TODO: Would portable keyboards be supported
        inputTypeControl = new SolUiControl(mobile ? null : menuLayout.buttonRect(-1, rowNo++), true, Input.Keys.C);
        inputTypeControl.setDisplayName("Control Type");
        controls.add(inputTypeControl);

        // The controls are not currently re-mappable on mobile platforms
        inputMapControl = new SolUiControl(mobile ? null : menuLayout.buttonRect(-1, rowNo++), true, Input.Keys.M);
        inputMapControl.setDisplayName("Controls");
        controls.add(inputMapControl);

        // Insert a space between the options and the back button
        rowNo++;
        backControl = new SolUiControl(menuLayout.buttonRect(-1, rowNo++), true, gameOptions.getKeyEscape());
        backControl.setDisplayName("Back");
        controls.add(backControl);

        backgroundTexture = Assets.getAtlasRegion("engine:mainMenuBg", Texture.TextureFilter.Linear);
    }

    @Override
    public void updateCustom(SolApplication solApplication, SolInputManager.InputPointer[] inputPointers, boolean clickedOutside) {
        SolInputManager inputManager = solApplication.getInputManager();
        MenuScreens screens = solApplication.getMenuScreens();
        GameOptions options = solApplication.getOptions();
        if (resolutionControl.isJustOff()) {
            inputManager.setScreen(solApplication, screens.resolutionScreen);
        }

        String controlName = solApplication.getOptions().controlType.getHumanName();
        inputTypeControl.setDisplayName("Input: " + controlName);
        if (inputTypeControl.isJustOff()) {
            solApplication.getOptions().advanceControlType(false);
        }

        if (backControl.isJustOff()) {
            solApplication.getNuiManager().pushScreen(screens.main);
        }

        if (inputMapControl.isJustOff()) {
            switch (solApplication.getOptions().controlType) {
                case KEYBOARD:
                    screens.inputMapScreen.setOperations(screens.inputMapScreen.inputMapKeyboardScreen);
                    break;
                case MIXED:
                    screens.inputMapScreen.setOperations(screens.inputMapScreen.inputMapMixedScreen);
                    break;
                case CONTROLLER:
                    screens.inputMapScreen.setOperations(screens.inputMapScreen.inputMapControllerScreen);
            }
            inputManager.setScreen(solApplication, screens.inputMapScreen);
        }

        soundVolumeControl.setDisplayName("Sound Volume: " + options.sfxVolume.getName());
        if (soundVolumeControl.isJustOff()) {
            options.advanceSoundVolMul();
        }

        musicVolumeControl.setDisplayName("Music Volume: " + options.musicVolume.getName());
        if (musicVolumeControl.isJustOff()) {
            options.advanceMusicVolMul();
            solApplication.getMusicManager().changeVolume(options);
        }

        mapScrollSpeedControl.setDisplayName("Map Pan Speed: " + options.getMapScrollSpeed());
        if (mapScrollSpeedControl.isJustOff()) {
            options.advanceMapScrollSpeed();
        }

        solApplication.getMenuBackgroundManager().update();
    }

    @Override
    public void drawBackground(UiDrawer uiDrawer, SolApplication solApplication) {
        uiDrawer.draw(backgroundTexture, displayDimensions.getRatio(), 1, displayDimensions.getRatio() / 2, 0.5f, displayDimensions.getRatio() / 2, 0.5f, 0, SolColor.WHITE);
        solApplication.getMenuBackgroundManager().draw(uiDrawer);
    }
}