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
import org.destinationsol.ui.nui.NUIManager;
import org.destinationsol.ui.nui.screens.InventoryScreen;
import org.destinationsol.ui.nui.screens.MenuScreen;
import org.destinationsol.ui.nui.screens.TalkScreen;

public class GameScreens {
    public final MainGameScreen mainGameScreen;
    public final MapScreen mapScreen;
    public final MenuScreen menuScreen;
    public final InventoryScreen inventoryScreen;
    public final TalkScreen talkScreen;
    public final WaypointCreationScreen waypointCreationScreen;
    public final ConsoleScreen consoleScreen;

    public GameScreens(SolApplication cmp, Context context) {
        SolLayouts layouts = cmp.getLayouts();
        RightPaneLayout rightPaneLayout = layouts.rightPaneLayout;
        NUIManager nuiManager = cmp.getNuiManager();

        mainGameScreen = new MainGameScreen(rightPaneLayout, context);
        mapScreen = new MapScreen(rightPaneLayout, cmp.isMobile(), cmp.getOptions());
        menuScreen = (MenuScreen) nuiManager.createScreen("engine:menuScreen");
        inventoryScreen = (InventoryScreen) nuiManager.createScreen("engine:inventoryScreen");
        talkScreen = (TalkScreen) nuiManager.createScreen("engine:talkScreen");
        waypointCreationScreen = new WaypointCreationScreen(layouts.menuLayout, cmp.getOptions(), mapScreen);
        consoleScreen = new ConsoleScreen(context);
    }

    // This was added for PlayerCreatorTest.java (used in PlayerCreator)
    // so that it can successfully mock the returned result.
    public MainGameScreen getMainGameScreen() {
        return mainGameScreen;
    }
}
