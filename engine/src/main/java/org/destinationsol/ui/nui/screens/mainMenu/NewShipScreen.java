/*
 * Copyright 2021 The Terasology Foundation
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
package org.destinationsol.ui.nui.screens.mainMenu;

import org.destinationsol.SolApplication;
import org.destinationsol.assets.Assets;
import org.destinationsol.assets.json.Json;
import org.destinationsol.assets.json.Validator;
import org.destinationsol.common.In;
import org.destinationsol.game.WorldConfig;
import org.destinationsol.game.planet.SystemsBuilder;
import org.destinationsol.ui.nui.NUIManager;
import org.destinationsol.ui.nui.NUIScreenLayer;
import org.destinationsol.ui.nui.widgets.KeyActivatedButton;
import org.terasology.gestalt.assets.ResourceUrn;
import org.terasology.nui.Canvas;
import org.terasology.nui.backends.libgdx.GDXInputUtil;
import org.terasology.nui.widgets.UIButton;

import java.util.ArrayList;
import java.util.List;

public class NewShipScreen extends NUIScreenLayer {
    @In
    private SolApplication solApplication;
    private int numberOfSystems = SystemsBuilder.DEFAULT_SYSTEM_COUNT;
    private int playerSpawnConfigIndex = 0;
    private List<String> playerSpawnConfigNames = new ArrayList<>();

    @Override
    public void initialise() {
        UIButton systemsButton = find("systemsButton", UIButton.class);
        systemsButton.setText("Systems: " + numberOfSystems);
        systemsButton.subscribe(button -> {
            int systemCount = (numberOfSystems + 1) % 10;
            if (systemCount < 2) {
                systemCount = 2;
            }
            numberOfSystems = systemCount;
            ((UIButton)button).setText("Systems: " + numberOfSystems);
        });

        for (ResourceUrn configUrn : Assets.getAssetHelper().listAssets(Json.class, "playerSpawnConfig")) {
            playerSpawnConfigNames.addAll(Validator.getValidatedJSON(configUrn.toString(), "engine:schemaPlayerSpawnConfig").keySet());
        }

        UIButton startingShipButton = find("startingShipButton", UIButton.class);
        startingShipButton.setText("Starting Ship: " + playerSpawnConfigNames.get(playerSpawnConfigIndex));
        startingShipButton.subscribe(button -> {
            playerSpawnConfigIndex = (playerSpawnConfigIndex + 1) % playerSpawnConfigNames.size();
            ((UIButton)button).setText("Starting Ship: " + playerSpawnConfigNames.get(playerSpawnConfigIndex));
        });

        // NOTE: The original code used getKeyEscape() for both the "OK" and "Cancel" buttons. This was probably a mistake.
        KeyActivatedButton okButton = find("okButton", KeyActivatedButton.class);
        okButton.setKey(GDXInputUtil.GDXToNuiKey(solApplication.getOptions().getKeyShoot()));
        okButton.subscribe(button -> {
            WorldConfig worldConfig = new WorldConfig();
            worldConfig.setNumberOfSystems(numberOfSystems);

            solApplication.getInputManager().setScreen(solApplication, solApplication.getMenuScreens().loading);
            solApplication.getMenuScreens().loading.setMode(false, playerSpawnConfigNames.get(playerSpawnConfigIndex), true, worldConfig);
            solApplication.getNuiManager().removeScreen(this);
        });

        KeyActivatedButton cancelButton = find("cancelButton", KeyActivatedButton.class);
        cancelButton.setKey(GDXInputUtil.GDXToNuiKey(solApplication.getOptions().getKeyEscape()));
        cancelButton.subscribe(button -> {
            solApplication.getNuiManager().removeScreen(this);
            solApplication.getNuiManager().pushScreen(solApplication.getMenuScreens().newGame);
        });
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        solApplication.getMenuBackgroundManager().update();
    }

    @Override
    public void onDraw(Canvas canvas) {
        try (NUIManager.LegacyUiDrawerWrapper wrapper = nuiManager.getLegacyUiDrawer()) {
            solApplication.getMenuBackgroundManager().draw(wrapper.getUiDrawer());
        }

        super.onDraw(canvas);
    }

    @Override
    protected boolean escapeCloses() {
        return false;
    }
}
