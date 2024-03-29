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

import org.destinationsol.game.Hero;
import org.destinationsol.game.SolGame;
import org.destinationsol.game.screens.GameScreens;
import org.destinationsol.game.screens.ShipUiControl;
import org.destinationsol.game.tutorial.TutorialStep;
import org.destinationsol.ui.nui.screens.UIShipControlsScreen;
import org.destinationsol.ui.nui.widgets.UIWarnButton;

import javax.inject.Inject;

/**
 * A tutorial step that completes when the player ship uses its ability.
 */
public class UseAbilityStep extends TutorialStep {
    @Inject
    protected SolGame game;
    @Inject
    protected GameScreens gameScreens;
    private final String message;
    private UIWarnButton abilityButton;

    @Inject
    protected UseAbilityStep() {
        throw new RuntimeException("Attempted to instantiate TutorialStep via DI. This is not supported.");
    }

    public UseAbilityStep(String message) {
        this.message = message;
    }

    public void start() {
        ShipUiControl shipUiControl = gameScreens.oldMainGameScreen.getShipControl();
        if (shipUiControl instanceof UIShipControlsScreen) {
            abilityButton = ((UIShipControlsScreen) shipUiControl).getAbilityButton();
        }
        setTutorialText(message);
    }
    public boolean checkComplete(float timeStep) {
        if (abilityButton != null) {
            abilityButton.enableWarn();
        }

        Hero hero = game.getHero();
        return hero.getAbilityAwait() > 0.0f;
    }
}
