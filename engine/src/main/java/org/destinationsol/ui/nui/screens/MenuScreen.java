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

package org.destinationsol.ui.nui.screens;

import org.destinationsol.SolApplication;
import org.destinationsol.common.In;
import org.destinationsol.ui.nui.NUIScreenLayer;
import org.destinationsol.ui.nui.widgets.KeyActivatedButton;
import org.terasology.nui.backends.libgdx.GDXInputUtil;
import org.terasology.nui.widgets.UIButton;

/**
 * This is the game menu, accessible from anytime in-game by pressing the menu key (default "Escape").
 * The game menu allows you to configure volume options, trigger a respawn or exit to the main menu.
 */
public class MenuScreen extends NUIScreenLayer {
    @In
    private SolApplication solApplication;

    @Override
    public void initialise() {
        UIButton canSellUsedItemsButton = find("canSellUsedItemsButton", UIButton.class);
        canSellUsedItemsButton.setText("Can sell used items: " + (solApplication.getOptions().canSellEquippedItems ? "Yes" : "No"));
        canSellUsedItemsButton.subscribe(button -> {
            solApplication.getOptions().canSellEquippedItems = !solApplication.getOptions().canSellEquippedItems;
            ((UIButton)button).setText("Can sell used items: " + (solApplication.getOptions().canSellEquippedItems ? "Yes" : "No"));
        });

        UIButton soundVolumeButton = find("soundVolumeButton", UIButton.class);
        soundVolumeButton.setText("Sound Volume: " + solApplication.getOptions().sfxVolume.getName());
        soundVolumeButton.subscribe(button -> {
            solApplication.getOptions().advanceSoundVolMul();
            ((UIButton)button).setText("Sound Volume: " + solApplication.getOptions().sfxVolume.getName());
        });

        UIButton musicVolumeButton = find("musicVolumeButton", UIButton.class);
        musicVolumeButton.setText("Music Volume: " + solApplication.getOptions().musicVolume.getName());
        musicVolumeButton.subscribe(button -> {
            solApplication.getOptions().advanceMusicVolMul();
            ((UIButton)button).setText("Music Volume: " + solApplication.getOptions().musicVolume.getName());
            solApplication.getMusicManager().changeVolume(solApplication.getOptions());
        });

        UIButton respawnButton = find("respawnButton", UIButton.class);
        respawnButton.subscribe(button -> {
            solApplication.getGame().respawn();
            nuiManager.removeScreen(this);
        });

        UIButton exitButton = find("exitButton", UIButton.class);
        exitButton.subscribe(button -> {
            solApplication.finishGame();
        });

        KeyActivatedButton resumeButton = find("resumeButton", KeyActivatedButton.class);
        resumeButton.setKey(GDXInputUtil.GDXToNuiKey(solApplication.getOptions().getKeyClose()));
        resumeButton.subscribe(button -> {
            nuiManager.removeScreen(this);
        });
    }

    @Override
    public void onAdded() {
        solApplication.getGame().setPaused(true);
    }

    @Override
    public void onRemoved() {
        solApplication.getGame().setPaused(false);
    }

    @Override
    protected boolean escapeCloses() {
        return false;
    }
}
