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
import org.destinationsol.ui.UiDrawer;
import org.destinationsol.ui.responsiveUi.UiRelativeLayout;
import org.destinationsol.ui.responsiveUi.UiTextButton;
import org.destinationsol.ui.responsiveUi.UiVerticalListLayout;
import static org.destinationsol.ui.UiDrawer.UI_POSITION_BOTTOM;
import static org.destinationsol.ui.responsiveUi.UiTextButton.BUTTON_PADDING;

public class OptionsScreen extends SolUiBaseScreen {
    private final TextureAtlas.AtlasRegion backgroundTexture;

    private DisplayDimensions displayDimensions;

    OptionsScreen(GameOptions gameOptions) {
        displayDimensions = SolApplication.displayDimensions;

        SolInputManager inputManager = SolApplication.getInputManager();
        MenuScreens screens = SolApplication.getMenuScreens();

        UiVerticalListLayout buttonList = new UiVerticalListLayout();

        UiTextButton soundVolumeButton = new UiTextButton().setDisplayName(getSoundVolumeString(gameOptions))
                                                           .enableSound();
        soundVolumeButton.setOnReleaseAction(() -> {
                                                       gameOptions.advanceSoundVolMul();
                                                       soundVolumeButton.setDisplayName(getSoundVolumeString(gameOptions));
                                                   });
        buttonList.addElement(soundVolumeButton);

        UiTextButton musicVolumeButton = new UiTextButton().setDisplayName(getMusicVolumeString(gameOptions))
                                                           .enableSound();
        musicVolumeButton.setOnReleaseAction(() -> {
                                                       gameOptions.advanceMusicVolMul();
                                                       musicVolumeButton.setDisplayName(getMusicVolumeString(gameOptions));
                                                   });
        buttonList.addElement(musicVolumeButton);

        buttonList.addElement(new UiTextButton().setDisplayName("Resolution")
                                                .setTriggerKey(Input.Keys.R)
                                                .enableSound()
                                                .setOnReleaseAction(() -> inputManager.changeScreen(screens.resolutionScreen)));

        UiTextButton controlTypeButton = new UiTextButton().setDisplayName(getControlTypeString(gameOptions))
                                                           .setTriggerKey(Input.Keys.C)
                                                           .enableSound();
        controlTypeButton.setOnReleaseAction(() -> {
                                                       gameOptions.advanceControlType(false);
                                                       controlTypeButton.setDisplayName(getControlTypeString(gameOptions));
                                                   });
        buttonList.addElement(controlTypeButton);

        buttonList.addElement(new UiTextButton().setDisplayName("Controls")
                                                .enableSound()
                                                .setOnReleaseAction(() -> {
                                                                              switch (gameOptions.controlType) {
                                                                                  case KEYBOARD:
                                                                                      screens.inputMapScreen.setOperations(screens.inputMapScreen.inputMapKeyboardScreen);
                                                                                      break;
                                                                                  case MIXED:
                                                                                      screens.inputMapScreen.setOperations(screens.inputMapScreen.inputMapMixedScreen);
                                                                                      break;
                                                                                  case CONTROLLER:
                                                                                      screens.inputMapScreen.setOperations(screens.inputMapScreen.inputMapControllerScreen);
                                                                              }
                                                                              inputManager.changeScreen(screens.inputMapScreen);
                                                                          }));

        buttonList.addElement(new UiTextButton().setDisplayName("Back")
                                                .setTriggerKey(gameOptions.getKeyEscape())
                                                .enableSound()
                                                .setOnReleaseAction(() -> inputManager.changeScreen(screens.mainScreen)));

        rootUiElement = new UiRelativeLayout().addElement(buttonList, UI_POSITION_BOTTOM, 0, -buttonList.getHeight()/2 - BUTTON_PADDING)
                                              .finalizeChanges();

        backgroundTexture = Assets.getAtlasRegion("engine:mainMenuBg", Texture.TextureFilter.Linear);
    }

    @Override
    public void drawBackground(UiDrawer uiDrawer, SolApplication solApplication) {
        uiDrawer.draw(backgroundTexture, displayDimensions.getRatio(), 1, displayDimensions.getRatio() / 2, 0.5f, displayDimensions.getRatio() / 2, 0.5f, 0, SolColor.WHITE);
    }

    private String getControlTypeString(GameOptions gameOptions) {
        return "Input: " + gameOptions.controlType.getHumanName();
    }

    private String getSoundVolumeString(GameOptions gameOptions) {
        return "Sound Volume: " + gameOptions.sfxVolume.getName();
    }

    private String getMusicVolumeString(GameOptions gameOptions) {
        return "Sound Volume: " + gameOptions.musicVolume.getName();
    }
}
