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

public class MenuScreens {
    public MainMenuScreen mainScreen;
    public OptionsScreen optionsScreen;
    public InputMapScreen inputMapScreen;
    public ResolutionScreen resolutionScreen;
    public CreditsScreen creditsScreen;
    public LoadingScreen loadingScreen;
    public NewGameScreen newGameScreen;
    public NewShipScreen newShipScreen;

    public void initialize(boolean mobile, GameOptions gameOptions) {
        mainScreen = new MainMenuScreen(mobile, gameOptions);
        optionsScreen = new OptionsScreen(gameOptions);
        inputMapScreen = new InputMapScreen(gameOptions);
        resolutionScreen = new ResolutionScreen(gameOptions);
        creditsScreen = new CreditsScreen(gameOptions);
        loadingScreen = new LoadingScreen();
        newGameScreen = new NewGameScreen(gameOptions);
        newShipScreen = new NewShipScreen(gameOptions);
    }
}
