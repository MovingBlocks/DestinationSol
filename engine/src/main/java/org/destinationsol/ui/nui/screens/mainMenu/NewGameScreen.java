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
import org.destinationsol.game.SaveManager;
import org.destinationsol.game.WorldConfig;
import org.destinationsol.ui.nui.NUIManager;
import org.destinationsol.ui.nui.NUIScreenLayer;
import org.destinationsol.ui.nui.widgets.KeyActivatedButton;
import org.terasology.nui.Canvas;
import org.terasology.nui.backends.libgdx.GDXInputUtil;
import org.terasology.nui.widgets.UIButton;

import javax.inject.Inject;

public class NewGameScreen extends NUIScreenLayer {
    private final SolApplication solApplication;
    private KeyActivatedButton continueButton;
    private UIButton newGameButton;
    private KeyActivatedButton cancelButton;

    @Inject
    public NewGameScreen(SolApplication solApplication) {
        this.solApplication = solApplication;
    }

    @Override
    public void initialise() {
        super.initialise();

        continueButton = find("continueButton", KeyActivatedButton.class);
        continueButton.setKey(GDXInputUtil.GDXToNuiKey(solApplication.getOptions().getKeyShoot()));
        continueButton.setEnabled(SaveManager.hasPreviousCompatibleShip());
        continueButton.subscribe(button -> {
            solApplication.getMenuScreens().loading.setMode(false, null, false,
                    SaveManager.loadWorld().orElseGet(WorldConfig::new));
            nuiManager.setScreen(solApplication.getMenuScreens().loading);
        });

        newGameButton = find("newGameButton", UIButton.class);
        newGameButton.subscribe(button -> {
            nuiManager.setScreen(solApplication.getMenuScreens().newShip);
        });

        cancelButton = find("cancelButton", KeyActivatedButton.class);
        cancelButton.setKey(GDXInputUtil.GDXToNuiKey(solApplication.getOptions().getKeyEscape()));
        cancelButton.subscribe(button -> {
            nuiManager.setScreen(solApplication.getMenuScreens().main);
        });
    }

    @Override
    public void onAdded() {
        continueButton.setEnabled(SaveManager.hasPreviousCompatibleShip());
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
