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
import org.destinationsol.game.input.Pilot;
import org.destinationsol.game.screens.GameScreens;
import org.destinationsol.game.screens.ShipUiControl;
import org.destinationsol.game.tutorial.TutorialStep;
import org.destinationsol.ui.nui.screens.UIShipControlsScreen;
import org.destinationsol.ui.nui.widgets.UIWarnButton;

import javax.inject.Inject;

/**
 * A tutorial step that completes once the player ship has attempted turning both left and right.
 */
public class TurnLeftRightStep extends TutorialStep {
    private static final float LEFT_TURN_DURATION = 0.75f;
    private static final float RIGHT_TURN_DURATION = 0.75f;
    @Inject
    protected SolGame game;
    @Inject
    protected GameScreens gameScreens;
    private final String message;
    private UIWarnButton leftButton;
    private UIWarnButton rightButton;
    private float leftSeconds = 0;
    private float rightSeconds = 0;

    @Inject
    protected TurnLeftRightStep() {
        throw new RuntimeException("Attempted to instantiate TutorialStep via DI. This is not supported.");
    }

    public TurnLeftRightStep(String message) {
        this.message = message;
    }

    public void start() {
        ShipUiControl shipUiControl = gameScreens.oldMainGameScreen.getShipControl();
        if (shipUiControl instanceof UIShipControlsScreen) {
            UIShipControlsScreen uiShipControlsScreen = (UIShipControlsScreen) shipUiControl;
            this.leftButton = uiShipControlsScreen.getLeftButton();
            this.rightButton = uiShipControlsScreen.getRightButton();
        }
        setTutorialText(message);
    }

    public boolean checkComplete(float timeStep) {
        if (this.leftButton != null && leftSeconds < LEFT_TURN_DURATION) {
            this.leftButton.enableWarn();
        }
        if (this.rightButton != null && rightSeconds < RIGHT_TURN_DURATION) {
            this.rightButton.enableWarn();
        }

        Hero hero = game.getHero();
        Pilot playerPilot = hero.getShip().getPilot();

        if (playerPilot.isLeft()) {
            leftSeconds += timeStep;
        }

        if (playerPilot.isRight()) {
            rightSeconds += timeStep;
        }

        return (leftSeconds >= LEFT_TURN_DURATION && rightSeconds >= RIGHT_TURN_DURATION);
    }
}
