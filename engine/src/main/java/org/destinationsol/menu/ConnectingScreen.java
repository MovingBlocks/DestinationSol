/*
 * Copyright 2020 The Terasology Foundation
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

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import org.destinationsol.SolApplication;
import org.destinationsol.assets.Assets;
import org.destinationsol.common.SolColor;
import org.destinationsol.game.WorldConfig;
import org.destinationsol.ui.DisplayDimensions;
import org.destinationsol.ui.FontSize;
import org.destinationsol.ui.SolInputManager;
import org.destinationsol.ui.SolUiBaseScreen;
import org.destinationsol.ui.UiDrawer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class ConnectingScreen extends SolUiBaseScreen {

    private final DisplayDimensions displayDimensions;
    private final TextureAtlas.AtlasRegion backgroundTexture;

    ConnectingScreen() {
        displayDimensions = SolApplication.displayDimensions;
        backgroundTexture = Assets.getAtlasRegion("engine:mainMenuBg", Texture.TextureFilter.Linear);
    }

    @Override
    public void updateCustom(SolApplication solApplication, SolInputManager.InputPointer[] inputPointers, boolean clickedOutside) {
        MenuScreens screens = solApplication.getMenuScreens();
        SolInputManager inputManager = solApplication.getInputManager();

        long seed;
        int numberOfSystems;

        try (Socket socket = new Socket("localhost", 8888)) {
            BufferedReader incoming = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            seed = Long.parseLong(incoming.readLine());
            numberOfSystems = Integer.parseInt(incoming.readLine());
        } catch (IOException ignore) {
            inputManager.setScreen(solApplication, screens.newGame);
            return;
        }

        inputManager.setScreen(solApplication, screens.loading);
        screens.loading.setMode(false, "Imperial Small", true, new WorldConfig(seed, numberOfSystems));
    }

    @Override
    public void drawText(UiDrawer uiDrawer, SolApplication solApplication) {
        uiDrawer.drawString("Connecting...", displayDimensions.getRatio() / 2, .5f, FontSize.MENU, true, SolColor.WHITE);
    }

    @Override
    public void drawBackground(UiDrawer uiDrawer, SolApplication solApplication) {
        uiDrawer.draw(backgroundTexture, displayDimensions.getRatio(), 1, displayDimensions.getRatio() / 2, 0.5f, displayDimensions.getRatio() / 2, 0.5f, 0, SolColor.WHITE);
    }
}
