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

import org.destinationsol.SolApplication;
import org.destinationsol.game.context.Context;
import org.destinationsol.ui.SolLayouts;
import org.destinationsol.ui.nui.screens.InventoryScreen;
import org.destinationsol.ui.nui.screens.MenuScreen;

public class GameScreens {
    private static final String NUI_MAIN_GAME_SCREEN_DESKTOP_URI = "engine:mainGameScreen_desktop";
    private static final String NUI_MAIN_GAME_SCREEN_MOBILE_URI = "engine:mainGameScreen_mobile";
    public final MainGameScreen oldMainGameScreen;
    public final MapScreen mapScreen;
    public final MenuScreen menuScreen;
    public final InventoryScreen inventoryScreen;
    public final TalkScreen talkScreen;
    public final WaypointCreationScreen waypointCreationScreen;
    public final ConsoleScreen consoleScreen;
    public final org.destinationsol.ui.nui.screens.MainGameScreen mainGameScreen;

    public GameScreens(SolApplication cmp, Context context) {
        SolLayouts layouts = cmp.getLayouts();
        RightPaneLayout rightPaneLayout = layouts.rightPaneLayout;
        oldMainGameScreen = new MainGameScreen(rightPaneLayout, context);
        mapScreen = new MapScreen(rightPaneLayout, cmp.isMobile(), cmp.getOptions());
        menuScreen = (MenuScreen) cmp.getNuiManager().createScreen("engine:menuScreen");
        inventoryScreen = (org.destinationsol.ui.nui.screens.InventoryScreen) cmp.getNuiManager().createScreen("engine:inventoryScreen");
        talkScreen = new TalkScreen(layouts.menuLayout, cmp.getOptions());
        waypointCreationScreen = new WaypointCreationScreen(layouts.menuLayout, cmp.getOptions(), mapScreen);
        consoleScreen = new ConsoleScreen(context);
        boolean isMobile = cmp.isMobile();
        if (!isMobile) {
            mainGameScreen = (org.destinationsol.ui.nui.screens.MainGameScreen) cmp.getNuiManager().createScreen(NUI_MAIN_GAME_SCREEN_DESKTOP_URI);
        } else {
            mainGameScreen = (org.destinationsol.ui.nui.screens.MainGameScreen) cmp.getNuiManager().createScreen(NUI_MAIN_GAME_SCREEN_MOBILE_URI);
        }
    }

    // This was added for PlayerCreatorTest.java (used in PlayerCreator)
    // so that it can successfully mock the returned result.
    public MainGameScreen getOldMainGameScreen() {
        return oldMainGameScreen;
    }
}
