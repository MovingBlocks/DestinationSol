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
import org.destinationsol.ui.SolUiBaseScreen;
import org.destinationsol.ui.UiDrawer;
import org.destinationsol.ui.responsiveUi.UiRelativeLayout;
import org.destinationsol.ui.responsiveUi.UiTextButton;
import org.destinationsol.ui.responsiveUi.UiVerticalListLayout;
import static org.destinationsol.ui.UiDrawer.UI_POSITION_BOTTOM;
import static org.destinationsol.ui.responsiveUi.UiTextButton.DEFAULT_BUTTON_PADDING;

public class OptionsScreen extends SolUiBaseScreen {
    private final TextureAtlas.AtlasRegion backgroundTexture;

    private DisplayDimensions displayDimensions;

    OptionsScreen(GameOptions gameOptions) {
        displayDimensions = SolApplication.displayDimensions;
        backgroundTexture = Assets.getAtlasRegion("engine:mainMenuBg", Texture.TextureFilter.Linear);

        UiVerticalListLayout buttonList = new UiVerticalListLayout();

        buttonList.addElement(new UiTextButton().setDisplayName(getSoundVolumeString(gameOptions))
                .enableSound()
                .setOnReleaseAction(uiElement -> {
                    gameOptions.advanceSoundVolMul();
                    // TODO: Check if we need to call soundManager.setVolume() here
                    ((UiTextButton)uiElement).setDisplayName(getSoundVolumeString(gameOptions));
                }));

        buttonList.addElement(new UiTextButton().setDisplayName(getMusicVolumeString(gameOptions))
                .enableSound()
                .setOnReleaseAction(uiElement -> {
                    gameOptions.advanceMusicVolMul();
                    // TODO: Check if we need to call musicManager.setVolume() here
                    ((UiTextButton)uiElement).setDisplayName(getMusicVolumeString(gameOptions));
                }));

        UiTextButton controlTypeButton = new UiTextButton().setDisplayName(getControlTypeString(gameOptions))
                .setTriggerKey(Input.Keys.C)
                .enableSound()
                .setOnReleaseAction(uiElement -> {
                    gameOptions.advanceControlType(false);
                    ((UiTextButton)uiElement).setDisplayName(getControlTypeString(gameOptions));
                });

        buttonList.addElement(new UiTextButton().setDisplayName(getFullscreenString(gameOptions))
                .enableSound()
                .setOnReleaseAction(uiElement -> {
                    gameOptions.advanceFullscreen();
                    ((UiTextButton)uiElement).setDisplayName(getFullscreenString(gameOptions));
                }));

        buttonList.addElement(new UiTextButton().setDisplayName("Controls")
                .enableSound()
                .setOnReleaseAction(uiElement -> {
                    switch (gameOptions.controlType) {
                        case KEYBOARD:
                            SolApplication.getMenuScreens().inputMapScreen.setOperations(SolApplication.getMenuScreens().inputMapScreen.inputMapKeyboardScreen);
                            break;
                        case MIXED:
                            SolApplication.getMenuScreens().inputMapScreen.setOperations(SolApplication.getMenuScreens().inputMapScreen.inputMapMixedScreen);
                            break;
                        case CONTROLLER:
                            SolApplication.getMenuScreens().inputMapScreen.setOperations(SolApplication.getMenuScreens().inputMapScreen.inputMapControllerScreen);
                    }
                    SolApplication.changeScreen(SolApplication.getMenuScreens().inputMapScreen);
                }));

        buttonList.addElement(new UiTextButton().setDisplayName("Back")
                .setTriggerKey(gameOptions.getKeyEscape())
                .enableSound()
                .setOnReleaseAction(uiElement -> SolApplication.changeScreen(SolApplication.getMenuScreens().mainScreen)));

        rootUiElement = new UiRelativeLayout().addElement(buttonList, UI_POSITION_BOTTOM, 0, -buttonList.getHeight() / 2 - DEFAULT_BUTTON_PADDING);

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
        return "Music Volume: " + gameOptions.musicVolume.getName();
    }

    private String getFullscreenString(GameOptions gameOptions) {
        return "Fullscreen: " + (gameOptions.fullscreen ? "On" : "Off");
    }
}
