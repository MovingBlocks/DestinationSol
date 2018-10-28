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
package org.destinationsol.game.screens;

import org.destinationsol.GameOptions;
import org.destinationsol.SolApplication;
import org.destinationsol.common.SolColor;
import org.destinationsol.ui.SolInputManager;
import org.destinationsol.ui.SolUiBaseScreen;
import org.destinationsol.ui.UiDrawer;
import org.destinationsol.ui.responsiveUi.UiRelativeLayout;
import org.destinationsol.ui.responsiveUi.UiTextButton;
import org.destinationsol.ui.responsiveUi.UiVerticalListLayout;
import static org.destinationsol.ui.UiDrawer.UI_POSITION_BOTTOM;
import static org.destinationsol.ui.responsiveUi.UiTextButton.BUTTON_PADDING;

public class MenuScreen extends SolUiBaseScreen {
    MenuScreen(GameOptions gameOptions) {
        UiVerticalListLayout buttonList = new UiVerticalListLayout();

        buttonList.addElement(new UiTextButton().setDisplayName(getCanSellEquippedItemsString(gameOptions))
                .enableSound()
                .setOnReleaseAction(uiElement -> {
                    gameOptions.canSellEquippedItems = !gameOptions.canSellEquippedItems;
                    ((UiTextButton)uiElement).setDisplayName(getCanSellEquippedItemsString(gameOptions));
                }));

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
                    // solApplication.getMusicManager().changeVolume(options);
                    ((UiTextButton)uiElement).setDisplayName(getMusicVolumeString(gameOptions));
                }));

        buttonList.addElement(new UiTextButton().setDisplayName("Respawn")
                .enableSound()
                .setOnReleaseAction(uiElement -> {
                    SolApplication.getInstance().getGame().respawn();
                    SolApplication.changeScreen(SolApplication.getInstance().getGame().getScreens().mainGameScreen);
                    SolApplication.getInstance().getGame().setPaused(false);
                }));

        buttonList.addElement(new UiTextButton().setDisplayName("Exit")
                .enableSound()
                .setOnReleaseAction(uiElement -> SolApplication.getInstance().finishGame()));

        buttonList.addElement(new UiTextButton().setDisplayName("Resume")
                .setTriggerKey(gameOptions.getKeyEscape())
                .enableSound()
                .setOnReleaseAction(uiElement -> {
                    SolApplication.getInstance().getGame().setPaused(false);
                    SolApplication.changeScreen(SolApplication.getInstance().getGame().getScreens().mainGameScreen);
                }));

        rootUiElement = new UiRelativeLayout().addElement(buttonList, UI_POSITION_BOTTOM, 0, -buttonList.getHeight() / 2 - BUTTON_PADDING);
    }

    private String getCanSellEquippedItemsString(GameOptions gameOptions) {
        return "Can sell equipped items: " + (gameOptions.canSellEquippedItems ? "Yes" : "No");
    }

    private String getSoundVolumeString(GameOptions gameOptions) {
        return "Sound Volume: " + gameOptions.sfxVolume.getName();
    }

    private String getMusicVolumeString(GameOptions gameOptions) {
        return "Sound Volume: " + gameOptions.musicVolume.getName();
    }

    @Override
    public void drawBackground(UiDrawer uiDrawer, SolApplication solApplication) {
        uiDrawer.draw(uiDrawer.filler, SolColor.UI_BG);
    }

    @Override
    public boolean isCursorOnBackground(SolInputManager.InputPointer inputPointer) {
        return true;
    }
}
