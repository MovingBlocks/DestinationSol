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
package org.destinationsol.game.screens;

import org.destinationsol.GameOptions;
import org.destinationsol.SolApplication;
import org.destinationsol.common.SolColor;
import org.destinationsol.game.SolGame;
import org.destinationsol.menu.MenuLayout;
import org.destinationsol.ui.SolInputManager;
import org.destinationsol.ui.SolUiControl;
import org.destinationsol.ui.SolUiScreen;
import org.destinationsol.ui.UiDrawer;

import java.util.ArrayList;
import java.util.List;

public class MenuScreen implements SolUiScreen {
    private final List<SolUiControl> controls = new ArrayList<>();
    private final SolUiControl closeControl;
    private final SolUiControl exitControl;
    private final SolUiControl respawnControl;
    private final SolUiControl soundVolControl;
    private final SolUiControl musicVolumeControl;
    private final SolUiControl doNotSellEquippedControl;

    public MenuScreen(MenuLayout menuLayout, GameOptions gameOptions) {
        doNotSellEquippedControl = new SolUiControl(menuLayout.buttonRect(-1, -1), true);
        doNotSellEquippedControl.setDisplayName("Can sell used items");
        controls.add(doNotSellEquippedControl);
        soundVolControl = new SolUiControl(menuLayout.buttonRect(-1, 1), true);
        soundVolControl.setDisplayName("Sound Volume");
        controls.add(soundVolControl);
        musicVolumeControl = new SolUiControl(menuLayout.buttonRect(-1, 0), true);
        musicVolumeControl.setDisplayName("Music Volume");
        controls.add(musicVolumeControl);
        respawnControl = new SolUiControl(menuLayout.buttonRect(-1, 2), true);
        respawnControl.setDisplayName("Respawn");
        controls.add(respawnControl);
        exitControl = new SolUiControl(menuLayout.buttonRect(-1, 3), true);
        exitControl.setDisplayName("Exit");
        controls.add(exitControl);
        closeControl = new SolUiControl(menuLayout.buttonRect(-1, 4), true, gameOptions.getKeyClose());
        closeControl.setDisplayName("Resume");
        controls.add(closeControl);
    }

    @Override
    public List<SolUiControl> getControls() {
        return controls;
    }

    @Override
    public void updateCustom(SolApplication solApplication, SolInputManager.InputPointer[] inputPointers, boolean clickedOutside) {
        SolGame game = solApplication.getGame();
        game.setPaused(true);
        SolInputManager im = solApplication.getInputMan();
        GameOptions options = solApplication.getOptions();
        soundVolControl.setDisplayName("Sound Volume: " + options.getSFXVolumeAsText());
        if (soundVolControl.isJustOff()) {
            options.advanceSoundVolMul();
        }
        musicVolumeControl.setDisplayName("Music Volume: " + options.getMusicVolumeAsText());
        if (musicVolumeControl.isJustOff()) {
            options.advanceMusicVolMul();
            solApplication.getMusicManager().resetVolume(options);
        }
        if (respawnControl.isJustOff()) {
            game.respawn();
            im.setScreen(solApplication, game.getScreens().mainScreen);
            game.setPaused(false);
        }
        if (exitControl.isJustOff()) {
            solApplication.finishGame();
        }
        if (closeControl.isJustOff()) {
            game.setPaused(false);
            im.setScreen(solApplication, game.getScreens().mainScreen);
        }
        doNotSellEquippedControl.setDisplayName("Can sell used items: " +
                                                  (options.canSellEquippedItems ? "Yes" : "No"));
        if (doNotSellEquippedControl.isJustOff()) {
            options.canSellEquippedItems = !options.canSellEquippedItems;
        }
    }

    @Override
    public void drawBg(UiDrawer uiDrawer, SolApplication solApplication) {
        uiDrawer.draw(uiDrawer.filler, SolColor.UI_BG);
    }

    @Override
    public boolean isCursorOnBg(SolInputManager.InputPointer inputPointer) {
        return true;
    }
}
