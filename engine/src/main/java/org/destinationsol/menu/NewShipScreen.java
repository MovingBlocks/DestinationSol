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
import org.destinationsol.GameOptions;
import org.destinationsol.SolApplication;
import org.destinationsol.assets.Assets;
import org.destinationsol.assets.json.Json;
import org.destinationsol.assets.json.Validator;
import org.destinationsol.common.SolColor;
import org.destinationsol.game.planet.SystemsBuilder;
import org.destinationsol.ui.DisplayDimensions;
import org.destinationsol.ui.FontSize;
import org.destinationsol.ui.SolUiBaseScreen;
import org.destinationsol.ui.UiDrawer;
import org.destinationsol.ui.responsiveUi.UiRelativeLayout;
import org.destinationsol.ui.responsiveUi.UiTextButton;
import org.destinationsol.ui.responsiveUi.UiVerticalListLayout;
import org.json.JSONObject;
import org.terasology.assets.ResourceUrn;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.destinationsol.ui.UiDrawer.UI_POSITION_BOTTOM;
import static org.destinationsol.ui.responsiveUi.UiTextButton.DEFAULT_BUTTON_PADDING;

public class NewShipScreen extends SolUiBaseScreen {
    private DisplayDimensions displayDimensions;

    private final TextureAtlas.AtlasRegion backgroundTexture;

    private int playerSpawnConfigIndex = 0;
    private List<String> playerSpawnConfigNames = new ArrayList<>();
    private int numberOfSystems = SystemsBuilder.DEFAULT_SYSTEM_COUNT;

    NewShipScreen(GameOptions gameOptions) {
        loadPlayerSpawnConfigs();

        displayDimensions = SolApplication.displayDimensions;

        UiVerticalListLayout buttonList = new UiVerticalListLayout();

        buttonList.addElement(new UiTextButton().setDisplayName(getNumberOfSystemsString())
                .enableSound()
                .setOnReleaseAction(uiElement -> {
                    numberOfSystems = Math.max(2, (numberOfSystems + 1) % 10);
                    ((UiTextButton)uiElement).setDisplayName(getNumberOfSystemsString());
                }));

        buttonList.addElement(new UiTextButton().setDisplayName(getPlayerSpawnConfigString())
                .enableSound()
                .setOnReleaseAction(uiElement -> {
                    playerSpawnConfigIndex = (playerSpawnConfigIndex + 1) % playerSpawnConfigNames.size();
                    ((UiTextButton)uiElement).setDisplayName(getPlayerSpawnConfigString());
                }));

        buttonList.addElement(new UiTextButton().setDisplayName("Play")
                .setTriggerKey(gameOptions.getKeyShoot())
                .enableSound()
                .setOnReleaseAction(uiElement -> SolApplication.getInstance().play(false, playerSpawnConfigNames.get(playerSpawnConfigIndex), true)));

        buttonList.addElement(new UiTextButton().setDisplayName("Cancel")
                .setTriggerKey(gameOptions.getKeyEscape())
                .enableSound()
                .setOnReleaseAction(uiElement -> SolApplication.changeScreen(SolApplication.getMenuScreens().newGameScreen)));

        rootUiElement = new UiRelativeLayout().addElement(buttonList, UI_POSITION_BOTTOM, 0, -buttonList.getHeight() / 2 - DEFAULT_BUTTON_PADDING);

        backgroundTexture = Assets.getAtlasRegion("engine:mainMenuBg", Texture.TextureFilter.Linear);
    }

    @Override
    public void draw(UiDrawer uiDrawer, SolApplication solApplication) {
        uiDrawer.drawString("Warning: This will erase any old ship you might have had!", .5f * displayDimensions.getRatio(), .3f, FontSize.MENU, true, SolColor.WHITE);
    }

    @Override
    public void drawBackground(UiDrawer uiDrawer, SolApplication solApplication) {
        uiDrawer.draw(backgroundTexture, displayDimensions.getRatio(), 1, displayDimensions.getRatio() / 2, 0.5f, displayDimensions.getRatio() / 2, 0.5f, 0, SolColor.WHITE);
    }

    private void loadPlayerSpawnConfigs() {
        Set<ResourceUrn> configUrnList = Assets.getAssetHelper().list(Json.class, "[a-zA-Z]*:playerSpawnConfig");

        for (ResourceUrn configUrn : configUrnList) {
            JSONObject rootNode = Validator.getValidatedJSON(configUrn.toString(), "engine:schemaPlayerSpawnConfig");

            for (String s : rootNode.keySet()) {
                playerSpawnConfigNames.add(s);
            }

        }
    }

    private String getNumberOfSystemsString() {
        return "Systems: " + numberOfSystems;
    }

    private String getPlayerSpawnConfigString() {
        return "Starting Ship: " + playerSpawnConfigNames.get(playerSpawnConfigIndex);
    }
}
