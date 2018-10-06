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
import org.destinationsol.di.Qualifier.MobileQualifier;
import org.destinationsol.ui.SolLayouts;
import org.destinationsol.ui.UiDrawer;

import javax.inject.Inject;

public class MenuScreens {
    public final MainMenuScreen main;
    public final OptionsScreen options;
    public final InputMapScreen inputMapScreen;
    public final ResolutionScreen resolutionScreen;
    public final CreditsScreen credits;
    public final LoadingScreen loading;
    public final NewGameScreen newGame;
    public final NewShipScreen newShip;

    @Inject
    public MenuScreens(SolLayouts layouts, @MobileQualifier boolean mobile, UiDrawer drawer, GameOptions gameOptions) {
        MenuLayout menuLayout = layouts.menuLayout;
        main = new MainMenuScreen(menuLayout, mobile, drawer.r, gameOptions);
        options = new OptionsScreen(menuLayout, gameOptions);
        inputMapScreen = new InputMapScreen(drawer.r, gameOptions);
        resolutionScreen = new ResolutionScreen(menuLayout, gameOptions);
        credits = new CreditsScreen(drawer.r, gameOptions);
        loading = new LoadingScreen();
        newGame = new NewGameScreen(menuLayout, gameOptions);
        newShip = new NewShipScreen(menuLayout, gameOptions);
    }
}
