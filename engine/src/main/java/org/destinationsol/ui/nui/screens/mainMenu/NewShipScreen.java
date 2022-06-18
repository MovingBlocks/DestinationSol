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
import org.destinationsol.game.WorldConfig;
import org.destinationsol.game.planet.SystemsBuilder;
import org.destinationsol.modules.ModuleManager;
import org.destinationsol.ui.nui.NUIManager;
import org.destinationsol.ui.nui.NUIScreenLayer;
import org.destinationsol.ui.nui.widgets.KeyActivatedButton;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.gestalt.assets.ResourceUrn;
import org.terasology.gestalt.module.Module;
import org.terasology.gestalt.naming.Name;
import org.terasology.nui.Canvas;
import org.terasology.nui.UITextureRegion;
import org.terasology.nui.backends.libgdx.GDXInputUtil;
import org.terasology.nui.widgets.UIButton;
import org.terasology.nui.widgets.UIImage;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class NewShipScreen extends NUIScreenLayer {
    private static final Logger logger = LoggerFactory.getLogger(NewShipScreen.class);
    private final SolApplication solApplication;
    private final ModuleManager moduleManager;
    private int playerSpawnConfigIndex = 0;
    private List<String> playerSpawnConfigNames = new ArrayList<>();
    private List<UITextureRegion> playerSpawnConfigTextures = new ArrayList<>();
    private WorldConfig worldConfig;

    @Inject
    public NewShipScreen(SolApplication solApplication, ModuleManager moduleManager) {
        this.solApplication = solApplication;
        this.moduleManager = moduleManager;
    }

    @Override
    public void initialise() {
        worldConfig = new WorldConfig();
        worldConfig.setNumberOfSystems(SystemsBuilder.DEFAULT_SYSTEM_COUNT);
        worldConfig.setModules(new HashSet<>(moduleManager.getEnvironment().getModulesOrderedByDependencies()));

        UIButton systemsButton = find("systemsButton", UIButton.class);
        systemsButton.setText("Systems: " + worldConfig.getNumberOfSystems());
        systemsButton.subscribe(button -> {
            int systemCount = (worldConfig.getNumberOfSystems() + 1) % 10;
            if (systemCount < 2) {
                systemCount = 2;
            }
            worldConfig.setNumberOfSystems(systemCount);
            ((UIButton)button).setText("Systems: " + worldConfig.getNumberOfSystems());
        });

        for (ResourceUrn configUrn : Assets.getAssetHelper().listAssets(Json.class, "playerSpawnConfig")) {
            JSONObject playerSpawnConfigs = Validator.getValidatedJSON(configUrn.toString(), "engine:schemaPlayerSpawnConfig");
            playerSpawnConfigNames.addAll(playerSpawnConfigs.keySet());
            for (String spawnConfigName : playerSpawnConfigs.keySet()) {
                JSONObject playerSpawnConfig = playerSpawnConfigs.getJSONObject(spawnConfigName);
                try {
                    playerSpawnConfigTextures.add(Assets.getDSTexture(playerSpawnConfig.getString("hull")).getUiTexture());
                } catch (RuntimeException e) {
                    logger.error("Failed to load ship texture!", e);
                    // Null values will not render any texture.
                    playerSpawnConfigTextures.add(null);
                }
            }
        }

        UIImage shipPreviewImage = find("shipPreviewImage", UIImage.class);
        shipPreviewImage.setImage(playerSpawnConfigTextures.get(playerSpawnConfigIndex));

        UIButton startingShipButton = find("startingShipButton", UIButton.class);
        startingShipButton.setText("Starting Ship: " + playerSpawnConfigNames.get(playerSpawnConfigIndex));
        startingShipButton.subscribe(button -> {
            playerSpawnConfigIndex = (playerSpawnConfigIndex + 1) % playerSpawnConfigNames.size();
            ((UIButton)button).setText("Starting Ship: " + playerSpawnConfigNames.get(playerSpawnConfigIndex));
            shipPreviewImage.setImage(playerSpawnConfigTextures.get(playerSpawnConfigIndex));
        });

        UIButton modulesButton = find("modulesButton", UIButton.class);
        modulesButton.subscribe(button -> {
            ModulesScreen modulesScreen = solApplication.getMenuScreens().modules;
            modulesScreen.setSelectedModules(worldConfig.getModules());
            nuiManager.setScreen(modulesScreen);
        });

        // NOTE: The original code used getKeyEscape() for both the "OK" and "Cancel" buttons. This was probably a mistake.
        KeyActivatedButton okButton = find("okButton", KeyActivatedButton.class);
        okButton.setKey(GDXInputUtil.GDXToNuiKey(solApplication.getOptions().getKeyShoot()));
        okButton.subscribe(button -> {
            LoadingScreen loadingScreen = solApplication.getMenuScreens().loading;
            loadingScreen.setMode(false, playerSpawnConfigNames.get(playerSpawnConfigIndex), true, worldConfig);
            nuiManager.setScreen(loadingScreen);
        });

        KeyActivatedButton cancelButton = find("cancelButton", KeyActivatedButton.class);
        cancelButton.setKey(GDXInputUtil.GDXToNuiKey(solApplication.getOptions().getKeyEscape()));
        cancelButton.subscribe(button -> {
            nuiManager.setScreen(solApplication.getMenuScreens().newGame);
        });
    }

    @Override
    public void onAdded() {
        worldConfig.setSeed(System.currentTimeMillis());

        String currentShip = playerSpawnConfigNames.get(playerSpawnConfigIndex);
        playerSpawnConfigNames.clear();
        Set<ResourceUrn> configUrns = Assets.getAssetHelper().listAssets(Json.class, "playerSpawnConfig");
        for (Module module : worldConfig.getModules()) {
            ResourceUrn configUrn = new ResourceUrn(module.getId(), new Name("playerSpawnConfig"));
            if (configUrns.contains(configUrn)) {
                playerSpawnConfigNames.addAll(Validator.getValidatedJSON(configUrn.toString(), "engine:schemaPlayerSpawnConfig").keySet());
            }
        }

        if (!playerSpawnConfigNames.contains(currentShip)) {
            // The player picked a ship that's now invalid, so reset their selection.
            playerSpawnConfigIndex = 0;
            UIButton startingShipButton = find("startingShipButton", UIButton.class);
            startingShipButton.setText("Starting Ship: " + playerSpawnConfigNames.get(playerSpawnConfigIndex));
        }
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
