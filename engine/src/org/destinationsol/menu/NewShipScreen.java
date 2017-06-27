/*
 * Copyright 2017 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.destinationsol.menu;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.JsonValue;
import org.destinationsol.GameOptions;
import org.destinationsol.SolApplication;
import org.destinationsol.assets.Assets;
import org.destinationsol.assets.json.Json;
import org.destinationsol.common.SolColor;
import org.destinationsol.files.HullConfigManager;
import org.destinationsol.game.ShipConfig;
import org.destinationsol.game.item.ItemManager;
import org.destinationsol.game.planet.SysConfig;
import org.destinationsol.ui.FontSize;
import org.destinationsol.ui.SolInputManager;
import org.destinationsol.ui.SolUiControl;
import org.destinationsol.ui.SolUiScreen;
import org.destinationsol.ui.UiDrawer;
import org.terasology.assets.ResourceUrn;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class NewShipScreen implements SolUiScreen {
    private final TextureAtlas.AtlasRegion bgTex;

    private final List<SolUiControl> controls = new ArrayList<>();
    private SolUiControl cancelControl;
    private List<String> playerSpawnConfigNames = new ArrayList<>();

    NewShipScreen(MenuLayout menuLayout, GameOptions gameOptions) {
        loadPlayerSpawnConfigs();

        int row = 1;
        for (String playerSpawnConfigName : playerSpawnConfigNames) {
            SolUiControl uiControl = new SolUiControl(menuLayout.buttonRect(-1, row++), true);
            uiControl.setDisplayName(playerSpawnConfigName);
            controls.add(uiControl);
        }

        cancelControl = new SolUiControl(menuLayout.buttonRect(-1, row), true, gameOptions.getKeyEscape());
        cancelControl.setDisplayName("Cancel");
        controls.add(cancelControl);

        bgTex = Assets.getAtlasRegion("engine:mainMenuBg", Texture.TextureFilter.Linear);
    }

    @Override
    public List<SolUiControl> getControls() {
        return controls;
    }

    @Override
    public void updateCustom(SolApplication solApplication, SolInputManager.InputPointer[] inputPointers, boolean clickedOutside) {
        if (cancelControl.isJustOff()) {
            solApplication.getInputMan().setScreen(solApplication, solApplication.getMenuScreens().newGame);
            return;
        }

        for (SolUiControl control : controls) {
            if (control == cancelControl) {
                continue;
            }

            if (control.isJustOff()) {
                solApplication.loadNewGame(false, control.getDisplayName());
                return;
            }
        }
    }

    @Override
    public void drawText(UiDrawer uiDrawer, SolApplication solApplication) {
        uiDrawer.drawString("Warning: This will erase any old ship you might have had!", .5f * uiDrawer.r, .3f, FontSize.MENU, true, SolColor.WHITE);
    }

    @Override
    public void drawBg(UiDrawer uiDrawer, SolApplication solApplication) {
        uiDrawer.draw(bgTex, uiDrawer.r, 1, uiDrawer.r / 2, 0.5f, uiDrawer.r / 2, 0.5f, 0, SolColor.WHITE);
    }

    private void loadPlayerSpawnConfigs() {
        Set<ResourceUrn> configUrnList = Assets.getAssetHelper().list(Json.class, "[a-z]*:playerSpawnConfig");

        for (ResourceUrn configUrn : configUrnList) {
            Json json = Assets.getJson(configUrn.toString());
            JsonValue rootNode = json.getJsonValue();

            for (JsonValue node : rootNode) {
                playerSpawnConfigNames.add(node.name);
            }

            json.dispose();
        }
    }
}
