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

import org.destinationsol.GameOptions;
import org.destinationsol.game.SolGame;
import org.destinationsol.game.tutorial.TutorialStep;
import org.destinationsol.ui.nui.screens.TutorialScreen;
import org.terasology.input.ControllerInput;
import org.terasology.input.InputType;
import org.terasology.input.MouseInput;
import org.terasology.nui.backends.libgdx.GDXInputUtil;

public class MessageStep extends TutorialStep {
    protected static final float MIN_STEP_DURATION = 0.5f;
    protected final TutorialScreen tutorialScreen;
    protected final SolGame game;
    protected final String message;
    protected float stepTimer;
    protected boolean interactComplete;

    public MessageStep(TutorialScreen tutorialScreen, SolGame game, String message) {
        this.tutorialScreen = tutorialScreen;
        this.game = game;
        this.message = message;
        this.stepTimer = 0.0f;
    }

    @Override
    public void start() {
        tutorialScreen.setTutorialText(message);
        GameOptions gameOptions = game.getSolApplication().getOptions();
        switch (gameOptions.controlType) {
            case KEYBOARD:
                tutorialScreen.setInteractHintInput(GDXInputUtil.GDXToNuiKey(gameOptions.getKeyShoot()));
                break;
            case MOUSE:
            case MIXED:
                tutorialScreen.setInteractHintInput(MouseInput.MOUSE_LEFT);
                break;
            case CONTROLLER:
                if (gameOptions.getControllerAxisShoot() > 0) {
                    // Ideally this would use CONTROLLER_AXIS but the ids do not quite match-up.
                    tutorialScreen.setInteractHintInput(ControllerInput.find(InputType.CONTROLLER_BUTTON, gameOptions.getControllerAxisShoot()));
                } else {
                    tutorialScreen.setInteractHintInput(ControllerInput.find(InputType.CONTROLLER_BUTTON, gameOptions.getControllerButtonShoot()));
                }
                break;
        }
        interactComplete = false;
        tutorialScreen.setInteractEvent(input -> {
            interactComplete = true;
        });
    }

    @Override
    public boolean checkComplete(float timeStep) {
        stepTimer += timeStep;
        return stepTimer >= MIN_STEP_DURATION && interactComplete;
    }
}