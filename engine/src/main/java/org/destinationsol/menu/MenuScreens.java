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

import org.destinationsol.GameOptions;
import org.destinationsol.ui.SolLayouts;
import org.destinationsol.ui.nui.NUIManager;
import org.destinationsol.ui.nui.screens.mainMenu.CreditsScreen;
import org.destinationsol.ui.nui.screens.mainMenu.InputMapScreen;
import org.destinationsol.ui.nui.screens.mainMenu.LoadingScreen;
import org.destinationsol.ui.nui.screens.mainMenu.MainMenuScreen;
import org.destinationsol.ui.nui.screens.mainMenu.ModulesScreen;
import org.destinationsol.ui.nui.screens.mainMenu.NewGameScreen;
import org.destinationsol.ui.nui.screens.mainMenu.NewShipScreen;
import org.destinationsol.ui.nui.screens.mainMenu.OptionsScreen;
import org.destinationsol.ui.nui.screens.mainMenu.ResolutionScreen;

public class MenuScreens {
    public final MainMenuScreen main;
    public final OptionsScreen options;
    public final InputMapScreen inputMapScreen;
    public final ResolutionScreen resolutionScreen;
    public final CreditsScreen credits;
    public final LoadingScreen loading;
    public final NewGameScreen newGame;
    public final NewShipScreen newShip;
    public final ModulesScreen modules;

    public MenuScreens(SolLayouts layouts, boolean mobile, GameOptions gameOptions, NUIManager nuiManager) {
        MenuLayout menuLayout = layouts.menuLayout;
        main = (MainMenuScreen) nuiManager.createScreen("engine:mainMenuScreen");
        options = (OptionsScreen) nuiManager.createScreen("engine:optionsScreen");
        inputMapScreen = (InputMapScreen) nuiManager.createScreen("engine:inputMapScreen");
        resolutionScreen = (ResolutionScreen) nuiManager.createScreen("engine:resolutionScreen");
        credits = (CreditsScreen) nuiManager.createScreen("engine:creditsScreen");
        loading = (LoadingScreen) nuiManager.createScreen("engine:loadingScreen");
        newGame = (NewGameScreen) nuiManager.createScreen("engine:newGameScreen");
        newShip = (NewShipScreen) nuiManager.createScreen("engine:newShipScreen");
        modules = (ModulesScreen) nuiManager.createScreen("engine:modulesScreen");
    }
}
