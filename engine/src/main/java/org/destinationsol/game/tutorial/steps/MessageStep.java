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
import org.destinationsol.SolApplication;
import org.destinationsol.game.tutorial.TutorialStep;
import org.terasology.input.ControllerInput;
import org.terasology.input.InputType;
import org.terasology.input.MouseInput;
import org.terasology.nui.backends.libgdx.GDXInputUtil;

import javax.inject.Inject;

/**
 * A tutorial step that displays a message to the player and waits for input before completing.
 * The input is hinted-at in the corner of the message box.
 * There is a cooldown to prevent continuously skipping these messages by holding down the advance input.
 */
public class MessageStep extends TutorialStep {
    protected static final float MIN_STEP_DURATION = 0.5f;
    @Inject
    protected SolApplication solApplication;
    protected final String message;
    protected float stepTimer;
    protected boolean interactComplete;

    @Inject
    protected MessageStep() {
        throw new RuntimeException("Attempted to instantiate TutorialStep via DI. This is not supported.");
    }

    public MessageStep(String message) {
        this.message = message;
        this.stepTimer = 0.0f;
    }

    @Override
    public void start() {
        setTutorialText(message);
        GameOptions gameOptions = solApplication.getOptions();
        switch (gameOptions.controlType) {
            case KEYBOARD:
                setRequiredInput(GDXInputUtil.GDXToNuiKey(gameOptions.getKeyShoot()));
                break;
            case MOUSE:
            case MIXED:
                setRequiredInput(MouseInput.MOUSE_LEFT);
                break;
            case CONTROLLER:
                if (gameOptions.getControllerAxisShoot() > 0) {
                    // Ideally this would use CONTROLLER_AXIS but the ids do not quite match-up.
                    setRequiredInput(ControllerInput.find(InputType.CONTROLLER_BUTTON, gameOptions.getControllerAxisShoot()));
                } else {
                    setRequiredInput(ControllerInput.find(InputType.CONTROLLER_BUTTON, gameOptions.getControllerButtonShoot()));
                }
                break;
        }
        if (solApplication.isMobile()) {
            setRequiredInput(MouseInput.MOUSE_LEFT);
        }
        interactComplete = false;
        setInputHandler(input -> {
            interactComplete = true;
        });
    }

    @Override
    public boolean checkComplete(float timeStep) {
        stepTimer += timeStep;
        return stepTimer >= MIN_STEP_DURATION && interactComplete;
    }
}