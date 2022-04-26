/*
 * Copyright 2021 The Terasology Foundation
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
package org.destinationsol.ui.nui.screens.mainMenu;

import org.destinationsol.SolApplication;
import org.destinationsol.ui.nui.NUIManager;
import org.destinationsol.ui.nui.NUIScreenLayer;
import org.destinationsol.ui.nui.widgets.KeyActivatedButton;
import org.terasology.nui.Canvas;
import org.terasology.nui.backends.libgdx.GDXInputUtil;
import org.terasology.nui.layouts.ColumnLayout;
import org.terasology.nui.widgets.UIButton;

import javax.inject.Inject;

/**
 * The options screen allows the user to change configurable game settings through the UI.
 * It is accessible via the {@link MainMenuScreen}.
 */
public class OptionsScreen extends NUIScreenLayer {

    private final SolApplication solApplication;

    @Inject
    public OptionsScreen(SolApplication solApplication) {
        this.solApplication = solApplication;
    }

    @Override
    public void initialise() {
        ColumnLayout menuButtonsLayout = find("menuButtons", ColumnLayout.class);

        UIButton musicVolumeButton = find("musicVolumeButton", UIButton.class);
        musicVolumeButton.setText("Music Volume: " + solApplication.getOptions().musicVolume.getName());
        musicVolumeButton.subscribe(button -> {
            solApplication.getOptions().advanceMusicVolMul();
            musicVolumeButton.setText("Music Volume: " + solApplication.getOptions().musicVolume.getName());
            solApplication.getMusicManager().changeVolume(solApplication.getOptions());
        });

        UIButton soundVolumeButton = find("soundVolumeButton", UIButton.class);
        soundVolumeButton.setText("Sound Volume: " + solApplication.getOptions().sfxVolume.getName());
        soundVolumeButton.subscribe(button -> {
            solApplication.getOptions().advanceSoundVolMul();
            soundVolumeButton.setText("Sound Volume: " + solApplication.getOptions().sfxVolume.getName());
        });

        UIButton resolutionButton = find("resolutionButton", UIButton.class);
        resolutionButton.subscribe(button -> {
            nuiManager.setScreen(solApplication.getMenuScreens().resolutionScreen);
        });

        UIButton mapPanSpeedButton = find("mapPanSpeedButton", UIButton.class);
        mapPanSpeedButton.setText("Map Pan Speed: " + solApplication.getOptions().getMapScrollSpeed());
        mapPanSpeedButton.subscribe(button -> {
            solApplication.getOptions().advanceMapScrollSpeed();
            mapPanSpeedButton.setText("Map Pan Speed: " + solApplication.getOptions().getMapScrollSpeed());
        });

        KeyActivatedButton controlTypeButton = find("controlTypeButton", KeyActivatedButton.class);
        if (solApplication.isMobile()) {
            menuButtonsLayout.removeWidget(controlTypeButton);
        } else {
            controlTypeButton.setText("Input: " + solApplication.getOptions().controlType.getHumanName());
            controlTypeButton.subscribe(button -> {
                solApplication.getOptions().advanceControlType(false);
                controlTypeButton.setText("Input: " + solApplication.getOptions().controlType.getHumanName());
            });
        }

        KeyActivatedButton controlsButton = find("controlsButton", KeyActivatedButton.class);
        if (solApplication.isMobile()) {
            menuButtonsLayout.removeWidget(controlsButton);
        } else {
            controlsButton.subscribe(button -> {
                InputMapScreen inputMapScreen = solApplication.getMenuScreens().inputMapScreen;
                switch (solApplication.getOptions().controlType) {
                    case KEYBOARD:
                        inputMapScreen.setOperations(inputMapScreen.getInputMapKeyboardScreen());
                        break;
                    case MIXED:
                        inputMapScreen.setOperations(inputMapScreen.getInputMapMixedScreen());
                        break;
                    case CONTROLLER:
                        inputMapScreen.setOperations(inputMapScreen.getInputMapControllerScreen());
                }
                nuiManager.setScreen(inputMapScreen);
            });
        }

        KeyActivatedButton cancelButton = find("cancelButton", KeyActivatedButton.class);
        cancelButton.setKey(GDXInputUtil.GDXToNuiKey(solApplication.getOptions().getKeyEscape()));
        cancelButton.subscribe(button -> {
            nuiManager.setScreen(solApplication.getMenuScreens().main);
        });
    }

    @Override
    public void onAdded() {
        UIButton musicVolumeButton = find("musicVolumeButton", UIButton.class);
        musicVolumeButton.setText("Music Volume: " + solApplication.getOptions().musicVolume.getName());

        UIButton soundVolumeButton = find("soundVolumeButton", UIButton.class);
        soundVolumeButton.setText("Sound Volume: " + solApplication.getOptions().sfxVolume.getName());
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        solApplication.getMenuBackgroundManager().update();
    }

    @Override
    public void onDraw(Canvas canvas) {
        try (NUIManager.LegacyUiDrawerWrapper wrapper = nuiManager.getLegacyUiDrawer()) {
            solApplication.getMenuBackgroundManager().draw(wrapper.getUiDrawer());
        }

        super.onDraw(canvas);
    }

    @Override
    protected boolean escapeCloses() {
        return false;
    }
}
