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

import org.destinationsol.GameOptions;
import org.destinationsol.ui.SolLayouts;

public class MenuScreens {
    public final MainMenuScreen mainScreen;
    public final OptionsScreen optionsScreen;
    public final InputMapScreen inputMapScreen;
    public final ResolutionScreen resolutionScreen;
    public final CreditsScreen creditsScreen;
    public final LoadingScreen loadingScreen;
    public final NewGameScreen newGameScreen;
    public final NewShipScreen newShipScreen;

    public MenuScreens(SolLayouts layouts, boolean mobile, GameOptions gameOptions) {
        MenuLayout menuLayout = layouts.menuLayout;
        mainScreen = new MainMenuScreen(mobile, gameOptions);
        optionsScreen = new OptionsScreen(menuLayout, gameOptions);
        inputMapScreen = new InputMapScreen(gameOptions);
        resolutionScreen = new ResolutionScreen(menuLayout, gameOptions);
        creditsScreen = new CreditsScreen(gameOptions);
        loadingScreen = new LoadingScreen();
        newGameScreen = new NewGameScreen(menuLayout, gameOptions);
        newShipScreen = new NewShipScreen(menuLayout, gameOptions);
    }
}
