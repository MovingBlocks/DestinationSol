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

import com.badlogic.gdx.Gdx;
import org.destinationsol.GameOptions;
import org.destinationsol.SolApplication;
import org.destinationsol.assets.music.OggMusicManager;
import org.destinationsol.game.WorldConfig;
import org.destinationsol.modules.ModuleManager;
import org.destinationsol.ui.nui.NUIManager;
import org.destinationsol.ui.nui.NUIScreenLayer;
import org.terasology.nui.Canvas;
import org.terasology.nui.widgets.UIButton;

import javax.inject.Inject;
import java.util.HashSet;

/**
 * The main menu screen. This is the first screen shown when you open the game.
 */
public class MainMenuScreen extends NUIScreenLayer {

    private final SolApplication solApplication;
    private final ModuleManager moduleManager;
    private UIButton tutorialButton;

    @Inject
    public MainMenuScreen(SolApplication solApplication, ModuleManager moduleManager) {
        this.solApplication = solApplication;
        this.moduleManager = moduleManager;
    }

    @Override
    public void initialise() {
        tutorialButton = find("tutorialButton", UIButton.class);
        tutorialButton.subscribe(button -> {
            WorldConfig worldConfig = new WorldConfig();
            worldConfig.setModules(new HashSet<>(moduleManager.getEnvironment().getModulesOrderedByDependencies()));
            solApplication.getMenuScreens().loading.setMode(true, "Imperial Small", true, worldConfig);
            nuiManager.setScreen(solApplication.getMenuScreens().loading);
        });

        UIButton playGameButton = find("playGameButton", UIButton.class);
        playGameButton.subscribe(button -> {
            nuiManager.setScreen(solApplication.getMenuScreens().newGame);
        });

        UIButton optionsButton = find("optionsButton", UIButton.class);
        optionsButton.subscribe(button -> {
            nuiManager.pushScreen(solApplication.getMenuScreens().options);
            nuiManager.removeScreen(this);
        });

        UIButton exitButton = find("exitButton", UIButton.class);
        exitButton.subscribe(button -> {
            if (!solApplication.isMobile()) {
                solApplication.getOptions().save();
            }
            Gdx.app.exit();
            nuiManager.removeScreen(this);
        });

        UIButton creditsButton = find("creditsButton", UIButton.class);
        creditsButton.subscribe(button -> {
            nuiManager.setScreen(solApplication.getMenuScreens().credits);
        });
    }

    @Override
    public void onAdded() {
        final OggMusicManager musicManager = solApplication.getMusicManager();
        if (!musicManager.getCurrentMusicSet().equals(OggMusicManager.MENU_MUSIC_SET)) {
            musicManager.playMusic(OggMusicManager.MENU_MUSIC_SET, solApplication.getOptions());
        }
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
