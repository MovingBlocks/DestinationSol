/*
 * Copyright 2023 The Terasology Foundation
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

package org.destinationsol.game.tutorial.steps;

import org.destinationsol.game.screens.GameScreens;
import org.destinationsol.game.tutorial.TutorialStep;
import org.destinationsol.ui.nui.widgets.UIWarnButton;

import javax.inject.Inject;

/**
 * A tutorial step that completes when the player selects an equipped weapon.
 * The step hints to the player those items that are already equipped.
 */
public class SelectEquippedWeaponStep extends TutorialStep {
    @Inject
    protected GameScreens gameScreens;
    private final String message;

    @Inject
    protected SelectEquippedWeaponStep() {
        throw new RuntimeException("Attempted to instantiate TutorialStep via DI. This is not supported.");
    }

    public SelectEquippedWeaponStep(String message) {
        this.message = message;
    }

    @Override
    public void start() {
        setTutorialText(message);
    }

    @Override
    public boolean checkComplete(float timeStep) {
        for (UIWarnButton button : gameScreens.inventoryScreen.getEquippedItemUIControlsForTutorial()) {
            button.enableWarn();
        }
        return gameScreens.inventoryScreen.getSelectedItem().isEquipped() > 0;
    }
}
