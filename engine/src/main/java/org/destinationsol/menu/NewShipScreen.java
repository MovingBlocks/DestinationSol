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

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.JsonValue;
import org.destinationsol.GameOptions;
import org.destinationsol.SolApplication;
import org.destinationsol.assets.Assets;
import org.destinationsol.assets.json.Json;
import org.destinationsol.common.SolColor;
import org.destinationsol.game.planet.SystemsBuilder;
import org.destinationsol.ui.DisplayDimensions;
import org.destinationsol.ui.FontSize;
import org.destinationsol.ui.SolInputManager;
import org.destinationsol.ui.SolUiBaseScreen;
import org.destinationsol.ui.SolUiControl;
import org.destinationsol.ui.UiDrawer;
import org.terasology.assets.ResourceUrn;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class NewShipScreen extends SolUiBaseScreen {
    private DisplayDimensions displayDimensions;

    private final TextureAtlas.AtlasRegion backgroundTexture;

    private SolUiControl okControl;
    private SolUiControl cancelControl;
    private SolUiControl systemCountControl;
    private SolUiControl playerSpawnConfigControl;
    private int playerSpawnConfigIndex = 0;
    private List<String> playerSpawnConfigNames = new ArrayList<>();
    private int numberOfSystems = SystemsBuilder.DEFAULT_SYSTEM_COUNT;

    NewShipScreen(MenuLayout menuLayout, GameOptions gameOptions) {
        displayDimensions = SolApplication.displayDimensions;

        loadPlayerSpawnConfigs();

        int row = 1;
        systemCountControl = new SolUiControl(menuLayout.buttonRect(-1, row++), true);
        systemCountControl.setDisplayName("Systems: " + numberOfSystems);
        controls.add(systemCountControl);

        playerSpawnConfigControl = new SolUiControl(menuLayout.buttonRect(-1, row++), true);
        playerSpawnConfigControl.setDisplayName("Starting Ship: " + playerSpawnConfigNames.get(playerSpawnConfigIndex));
        controls.add(playerSpawnConfigControl);

        okControl = new SolUiControl(menuLayout.buttonRect(-1, row++), true, gameOptions.getKeyEscape());
        okControl.setDisplayName("OK");
        controls.add(okControl);

        cancelControl = new SolUiControl(menuLayout.buttonRect(-1, row), true, gameOptions.getKeyEscape());
        cancelControl.setDisplayName("Cancel");
        controls.add(cancelControl);

        backgroundTexture = Assets.getAtlasRegion("engine:mainMenuBg", Texture.TextureFilter.Linear);
    }

    @Override
    public void updateCustom(SolApplication solApplication, SolInputManager.InputPointer[] inputPointers, boolean clickedOutside) {
        if (okControl.isJustOff()) {
            solApplication.play(false, playerSpawnConfigNames.get(playerSpawnConfigIndex), true);
            return;
        }

        if (cancelControl.isJustOff()) {
            solApplication.getInputManager().setScreen(solApplication, solApplication.getMenuScreens().newGame);
            return;
        }

        if (systemCountControl.isJustOff()) {
            int systemCount = (numberOfSystems + 1) % 10;
            if (systemCount < 2) {
                systemCount = 2;
            }
            numberOfSystems = systemCount;
            systemCountControl.setDisplayName("Systems: " + numberOfSystems);
        }

        if (playerSpawnConfigControl.isJustOff()) {
            playerSpawnConfigIndex = (playerSpawnConfigIndex + 1) % playerSpawnConfigNames.size();
            playerSpawnConfigControl.setDisplayName("Starting Ship: " + playerSpawnConfigNames.get(playerSpawnConfigIndex));
        }
    }

    @Override
    public void drawText(UiDrawer uiDrawer, SolApplication solApplication) {
        uiDrawer.drawString("Warning: This will erase any old ship you might have had!", .5f * displayDimensions.getRatio(), .3f, FontSize.MENU, true, SolColor.WHITE);
    }

    @Override
    public void drawBackground(UiDrawer uiDrawer, SolApplication solApplication) {
        uiDrawer.draw(backgroundTexture, displayDimensions.getRatio(), 1, displayDimensions.getRatio() / 2, 0.5f, displayDimensions.getRatio() / 2, 0.5f, 0, SolColor.WHITE);
    }

    private void loadPlayerSpawnConfigs() {
        Set<ResourceUrn> configUrnList = Assets.getAssetHelper().list(Json.class, "[a-zA-Z]*:playerSpawnConfig");

        for (ResourceUrn configUrn : configUrnList) {
            Json json = Assets.getJson(configUrn.toString());
            JsonValue rootNode = json.getJsonValue();

            for (JsonValue node : rootNode) {
                playerSpawnConfigNames.add(node.name);
            }

            json.dispose();
        }
    }

    public int getNumberOfSystems() {
        return numberOfSystems;
    }
}
